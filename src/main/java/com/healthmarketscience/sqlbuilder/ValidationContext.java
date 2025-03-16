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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import com.healthmarketscience.sqlbuilder.dbspec.Column;
import com.healthmarketscience.sqlbuilder.dbspec.Table;

/**
 * Object used to accummulate state during query validation.
 *
 * @author james
 */
public class ValidationContext {

  public static final boolean DEFAULT_LOCAL_ONLY = false;

  private final ValidationContext _parent;

  private Collection<Column> _columns;
  private Collection<Table> _tables;
  /** whether or not collection/validation should proceed into nested
      subqueries */
  private boolean _localOnly;
  private Collection<Map.Entry<ValidationContext,? extends Verifiable<?>>> _verifiables;

  public ValidationContext() {
    this(null, null, null, DEFAULT_LOCAL_ONLY);
  }

  public ValidationContext(ValidationContext parent) {
    this(parent, null, null, DEFAULT_LOCAL_ONLY);
  }

  public ValidationContext(boolean localOnly) {
    this(null, null, null, localOnly);
  }

  public ValidationContext(Collection<Table> tables,
                           Collection<Column> columns) {
    this(null, tables, columns, DEFAULT_LOCAL_ONLY);
  }

  public ValidationContext(ValidationContext parent,
                           Collection<Table> tables,
                           Collection<Column> columns,
                           boolean localOnly) {
    _parent = parent;
    _tables = ((tables != null) ? tables : new HashSet<Table>());
    _columns = ((columns != null) ? columns : new HashSet<Column>());
    _localOnly = localOnly;
    _verifiables = ((_parent != null) ? _parent._verifiables :
                    new ArrayList<Map.Entry<ValidationContext,? extends Verifiable<?>>>(2));
  }

  public ValidationContext getParent() {
    return _parent;
  }

  public Collection<Table> getTables() {
    return _tables;
  }

  public void setTables(Collection<Table> newTables) {
    _tables = newTables;
  }

  public void addTable(Table table) {
    _tables.add(table);
  }

  public Collection<Column> getColumns() {
    return _columns;
  }

  public void setColumns(Collection<Column> newColumns) {
    _columns = newColumns;
  }

  public void addColumn(Column column) {
    _columns.add(column);
  }

  public boolean isLocalOnly() {
    return _localOnly;
  }

  public void setLocalOnly(boolean newLocalOnly) {
    _localOnly = newLocalOnly;
  }

  public void addVerifiable(Verifiable<?> verifiable)
  {
    if(verifiable == null) {
      throw new IllegalArgumentException("verifiable was null");
    }
    _verifiables.add(new AbstractMap.SimpleImmutableEntry<>(this, verifiable));
  }

  public void validateAll() throws ValidationException {
    for(Map.Entry<ValidationContext,? extends Verifiable<?>> verifiable : _verifiables) {
      try {
        verifiable.getValue().validate(verifiable.getKey());
      } catch(ValidationException e) {
        e.setFailedVerifiable(verifiable);
        throw e;
      }
    }
  }

  /**
   * Handles schema object collection for nested queries.
   */
  public void collectNestedQuerySchemaObjects(SqlObject nestedQuery) {
    // we do not collect into the subquery if this a "local only" collection
    if((nestedQuery != null) && !isLocalOnly()) {
      // subqueries need a nested validation context because their schema
      // objects *do not* affect the outer query, but the outer query's
      // schema objects *do* affect their query
      nestedQuery.collectSchemaObjects(new ValidationContext(this));
    }
  }

  /**
   * Retrieves the tables referenced by the column objects.
   *
   * @return a new columnTables collection
   */
  protected Collection<Table> getColumnTables()
  {
    return getColumnTables(null);
  }

  /**
   * Retrieves the tables referenced by the column objects.
   *
   * @param columnTables (out) all tables referenced by the given columns
   * @return the given columnTables collection
   */
  protected Collection<Table> getColumnTables(Collection<Table> columnTables)
  {
    if(columnTables == null) {
      columnTables = new HashSet<Table>();
    }
    // get the tables from the columns referenced
    for(Column column : _columns) {
      columnTables.add(column.getTable());
    }
    return columnTables;
  }

}
