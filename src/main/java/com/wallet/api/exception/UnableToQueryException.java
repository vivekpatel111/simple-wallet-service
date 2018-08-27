package com.wallet.api.exception;

public class UnableToQueryException extends DatabaseException
{
    public UnableToQueryException(String message)
    {
        super(message);
    }
    
    public UnableToQueryException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
