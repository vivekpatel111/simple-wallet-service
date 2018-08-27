package com.wallet.api;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wallet.api.data.Wallet;
import com.wallet.api.data.enums.WalletStatus;
import com.wallet.api.exception.DatabaseException;
import com.wallet.api.exception.InternalError;
import com.wallet.api.exception.InvalidParamsException;
import com.wallet.api.exception.NoResourceFoundException;
import com.wallet.api.exception.UnableToCreateException;
import com.wallet.api.exception.UnableToQueryException;
import com.wallet.api.exception.UnableToSaveException;
import com.wallet.api.requests.PostWalletRequest;
import com.wallet.api.response.WalletResponse;
import com.wallet.dbaccess.models.WalletModel;
import com.wallet.utils.Constants;

import spark.Request;
import spark.Response;
import spark.Route;

public class WalletApi
{
    private static final String WALLET_ID_KEY = "walletId";
    
    private static final Logger LOGGER =
        LoggerFactory.getLogger(WalletApi.class);
    
    public static Route createWallet = new Route()
    {
        private Gson gson = new Gson();
        
        @Override
        public Object handle(Request request, Response response) 
            throws JsonSyntaxException, InvalidParamsException, 
                   UnableToCreateException, InternalError, 
                   UnableToQueryException, UnableToSaveException, 
                   DatabaseException, NoResourceFoundException
        {
            String requestBody = request.body();
            
            LOGGER.info("Creating wallet with request - " + requestBody);
            PostWalletRequest createRequest = 
                gson.fromJson(requestBody, PostWalletRequest.class);
            
            createRequest.validateRequest();
            
            WalletModel walletModel = WalletModel.create(createRequest);
            
            Wallet wallet = walletModel.getWallet();
            
            response.type(Constants.JSON_RESPONSE_TYPE);
            response.status(200);
            
            return new WalletResponse(wallet);
        }
    };
    
    public static Route getWallet = new Route()
    {
        @Override
        public Object handle(Request request, Response response) 
            throws InvalidParamsException, InternalError, 
                   UnableToQueryException, DatabaseException, 
                   NoResourceFoundException
        {
            String walletId = request.params(WALLET_ID_KEY);
            LOGGER.info("Getting wallet info for - " + walletId);
            
            if (StringUtils.isBlank(walletId))
            {
                String errorMessage = 
                    "walletId is required for getting wallet info.";
                LOGGER.error(errorMessage);
                throw new InvalidParamsException(errorMessage);
            }
            
            WalletModel walletModel = WalletModel.getByWalletId(walletId);
            
            Wallet wallet = walletModel.getWallet();
            
            response.type(Constants.JSON_RESPONSE_TYPE);
            response.status(200);
            
            return new WalletResponse(wallet);
        }
    };
    
    public static Route deleteWallet = new Route()
    {
        @Override
        public Object handle(Request request, Response response) 
            throws InvalidParamsException, InternalError, 
                   UnableToQueryException, DatabaseException, 
                   NoResourceFoundException
        {
            String walletId = request.params(WALLET_ID_KEY);
            LOGGER.info("Deleting wallet info for - " + walletId);
            
            if (StringUtils.isBlank(walletId))
            {
                String errorMessage = 
                    "walletId is required for deleting wallet info.";
                LOGGER.error(errorMessage);
                throw new InvalidParamsException(errorMessage);
            }
            
            WalletModel walletModel = WalletModel.getByWalletId(walletId);
            
            if (WalletStatus.ACTIVE.name().
                    equals(walletModel.getStatusType().getName()))
            {
                walletModel.deactivate();
                LOGGER.debug("Deleted wallet info for - " + walletId);
            }
            else
            {
                LOGGER.debug(
                    String.format("wallet with id <%s> is already deleted.", 
                                  walletId)
                );
            }
            
            Wallet wallet = walletModel.getWallet();
            
            response.type(Constants.JSON_RESPONSE_TYPE);
            response.status(200);
            
            return new WalletResponse(wallet);
        }
    };
}
