package com.wallet.dbaccess.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.wallet.api.data.Transaction;
import com.wallet.api.data.enums.TransactionStatus;
import com.wallet.api.data.enums.TransactionType;
import com.wallet.api.exception.DatabaseException;
import com.wallet.api.exception.InternalError;
import com.wallet.api.exception.NoResourceFoundException;
import com.wallet.api.exception.TransactionFailedException;
import com.wallet.api.exception.UnableToQueryException;
import com.wallet.api.exception.UnableToSaveException;
import com.wallet.api.requests.PostTransactionRequest;
import com.wallet.dbaccess.HibernateUtil;
import com.wallet.utils.Constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Entity
@Table(name = "transactions")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(exclude = { "transactionType", "statusType"})
public class TransactionModel extends BaseDbModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;
    
    @Column(name = "transaction_id")
    protected String transactionId;
    
    @Column(name = "wallet_id")
    protected String walletId;
    
    @Column(name = "transaction_type_id")
    protected Long transactionTypeId;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="transaction_type_id", insertable=false, 
                updatable=false, referencedColumnName="id")
    protected TransactionTypeModel transactionType;
    
    @Column(name = "status_type_id")
    protected Long statusTypeId;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="status_type_id", insertable=false, 
                updatable=false, referencedColumnName="id")
    protected TransactionStatusTypeModel statusType;
    
    @Column(name = "amount")
    protected Double amount;
    
    @Column(name = "log_message")
    protected String logMessage;

    public static TransactionModel getById(Long id) 
        throws UnableToQueryException, DatabaseException, 
               NoResourceFoundException
    {
        TransactionModel transaction = null;
        LOGGER.info("Fetching transaction with id - " + id);
        String queryString = 
            "from TransactionModel where id = :id and isDeleted = 0";
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("id", id);
        
        List<TransactionModel> result = 
            queryDatabase(queryString, queryParams);
        
        if (null != result && !result.isEmpty())
        {
            transaction = result.get(0);
        }
        else
        {
            String errorMessage = "No transaction exists with id - " + id;
            LOGGER.error(errorMessage);
            throw new NoResourceFoundException(errorMessage);
        }
        
        LOGGER.debug(
            String.format("Fetched transaction with id - %s = %s", 
                          id, transaction)
        );
        
        return transaction;
    }
    
    public static TransactionModel 
    getByTransactionId(String walletId, String transactionId) 
        throws UnableToQueryException, DatabaseException, 
               NoResourceFoundException
    {
        TransactionModel transaction = null;
        LOGGER.info(
            String.format(
                "Fetching transaction with id - %s with walletId - %s", 
                transactionId, walletId
            )
        );
        String queryString = 
            "from TransactionModel where " + 
                "transactionId = :transactionId and " + 
                "walletId = :walletId and isDeleted = 0";
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("transactionId", transactionId);
        queryParams.put("walletId", walletId);
        
        List<TransactionModel> result = 
            queryDatabase(queryString, queryParams);
        
        if (null != result && !result.isEmpty())
        {
            transaction = result.get(0);
        }
        else
        {
            String errorMessage = 
                String.format(
                    "No transaction exists with id - %s with walletId - %s", 
                    transactionId, walletId
                );
            LOGGER.error(errorMessage);
            throw new NoResourceFoundException(errorMessage);
        }
        
        LOGGER.debug(
            String.format(
                "Fetched transaction with id - %s with walletId - %s = %s", 
                transactionId, walletId, transaction
            )
        );
        return transaction;
    }

    public Transaction getTransaction() 
        throws DatabaseException, InternalError, NoResourceFoundException
    {
        TransactionModel transactionModel = getById(id);
        
        String transactionType = 
            transactionModel.getTransactionType().getName();
        String statusType = 
            transactionModel.getStatusType().getName();
        
        return Transaction.builder().
                   id(transactionModel.getTransactionId()).
                   amount(transactionModel.getAmount()).
                   type(transactionType).
                   status(statusType).
                   transactionDate(transactionModel.getCreationDate()).
                   message(transactionModel.getLogMessage()).
                   build();
    }

    public static TransactionModel create(
        WalletModel walletModel, PostTransactionRequest transactionRequest
    ) 
        throws UnableToQueryException, UnableToSaveException, 
               DatabaseException 
    {
        TransactionModel transaction = null;
        LOGGER.info(
            String.format(
                "Creating transaction in wallet (%s) on request - %s", 
                walletModel, transactionRequest
            )
        );
        
        TransactionTypeModel transactionTypeModel = 
            TransactionTypeModel.getByName(transactionRequest.getType().name());
        
        TransactionStatusTypeModel statusTypeModel = 
            TransactionStatusTypeModel.
                getByName(Constants.DEFAULT_TRANSACTION_STATUS_TYPE);
        
        transaction = 
            getModel(walletModel.getWalletId(), transactionTypeModel, 
                     statusTypeModel, transactionRequest.getAmount(),
                     Constants.NEW_TRANSACTION_MESSAGE);
        
        saveData(transaction);
        
        return transaction;
    }
    
    private static TransactionModel getModel(
        String walletId, 
        TransactionTypeModel transactionTypeModel,
        TransactionStatusTypeModel statusTypeModel,
        Double amount, String logMessage
    )
    {
        TransactionModel transaction = new TransactionModel();
        transaction.setWalletId(walletId);
        transaction.setTransactionTypeId(transactionTypeModel.getId());
        transaction.setStatusTypeId(statusTypeModel.getId());
        transaction.setAmount(amount);
        transaction.setLogMessage(logMessage);
        
        return transaction;
    }

    public Double cancel(WalletModel walletModel) 
        throws UnableToQueryException, UnableToSaveException, 
               DatabaseException, InternalError, NoResourceFoundException, 
               TransactionFailedException 
    {
        TransactionModel transaction = getById(this.id);
        LOGGER.info(
            String.format("Cancelling transaction with id - %s", 
                          transaction.getTransactionId())
        );
        
        WalletTypeModel walletType = walletModel.getWalletType();
        Double currentBalance = walletModel.getBalance();
        Double transactionAmount = 
            getTransactionAmount(transaction.getTransactionType());
        
        Double finalAmount = currentBalance - transactionAmount;
        Double minimumAccountBalance = walletType.getMinimumBalance();
        Boolean updateWallet = false;
        
        TransactionStatus transactionStatus = 
            TransactionStatus.valueOf(transaction.getStatusType().getName());
        
        String logMessage = null;
        
        if (TransactionStatus.SUCCESSFUL.equals(transactionStatus))
        {
            // revert balance for only successful transaction
            updateWallet = true;
            logMessage = Constants.REVERT_TRANSACTION_MESSAGE;
        }
        else
        {
            finalAmount = currentBalance;
            logMessage = Constants.CANCEL_TRANSACTION_MESSAGE;
        }
        
        LOGGER.debug(
            String.format(
                "Wallet balance = %f\nTransaction Amount = %f\n" +
                    "Minimum required balance = %f",
                currentBalance, transactionAmount, minimumAccountBalance
            )
        );
        if (minimumAccountBalance <= finalAmount)
        {
            LOGGER.debug("Cancelling transaction...");
            TransactionStatusTypeModel statusTypeModel = 
                TransactionStatusTypeModel.
                    getByName(TransactionStatus.CANCELLED.name());
            
            try
            {
                Session session = 
                    HibernateUtil.getInstance().getFactory().openSession();
                session.beginTransaction();
                
                this.setStatusTypeId(statusTypeModel.getId());
                this.setLogMessage(logMessage);
                session.update(this);
                
                if (updateWallet)
                {
                    walletModel.setBalance(finalAmount);
                    session.update(walletModel);
                }
                
                session.getTransaction().commit();
                session.close();
            } 
            catch (HibernateException e)
            {
                throw new UnableToSaveException(e.getMessage(), e);
            }
        }
        else
        {
            String errorMessage = 
                "Insufficient balance for cancelling transaction.";
            LOGGER.error(errorMessage);
            throw new TransactionFailedException(errorMessage);
        }
        
        return finalAmount;
    }

    public static List<TransactionModel> 
    getTransactionsByWalletId(String walletId, Integer offset, Integer limit) 
        throws UnableToQueryException, DatabaseException 
    {
        List<TransactionModel> transactions = new ArrayList<>();
        LOGGER.info("Fetching transactions for walletId - " + walletId);
        String queryString = 
            "from TransactionModel where " + 
                "walletId = :walletId and isDeleted = 0 " +
                "order by creationDate desc";
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("walletId", walletId);
        
        List<TransactionModel> result = 
            queryDatabase(queryString, queryParams, offset, limit);
        
        if (null != result && !result.isEmpty())
        {
            transactions = result;
        }
        
        LOGGER.debug(
            String.format(
                "Fetched transactions for walletId - %s with count - %d", 
                walletId, transactions.size()
            )
        );
        return transactions;
    }

    public Double apply(WalletModel walletModel) 
        throws UnableToQueryException, UnableToSaveException, 
               DatabaseException, InternalError, NoResourceFoundException
    {
        TransactionModel transaction = getById(this.id);
        LOGGER.info(
            String.format("Applying transaction - (%s) on wallet - (%s)", 
                          transaction, walletModel)
        );
        WalletTypeModel walletType = walletModel.getWalletType();
        Double currentBalance = walletModel.getBalance();
        Double transactionAmount = getTransactionAmount(transaction.getTransactionType());
        
        Double finalAmount = currentBalance + transactionAmount;
        Double minimumAccountBalance = walletType.getMinimumBalance();
        
        TransactionStatus transactionStatus = 
            TransactionStatus.valueOf(transaction.getStatusType().getName());
        
        Boolean updateWallet = false;
        String logMessage = null;
        LOGGER.debug(
            String.format(
                "Wallet balance = %f\nTransaction Amount = %f\n" +
                    "Minimum required balance = %f",
                currentBalance, transactionAmount, minimumAccountBalance
            )
        );
        if (minimumAccountBalance <= finalAmount)
        {
            LOGGER.info("Transaction successful - " + transaction.id);
            transactionStatus = TransactionStatus.SUCCESSFUL;
            updateWallet = true;
            logMessage = Constants.SUCCESS_TRANSACTION_MESSAGE;
        }
        else
        {
            LOGGER.info("Transaction failed - " + transaction.id);
            finalAmount = currentBalance;
            transactionStatus = TransactionStatus.FAILED;
            logMessage = Constants.FAILED_TRANSACTION_MESSAGE_BALANCE;
        }
        
        TransactionStatusTypeModel statusTypeModel = 
            TransactionStatusTypeModel.getByName(transactionStatus.name());
        
        try
        {
            Session session = HibernateUtil.getInstance().getFactory().openSession();
            session.beginTransaction();
            
            if (updateWallet)
            {
                walletModel.setBalance(finalAmount);
                session.update(walletModel);
            }
            
            transaction.setStatusTypeId(statusTypeModel.getId());
            transaction.setLogMessage(logMessage);
            session.update(transaction);
            
            session.getTransaction().commit();
            session.close();
            LOGGER.info("Transaction applied successfully - " + transaction.id);
        } 
        catch (HibernateException e)
        {
            LOGGER.info("Transaction failed - " + transaction.id, e);
        } 
        
        return finalAmount;
    }

    private Double getTransactionAmount(TransactionTypeModel transactionTypeModel)
    {
        Double amount = this.getAmount();
        TransactionType transactionType = 
            TransactionType.valueOf(transactionTypeModel.getName());
        
        switch (transactionType)
        {
            case CREDIT:
                //keep it as it is
                break;
            case DEBIT:
                //will be negated from balance
                amount *= -1;
                break;
        }
        
        return amount;
    }
}
