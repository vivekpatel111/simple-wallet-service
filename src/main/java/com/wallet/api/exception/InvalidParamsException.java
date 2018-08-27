package com.wallet.api.exception;

public class InvalidParamsException extends ApiException
{
    public InvalidParamsException(String message)
    {
        super(message);
    }
    
    public InvalidParamsException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    @Override
    public Integer getResponseCode()
    {
        return 500; //internal error
    }
}
