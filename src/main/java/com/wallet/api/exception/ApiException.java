package com.wallet.api.exception;

public abstract class ApiException extends Exception
{
    public ApiException(String message)
    {
        super(message);
    }
    
    public ApiException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    abstract public Integer getResponseCode();
}

