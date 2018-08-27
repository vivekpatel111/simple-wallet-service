package com.wallet.api.response;

import java.util.List;

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
public class ListTransactionResponse extends ApiResponse
{
    private Wallet wallet;
    private List<Transaction> transactions;
    
    public ListTransactionResponse(String message, Wallet wallet,
                                   List<Transaction> transactions)
    {
        super(message);
        this.wallet = wallet;
        this.transactions = transactions;
    }
    
    public ListTransactionResponse(Wallet wallet,
                                   List<Transaction> transactions)
    {
        super(Constants.STATUS_OK_MESSAGE);
        this.wallet = wallet;
        this.transactions = transactions;
    }
}
