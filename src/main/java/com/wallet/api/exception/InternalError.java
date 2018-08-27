package com.wallet.api.exception;

public class InternalError extends ApiException
{
    public InternalError(String message)
    {
        super(message);
    }
    
    public InternalError(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    @Override
    public Integer getResponseCode()
    {
        return 500; //internal error
    }
}
