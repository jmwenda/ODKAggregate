/*
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opendatakit.aggregate.ContextFactory;
import org.opendatakit.aggregate.constants.ErrorConsts;
import org.opendatakit.aggregate.constants.HtmlUtil;
import org.opendatakit.common.constants.HtmlConsts;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.security.common.GrantedAuthorityNames;
import org.opendatakit.common.security.server.SecurityServiceUtil;
import org.opendatakit.common.security.spring.GrantedAuthorityHierarchyTable;
import org.opendatakit.common.web.CallingContext;

/**
 * Displays all the groups defined by the system and allows users to define
 * their own, new, groups.
 * 
 * @author mitchellsundt@gmail.com
 *
 */
public class GroupManagementServlet extends ServletUtilBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1714321138606520583L;

	/**
	 * URI from base
	 */
	public static final String ADDR = "access/group-management";

	/**
	 * Title for generated webpage
	 */
	public static final String TITLE_INFO = "Manage User Groups";


	/**
	 * Handler for HTTP Get request that responds with an XML list of forms to
	 * download
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		CallingContext cc = ContextFactory.getCallingContext(this, req);

		TreeMap<String, TreeSet<String>> inheritFrom = null; 
		try {
			inheritFrom = GrantedAuthorityHierarchyTable.getEntireGrantedAuthorityHierarchy(
								cc.getDatastore(), cc.getCurrentUser());
		} catch (ODKDatastoreException e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					ErrorConsts.PERSISTENCE_LAYER_PROBLEM);
			return;
		}

		beginBasicHtmlResponse(TITLE_INFO, resp, true, cc); // header info

		PrintWriter out = resp.getWriter();
		
		out.write("<p>Define user groups for your system to manage the access rights of " +
				"groups of users. Then, after setting the access rights, place the individual " +
				"users in the user groups to which they should belong.</p>");
		/**
		 * Add new user group...
		 */
		out.write("<hr/><h2>Add or Update User Group</h2>");
		out.write(HtmlUtil.createFormBeginTag(cc
				.getWebApplicationURL(GroupModifyServlet.ADDR), null,
				HtmlConsts.GET));

		out.write("User group to add or update:" + HtmlConsts.LINE_BREAK);
		out.write(HtmlUtil.createInput(HtmlConsts.INPUT_TYPE_TEXT,
				GroupModifyServlet.GROUPNAME, null));
		out.write(HtmlUtil.createInput("submit", null, "Add or update"));
		out.write("<p>In addition to your own defined groups, the system defines the " +
				"following groups that you can also add or remove access rights from:" +
				"</p><ol>");
		for ( GrantedAuthorityNames n : GrantedAuthorityNames.values() ) {
			if ( !GrantedAuthorityNames.permissionsCanBeAssigned(n.toString()) ) continue;
			
			out.write("<li>" + n.toString() + " - " + n.getDescription() + "</li>");
		}
		out.write("</ol><p>If you support OpenID logins from providers other than Gmail, the " +
				"system will create additional MAILTO_... groups for each distinct" +
				" e-mail domain of all OpenID-authenticated users.  These additional group " +
				"names have MAILTO_ prepended to the all-capitalized e-mail domain name, " +
				"with any non-alphanumeric characters (e.g., periods) replaced by " +
				"underscores.</p>");
		out.write("</form>");

		/**
		 * Modify status of existing user groups.
		 */
		
		out.write("<hr/><h2>All User Groups</h2>");
		
		boolean doesGoogleEarthWork = isAttachmentViewerAnonymous(cc);
		
		if ( !doesGoogleEarthWork) {
			out.write("<font style=\"color: red;\"><b>");
		}
		out.write("<p>NOTE: ");
		if ( !doesGoogleEarthWork) {
			out.write("</b></font>");
		}
		
		out.write("Google Earth will not display images in the information ballons " +
				"unless the " + 
					GrantedAuthorityNames.ROLE_ATTACHMENT_VIEWER.name() + " permission is " +
					"granted to " + GrantedAuthorityNames.USER_IS_ANONYMOUS.name() + 
					".</p><p>Whether or not " +
					GrantedAuthorityNames.ROLE_ATTACHMENT_VIEWER.name() + " is assigned to " + 
					GrantedAuthorityNames.USER_IS_ANONYMOUS.name() + ", the images remain " +
					"accessible when the user clicks the `View` link within the information " +
					"ballon provided the user has " +
					"the necessary privileges to view those images (upon the first such access, " +
					"the user may be prompted to log in).</p>");

		out.write("<p>NOTE: if a user group neither inherits access rights from " +
				"another user group nor has any directly granted access rights, it " +
				"will not appear in this list.  To modify that group, type the name " +
				"of the group into the 'Add or Update user Group' form, above.</p>");
		
		out.print(HtmlConsts.TABLE_OPEN);

		String[] headers = new String[] { "", "User Group", 
							"Inherits some Access Rights<br/>from these User Groups", 
							"Directly Granted Access Rights", 
							"Full Set of Granted Access Rights" };

		for (String header : headers) {
			out.print(HtmlUtil.wrapWithHtmlTags(HtmlConsts.TABLE_HEADER, header));
	    }
		
		int recordNum = 0;
		for ( Map.Entry<String, TreeSet<String>> e : inheritFrom.entrySet() ) {
			++recordNum;
			out.print(HtmlConsts.TABLE_ROW_OPEN);
			out.print(HtmlUtil.wrapWithHtmlTags(HtmlConsts.TABLE_DATA,
					createEditButton(e.getKey(), cc)));

			out.print(HtmlUtil.wrapWithHtmlTags(HtmlConsts.TABLE_DATA, 
					groupNameAndMembershipLink(e.getKey(),cc)));
			out.print(HtmlUtil.wrapWithHtmlTags(HtmlConsts.TABLE_DATA, 
					organizeGroups(e.getValue(), false)));
			out.print(HtmlUtil.wrapWithHtmlTags(HtmlConsts.TABLE_DATA, 
					organizeGroups(e.getValue(), true)));
			out.print(HtmlUtil.wrapWithHtmlTags(HtmlConsts.TABLE_DATA, 
					fetchFullSet(e.getKey(), cc)));

			out.print(HtmlConsts.TABLE_ROW_CLOSE);
		}

		out.print(HtmlConsts.TABLE_CLOSE);
		finishBasicHtmlResponse(resp);
	}
	
	private String groupNameAndMembershipLink( String groupname, CallingContext cc ) {
		if ( SecurityServiceUtil.isSpecialName(groupname) ) {
			return groupname;
		} else {
	        Map<String, String> properties = new HashMap<String, String>();
	        properties.put(GroupModifyMembersServlet.GROUPNAME, groupname);
	    
	        return groupname + "<br/>" + HtmlUtil.createHrefWithProperties(
        		cc.getWebApplicationURL(GroupModifyMembersServlet.ADDR), properties,
	        	"View or update the users in this group");
		}
	}

	private String fetchFullSet(String key, CallingContext cc) {
		TreeSet<String> orderedSet = fetchGrantedAuthoritySet(key, cc);

		return buildList(orderedSet);
	}

	private String organizeGroups(Collection<String> directGrants, boolean rolesOnly) {
		TreeSet<String> orderedSet = new TreeSet<String>();
		for ( String g : directGrants ) {
			boolean role = ! GrantedAuthorityNames.permissionsCanBeAssigned(g);
			if ( rolesOnly ? role : !role ) {
				orderedSet.add(g);
			}
		}
		return buildList(orderedSet);
	}

	private String buildList(TreeSet<String> orderedSet) {
		StringBuilder b = new StringBuilder();
		boolean first = true;
		for ( String g : orderedSet ) {
			if ( !first ) {
				b.append("<br/>");
			}
			first = false;
			b.append(g);
		}
		return b.toString();
	}

	public String createEditButton(String groupName, CallingContext cc) {
		StringBuilder html = new StringBuilder();
		html.append(HtmlUtil.createFormBeginTag(cc
				.getWebApplicationURL(GroupModifyServlet.ADDR), null,
				HtmlConsts.GET));

		html.append(HtmlUtil.createInput(HtmlConsts.INPUT_TYPE_HIDDEN, GroupModifyServlet.GROUPNAME, groupName));
		html.append(HtmlUtil.createInput("submit", null, "Edit"));
		html.append("</form>");
		return html.toString();
	}

}
