package com.wallet.dbaccess.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.wallet.api.exception.DatabaseException;
import com.wallet.api.exception.InternalError;
import com.wallet.api.exception.UnableToQueryException;
import com.wallet.dbaccess.HibernateUtil;
import com.wallet.utils.CacheUtils;
import com.wallet.utils.Constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "transaction_types")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionTypeModel extends BaseDbModel
{
    private static final CacheUtils<Long, TransactionTypeModel> idToDataCache = 
        new CacheUtils<>(Constants.CACHE_TTL_IN_MS);
    private static final CacheUtils<String, TransactionTypeModel> nameToDataCache = 
        new CacheUtils<>(Constants.CACHE_TTL_IN_MS);
    
    @Id
    @GeneratedValue
    @Column(name = "id")
    protected Long id;
    
    @Column(name = "name")
    protected String name;
    
    public static TransactionTypeModel getByName(String name) 
        throws UnableToQueryException, DatabaseException 
    {
        LOGGER.info("Fetching transaction type with name - " + name);
        TransactionTypeModel transactionTypeModel = 
            nameToDataCache.get(name);
        
        if (null == transactionTypeModel)
        {
            String queryString = 
                "from TransactionTypeModel where name = :name and isDeleted = 0";
            
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("name", name);
            
            List<TransactionTypeModel> result = 
                queryDatabase(queryString, queryParams);
            
            if (null != result && !result.isEmpty())
            {
                transactionTypeModel = result.get(0);
                
                nameToDataCache.put(name, transactionTypeModel);
            }
        }
        
        LOGGER.debug("Fetched transaction type - " + transactionTypeModel);
        
        return transactionTypeModel;
    }

    public static TransactionTypeModel getById(Long id) 
        throws UnableToQueryException, DatabaseException
    {
        LOGGER.info("Fetching transaction type with name - " + id);
        TransactionTypeModel transactionTypeModel = 
            idToDataCache.get(id);
        
        if (null == transactionTypeModel)
        {
            String queryString = 
                "from TransactionTypeModel where id = :id and isDeleted = 0";
            
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", id);
            
            List<TransactionTypeModel> result = 
                queryDatabase(queryString, queryParams);
            
            if (null != result && !result.isEmpty())
            {
                transactionTypeModel = result.get(0);
                
                idToDataCache.put(id, transactionTypeModel);
            }
        }
        
        LOGGER.debug("Fetched transaction type - " + transactionTypeModel);
        
        return transactionTypeModel;
    }
}
