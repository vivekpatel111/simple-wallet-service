package com.wallet.interfaces;

import com.wallet.api.exception.InvalidParamsException;

public interface RequestValidator
{
    abstract public void validateRequest()
        throws InvalidParamsException;
}
