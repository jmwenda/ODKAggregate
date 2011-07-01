package org.opendatakit.aggregate.odktables.client.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * A Row represents a row in a table. A row has three attributes:
 * <ul>
 * <li>rowID: the unique identifier of the row. This will always be provided by
 * the client upon insertion of the row.</li>
 * <li>rowUUID: the universally unique identifier of the row. This will
 * initially be provided by Aggregate upon insertion and from then on will be
 * used by the client to identify the row to Aggregate.</li>
 * <li>values: a set of name-value pairs where each name is a column name and
 * the value is the value for that column</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Row is mutable and currently not threadsafe.
 * </p>
 * 
 * @author the.dylan.price@gmail.com
 */
public final class Row
{

    private String rowID;
    private String rowUUID;
    private final Map<String, String> values;

    /**
     * Constructs a new Row. If this Row is meant for insertion, then you must
     * make sure to call setRowID before using it in an API call. If this Row is
     * meant for updating or deletion, then you must similarly make sure to call
     * setRowUUID.
     */
    public Row()
    {
        this.rowID = null;
        this.rowUUID = null;
        this.values = new HashMap<String, String>();
    }

    /**
     * Retrieves the current value of the specified column.
     * 
     * @param column
     *            the name of the column
     * @return the value currently set for the given column name, or null if no
     *         such value exists.
     */
    public String getValue(String column)
    {
        return this.values.get(column);
    }

    /**
     * Sets the value of a column.
     * 
     * @param column
     *            the name of the column
     * @param value
     *            the value of the column
     */
    public void setValue(String column, String value)
    {
        this.values.put(column, value);
    }

    /**
     * @return a map of column names to column values in this Row, as an
     *         unmodifiable map.
     */
    public Map<String, String> getColumnValuePairs()
    {
        return Collections.unmodifiableMap(values);
    }

    /**
     * @return the rowID of this Row.
     */
    public String getRowID()
    {
        return this.rowID;
    }

    /**
     * Sets the rowID
     * 
     * @param rowID
     *            the rowID of this row (provided by the client).
     */
    public void setRowID(String rowID)
    {
        this.rowID = rowID;
    }

    /**
     * @return the rowUUID of this Row.
     */
    public String getRowUUID()
    {
        return this.rowUUID;
    }

    /**
     * Sets the rowUUID.
     * 
     * @param rowUUID
     *            the rowUUID of this row (initially generated by Aggregate and
     *            provided by the client to identify the row).
     */
    public void setRowUUID(String rowUUID)
    {
        this.rowUUID = rowUUID;
    }

    @Override
    public String toString()
    {
        return String
                .format("rowID: %s, rowUUID: %s", this.rowID, this.rowUUID);
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Row))
            return false;
        Row o = (Row) other;
        return o.rowID.equals(this.rowID) && o.rowUUID.equals(this.rowUUID)
                && o.values.equals(this.values);
    }

    @Override
    public int hashCode()
    {
        return 3 * this.rowID.hashCode() + 5 * this.rowUUID.hashCode() + 7
                * this.values.hashCode();
    }
}
