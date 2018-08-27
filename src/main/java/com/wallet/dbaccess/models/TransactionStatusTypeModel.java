package com.wallet.dbaccess.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.wallet.api.exception.DatabaseException;
import com.wallet.api.exception.UnableToQueryException;
import com.wallet.utils.CacheUtils;
import com.wallet.utils.Constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "transaction_status_types")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionStatusTypeModel extends BaseDbModel
{
    private static final CacheUtils<Long, TransactionStatusTypeModel> 
    idToDataCache = new CacheUtils<>(Constants.CACHE_TTL_IN_MS);
    private static final CacheUtils<String, TransactionStatusTypeModel> 
    nameToDataCache = new CacheUtils<>(Constants.CACHE_TTL_IN_MS);
    
    
    @Id
    @GeneratedValue
    @Column(name = "id")
    protected Long id;
    
    @Column(name = "name")
    protected String name;
    
    public static TransactionStatusTypeModel getByName(String name) 
        throws UnableToQueryException, DatabaseException
    {
        LOGGER.info("Fetching transaction status with name - " + name);
        TransactionStatusTypeModel transactionStatusTypeModel = 
            nameToDataCache.get(name);
        
        if (null == transactionStatusTypeModel)
        {
            String queryString = 
                "from TransactionStatusTypeModel where name = :name and isDeleted = 0";
            
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("name", name);
            
            List<TransactionStatusTypeModel> result = 
                queryDatabase(queryString, queryParams);
            
            if (null != result && !result.isEmpty())
            {
                transactionStatusTypeModel = result.get(0);
                
                nameToDataCache.put(name, transactionStatusTypeModel);
            }
        }
        
        LOGGER.debug("Fetched transaction status - " + transactionStatusTypeModel);
        
        return transactionStatusTypeModel;
    }

    public static TransactionStatusTypeModel getById(Long id) 
        throws UnableToQueryException, DatabaseException 
    {
        LOGGER.info("Fetching transaction status with id - " + id);
        TransactionStatusTypeModel transactionStatusTypeModel = 
            idToDataCache.get(id);
        
        if (null == transactionStatusTypeModel)
        {
            String queryString = 
                "from TransactionStatusTypeModel where id = :id and isDeleted = 0";
            
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", id);
            
            List<TransactionStatusTypeModel> result = 
                queryDatabase(queryString, queryParams);
            
            if (null != result && !result.isEmpty())
            {
                transactionStatusTypeModel = result.get(0);
                
                idToDataCache.put(id, transactionStatusTypeModel);
            }
        }
        
        LOGGER.debug("Fetched transaction status - " + transactionStatusTypeModel);
        
        return transactionStatusTypeModel;
    }
}
