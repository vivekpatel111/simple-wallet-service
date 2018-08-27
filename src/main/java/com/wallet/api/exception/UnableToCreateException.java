package com.wallet.api.exception;

public class UnableToCreateException extends ApiException
{
    public UnableToCreateException(String message)
    {
        super(message);
    }
    
    public UnableToCreateException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    @Override
    public Integer getResponseCode()
    {
        return 500; //internal error
    }
}
