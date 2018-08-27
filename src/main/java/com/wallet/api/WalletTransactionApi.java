package com.wallet.api;

import java.util.ArrayList;
import java.util.List;

import com.wallet.utils.Constants;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.wallet.api.data.Transaction;
import com.wallet.api.data.Wallet;
import com.wallet.api.data.enums.TransactionStatus;
import com.wallet.api.data.enums.WalletStatus;
import com.wallet.api.exception.DatabaseException;
import com.wallet.api.exception.InternalError;
import com.wallet.api.exception.InvalidParamsException;
import com.wallet.api.exception.InvalidStateException;
import com.wallet.api.exception.NoResourceFoundException;
import com.wallet.api.exception.TransactionFailedException;
import com.wallet.api.exception.UnableToQueryException;
import com.wallet.api.requests.PostTransactionRequest;
import com.wallet.api.response.ListTransactionResponse;
import com.wallet.api.response.TransactionResponse;
import com.wallet.dbaccess.models.TransactionModel;
import com.wallet.dbaccess.models.WalletModel;

import spark.Request;
import spark.Response;
import spark.Route;

public class WalletTransactionApi
{
    private static final String WALLET_ID_KEY = "walletId";
    private static final String TRANSACTION_ID_KEY = "transactionId";
    private static final String LIMIT_KEY = "limit";
    private static final String OFFSET_KEY = "offset";
    
    private static final Logger LOGGER =
        LoggerFactory.getLogger(WalletTransactionApi.class);
    
    public static Route createTransaction = new Route()
    {
        private Gson gson = new Gson();
        
        @Override
        public Object handle(Request request, Response response) 
            throws InvalidStateException, InvalidParamsException, 
                   InternalError, UnableToQueryException, DatabaseException, 
                   NoResourceFoundException
        {
            String walletId = request.params(WALLET_ID_KEY);
            LOGGER.info("Getting wallet info for - " + walletId);
            
            if (StringUtils.isBlank(walletId))
            {
                String errorMessage = 
                    "walletId is required for creating transaction.";
                LOGGER.error(errorMessage);
                throw new InvalidParamsException(errorMessage);
            }
            
            String requestBody = request.body();
            PostTransactionRequest transactionRequest = 
                gson.fromJson(requestBody, PostTransactionRequest.class);
            
            transactionRequest.validateRequest();
            
            WalletModel walletModel = WalletModel.getByWalletId(walletId);
            
            Wallet wallet = walletModel.getWallet();
            
            if (WalletStatus.ACTIVE.name().
                    equals(walletModel.getStatusType().getName()))
            {
                TransactionModel transactionModel = 
                    TransactionModel.create(walletModel, transactionRequest);
                
                LOGGER.debug("transaction created with id - " + 
                                 transactionModel.getId());
                
                Double finalBalance = transactionModel.apply(walletModel);
                wallet.setBalance(finalBalance);
                
                Transaction transaction = transactionModel.getTransaction();
                LOGGER.debug("transaction successful with id - " + 
                                 transaction.getId());
                
                response.type(Constants.JSON_RESPONSE_TYPE);
                response.status(200);
                
                return new TransactionResponse(wallet, transaction);
            }
            else
            {
                String errorMessage = "wallet is not active.";
                LOGGER.error(errorMessage);
                throw new InvalidStateException(errorMessage);
            }
        }
    };
    
    public static Route getTransaction = new Route()
    {
        @Override
        public Object handle(Request request, Response response) 
            throws InvalidParamsException, InternalError, 
                   UnableToQueryException, DatabaseException, 
                   NoResourceFoundException
        {
            String walletId = request.params(WALLET_ID_KEY);
            String transactionId = request.params(TRANSACTION_ID_KEY);
            
            if (StringUtils.isBlank(walletId) || 
                StringUtils.isBlank(transactionId))
            {
                String errorMessage = 
                    "both walletId and transactionId required for " + 
                        "fetching transaction.";
                
                LOGGER.error(errorMessage);
                throw new InvalidParamsException(errorMessage);
            }
            
            WalletModel walletModel = WalletModel.getByWalletId(walletId);
            
            Wallet wallet = walletModel.getWallet();
            
            TransactionModel transactionModel = 
                TransactionModel.getByTransactionId(walletId, transactionId);
            
            Transaction transaction = transactionModel.getTransaction();
            
            response.type(Constants.JSON_RESPONSE_TYPE);
            response.status(200);
            
            return new TransactionResponse(wallet, transaction);
        }
    };
    
    public static Route listTransactions = new Route()
    {
        
        @Override
        public Object handle(Request request, Response response) 
            throws InvalidParamsException, InternalError, 
                   UnableToQueryException, DatabaseException, 
                   NoResourceFoundException
        {
            String walletId = request.params(WALLET_ID_KEY);
            Integer offset =
                Integer.parseInt(
                    request.queryParamOrDefault(
                        OFFSET_KEY, Constants.DEFAULT_OFFSET.toString()
                    )
                );
            Integer limit =
                Integer.parseInt(
                    request.queryParamOrDefault(
                        LIMIT_KEY, Constants.TRANSACTION_MAX_RESULT_SIZE.toString()
                    )
                );
            
            if (StringUtils.isBlank(walletId))
            {
                String errorMessage = 
                    "walletId is required for fetching transactions.";
                LOGGER.error(errorMessage);
                throw new InvalidParamsException(errorMessage);
            }
            else if (0 > offset)
            {
                String errorMessage = 
                    "offset must be non-negative for fetching transactions.";
                LOGGER.error(errorMessage);
                throw new InvalidParamsException(errorMessage);
            }
            else if (0 > limit || 
                     limit > Constants.TRANSACTION_MAX_RESULT_SIZE)
            {
                String errorMessage = 
                    String.format(
                        "limit must be between 0 to %d for fetching transactions.", 
                        Constants.TRANSACTION_MAX_RESULT_SIZE
                    );
                LOGGER.error(errorMessage);
                throw new InvalidParamsException(errorMessage);
            }
            
            WalletModel walletModel = WalletModel.getByWalletId(walletId);
            
            Wallet wallet = walletModel.getWallet();
            
            List<TransactionModel> transactionModels = 
                TransactionModel.
                    getTransactionsByWalletId(walletId, offset, limit);
            
            List<Transaction> transactions = new ArrayList<>();
            
            for (TransactionModel model : transactionModels)
            {
                transactions.add(model.getTransaction());
            }
            
            response.type(Constants.JSON_RESPONSE_TYPE);
            response.status(200);
            
            return new ListTransactionResponse(wallet, transactions);
        }
    };
    
    public static Route cancelTransaction = new Route()
    {
        @Override
        public Object handle(Request request, Response response) 
            throws InvalidParamsException, InvalidStateException, 
                   InternalError, UnableToQueryException, DatabaseException, 
                   NoResourceFoundException, TransactionFailedException
        {
            String walletId = request.params(WALLET_ID_KEY);
            String transactionId = request.params(TRANSACTION_ID_KEY);
            
            if (StringUtils.isBlank(walletId) || 
                StringUtils.isBlank(transactionId))
            {
                String errorMessage = 
                    "both walletId and transactionId required for " + 
                        "cancelling transaction.";
                
                LOGGER.error(errorMessage);
                throw new InvalidParamsException(errorMessage);
            }
            
            WalletModel walletModel = WalletModel.getByWalletId(walletId);
            
            if (WalletStatus.ACTIVE.name().
                    equals(walletModel.getStatusType().getName()))
            {
                Wallet wallet = walletModel.getWallet();
                
                TransactionModel transactionModel = 
                    TransactionModel.getByTransactionId(walletId, transactionId);
                
                TransactionStatus transactionStatus = 
                    TransactionStatus.valueOf(transactionModel.getStatusType().getName());
                
                if (TransactionStatus.NEW.equals(transactionStatus) ||
                    TransactionStatus.SUCCESSFUL.equals(transactionStatus))
                {
                    Double finalBalance = transactionModel.cancel(walletModel);
                    wallet.setBalance(finalBalance);
                    LOGGER.debug(
                        String.format("transaction#%s is cancelled successfully.", 
                                      transactionId)
                    );
                    
                    Transaction transaction = transactionModel.getTransaction();
                    response.type(Constants.JSON_RESPONSE_TYPE);
                    response.status(200);
                    return new TransactionResponse(wallet, transaction);
                }
                else
                {
                    String errorMessage = 
                        "transaction cannot be cancelled due to status - " + 
                            transactionStatus;
                    LOGGER.error(errorMessage);
                    throw new InvalidStateException(errorMessage);
                }
            }
            else
            {
                String errorMessage = "wallet is not active.";
                LOGGER.error(errorMessage);
                throw new InvalidStateException(errorMessage);
            }
        }
    };
}
