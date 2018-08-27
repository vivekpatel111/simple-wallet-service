package com.wallet.api.requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.api.data.enums.TransactionType;
import com.wallet.api.exception.InvalidParamsException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
public class PostTransactionRequest extends ApiRequest
{
    private static final Logger LOGGER =
        LoggerFactory.getLogger(PostTransactionRequest.class);
    
    private TransactionType type;
    private Double amount;
    
    @Override
    public void validateRequest()
        throws InvalidParamsException
    {
        StringBuilder builder = new StringBuilder();
        
        if (null == type)
        {
            builder.append("type is required.");
        }
        
        if (null == amount || 0 >= amount)
        {
            builder.append("amount must be positive.");
        }
        
        if (0 != builder.length())
        {
            LOGGER.error("PostTransactionRequest validation failed - " + builder);
            throw new InvalidParamsException(builder.toString());
        }
    }
}
