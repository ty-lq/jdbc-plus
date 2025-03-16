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

import com.healthmarketscience.common.util.AppendableExt;


/**
 * An object representing a value expression.
 *
 * @author James Ahlborn
 */
public abstract class Expression extends NestableClause
{
  /** an Expression object which will always return <code>true</code> for
      {@link Expression#isEmpty}.  useful for selectively including expression
      blocks */
  public static final Expression EMPTY = new Expression() {
      @Override
      public boolean isEmpty() { return true; }
      @Override
      protected void collectSchemaObjects(ValidationContext vContext) {}
      @Override
      public void appendTo(AppendableExt app) throws IOException {
        throw new UnsupportedOperationException("Should never be called");
      }
    };


  protected Expression() {}

  @Override
  public Expression setDisableParens(boolean disableParens) {
    super.setDisableParens(disableParens);
    return this;
  }
}
