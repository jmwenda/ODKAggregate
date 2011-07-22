package org.opendatakit.aggregate.odktables.entity;

import org.opendatakit.aggregate.odktables.relation.Permissions;
import org.opendatakit.aggregate.odktables.relation.Users;
import org.opendatakit.common.ermodel.simple.Entity;
import org.opendatakit.common.ermodel.simple.typedentity.TypedEntity;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.web.CallingContext;

/**
 * <p>
 * A User is a (aggregateUserIdentifier, userID, userName) tuple, where
 * <ul>
 * <li>aggregateUserIdentifier = the public unique identifier of a user</li>
 * <li>userID = the private unique identifier of the user, known only to the
 * user</li>
 * <li>userName = the human readable name of the user</li>
 * </ul>
 * </p>
 * 
 * @author the.dylan.price@gmail.com
 * 
 */
public class InternalUser extends TypedEntity
{
    private CallingContext cc;

    public InternalUser(String userID, String userName, CallingContext cc)
            throws ODKDatastoreException
    {
        super(Users.getInstance(cc).newEntity());
        this.cc = cc;
        setID(userID);
        setName(userName);
    }

    public InternalUser(Entity entity, CallingContext cc)
            throws ODKDatastoreException
    {
        super(entity);
        this.cc = cc;
    }

    public String getAggregateIdentifier()
    {
        return entity.getAggregateIdentifier();
    }

    public String getID()
    {
        return entity.getString(Users.USER_ID);
    }

    public void setID(String value)
    {
        entity.set(Users.USER_ID, value);
    }

    public String getName()
    {
        return entity.getString(Users.USER_NAME);
    }

    public void setName(String value)
    {
        entity.set(Users.USER_NAME, value);
    }

    public boolean hasPerm(String aggregateTableIdentifier, String permission)
            throws ODKDatastoreException
    {
        return Permissions
                .getInstance(cc)
                .query()
                .equal(Permissions.AGGREGATE_TABLE_IDENTIFIER,
                        aggregateTableIdentifier).equal(permission, true)
                .exists();
    }

    @Override
    public String toString()
    {
        return String.format(
                "InternalUser[aggregateIdentifier=%s, userID=%s, name=%s",
                getAggregateIdentifier(), getID(), getName());
    }

    public static InternalUser fromEntity(Entity entity, CallingContext cc)
            throws ODKDatastoreException
    {
        return new InternalUser(entity, cc);
    }
}
