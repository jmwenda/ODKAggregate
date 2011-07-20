package org.opendatakit.aggregate.odktables.client.api;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.opendatakit.aggregate.odktables.client.entity.Column;
import org.opendatakit.aggregate.odktables.client.entity.Row;
import org.opendatakit.aggregate.odktables.client.exception.AggregateInternalErrorException;
import org.opendatakit.aggregate.odktables.client.exception.PermissionDeniedException;
import org.opendatakit.aggregate.odktables.client.exception.TableAlreadyExistsException;
import org.opendatakit.aggregate.odktables.client.exception.TableDoesNotExistException;
import org.opendatakit.aggregate.odktables.client.exception.UserDoesNotExistException;

/**
 * SimpleAPI contains API calls for using Aggregate as a simple table storage
 * service.
 */
public class SimpleAPI extends CommonAPI
{

    /**
     * Constructs a new instance of SimpleAPI, using the supplied user
     * identification for API calls which require it.
     * 
     * @param aggregateURI
     *            the URI of a running ODK Aggregate instance
     * @param userID
     *            the ID of the user to use for API calls
     * @throws IOException
     *             if there is a problem communicating with the Aggregate server
     *             or if it does not exist
     * @throws UserDoesNotExistException
     *             if no user with userID exists in Aggregate
     * @throws AggregateInternalErrorException
     *             if Aggregate encounters an internal error that causes the
     *             initial communication to fail
     */
    public SimpleAPI(URI aggregateURI, String userID)
    {
        super(aggregateURI);
        throw new NotImplementedException();
    }

    /**
     * Creates a new simple table. The userID used to make the API call will
     * become the owner of the table, and automatically be granted all
     * permissions.
     * 
     * @param tableID
     *            the identifier which you will use to track the table on the
     *            client side
     * @param tableName
     *            the human readable name of the table
     * @param columns
     *            a list of columns defining the columns the table should have
     * @return the aggregateTableIdentifier of the table, which is universally
     *         unique.
     * @throws TableAlreadyExistsException
     *             if you have already created a table with tableID
     * @throws AggregateInternalErrorException
     *             if Aggregate encounters an internal error that causes the
     *             call to fail
     * @throws IOException
     *             if there is a problem communicating with the Aggregate server
     */
    public String createTable(String tableID, String tableName, List<Column> columns)
    {
        throw new NotImplementedException();
    }

    /**
     * Deletes a table.
     * 
     * @param tableID
     *            the caller's identifier for the table
     * @throws TableDoesNotExistException
     *             if the table does not exist
     * @throws PermissionDeniedException
     *             if the userID used to make the API call does not have delete
     *             permission on the table
     * @throws AggregateInternalErrorException
     *             if Aggregate encounters an internal error that causes the
     *             call to fail
     * @throws IOException
     *             if there is a problem communicating with the Aggregate server
     */
    public void deleteTable(String tableID)
    {
        throw new NotImplementedException();
    }

    /**
     * @return a list of all rows in the table with tableID
     * @throws TableDoesNotExistException
     *             if no table with tableID exists
     * @throws PermissonDeniedException
     *             if the userID used to make the API call does not have read
     *             permission on the table
     * @throws AggregateInternalErrorException
     *             if Aggregate encounters an internal error that causes the
     *             call to fail
     * @throws IOException
     *             if there is a problem communicating with the Aggregate server
     */
    public List<Row> getAllRows(String tableID)
    {
        throw new NotImplementedException();
    }

    /**
     * Inserts the given rows into the given table.
     * 
     * @param tableID
     *            the caller's identifier for a table
     * @param rows
     *            the rows to insert into the table. The rowIDs of these rows
     *            should not have previously been inserted into the table.
     * @return a map of the rowIDs inserted to the aggregateRowIdentifiers
     *         generated by Aggregate. You must store these
     *         aggregateRowIdentifiers in order to identify the rows to
     *         Aggregate in the future.
     * @throws TableDoesNotExistException
     *             if no table with tableID exists
     * @throws PermissionDeniedException
     *             if the userID used to make the API call does not have write
     *             permission on the table
     * @throws AggregateInternalErrorException
     *             if Aggregate encounters an internal error that causes the
     *             call to fail
     * @throws IOException
     *             if there is a problem communicating with the Aggregate server
     */
    public Map<String, String> insertRows(String tableID, List<Row> rows)
    {
        throw new NotImplementedException();
    }
}
