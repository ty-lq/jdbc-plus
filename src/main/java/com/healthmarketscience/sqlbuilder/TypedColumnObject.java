/*
Copyright (c) 2008 Health Market Science, Inc.

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
import java.util.Iterator;
import java.util.List;

import com.healthmarketscience.common.util.AppendableExt;
import com.healthmarketscience.sqlbuilder.dbspec.Column;


/**
 * Outputs the name of a column, its type information and any constraints
 * <code>"&lt;column&gt; &lt;type&gt; [ &lt;constraint&gt; ... ]"</code> (for
 * CREATE statements).
 *
 * @author James Ahlborn
 */
class TypedColumnObject extends ColumnObject
{
  private String _typeName;
  private SqlObjectList<SqlObject> _constraints = SqlObjectList.create(" ");
  private SqlObject _defaultValue;

  TypedColumnObject(Column column) {
    super(column);

    _typeName = column.getTypeNameSQL();
    _constraints.addObjects(Converter.CUSTOM_TO_CONSTRAINTCLAUSE,
                            column.getConstraints());
    Object defVal = column.getDefaultValue();
    if(defVal != null) {
      _defaultValue = Converter.toValueSqlObject(defVal);
    }
  }

  /**
   * Sets the column type name
   */
  void setTypeName(String typeName) {
    _typeName = typeName;
  }

  /**
   * Adds the given object as a column constraint.
   * <p>
   * {@code Object} -&gt; {@code SqlObject} constraint conversions handled by
   * {@link Converter#toCustomConstraintClause}.
   */
  void addConstraint(Object obj) {
    _constraints.addObject(Converter.toCustomConstraintClause(obj));
  }

  /**
   * Sets the given value as the column default value.
   * <p>
   * {@code Object} -&gt; {@code SqlObject} value conversions handled by
   * {@link Converter#toValueSqlObject}.
   */
  void setDefaultValue(Object val) {
    _defaultValue = Converter.toValueSqlObject(val);
  }

  @Override
  protected void collectSchemaObjects(ValidationContext vContext) {
    super.collectSchemaObjects(vContext);
    _constraints.collectSchemaObjects(vContext);
    if(_defaultValue != null) {
      _defaultValue.collectSchemaObjects(vContext);
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public void appendTo(AppendableExt app) throws IOException {

    app.append(_column.getColumnNameSQL()).append(" ").append(_typeName);

    List<?> colQuals = _column.getTypeQualifiers();
    if(colQuals != null) {

      if(!colQuals.isEmpty()) {
        app.append("(");
        Iterator<?> iter = colQuals.iterator();
        app.append(iter.next());
        while(iter.hasNext()) {
          app.append(SqlObjectList.DEFAULT_DELIMITER).append(iter.next());
        }
        app.append(")");
      }

    } else {

      // backwards compat code
      Integer colFieldLength = _column.getTypeLength();
      if(colFieldLength != null) {
        app.append("(").append(colFieldLength).append(")");
      }
    }

    if(_defaultValue != null) {
      app.append(" DEFAULT ").append(_defaultValue);
    }

    if(!_constraints.isEmpty()) {

      SqlContext context = SqlContext.pushContext(app);
      // generate constraint clauses in their "column" format
      context.setUseTableConstraints(false);

      app.append(" ").append(_constraints);

      SqlContext.popContext(app, context);
    }

  }

}
