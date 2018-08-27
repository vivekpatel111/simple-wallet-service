package com.wallet.api.requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.api.data.enums.WalletType;
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
public class PostWalletRequest  extends ApiRequest
{
    private static final Logger LOGGER =
        LoggerFactory.getLogger(PostWalletRequest.class);
    
    private WalletType type;
    
    @Override
    public void validateRequest()
        throws InvalidParamsException
    {
        StringBuilder builder = new StringBuilder();
        
        if (null == type)
        {
            builder.append("type is required.");
        }
        
        if (0 != builder.length())
        {
            LOGGER.error("CreateWalletRequest validation failed - " + builder);
            throw new InvalidParamsException(builder.toString());
        }
    }
}
