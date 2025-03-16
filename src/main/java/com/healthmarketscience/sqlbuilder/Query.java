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
import java.util.Collection;
import java.util.HashSet;

import com.healthmarketscience.common.util.AppendableExt;
import com.healthmarketscience.sqlbuilder.dbspec.Table;

/**
 * Base class for all query statements which adds a validation facility.
 *
 * The query classes are designed for "builder" type use, so all return
 * values are the query object itself.
 *
 * @author James Ahlborn
 */
public abstract class Query<ThisType extends Query<ThisType>>
  extends CustomizableSqlObject implements Verifiable<ThisType>
{
  protected Query() {}

  @Override
  public final ThisType validate()
    throws ValidationException
  {
    doValidate();
    return getThisType();
  }

  @Override
  public void validate(ValidationContext vContext)
    throws ValidationException
  {
    // by default, just validate that all the necessary tables exist for all
    // the referenced columns
    validateTables(vContext);
  }

  /**
   * Verifies that any columns referenced in the query have their respective
   * tables also referenced in the query.
   *
   * @param vContext handle to the current validation context
   */
  protected void validateTables(ValidationContext vContext)
    throws ValidationException
  {
    Collection<Table> allTables = vContext.getTables();
    if(vContext.getParent() != null) {
      // tables could be defined in any outer contexts, so need to track back
      allTables = new HashSet<Table>(allTables);
      ValidationContext tmpVContext = vContext;
      while((tmpVContext = tmpVContext.getParent()) != null) {
        allTables.addAll(tmpVContext.getTables());
      }
    }

    // make sure all column tables are referenced by a table (if desired)
    Collection<Table> contextTables = vContext.getColumnTables();
    if(!allTables.containsAll(contextTables)) {
      contextTables.removeAll(allTables);
      throw new ValidationException("Columns used for unreferenced tables " + contextTables);
    }
  }

  @Override
  protected void collectSchemaObjects(ValidationContext vContext) {
    // always add this query to the list of things to verify
    vContext.addVerifiable(this);
    super.collectSchemaObjects(vContext);
  }

  @Override
  public final void appendTo(AppendableExt app) throws IOException {
    prependTo(app);

    SqlContext newContext = SqlContext.pushContext(app);
    newContext.setQuery(this);
    appendTo(app, newContext);
    // note, this is not within a finally block because any exceptions from
    // appendTo are expected to be unrecoverable, and we don't want to muddy
    // the water with possible exceptions from popContext
    SqlContext.popContext(app, newContext);
  }

  /** @return the handle to this object as the subclass type */
  @SuppressWarnings("unchecked")
  protected final ThisType getThisType() {
    return (ThisType)this;
  }

  /**
   * Called by {@link #appendTo(AppendableExt)} before
   * {@link #appendTo(AppendableExt,SqlContext)} within the original
   * SqlContext.
   */
  protected void prependTo(AppendableExt app) throws IOException {
    // base does nothing
  }

  /**
   * Appends the sql query to the given AppendableExt within the given,
   * modifiable SqlContext.  This method is invoked by the
   * {@link #appendTo(AppendableExt)} method within the context of calls to
   * {@link SqlContext#pushContext} and {@link SqlContext#popContext}, so
   * the implementation is free to modify the given SqlContext.
   * @param app the target for the sql query generation
   * @param newContext modifiable SqlContext for nested Appendees
   */
  protected abstract void appendTo(AppendableExt app, SqlContext newContext)
    throws IOException;


}
