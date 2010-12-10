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
package org.opendatakit.aggregate.task.gae.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opendatakit.aggregate.ContextFactory;
import org.opendatakit.aggregate.constants.BeanDefs;
import org.opendatakit.aggregate.constants.ServletConsts;
import org.opendatakit.aggregate.datamodel.FormElementKey;
import org.opendatakit.aggregate.datamodel.FormElementModel;
import org.opendatakit.aggregate.exception.ODKFormNotFoundException;
import org.opendatakit.aggregate.form.Form;
import org.opendatakit.aggregate.servlet.KmlServlet;
import org.opendatakit.aggregate.servlet.KmlSettingsServlet;
import org.opendatakit.aggregate.servlet.ServletUtilBase;
import org.opendatakit.aggregate.submission.SubmissionKey;
import org.opendatakit.aggregate.task.KmlWorkerImpl;
import org.opendatakit.common.persistence.Datastore;
import org.opendatakit.common.security.User;
import org.opendatakit.common.security.UserService;

/**
 * 
 * @author wbrunette@gmail.com
 * @author mitchellsundt@gmail.com
 * 
 */
public class KmlGeneratorTaskServlet extends ServletUtilBase {
  /**
   * Serial number for serialization
   */
  private static final long serialVersionUID = 8647919526257827291L;

  /**
   * URI from base
   */
  public static final String ADDR = "kmlGeneratorTask";

  /**
   * Handler for HTTP Get request to create xform upload page
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    // TODO: talk to MITCH about the fact the user will be incorrect
    UserService userService = (UserService) ContextFactory.get().getBean(BeanDefs.USER_BEAN);
    User user = userService.getCurrentUser();

    // get parameter
    String formId = getParameter(req, ServletConsts.FORM_ID);

    String geopointFieldName = getParameter(req, KmlServlet.GEOPOINT_FIELD);
    String titleFieldName = getParameter(req, KmlServlet.TITLE_FIELD);
    String imageFieldName = getParameter(req, KmlServlet.IMAGE_FIELD);
    String persistentResultsString = getParameter(req, ServletConsts.PERSISTENT_RESULTS_KEY);
    if ( persistentResultsString == null ) {
    	errorBadParam(resp);
    	return;
    }
    SubmissionKey persistentResultsKey = new SubmissionKey(persistentResultsString);
    String attemptCountString = getParameter(req, ServletConsts.ATTEMPT_COUNT);
    if ( attemptCountString == null ) {
    	errorBadParam(resp);
    	return;
    }
    Long attemptCount = Long.valueOf(attemptCountString);
    
    Datastore ds = (Datastore) ContextFactory.get().getBean(BeanDefs.DATASTORE_BEAN);

    Form form = null;
    FormElementModel titleField = null;
    FormElementModel geopointField = null;
    FormElementModel imageField = null;
    try {
      form = Form.retrieveForm(formId, ds, user);

      if (titleFieldName != null) {
        FormElementKey titleKey = new FormElementKey(titleFieldName);
        titleField = FormElementModel.retrieveFormElementModel(form, titleKey);
      }
      if (geopointFieldName != null) {
        FormElementKey geopointKey = new FormElementKey(geopointFieldName);
        geopointField = FormElementModel.retrieveFormElementModel(form, geopointKey);
      }
      if (imageFieldName != null) {
        if (!imageFieldName.equals(KmlSettingsServlet.NONE)) {
          FormElementKey imageKey = new FormElementKey(imageFieldName);
          imageField = FormElementModel.retrieveFormElementModel(form, imageKey);
        }
      }

    } catch (ODKFormNotFoundException e) {
        odkIdNotFoundError(resp);
        return;
    }

    KmlWorkerImpl worker = new KmlWorkerImpl(form, persistentResultsKey, attemptCount, titleField, geopointField, imageField, getServerURL(req), ds, user);
	worker.generateKml();
  }
}