package org.opendatakit.aggregate.odktables.command.synchronize;

import org.opendatakit.aggregate.odktables.command.Command;
import org.opendatakit.common.utils.Check;

/**
 * DeleteSynchronizedTable is immutable.
 *
 * @author the.dylan.price@gmail.com
 */
public class DeleteSynchronizedTable implements Command
{
    private static final String path = "/odktables/synchronize/deleteSynchronizedTable";
    
    private final String requestingUserID;
    private final String tableUUID;
    

    /**
     * For serialization by Gson
     */
    @SuppressWarnings("unused")
    private DeleteSynchronizedTable()
    {
       this.requestingUserID = null;
       this.tableUUID = null;
       
    }

    /**
     * Constructs a new DeleteSynchronizedTable.
     */
    public DeleteSynchronizedTable(String requestingUserID, String tableUUID)
    {
        
        Check.notNullOrEmpty(requestingUserID, "requestingUserID");
        Check.notNullOrEmpty(tableUUID, "tableUUID"); 
        
        this.requestingUserID = requestingUserID;
        this.tableUUID = tableUUID;
    }

    
    /**
     * @return the requestingUserID
     */
    public String getRequestingUserID()
    {
        return this.requestingUserID;
    }
    
    /**
     * @return the tableUUID
     */
    public String getTableUUID()
    {
        return this.tableUUID;
    }
    

    @Override
    public String toString()
    {
        return String.format("DeleteSynchronizedTable: " +
                "requestingUserID=%s " +
                "tableUUID=%s " +
                "", requestingUserID, tableUUID);
    }

    @Override
    public String getMethodPath()
    {
        return methodPath();
    }

    /**
     * @return the path of this Command relative to the address of an Aggregate
     *         instance. For example, if the full path to a command is
     *         http://aggregate.opendatakit.org/odktables/createTable, then this
     *         method would return '/odktables/createTable'.
     */
    public static String methodPath()
    {
        return path;
    }
}

