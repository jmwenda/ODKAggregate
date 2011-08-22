package org.opendatakit.aggregate.odktables.client.exception;

public class AggregateInternalErrorException extends ODKTablesClientException
{
    /**
     * Serial number for serialization.
     */
    private static final long serialVersionUID = 4499968035606050096L;

    public AggregateInternalErrorException(String message)
    {
        super(message);
    }
}
