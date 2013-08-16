/*
 * Copyright (C) 2012-2013 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.opendatakit.aggregate.odktables.relation;

import java.util.ArrayList;
import java.util.List;

import org.opendatakit.aggregate.odktables.rest.entity.TableDefinition;
import org.opendatakit.common.ermodel.simple.Entity;
import org.opendatakit.common.ermodel.simple.Query;
import org.opendatakit.common.ermodel.simple.Relation;
import org.opendatakit.common.persistence.DataField;
import org.opendatakit.common.persistence.DataField.DataType;
import org.opendatakit.common.persistence.DataField.IndexType;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.web.CallingContext;

/**
 * Represents the TableDefinitions table in the datastore. Analogous to the
 * eponymous Table in the ODK Tables data model.
 * <p>
 * NB: This is NOT directly analogous to the {@link TableDefinition} object,
 * which represents the XML document defining a table by which ODKTables
 * talks to the server.
 * @author sudar.sam@gmail.com
 *
 */
public class DbTableDefinitions {

  // Column names. Based on the ODK Tables Schema google doc for the
  // non client-local columns.
  public static final String TABLE_ID =  "TABLE_ID";
  public static final String TABLE_KEY = "TABLE_KEY";
  public static final String DB_TABLE_NAME = "DB_TABLE_NAME";
  public static final String TYPE = "TYPE";
  public static final String TABLE_ID_ACCESS_CONTROLS =
      "TABLE_ID_ACCESS_CONTROLS";

  // The name of the table/relation in the datastore.
  private static final String RELATION_NAME = "TABLE_DEFINITIONS";

  private static final List<DataField> dataFields;
  static {
    // Initialize the fields/columns in this table. Using the types based on
    // the ODK Tables Schema google document.
    dataFields = new ArrayList<DataField>();
    dataFields.add(new DataField(TABLE_ID, DataType.STRING, false)
      .setIndexable(IndexType.HASH));
    dataFields.add(new DataField(TABLE_KEY, DataType.STRING, false));
    dataFields.add(new DataField(DB_TABLE_NAME, DataType.STRING, false));
    dataFields.add(new DataField(TYPE, DataType.STRING, false));
    dataFields.add(
        new DataField(TABLE_ID_ACCESS_CONTROLS, DataType.STRING, true));
  }

  private static Relation theRelation = null;

  public static synchronized Relation getRelation(CallingContext cc)
      throws ODKDatastoreException {
    if ( theRelation == null) {
      Relation relation =
        new Relation(RUtil.NAMESPACE, RELATION_NAME, dataFields, cc);
      theRelation = relation;
    }
    return theRelation;
  }

  public static Entity getDefinition(String tableId, CallingContext cc)
      throws ODKDatastoreException {
    Query query =
        getRelation(cc).query("DbTableDefinitions.getDefinition()", cc);
    query.equal(TABLE_ID, tableId);
    Entity definition = query.get();
    return definition;
  }
}
