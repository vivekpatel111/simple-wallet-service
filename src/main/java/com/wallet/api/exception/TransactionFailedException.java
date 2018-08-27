package com.wallet.api.exception;

public class TransactionFailedException extends ApiException
{
    public TransactionFailedException(String message)
    {
        super(message);
    }
    
    public TransactionFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    @Override
    public Integer getResponseCode()
    {
        return 500; //internal error
    }
}
