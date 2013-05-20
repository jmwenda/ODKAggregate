package org.opendatakit.aggregate.client.odktables;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ServerTableACLServiceAsync {

	void getAcls(String tableId, AsyncCallback<List<TableAclClient>> callback);

	void getUserAcls(String tableId, AsyncCallback<List<TableAclClient>> callback);

	void getGroupAcls(String tableId, AsyncCallback<List<TableAclClient>> callback);

	void getDefaultAcl(String tableId, AsyncCallback<TableAclClient> callback);

	void getUserAcl(String userId, String tableId, AsyncCallback<TableAclClient> callback);

	void getGroupAcl(String groupId, String tableId, AsyncCallback<TableAclClient> callback);

	void setDefaultAcl(TableAclClient acl, String tableId, AsyncCallback<TableAclClient> callback);

	void setUserAcl(String userId, TableAclClient acl,
			String tableId, AsyncCallback<TableAclClient> callback);

	void setGroupAcl(String groupId, TableAclClient acl,
			String tableId, AsyncCallback<TableAclClient> callback);

	void deleteDefaultAcl(String tableId, AsyncCallback<Void> callback);

	void deleteUserAcl(String userId, String tableId, AsyncCallback<Void> callback);

	void deleteGroupAcl(String groupId, String tableId, AsyncCallback<Void> callback);


}
