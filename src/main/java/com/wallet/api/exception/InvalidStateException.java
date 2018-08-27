package com.wallet.api.exception;

public class InvalidStateException extends ApiException
{
    public InvalidStateException(String message)
    {
        super(message);
    }
    
    public InvalidStateException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    @Override
    public Integer getResponseCode()
    {
        return 401; //unauthorised
    }
}
