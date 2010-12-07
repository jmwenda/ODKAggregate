/*
 * Copyright (C) 2010 University of Washington
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
package org.opendatakit.aggregate.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opendatakit.aggregate.ContextFactory;
import org.opendatakit.aggregate.constants.BeanDefs;
import org.opendatakit.aggregate.datamodel.FormDataModel;
import org.opendatakit.aggregate.datamodel.FormElementModel;
import org.opendatakit.aggregate.exception.ODKConversionException;
import org.opendatakit.aggregate.exception.ODKFormAlreadyExistsException;
import org.opendatakit.aggregate.parser.MultiPartFormItem;
import org.opendatakit.aggregate.submission.Submission;
import org.opendatakit.aggregate.submission.SubmissionSet;
import org.opendatakit.aggregate.submission.SubmissionField.BlobSubmissionOutcome;
import org.opendatakit.aggregate.submission.type.BlobSubmissionType;
import org.opendatakit.aggregate.submission.type.BooleanSubmissionType;
import org.opendatakit.aggregate.submission.type.LongSubmissionType;
import org.opendatakit.aggregate.submission.type.RepeatSubmissionType;
import org.opendatakit.aggregate.submission.type.StringSubmissionType;
import org.opendatakit.common.persistence.CommonFieldsBase;
import org.opendatakit.common.persistence.Datastore;
import org.opendatakit.common.persistence.DynamicCommonFieldsBase;
import org.opendatakit.common.persistence.TopLevelDynamicBase;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.persistence.exception.ODKEntityNotFoundException;
import org.opendatakit.common.security.User;
import org.opendatakit.common.security.UserService;

/**
 * Defines the aggregate.opendatakit.org:FormInfo form which is used to hold all the 
 * form definitions in Aggregate.
 *  
 * @author mitchellsundt@gmail.com
 * @author wbrunette@gmail.com
 * 
 */
public class FormInfo {
	private static FormDefinition formDefinition = null;
	private static Form formInfoForm = null;
	
	private static FormInfoTable reference = null;
	
	public static final XFormParameters formInfoXFormParameters =
		new XFormParameters(FormDataModel.URI_FORM_ID_VALUE_FORM_INFO, 1L, 0L);
	/*
	 * Following public fields are valid after the first 
	 * successful call to getFormDefinition()
	 */
	
	// FormInfoTable element...
	static FormElementModel formId = null;
	// FormInfoDescriptionTable element...
	static FormElementModel fiDescriptionTable = null;
	static FormElementModel languageCode = null;
	static FormElementModel formName = null;
	static FormElementModel description = null;
	static FormElementModel descriptionUrl = null;
	// FormInfoFilesetTable element...
	static FormElementModel fiFilesetTable = null;
	static FormElementModel rootElementModelVersion = null;
	static FormElementModel rootElementUiVersion = null;
	static FormElementModel isFilesetComplete = null;
	static FormElementModel isDownloadAllowed = null;
	static FormElementModel xformDefinition = null;
	static FormElementModel manifestFileset = null;
	// FormInfoSubmissionTable element...
	static FormElementModel fiSubmissionTable = null;
	static FormElementModel submissionFormId = null;
	static FormElementModel submissionModelVersion = null;
	static FormElementModel submissionUiVersion = null;
	
	/**
	 * Return the form definition for the FormInfo submissions.  These are the 
	 * submissions that hold the form definitions.  As a side-efect, all the 
	 * public static variables of this class are initialized to non-null values.
	 * 
	 * @param datastore
	 * @return
	 * @throws ODKDatastoreException 
	 */
	static FormDefinition getFormDefinition(Datastore datastore) throws ODKDatastoreException {
		if ( formDefinition == null ) {
			// The FormDefn list of registered forms is itself a well-known
			// form within the Aggregate instance.  Load the form definition
			// for that well-known form id.
	        UserService userService = (UserService) ContextFactory.get().getBean(
	                BeanDefs.USER_BEAN);
            User user = userService.getDaemonAccountUser();
		    formDefinition = FormDefinition.getFormDefinition(formInfoXFormParameters, datastore, user);

		    String formInfoUri = CommonFieldsBase.newMD5HashUri(formInfoXFormParameters.formId);
		    
		    if ( formDefinition == null ) {
				// doesn't have the form definition tables defined ...
				// need to insert definition records into the fdm table...
				final FormDataModel fdm = FormDataModel.createRelation(datastore, user);
				
				List<FormDataModel> idDefn = new ArrayList<FormDataModel>();
				FormInfoTable.createFormDataModel(idDefn, datastore, user);
				FormDefinition.assertModel(formInfoXFormParameters, idDefn, datastore, user);
				
			    formDefinition = FormDefinition.getFormDefinition(formInfoXFormParameters, datastore, user);
				
				if ( formDefinition == null ) {
					throw new IllegalStateException("Unable to create form definition definition!");
				}
				
			}
				
			// and update the FormInfo object so it has the canonical data fields...
			reference = (FormInfoTable) formDefinition.getTopLevelGroup().getBackingObjectPrototype();
			
			formId = FormDefinition.findElement(formDefinition.getTopLevelGroupElement(), FormInfoTable.createRelation(datastore, user).formId);
			// FormInfoDescriptionTable element...
			{
				fiDescriptionTable = formDefinition.getTopLevelGroupElement().findElementByName(FormInfoDescriptionTable.TABLE_NAME);
				FormInfoDescriptionTable f = FormInfoDescriptionTable.createRelation(datastore, user);
				languageCode = FormDefinition.findElement(fiDescriptionTable, f.languageCode);
				formName = FormDefinition.findElement(fiDescriptionTable, f.formName);
				description = FormDefinition.findElement(fiDescriptionTable, f.description);
				descriptionUrl = FormDefinition.findElement(fiDescriptionTable, f.descriptionUrl);
			}
			// FormInfoFilesetTable element...
			{
				fiFilesetTable = formDefinition.getTopLevelGroupElement().findElementByName(FormInfoFilesetTable.TABLE_NAME);
				FormInfoFilesetTable f = FormInfoFilesetTable.createRelation(datastore, user);
				rootElementModelVersion = FormDefinition.findElement(fiFilesetTable, f.rootElementModelVersion);
				rootElementUiVersion = FormDefinition.findElement(fiFilesetTable, f.rootElementUiVersion);
				isFilesetComplete = FormDefinition.findElement(fiFilesetTable, f.isFilesetComplete);
				isDownloadAllowed = FormDefinition.findElement(fiFilesetTable, f.isDownloadAllowed);
				xformDefinition = fiFilesetTable.findElementByName(FormInfoFilesetTable.ELEMENT_NAME_XFORM_DEFINITION);
				manifestFileset = fiFilesetTable.findElementByName(FormInfoFilesetTable.ELEMENT_NAME_MANIFEST_FILESET);
			}
			// FormInfoSubmissionTable element...
			{
				fiSubmissionTable = formDefinition.getTopLevelGroupElement().findElementByName(FormInfoSubmissionTable.TABLE_NAME);
				FormInfoSubmissionTable f = FormInfoSubmissionTable.createRelation(datastore, user);
				submissionFormId = FormDefinition.findElement(fiSubmissionTable, f.submissionFormId);
				submissionModelVersion = FormDefinition.findElement(fiSubmissionTable, f.submissionModelVersion);
				submissionUiVersion = FormDefinition.findElement(fiSubmissionTable, f.submissionUiVersion);
			}

			// Now create a record in the FormInfo table for the FormInfo table itself...
			TopLevelDynamicBase fi = null;
			try {
				fi = datastore.getEntity(reference, formInfoUri, user);
			} catch ( ODKEntityNotFoundException e ) {
				// we must have failed before persisting a FormInfo record
				// or this must be our first time through...
				Submission formInfo = new Submission(formInfoXFormParameters.modelVersion, formInfoXFormParameters.uiVersion,
													 formInfoUri, formDefinition, datastore, user);
				((StringSubmissionType) formInfo.getElementValue(formId)).setValueFromString(formInfoXFormParameters.formId);
				// default description...
				{
					RepeatSubmissionType r = (RepeatSubmissionType) formInfo.getElementValue(fiDescriptionTable);
					SubmissionSet sDescription = new SubmissionSet(formInfo, 1L, fiDescriptionTable, formDefinition, formInfo.getKey(), datastore, user);
					((StringSubmissionType) sDescription.getElementValue(formName)).setValueFromString("Form Information");
					((StringSubmissionType) sDescription.getElementValue(description)).setValueFromString("Form information table used by Aggregate to track all forms uploaded to this server.");
					r.addSubmissionSet(sDescription);
				}
				// fileset...
				{
					RepeatSubmissionType r = (RepeatSubmissionType) formInfo.getElementValue(fiFilesetTable);
					SubmissionSet sFileset = new SubmissionSet(formInfo, 1L, fiFilesetTable, formDefinition, formInfo.getKey(), datastore, user);
					((LongSubmissionType) sFileset.getElementValue(rootElementModelVersion)).setValueFromString(formInfoXFormParameters.modelVersion.toString());
					((LongSubmissionType) sFileset.getElementValue(rootElementUiVersion)).setValueFromString(formInfoXFormParameters.uiVersion.toString());
					((BooleanSubmissionType) sFileset.getElementValue(isFilesetComplete)).setValueFromString("yes");
					((BooleanSubmissionType) sFileset.getElementValue(isDownloadAllowed)).setValueFromString("yes");
					r.addSubmissionSet(sFileset);
				}
				// submission...
				{
					RepeatSubmissionType r = (RepeatSubmissionType) formInfo.getElementValue(fiSubmissionTable);
					SubmissionSet sSubmission = new SubmissionSet(formInfo, 1L, fiSubmissionTable, formDefinition, formInfo.getKey(), datastore, user);
					((StringSubmissionType) sSubmission.getElementValue(submissionFormId)).setValueFromString(formInfoXFormParameters.formId);
					((LongSubmissionType) sSubmission.getElementValue(submissionModelVersion)).setValueFromString(formInfoXFormParameters.modelVersion.toString());
					((LongSubmissionType) sSubmission.getElementValue(submissionUiVersion)).setValueFromString(formInfoXFormParameters.uiVersion.toString());
					r.addSubmissionSet(sSubmission);
				}
				formInfo.persist(datastore, user);
				
				fi = datastore.getEntity(reference, formInfoUri, user);
			}
			
			// and retrieve cleanly... 
		    Submission formInfo = new Submission(fi, formDefinition, datastore, user);
			formInfoForm = new Form(formInfo, datastore, user);

			try {
				// and we can create other well-known forms here 
				PersistentResults.createForm(datastore,user);
			} catch ( ODKDatastoreException e ) {
				// we have an error...
				formDefinition = null;
				formInfoForm = null;
				throw e;
			}
		}
		return formDefinition;
	}
	
	static final Form getFormInfoForm(Datastore datastore) throws ODKDatastoreException {
		getFormDefinition(datastore);
		return formInfoForm;
	}

	public static final void populateBackingTableMap(Map<String, DynamicCommonFieldsBase> backingTableMap) {
		try {
		    UserService userService = (UserService) ContextFactory.get().getBean(
		    		BeanDefs.USER_BEAN);
		    User user = userService.getDaemonAccountUser();
		    Datastore datastore = (Datastore) ContextFactory.get().getBean(BeanDefs.DATASTORE_BEAN);

		    DynamicCommonFieldsBase b;
			b = FormInfoTable.createRelation(datastore, user);
			backingTableMap.put(b.getSchemaName() + "." + b.getTableName(), b);
			b = FormInfoDescriptionTable.createRelation(datastore, user);
			backingTableMap.put(b.getSchemaName() + "." + b.getTableName(), b);
			b = FormInfoFilesetTable.createRelation(datastore, user);
			backingTableMap.put(b.getSchemaName() + "." + b.getTableName(), b);
			b = FormInfoSubmissionTable.createRelation(datastore, user);
			backingTableMap.put(b.getSchemaName() + "." + b.getTableName(), b);
		} catch (ODKDatastoreException e) {
			throw new IllegalStateException("the relations should already have been created");
		}
	}
	
	public static final void setFormDescription( Submission aFormDefinition, String languageCodeValue, String formNameValue, String descriptionValue, String descriptionUrlValue, Datastore datastore, User user ) throws ODKDatastoreException {
	    RepeatSubmissionType r = (RepeatSubmissionType) aFormDefinition.getElementValue(FormInfo.fiDescriptionTable);
	    List<SubmissionSet> descriptions = r.getSubmissionSets();
	    SubmissionSet matchingSet = null;
	    for ( SubmissionSet f : descriptions ) {
	    	String languageCodeString = ((StringSubmissionType) f.getElementValue(languageCode)).getValue();
	    	if (languageCodeString == null ) {
	    		if ( languageCodeValue == null ) {
	    			matchingSet = f;
	    			break;
	    		}
	    	} else if ( languageCodeValue != null && languageCodeString.equals(languageCodeValue)) {
	    		matchingSet = f;
	    		break;
	    	}
	    }
	    
	    if ( matchingSet == null ) {
	    	// create a matching set...
			SubmissionSet sDescription = new SubmissionSet(aFormDefinition, descriptions.size()+1L, fiDescriptionTable, formDefinition, aFormDefinition.getKey(), datastore, user);
			((StringSubmissionType) sDescription.getElementValue(languageCode)).setValueFromString(languageCodeValue);
			((StringSubmissionType) sDescription.getElementValue(formName)).setValueFromString(formNameValue);
			((StringSubmissionType) sDescription.getElementValue(description)).setValueFromString(descriptionValue);
			((StringSubmissionType) sDescription.getElementValue(descriptionUrl)).setValueFromString(descriptionUrlValue);
			r.addSubmissionSet(sDescription);
			matchingSet = sDescription;
	    } else {
	    	// update name, description, etc.
			((StringSubmissionType) matchingSet.getElementValue(formName)).setValueFromString(formNameValue);
			((StringSubmissionType) matchingSet.getElementValue(description)).setValueFromString(descriptionValue);
			((StringSubmissionType) matchingSet.getElementValue(descriptionUrl)).setValueFromString(descriptionUrlValue);
	    }
	}
	
	/**
	 * Set the Xform definition.
	 * 
	 * @param aFormDefinition
	 * @param rootModelVersion
	 * @param rootUiVersion
	 * @param title
	 * @param definition
	 * @param datastore
	 * @param user
	 * @return true if the file is identical to the currently-stored one.
	 * @throws ODKDatastoreException
	 * @throws ODKConversionException
	 * @throws ODKFormAlreadyExistsException 
	 */
	public static final boolean setXFormDefinition( Submission aFormDefinition, Long rootModelVersion, Long rootUiVersion, String title, byte[] definition, Datastore datastore, User user ) throws ODKDatastoreException, ODKConversionException, ODKFormAlreadyExistsException {
	    RepeatSubmissionType r = (RepeatSubmissionType) aFormDefinition.getElementValue(FormInfo.fiFilesetTable);
	    List<SubmissionSet> filesets = r.getSubmissionSets();
	    SubmissionSet matchingSet = null;
	    for ( SubmissionSet f : filesets ) {
	    	Long rootModel = ((LongSubmissionType) f.getElementValue(rootElementModelVersion)).getValue();
	    	Long rootUi = ((LongSubmissionType) f.getElementValue(rootElementUiVersion)).getValue();
	    	if ((rootModelVersion == rootModel) && (rootUiVersion == rootUi)) {
	    		matchingSet = f;
	    	}
	    }
	    
	    boolean matchingFile = true;
	    if ( matchingSet == null ) {	    	
	    	// we don't support multiple file versions yet...
	    	if ( filesets.size() != 0 ) throw new ODKFormAlreadyExistsException();
	    	
	    	matchingFile = false; // nothing there yet...
	    	// create a matching set...
			SubmissionSet sFileset = new SubmissionSet(aFormDefinition, filesets.size()+1L, fiFilesetTable, formDefinition, aFormDefinition.getKey(), datastore, user);
			((LongSubmissionType) sFileset.getElementValue(rootElementModelVersion)).setValueFromString(rootModelVersion == null ? null : rootModelVersion.toString());
			((LongSubmissionType) sFileset.getElementValue(rootElementUiVersion)).setValueFromString(rootUiVersion == null ? null : rootUiVersion.toString());
			((BooleanSubmissionType) sFileset.getElementValue(isFilesetComplete)).setValueFromString("yes");
			((BooleanSubmissionType) sFileset.getElementValue(isDownloadAllowed)).setValueFromString("yes");
			r.addSubmissionSet(sFileset);
			matchingSet = sFileset;
	    }
	    
	    BlobSubmissionType bt = (BlobSubmissionType) matchingSet.getElementValue(FormInfo.xformDefinition);
	    if ( bt.getAttachmentCount() == 0 ) {
	    	return bt.setValueFromByteArray(definition, "text/xml", Long.valueOf(definition.length), title + ".xml",
	    			datastore, user) == BlobSubmissionOutcome.FILE_UNCHANGED;
	    } else {
	    	List<String> versions = bt.getBinaryVersions(1);
	    	if ( versions.size() > 1 || bt.getAttachmentCount() > 1) throw new ODKFormAlreadyExistsException();
	    	String hash = bt.getContentHash(1, versions.get(0));
	    	String thisHash = CommonFieldsBase.newMD5HashUri(definition);
	    	if (! hash.equals(thisHash) ) throw new ODKFormAlreadyExistsException();
	    	return true;
	    }
	}
	
	/**
	 * Media files are assumed to be in a directory one level deeper than the xml definition.
	 * So the filename reported on the mime item has an extra leading directory.  Strip that off.
	 * 
	 * @param aFormDefinition
	 * @param rootModelVersion
	 * @param rootUiVersion
	 * @param item
	 * @param datastore
	 * @param user
	 * @return true if the files are completely new or are identical to the currently-stored ones.
	 * @throws ODKDatastoreException
	 * @throws ODKConversionException
	 */
	public static final boolean setXFormMediaFile( Submission aFormDefinition, Long rootModelVersion, Long rootUiVersion, MultiPartFormItem item, Datastore datastore, User user ) throws ODKDatastoreException, ODKConversionException {
	    RepeatSubmissionType r = (RepeatSubmissionType) aFormDefinition.getElementValue(FormInfo.fiFilesetTable);
	    List<SubmissionSet> filesets = r.getSubmissionSets();
	    SubmissionSet matchingSet = null;
	    for ( SubmissionSet f : filesets ) {
	    	Long rootModel = ((LongSubmissionType) f.getElementValue(rootElementModelVersion)).getValue();
	    	Long rootUi = ((LongSubmissionType) f.getElementValue(rootElementUiVersion)).getValue();
	    	if ((rootModelVersion == rootModel) && (rootUiVersion == rootUi)) {
	    		matchingSet = f;
	    	}
	    }
	    
	    if ( matchingSet == null ) {
	    	// create a matching set...
			SubmissionSet sFileset = new SubmissionSet(aFormDefinition, filesets.size()+1L, fiFilesetTable, formDefinition, aFormDefinition.getKey(), datastore, user);
			((LongSubmissionType) sFileset.getElementValue(rootElementModelVersion)).setValueFromString(rootModelVersion == null ? null : rootModelVersion.toString());
			((LongSubmissionType) sFileset.getElementValue(rootElementUiVersion)).setValueFromString(rootUiVersion == null ? null : rootUiVersion.toString());
			((BooleanSubmissionType) sFileset.getElementValue(isFilesetComplete)).setValueFromString("yes");
			((BooleanSubmissionType) sFileset.getElementValue(isDownloadAllowed)).setValueFromString("yes");
			r.addSubmissionSet(sFileset);
			matchingSet = sFileset;
	    }

	    boolean matchingFiles = true;
	    BlobSubmissionType bt = (BlobSubmissionType) matchingSet.getElementValue(FormInfo.manifestFileset);
    	String filePath = item.getFilename();
    	if ( filePath.indexOf("/") != -1 ) {
    		filePath = filePath.substring(filePath.indexOf("/")+1);
    	}
		matchingFiles = matchingFiles &&
			(BlobSubmissionOutcome.NEW_FILE_VERSION != 
				bt.setValueFromByteArray(item.getStream().toByteArray(), item.getContentType(), item.getContentLength(), filePath,
		    datastore, user));
		return matchingFiles;
	}

	public static void setFormSubmission(Submission aFormDefinition, String submissionFormIdValue,
			Long modelVersionValue, Long uiVersionValue, Datastore datastore, User user) throws ODKDatastoreException {
		if ( submissionFormIdValue == null ) {
			throw new IllegalArgumentException("submission form id cannot be null");
		}
	    RepeatSubmissionType r = (RepeatSubmissionType) aFormDefinition.getElementValue(FormInfo.fiSubmissionTable);
	    List<SubmissionSet> submissionDefns = r.getSubmissionSets();

	    for ( SubmissionSet f : submissionDefns ) {
	    	String formIdStr = ((StringSubmissionType) f.getElementValue(submissionFormId)).getValue();
	    	Long subModel = ((LongSubmissionType) f.getElementValue(submissionModelVersion)).getValue();
	    	Long subUi = ((LongSubmissionType) f.getElementValue(submissionUiVersion)).getValue();
	    	if (( modelVersionValue == subModel ) && ( uiVersionValue == subUi) &&
	    		( submissionFormIdValue.equals(formIdStr)) ) {
	    		return;
	    	}
	    }
	    
    	// create a matching set...
		SubmissionSet sDescription = new SubmissionSet(aFormDefinition, submissionDefns.size()+1L, fiSubmissionTable, formDefinition, aFormDefinition.getKey(), datastore, user);
		((StringSubmissionType) sDescription.getElementValue(submissionFormId)).setValueFromString(submissionFormIdValue);
		((LongSubmissionType) sDescription.getElementValue(submissionModelVersion)).setValueFromString((modelVersionValue == null) ? null : modelVersionValue.toString());
		((LongSubmissionType) sDescription.getElementValue(submissionUiVersion)).setValueFromString((uiVersionValue == null) ? null : uiVersionValue.toString());
		r.addSubmissionSet(sDescription);
	}
}
