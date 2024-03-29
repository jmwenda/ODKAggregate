/*
 * Copyright (C) 2011 University of Washington
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.opendatakit.aggregate.ContextFactory;
import org.opendatakit.common.security.UserService;
import org.opendatakit.common.web.CallingContext;

/**
 * Simple servlet used to clear the session cookie of a client and present the
 * openid_login.html page to them. This allows for an anonymous user to choose
 * to provide credentials.
 * 
 * @author mitchellsundt@gmail.com
 * 
 */
public class ClearSessionThenLoginServlet extends ServletUtilBase {

  /*
   * Standard fields
   */

  private static final long serialVersionUID = 629046684126101848L;

  public static final String ADDR = "relogin.html";

  public static final String TITLE_INFO = "ODK Aggregate";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    CallingContext cc = ContextFactory.getCallingContext(this, req);
    UserService userService = cc.getUserService();
    boolean isAnon = !userService.isUserLoggedIn();

    HttpSession s = req.getSession();
    if (s != null) {
      s.invalidate();
    }
    String newUrl;
    if (isAnon) {
      // anonymous user -- go to the login page...
      newUrl = cc.getWebApplicationURL("openid_login.html");
    } else {
      // we are logged in via openid or basic or digest auth.
      // redirect to Spring's logout url...
      newUrl = cc.getWebApplicationURL(cc.getUserService().createLogoutURL());
    }
    // preserve the query string (helps with GWT debugging)
    String query = req.getQueryString();
    if (query != null && query.length() != 0) {
      newUrl += "?" + query;
    }
    resp.sendRedirect(newUrl);
  }
}
