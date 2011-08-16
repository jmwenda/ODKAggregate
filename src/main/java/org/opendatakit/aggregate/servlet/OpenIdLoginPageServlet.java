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
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opendatakit.common.constants.HtmlConsts;

/**
 * Simple servlet to display the openId login page.  
 * Invalidates the user's session before displaying the page.
 * 
 * @author user
 *
 */
public class OpenIdLoginPageServlet extends ServletUtilBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1036419513113652548L;
	private static final Logger logger = Logger.getLogger(OpenIdLoginPageServlet.class.getName());
	
	public static final String ADDR = "openid_login.html";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		logger.info("Invalidating login session " + req.getSession().getId());
		// Invalidate session.
		req.getSession().invalidate();
		// Display page.
		resp.setContentType(HtmlConsts.RESP_TYPE_HTML);
	    resp.setCharacterEncoding(HtmlConsts.UTF8_ENCODE);
	    resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
	    resp.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
	    resp.setHeader("Pragma", "no-cache");
	    PrintWriter out = resp.getWriter();
	    out.print(
"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" +
"<html>" +
"<head>" +
"<meta http-equiv=\"cache-control\" content=\"no-store, no-cache, must-revalidate\"/>" +
"<meta http-equiv=\"expires\" content=\"Mon, 26 Jul 1997 05:00:00 GMT\"/>" +
"<meta http-equiv=\"pragma\" content=\"no-cache\"/>" +
"<link rel=\"shortcut icon\" href=\"favicon.ico\"/>" +
"<title>Log onto Aggregate</title>" +
"<link type=\"text/css\" rel=\"stylesheet\" href=\"AggregateUI.css\">" +
"<link type=\"text/css\" rel=\"stylesheet\" href=\"stylesheets/button.css\">" +
"<link type=\"text/css\" rel=\"stylesheet\" href=\"stylesheets/table.css\">" +
"<link type=\"text/css\" rel=\"stylesheet\" href=\"stylesheets/navigation.css\">" +
"<script type=\"text/javascript\">" +
"window.onbeforeunload=function() {\n" +
"var e=document.getElementById(\"stale\");\n" +
"e.value=\"yes\";\n" +
"}\n" +
"window.onload=function(){\n" +
"var e=document.getElementById(\"stale\");\n" +
"if(e.value==\"yes\") {window.location.reload(true);}\n" +
"}\n" +
"</script>" +
"</head>" +
"<body>" +
"<input type=\"hidden\" id=\"stale\" value=\"no\">" +
"<table width=\"100%\" cellspacing=\"30\"><tr>" +
"<td align=\"LEFT\" width=\"10%\"><img src=\"odk_color.png\" id=\"odk_aggregate_logo\" /></td>" +
"<td align=\"LEFT\" width=\"90%\"><font size=\"7\">Log onto Aggregate</font></td></tr></table>" +
"<table cellspacing=\"20\">" +
"<tr><td valign=\"top\">" +
	"<form action=\"local_login.html\" method=\"get\">" +
		"<input class=\"gwt-Button\" type=\"submit\" value=\"Sign in with Aggregate password\"/>" +
	"</form></td>" +
"<td valign=\"top\">Click this button to log onto Aggregate using the username " +
"and password that have been assigned to you by the Aggregate site administrator.</td></tr>" +
"<tr><td valign=\"top\">" +
	"<form action=\"j_spring_openid_security_check\" method=\"post\">" +
		"<input name=\"openid_identifier\" size=\"50\" maxlength=\"100\" " +
				"type=\"hidden\" value=\"https://www.google.com/accounts/o8/id\"/>" +
		"<input class=\"gwt-Button\" type=\"submit\" value=\"Sign in with Google\"/>" +
  "</form></td>" +
  "<td valign=\"top\">Click this button to log onto Aggregate using your Google account (via OpenID).<p>" +
"<font color=\"blue\">NOTE:</font> you must allow this site to obtain your e-mail address. " +
"Your e-mail address will only be used for establishing website access permissions.</p></td></tr>" +
"<tr><td valign=\"top\">" +
	"<form action=\"Aggregate.html\" method=\"get\">" +
		"<input class=\"gwt-Button\" type=\"submit\" value=\"Anonymous Access\"/>" +
	"</form></td>" +
"<td valign=\"top\">Click this button to access Aggregate without logging in.</td></tr>" +
"</table>" +
"</body>" +
"</html>");
	}

}
