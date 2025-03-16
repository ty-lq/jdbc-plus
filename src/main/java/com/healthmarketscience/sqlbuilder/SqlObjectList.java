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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.healthmarketscience.common.util.AppendableExt;


/**
 * Maintains a list of SqlObjects.  Outputs each object separated by the
 * given delimiter (defaults to {@link #DEFAULT_DELIMITER}).
 * <p>
 * Note that this class is generally intended to be used internally by the
 * other SqlObjects.
 * <p>
 * The default list item separator used by this framework is a simple ",".
 * This is generally sufficient for interacting with most databases.  That
 * said, sometimes it is desirable to have the more readable separator of
 * ", ".  This can be enabled by setting the system property
 * {@value USE_SPACE_AFTER_DELIMITER_PROPERTY} to {@code true}.
 *
 * @author James Ahlborn
 */
public class SqlObjectList<ObjType extends SqlObject> extends SqlObject
  implements Iterable<ObjType>
{
  public static final String USE_SPACE_AFTER_DELIMITER_PROPERTY =
    "com.healthmarketscience.sqlbuilder.useSpaceAfterDelimiter";

  /** the default delimiter used by a SqlObjectList */
  public static final String DEFAULT_DELIMITER =
    (Boolean.getBoolean(USE_SPACE_AFTER_DELIMITER_PROPERTY) ? ", " : ",");

  private final String _delimiter;
  private final List<ObjType> _objects;

  public SqlObjectList() {
    this(DEFAULT_DELIMITER, new ArrayList<ObjType>(4));
  }

  public SqlObjectList(String delimiter) {
    this(delimiter, new ArrayList<ObjType>(4));
  }

  public SqlObjectList(String delimiter, List<ObjType> objects) {
    _delimiter = delimiter;
    _objects = objects;
  }

  /**
   * Constructs and returns a new SqlObjectList, conveniently allows
   * construction without respecifying generic param type.
   * @return a new SqlObjectList with the default delimiter
   */
  public static <ObjType extends SqlObject> SqlObjectList<ObjType> create() {
    return new SqlObjectList<ObjType>();
  }

  /**
   * Constructs and returns a new SqlObjectList, conveniently allows
   * construction without respecifying generic param type.
   * @param delimiter to use when appending the list
   * @return a new SqlObjectList with the given delimiter
   */
  public static <ObjType extends SqlObject> SqlObjectList<ObjType> create(
      String delimiter) {
    return new SqlObjectList<ObjType>(delimiter);
  }

  public String getDelimiter() {
    return _delimiter;
  }

  /**
   * @return the number of objects in the list
   */
  public int size() { return _objects.size(); }

  /**
   * @return {@code true} if there are no objects in the list, {@code false}
   *         otherwise.
   */
  public boolean isEmpty() { return _objects.isEmpty(); }

  /**
   * Removes all objects from the list.
   */
  public void clear() { _objects.clear(); }

  /**
   * Returns the object at the specified index.
   */
  public ObjType get(int index) { return _objects.get(index); }

  /**
   * @return a mutable Iterator over the objects in the list
   */
  @Override
  public Iterator<ObjType> iterator() { return _objects.iterator(); }

  /**
   * @return a mutable ListIterator over the objects in the list
   */
  public ListIterator<ObjType> listIterator() {
    return _objects.listIterator();
  }

  /**
   * Adds the given object to the list
   * @param obj the object to be added
   */
  public SqlObjectList<ObjType> addObject(ObjType obj) {
    _objects.add(obj);
    return this;
  }

  /**
   * Adds the given objects to the list
   * @param objs the objects to be added, no-op if {@code null}
   */
  @SuppressWarnings("unchecked")
  public SqlObjectList<ObjType> addObjects(ObjType... objs) {
    if(objs == null) {
      return this;
    }
    for(ObjType obj : objs) {
      _objects.add(obj);
    }
    return this;
  }

  /**
   * Adds the given objects to the list
   * @param objs the objects to be added, no-op if {@code null}
   */
  public SqlObjectList<ObjType> addObjects(Iterable<? extends ObjType> objs) {
    if(objs == null) {
      return this;
    }
    for(ObjType obj : objs) {
      _objects.add(obj);
    }
    return this;
  }

  /**
   * Adds the given objects to the list after converting each of them using
   * the given converter.
   * @param converter Converter which generates the actual objects to be added
   *                  from the given objects
   * @param objs the objects to be added, no-op if {@code null}
   */
  @SuppressWarnings("unchecked")
  public <SrcType, DstType extends ObjType> SqlObjectList<ObjType> addObjects(
      Converter<SrcType, DstType> converter, SrcType... objs)
  {
    if(objs == null) {
      return this;
    }
    for(SrcType obj : objs) {
      _objects.add(converter.convert(obj));
    }
    return this;
  }

  /**
   * Adds the given objects to the list after converting each of them using
   * the given converter.
   * @param converter Converter which generates the actual objects to be added
   *                  from the given objects
   * @param objs the objects to be added, no-op if {@code null}
   */
  public <SrcType, DstType extends ObjType> SqlObjectList<ObjType> addObjects(
      Converter<SrcType, DstType> converter,
      Iterable<? extends SrcType> objs)
  {
    if(objs == null) {
      return this;
    }
    for(SrcType obj : objs) {
      _objects.add(converter.convert(obj));
    }
    return this;
  }

  @Override
  protected void collectSchemaObjects(ValidationContext vContext) {
    for(ObjType obj : _objects) {
      obj.collectSchemaObjects(vContext);
    }
  }

  @Override
  public void appendTo(AppendableExt app) throws IOException
  {
    app.append(this, _delimiter);
  }
}
