package com.wallet.utils;

import com.wallet.api.data.enums.TransactionStatus;
import com.wallet.api.data.enums.WalletStatus;

public class Constants
{
    public static final String DEFAULT_WALLET_STATUS_TYPE = 
        WalletStatus.ACTIVE.name();
    
    public static final String DEFAULT_TRANSACTION_STATUS_TYPE = 
        TransactionStatus.NEW.name();

    public static final String STATUS_OK_MESSAGE = "OK";

    public static final Integer TRANSACTION_MAX_RESULT_SIZE = 10;

    public static final Long CACHE_TTL_IN_MS = 1000 * 60 * 5L; //5 min

    public static final Integer DEFAULT_OFFSET = 0;

    public static final Integer DEFAULT_APP_PORT = 9000;

    public static final String JSON_RESPONSE_TYPE = "application/json";

    public static final String NEW_TRANSACTION_MESSAGE = 
        "Transaction added.";
    
    public static final String SUCCESS_TRANSACTION_MESSAGE = 
        "Transaction processed.";

    public static final String REVERT_TRANSACTION_MESSAGE = 
        "Transaction cancelled and amount reverted.";
    
    public static final String CANCEL_TRANSACTION_MESSAGE = 
        "Transaction cancelled.";

    public static final String FAILED_TRANSACTION_MESSAGE_BALANCE = 
        "Transaction failed due to insufficient balance.";
}
