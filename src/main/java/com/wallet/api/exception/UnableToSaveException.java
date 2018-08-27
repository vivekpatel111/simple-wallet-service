package com.wallet.api.exception;

public class UnableToSaveException extends DatabaseException
{
    public UnableToSaveException(String message)
    {
        super(message);
    }
    
    public UnableToSaveException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
