/*
 * Copyright (C) 2009 Google Inc. 
 * Copyright (C) 2010 University of Washington.
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

package org.opendatakit.aggregate.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.opendatakit.aggregate.ContextFactory;
import org.opendatakit.aggregate.constants.BeanDefs;
import org.opendatakit.aggregate.constants.ErrorConsts;
import org.opendatakit.aggregate.constants.HtmlUtil;
import org.opendatakit.aggregate.constants.ServletConsts;
import org.opendatakit.aggregate.constants.common.UIConsts;
import org.opendatakit.aggregate.exception.ODKConversionException;
import org.opendatakit.aggregate.exception.ODKExternalServiceException;
import org.opendatakit.aggregate.exception.ODKFormNotFoundException;
import org.opendatakit.aggregate.exception.ODKFormSubmissionsDisabledException;
import org.opendatakit.aggregate.exception.ODKIncompleteSubmissionData;
import org.opendatakit.aggregate.exception.ODKParseException;
import org.opendatakit.aggregate.externalservice.ExternalService;
import org.opendatakit.aggregate.externalservice.FormServiceCursor;
import org.opendatakit.aggregate.form.Form;
import org.opendatakit.aggregate.parser.MultiPartFormData;
import org.opendatakit.aggregate.parser.SubmissionParser;
import org.opendatakit.aggregate.submission.Submission;
import org.opendatakit.aggregate.task.UploadSubmissions;
import org.opendatakit.common.constants.BasicConsts;
import org.opendatakit.common.constants.HtmlConsts;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.persistence.exception.ODKEntityPersistException;
import org.opendatakit.common.web.CallingContext;

/**
 * Servlet to process a submission from a form
 * 
 * @author wbrunette@gmail.com
 * @author mitchellsundt@gmail.com
 * 
 */
public class SubmissionServlet extends ServletUtilBase {

	private static final Logger logger = Logger.getLogger(SubmissionServlet.class.getName());
  /**
   * Serial number for serialization
   */
  private static final long serialVersionUID = -9115712148453543651L;

  /**
   * URI from base
   */
  public static final String ADDR = UIConsts.SUBMISSION_SERVLET_ADDR;

  /**
   * Title for generated webpage
   */
  private static final String TITLE = "Submission Upload";

  /**
   * Script path to include...
   */
  private static final String UPLOAD_SCRIPT_RESOURCE = "javascript/upload_control.js";

  private static final String UPLOAD_PAGE_BODY_START = 

"<div style=\"overflow: auto;\">" +
"<p><b>Upload one submission into ODK Aggregate</b></p>" +
"<!--[if true]><p style=\"color: red;\">For a better user experience, use Chrome, Firefox or Safari</p>" +
"<![endif] -->" +
"<form id=\"ie_backward_compatible_form\"" + 
"                        accept-charset=\"UTF-8\" method=\"POST\" encoding=\"multipart/form-data\" enctype=\"multipart/form-data\"" + 
"                        action=\"";// emit the ADDR
  private static final String UPLOAD_PAGE_BODY_MIDDLE = "\">" +
"    <table>" +
"     <tr>" +
"        <td><label for=\"xml_submission_file\">Submission data file:</label></td>" +
"        <td><input id=\"xml_submission_file\" type=\"file\" size=\"80\"" +
"           name=\"xml_submission_file\" /></td>" +
"     </tr>" +
"     <tr>" +
"        <td><label for=\"mediaFiles\">Associated data file(s):</label></td>" +
"        <td><input id=\"mediaFiles\" type=\"file\" size=\"80,20\" name=\"datafile\" multiple /><input id=\"clear_media_files\" type=\"button\" value=\"Clear\" onClick=\"clearMediaInputField('mediaFiles')\" /></td>" +
"     </tr>" +
"     <!--[if true]>" +
"        <tr>" +
"            <td><label for=\"mediaFiles2\">Associated data file #2:</label></td>" +
"            <td><input id=\"mediaFiles2\" type=\"file\" size=\"80\" name=\"datafile\" /><input id=\"clear_media_files2\" type=\"button\" value=\"Clear\" onClick=\"clearMediaInputField('mediaFiles2')\" /></td>" +
"        </tr>" +
"        <tr>" +
"            <td><label for=\"mediaFiles3\">Associated data file #3:</label></td>" +
"            <td><input id=\"mediaFiles3\" type=\"file\" size=\"80\" name=\"datafile\" /><input id=\"clear_media_files3\" type=\"button\" value=\"Clear\" onClick=\"clearMediaInputField('mediaFiles3')\" /></td>" +
"        </tr>" +
"        <tr>" +
"            <td><label for=\"mediaFiles4\">Associated data file #4:</label></td>" +
"            <td><input id=\"mediaFiles4\" type=\"file\" size=\"80\" name=\"datafile\" /><input id=\"clear_media_files4\" type=\"button\" value=\"Clear\" onClick=\"clearMediaInputField('mediaFiles4')\" /></td>" +
"        </tr>" +
"        <tr>" +
"            <td><label for=\"mediaFiles5\">Associated data file #5:</label></td>" +
"            <td><input id=\"mediaFiles5\" type=\"file\" size=\"80\" name=\"datafile\" /><input id=\"clear_media_files5\" type=\"button\" value=\"Clear\" onClick=\"clearMediaInputField('mediaFiles5')\" /></td>" +
"        </tr>" +
"        <tr>" +
"            <td><label for=\"mediaFiles6\">Associated data file #6:</label></td>" +
"            <td><input id=\"mediaFiles6\" type=\"file\" size=\"80\" name=\"datafile\" /><input id=\"clear_media_files6\" type=\"button\" value=\"Clear\" onClick=\"clearMediaInputField('mediaFiles6')\" /></td>" +
"        </tr>" +
"        <![endif]-->" +
"     <tr>" +
"        <td><input type=\"submit\" name=\"button\" value=\"Upload Submission\" /></td>" +
"        <td />" +
"     </tr>" +
"    </table>" +
"    </form>" +
"<p>Submissions are located under the <code>/odk/instances</code> directory on the phone's " +
"sdcard.  This directory will contain subdirectories with names of the form: <code>formID_yyyy-mm-dd_hh-MM-ss</code></p>" +
"<p>Within each of these subdirectories are the submission data file (named: <code>formID_yyyy-mm-dd_hh-MM-ss.xml</code>)," +
"and zero or more associated data files for the images, audio clips, video clips, " +
"etc. linked with this submission.</p>" +
"</div>";

  /**
   * Handler for HTTP Get request that processes a form submission
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
   CallingContext cc = ContextFactory.getCallingContext(this, req);

   Double openRosaVersion = getOpenRosaVersion(req);
   if ( openRosaVersion != null ) {
      /*
       * If we have an OpenRosa version header, assume that this is due to a 
       * channel redirect (http: => https:) and that the request was originally
       * a HEAD request.  Reply with a response appropriate for a HEAD request.
       * 
       * It is unclear whether this is a GAE issue or a Spring Frameworks issue.
       */
	  logger.warning("Inside doGet -- replying as doHead");
      doHead(req, resp);
      return;
   }

   StringBuilder headerString = new StringBuilder();
   headerString.append("<script type=\"application/javascript\" src=\"");
   headerString.append(cc.getWebApplicationURL(UPLOAD_SCRIPT_RESOURCE));
   headerString.append("\"></script>");
   beginBasicHtmlResponse(TITLE, headerString.toString(), resp, cc );// header info
   PrintWriter out = resp.getWriter();
   out.write(UPLOAD_PAGE_BODY_START);
   out.write(cc.getWebApplicationURL(ADDR));
   out.write(UPLOAD_PAGE_BODY_MIDDLE);
   finishBasicHtmlResponse(resp);
  }

  /**
   * Handler for HTTP head request.  This is used to verify that channel
   * security and authentication have been properly established. 
   */
  @Override
  protected void doHead(HttpServletRequest req, HttpServletResponse resp)
         throws IOException {
   CallingContext cc = ContextFactory.getCallingContext(this, req);
   logger.info("Inside doHead");

   addOpenRosaHeaders(resp);
   String serverUrl = cc.getServerURL();
   String url = serverUrl +  BasicConsts.FORWARDSLASH + ADDR;
   resp.setHeader("Location", url);
   resp.setStatus(204); // no content...
  }

  /**
   * Handler for HTTP post request that processes a form submission Currently
   * supports plain/xml and multipart
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
   CallingContext cc = ContextFactory.getCallingContext(this, req);

   Double openRosaVersion = getOpenRosaVersion(req);
   String isIncompleteFlag = null;
    try {
      SubmissionParser submissionParser = null;
      if (ServletFileUpload.isMultipartContent(req)) {
      MultiPartFormData uploadedSubmissionItems = new MultiPartFormData(req);
        isIncompleteFlag = uploadedSubmissionItems.getSimpleFormField(ServletConsts.TRANSFER_IS_INCOMPLETE);
        submissionParser = new SubmissionParser(uploadedSubmissionItems, cc);
      } else {
        // TODO: check that it is the proper types we can deal with
        // XML received, we hope...
        submissionParser = new SubmissionParser(req.getInputStream(), cc);
      }

      // TODO: mitch are we assuming the submissionParser always persists?
      Form form = Form.retrieveForm(submissionParser.getSubmissionFormId(), cc);
      Submission submission = submissionParser.getSubmission();

      // send information to remote servers that need to be notified
      List<ExternalService> tmp = FormServiceCursor.getExternalServicesForForm(form, cc);
      UploadSubmissions uploadTask = (UploadSubmissions) cc.getBean(BeanDefs.UPLOAD_TASK_BEAN);

      // publication failures should not fail the submission...
      try {
	     CallingContext ccDaemon = ContextFactory.getCallingContext(this, req);
	     ccDaemon.setAsDaemon(true);
	     for (ExternalService rs : tmp) {
	        uploadTask.createFormUploadTask(rs.getFormServiceCursor(), ccDaemon);
	     }
      } catch (ODKExternalServiceException e) {
          logger.info("Publishing enqueue failure (this is recoverable) - " + e.getMessage());
          e.printStackTrace();
      }
      
      // form full url including scheme...
     String serverUrl = cc.getServerURL();
     String url = serverUrl +  BasicConsts.FORWARDSLASH + ADDR;
     resp.setHeader("Location", url);

     resp.setStatus(HttpServletResponse.SC_CREATED);
      if ( openRosaVersion == null ) {
    	logger.info("Successful non-OpenRosa submission");

        resp.setContentType(HtmlConsts.RESP_TYPE_HTML);
        resp.setCharacterEncoding(HtmlConsts.UTF8_ENCODE);
        PrintWriter out = resp.getWriter();
        out.write(HtmlConsts.HTML_OPEN);
        out.write(HtmlConsts.BODY_OPEN);
        out.write("Successful submission upload.  Click ");
        out.write(HtmlUtil.createHref(cc.getWebApplicationURL(ADDR), "here"));
        out.write(" to return to upload submissions page.");
        out.write(HtmlConsts.BODY_CLOSE);
        out.write(HtmlConsts.HTML_CLOSE);
      } else {
    	logger.info("Successful OpenRosa submission");

  	    addOpenRosaHeaders(resp);
        resp.setContentType(HtmlConsts.RESP_TYPE_XML);
        resp.setCharacterEncoding(HtmlConsts.UTF8_ENCODE);
        PrintWriter out = resp.getWriter();
        out.write("<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">");
        if ( isIncompleteFlag != null && isIncompleteFlag.compareToIgnoreCase("YES") == 0) {
           out.write("<message>partial submission upload was successful!</message>");
        } else {
           out.write("<message>full submission upload was successful!</message>");
        }
        out.write("</OpenRosaResponse>");
      }
    } catch (ODKFormNotFoundException e) {
      e.printStackTrace();
      logger.warning("Form not found - " + e.getMessage());
      odkIdNotFoundError(resp);
    } catch (ODKParseException e) {
      logger.warning("Parsing failure - " + e.getMessage());
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.PARSING_PROBLEM);
    } catch (ODKEntityPersistException e) {
      logger.severe("Persist failure - " + e.getMessage());
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorConsts.PARSING_PROBLEM);
   } catch (ODKIncompleteSubmissionData e) {
      logger.warning("Incomplete submission failure - " + e.getMessage());
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.PARSING_PROBLEM);
    } catch (ODKConversionException e) {
      logger.warning("Datatype casting failure - " + e.getMessage());
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.PARSING_PROBLEM);
    } catch (ODKDatastoreException e) {
      logger.severe("Datastore failure - " + e.getMessage());
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorConsts.PARSING_PROBLEM);
    } catch (FileUploadException e) {
      logger.warning("Attachments parsing failure - " + e.getMessage());
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.PARSING_PROBLEM);
    } catch (ODKFormSubmissionsDisabledException e) {
      logger.warning("Form submission disabled - " + e.getMessage());
	  e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.FORM_DOES_NOT_ALLOW_SUBMISSIONS);
	} catch (Exception e) {
	  logger.severe("Unexpected exception: " + e.getMessage());
	  e.printStackTrace();
	  resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected exception");
	}
  }
}
