package com.wallet.api.exception;

public class NoResourceFoundException extends ApiException
{
    public NoResourceFoundException(String message)
    {
        super(message);
    }
    
    public NoResourceFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    @Override
    public Integer getResponseCode()
    {
        return 401; //unauthorised
    }
}
