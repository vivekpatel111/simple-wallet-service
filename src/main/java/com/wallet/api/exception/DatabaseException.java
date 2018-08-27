package com.wallet.api.exception;

public class DatabaseException extends ApiException
{
    public DatabaseException(String message)
    {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    @Override
    public Integer getResponseCode()
    {
        return 500; //internal error
    }
}
