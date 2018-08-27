package com.wallet.api.data.enums;

public enum TransactionStatus
{
    /**
     * initial status of transaction
     */
    NEW,
    
    /**
     * final status if transaction is successful after adjustment
     */
    SUCCESSFUL,
    
    /**
     * final status if transaction is failed after adjustment
     */
    FAILED,
    
    /**
     * final status if transaction is cancelled
     */
    CANCELLED;
}
