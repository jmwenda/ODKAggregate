/**
 * Copyright (C) 2010 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opendatakit.aggregate.datamodel;

import org.opendatakit.common.persistence.InstanceDataBase;

/**
 * For use by dynamically discovered tables.  Tables that 
 * have specific structures required by Aggregate should 
 * extend InstanceDataBase.
 * <p>
 * All instance data for an xform is stored in InstanceData tables
 * specific to that xform.
 * <p>
 * The xml tag for the xform's instance data field is used to name
 * the specific column that holds that tag's data value. 
 * <p>Databases generally have an upper limit to how many columns a
 * database table can have.  If the xform exceeds this limit, the 
 * data fields will be split across one primary table and one or more
 * phantom tables with the <code>PARENT_AURI</code> of the phantom 
 * tables pointing to the row of the primary table from which this 
 * phantom table was cleaved.  
 * <p>Each repeat group in an xform is represented as its own 
 * InstanceData table with the <code>PARENT_AURI</code> pointing to
 * the enclosing repeat group (if it is nested within one) or to the
 * top level xform table. 
 * <p>Non-repeat groups are not split into separate tables; their 
 * instance data fields are in-lined into the enclosing repeat group
 * (if it is nested within one) or into the top level xform table.
 * <p>Each selection choice data field exists as a separate
 * {@link SelectChoice} table. This is 
 * to support multiple-choice selections and to allow a single-choice 
 * selection to be easily converted into a multiple-choice selection 
 * without altering the structures of the tables.
 * <p>Each binary data field exists as a separate 
 * {@link BinaryContent}
 * table and associated 
 * {@link VersionedBinaryContentRefBlob}
 *  and {@link RefBlob}
 * table.  Together, these support retaining multiple versions of 
 * binary data for a given submission.  An example is the uploading of
 * an xform with audio, video, and image prompts.  Over time, it is 
 * expected that the base xform may change (adding translations) or 
 * that the media prompts may be updated.  We support that use case.
 * 
 * @author mitchellsundt@gmail.com
 *
 */
public final class InstanceData extends InstanceDataBase {
	
	public InstanceData(String databaseSchema, String tableName) {
		super(databaseSchema, tableName);
	}

	/**
	 * Copy constructor for use by {@link #getEmptyRow(Class)}   
	 * This does not populate any fields related to the values of this row. 
	 *
	 * @param d
	 */
	public InstanceData(InstanceData ref) {
		super(ref);
	}
}
