/*
Copyright (c) 2011 James Ahlborn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.healthmarketscience.sqlbuilder;

import java.io.IOException;
import java.util.List;

import com.healthmarketscience.common.util.AppendableExt;
import com.healthmarketscience.sqlbuilder.dbspec.CheckConstraint;
import com.healthmarketscience.sqlbuilder.dbspec.Column;
import com.healthmarketscience.sqlbuilder.dbspec.Constraint;
import com.healthmarketscience.sqlbuilder.dbspec.ForeignKeyConstraint;

/**
 * Outputs a table or column constraint clause (depending on the current
 * context) <code>[ CONSTRAINT &lt;name&gt; ] &lt;type&gt; [ (&lt;col1&gt; ...) ]</code>.
 *
 * @author James Ahlborn
 */
public class ConstraintClause extends SqlObject
{
  /**
   * Enum representing the types of constraints supported for a column or
   * table.
   */
  public enum Type
  {
    NOT_NULL("NOT NULL"),
    UNIQUE("UNIQUE"),
    PRIMARY_KEY("PRIMARY KEY"),
    FOREIGN_KEY("FOREIGN KEY", "REFERENCES"),
    CHECK("CHECK");

    private final String _tableTypeStr;
    private final String _colTypeStr;

    private Type(String colTypeStr) {
      this(colTypeStr, colTypeStr);
    }

    private Type(String tableTypeStr, String colTypeStr) {
      _tableTypeStr = tableTypeStr;
      _colTypeStr = colTypeStr;
    }

    public String toString(boolean forTable) {
      return (forTable ? _tableTypeStr : _colTypeStr);
    }
  }

  /**
   * Enum representing the different times that a constraint can be checked.
   */
  public enum CheckTime
  {
    NOT_DEFERRABLE("NOT DEFERRABLE"),
    DEFERRABLE_INITIALLY_DEFERRED("DEFERRABLE INITIALLY DEFERRED"),
    DEFERRABLE_INITIALLY_IMMEDIATE("DEFERRABLE INITIALLY IMMEDIATE");

    private final String _str;

    private CheckTime(String str) {
      _str = str;
    }

    @Override
    public String toString() {
      return _str;
    }
  }


  protected final Type _type;
  protected final SqlObject _name;
  protected SqlObjectList<SqlObject> _columns = SqlObjectList.create();
  protected Object _checkTime;

  public ConstraintClause(Constraint constraint) {
    this(getType(constraint.getType()), constraint, constraint.getColumns());
  }

  public ConstraintClause(Type type, Object name) {
    this(type, name, null);
  }

  protected ConstraintClause(Type type, Object name, List<?> columns) {
    _type = type;
    _name = Converter.toCustomConstraintSqlObject(name);
    _columns.addObjects(Converter.CUSTOM_COLUMN_TO_OBJ, columns);
  }

  /**
   * Adds a column to the constraint definition.
   */
  public ConstraintClause addColumns(Column... columns) {
    return addCustomColumns((Object[])columns);
  }

  /**
   * Adds a custom column to the constraint definition.
   * <p>
   * {@code Object} -&gt; {@code SqlObject} conversions handled by
   * {@link Converter#CUSTOM_COLUMN_TO_OBJ}.
   */
  public ConstraintClause addCustomColumns(Object... columnStrs) {
    _columns.addObjects(Converter.CUSTOM_COLUMN_TO_OBJ, columnStrs);
    return this;
  }

  /**
   * Sets the check time for this constraint.
   *
   * @see CheckTime
   */
  public ConstraintClause setCheckTime(Object checkTime) {
    _checkTime = checkTime;
    return this;
  }

  @Override
  protected void collectSchemaObjects(ValidationContext vContext) {
    if(_name != null) {
      _name.collectSchemaObjects(vContext);
    }
    _columns.collectSchemaObjects(vContext);
  }

  @Override
  public void appendTo(AppendableExt app) throws IOException {
    preAppendTo(app);
    postAppendTo(app);
  }

  protected void preAppendTo(AppendableExt app) throws IOException {
    if(_name != null) {
      app.append(_name);
    }
    boolean forTable = SqlContext.getContext(app).getUseTableConstraints();
    app.append(_type.toString(forTable));
    if(forTable && !_columns.isEmpty()) {
      app.append(" (").append(_columns).append(")");
    }
  }

  protected void postAppendTo(AppendableExt app) throws IOException {
    if(_checkTime != null) {
      app.append(" ").append(_checkTime);
    }
  }


  /**
   * Returns the appropriate {@link Type} for the given
   * {@link Constraint#Type}.
   */
  private static Type getType(Constraint.Type consType) {
    return Type.valueOf(consType.name());
  }

  /**
   * Returns the appropriately configured ConstraintClause (or
   * ForeignKeyConstraintClause) for the given Constraint.
   */
  public static ConstraintClause from(Constraint cons) {
    switch(cons.getType()) {
    case FOREIGN_KEY:
      return new ForeignKeyConstraintClause((ForeignKeyConstraint)cons);
    case CHECK:
      return new CheckConstraintClause((CheckConstraint)cons);
    default:
      return new ConstraintClause(cons);
    }
  }

  /**
   * Convenience method for generating an unnamed not null constraint.
   */
  public static ConstraintClause notNull() {
    return notNull(null);
  }

  /**
   * Convenience method for generating a not null constraint with the given
   * name.
   * @param name name of the constraint, may be {@code null}
   */
  public static ConstraintClause notNull(Object name) {
    return new ConstraintClause(Type.NOT_NULL, name);
  }

  /**
   * Convenience method for generating an unnamed unique constraint.
   */
  public static ConstraintClause unique() {
    return unique(null);
  }

  /**
   * Convenience method for generating a unique constraint with the given
   * name.
   * @param name name of the constraint, may be {@code null}
   */
  public static ConstraintClause unique(Object name) {
    return new ConstraintClause(Type.UNIQUE, name);
  }

  /**
   * Convenience method for generating an unnamed primary key constraint.
   */
  public static ConstraintClause primaryKey() {
    return primaryKey(null);
  }

  /**
   * Convenience method for generating a primary key constraint with the given
   * name.
   * @param name name of the constraint, may be {@code null}
   */
  public static ConstraintClause primaryKey(Object name) {
    return new ConstraintClause(Type.PRIMARY_KEY, name);
  }

  /**
   * Convenience method for generating an unnamed foreign key constraint.
   */
  public static ForeignKeyConstraintClause foreignKey(Object refTableStr) {
    return foreignKey(null, refTableStr);
  }

  /**
   * Convenience method for generating a foreign key constraint with the given
   * name.
   * @param name name of the constraint, may be {@code null}
   * @param refTableStr the table referenced by this constraint
   */
  public static ForeignKeyConstraintClause foreignKey(Object name,
                                                      Object refTableStr) {
    return new ForeignKeyConstraintClause(name, refTableStr);
  }

  /**
   * Convenience method for generating an unnamed check constraint.
   */
  public static CheckConstraintClause checkCondition(Condition checkCondition) {
    return checkCondition(null, checkCondition);
  }

  /**
   * Convenience method for generating a check constraint with the given name.
   * @param name name of the constraint, may be {@code null}
   * @param checkCondition the check condition
   */
  public static CheckConstraintClause checkCondition(Object name,
                                                     Condition checkCondition) {
    return new CheckConstraintClause(name, checkCondition);
  }

  /**
   * Wrapper around the constraint name which generates the appropriate
   * constraint clause prefix.
   */
  static class Prefix extends SqlObject
  {
    private SqlObject _name;

    Prefix(SqlObject name) {
      _name = name;
    }

    @Override
    protected void collectSchemaObjects(ValidationContext vContext) {
      _name.collectSchemaObjects(vContext);
    }

    @Override
    public void appendTo(AppendableExt app) throws IOException {
      app.append("CONSTRAINT ").append(_name).append(" ");
    }
  }

}
