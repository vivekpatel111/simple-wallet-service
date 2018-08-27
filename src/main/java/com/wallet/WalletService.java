package com.wallet;

import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.wallet.api.JsonTransformer;
import com.wallet.api.WalletApi;
import com.wallet.api.WalletTransactionApi;
import com.wallet.api.exception.ApiException;
import com.wallet.api.exception.InternalError;
import com.wallet.api.response.ErrorResponse;
import com.wallet.dbaccess.HibernateUtil;
import com.wallet.utils.Constants;

public class WalletService
{
    private static final Logger LOGGER =
        LoggerFactory.getLogger(WalletService.class);
    
    public static void main(String[] args) throws Exception 
    {
        LOGGER.info("Starting application...");
        Integer servicePort = parseArguments(args);

        LOGGER.info("Initilizing hibernate...");
        HibernateUtil.init();
        
        LOGGER.info("Starting spark service to port number -" + servicePort);
        port(servicePort);
        
        LOGGER.info("Initilizing spark...");
        path("/api", () -> 
        {
            path("/wallet", () ->
            {
                post("/", WalletApi.createWallet, new JsonTransformer());
                
                get("/:walletId", WalletApi.getWallet, new JsonTransformer());
                
                delete("/:walletId", 
                       WalletApi.deleteWallet, 
                       new JsonTransformer());
                
                get("/:walletId/transaction", 
                    WalletTransactionApi.listTransactions, 
                    new JsonTransformer());
                
                post("/:walletId/transaction", 
                     WalletTransactionApi.createTransaction, 
                     new JsonTransformer());
                
                get("/:walletId/transaction/:transactionId", 
                    WalletTransactionApi.getTransaction, 
                    new JsonTransformer());
                
                delete("/:walletId/transaction/:transactionId", 
                       WalletTransactionApi.cancelTransaction, 
                       new JsonTransformer());
            });
        });
        
        exception(ApiException.class, (exception, request, response) -> {
            response.type(Constants.JSON_RESPONSE_TYPE);
            response.status(exception.getResponseCode());
            
            ErrorResponse error = 
                new ErrorResponse(exception.getMessage(), 
                                  exception.getClass().getSimpleName());
            
            response.body(new Gson().toJson(error));
        });
        
        exception(Exception.class, (exception, request, response) -> {
            response.type(Constants.JSON_RESPONSE_TYPE);
            
            InternalError internalError = 
                new InternalError(exception.getMessage());
            
            response.status(internalError.getResponseCode());
            
            ErrorResponse error = 
                new ErrorResponse(internalError.getMessage(), 
                                  internalError.getClass().getSimpleName());
            
            response.body(new Gson().toJson(error));
        });
        
        notFound((request, response) -> {
            response.type("application/json");
            response.status(404);
            
            ErrorResponse error = 
                new ErrorResponse("This path is not implemented yet.", 
                          "NoResourceFoundException");
            
            return new Gson().toJson(error);
        });
    }

    private static Integer parseArguments(String[] args)
        throws ParseException
    {
        Integer port = Constants.DEFAULT_APP_PORT;

        Options options = new Options();
        options.addOption("port", true, "port for service");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("port"))
        {
            port = Integer.parseInt(cmd.getOptionValue("port"));
        }

        return port;
    }
}
