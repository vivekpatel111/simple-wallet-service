package com.wallet.api.response;

import com.wallet.api.data.Transaction;
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
public class TransactionResponse extends ApiResponse
{
    private Wallet wallet;
    private Transaction transaction;

    public TransactionResponse(String message, Wallet wallet, 
                               Transaction transaction)
    {
        super(message);
        this.wallet = wallet;
        this.transaction = transaction;
    }
    
    public TransactionResponse(Wallet wallet, Transaction transaction)
    {
        super(Constants.STATUS_OK_MESSAGE);
        this.wallet = wallet;
        this.transaction = transaction;
    }
}
