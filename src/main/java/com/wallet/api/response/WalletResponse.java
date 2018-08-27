package com.wallet.api.response;

import com.wallet.api.data.Wallet;
import com.wallet.utils.Constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PRIVATE)
@ToString
public class WalletResponse extends ApiResponse
{
    private Wallet wallet;

    public WalletResponse(String message, Wallet wallet)
    {
        super(message);
        this.wallet = wallet;
    }
    
    public WalletResponse(Wallet wallet)
    {
        super(Constants.STATUS_OK_MESSAGE);
        this.wallet = wallet;
    }
}
