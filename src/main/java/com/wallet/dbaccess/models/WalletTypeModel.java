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
@Table(name = "wallet_types")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WalletTypeModel extends BaseDbModel
{
    private static final CacheUtils<Long, WalletTypeModel> idToDataCache = 
        new CacheUtils<>(Constants.CACHE_TTL_IN_MS);
    private static final CacheUtils<String, WalletTypeModel> nameToDataCache = 
        new CacheUtils<>(Constants.CACHE_TTL_IN_MS);
    
    @Id
    @GeneratedValue
    @Column(name = "id")
    protected Long id;
    
    @Column(name = "name")
    protected String name;
    
    @Column(name = "minimum_balance")
    protected Double minimumBalance;
    
    public static WalletTypeModel getByName(String name) 
        throws UnableToQueryException, DatabaseException
    {
        LOGGER.info("Fetching wallet type with name - " + name);
        WalletTypeModel walletType = nameToDataCache.get(name);
        
        if (null == walletType)
        {
            String queryString = 
                "from WalletTypeModel where name = :name and isDeleted = 0";
            
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("name", name);
            
            List<WalletTypeModel> result = 
                queryDatabase(queryString, queryParams);
            
            if (null != result && !result.isEmpty())
            {
                walletType = result.get(0);
                
                nameToDataCache.put(name, walletType);
            }
        }
        
        LOGGER.debug("Fetched wallet type - " + walletType);
        
        return walletType;
    }

    public static WalletTypeModel getById(Long id) 
        throws UnableToQueryException, DatabaseException
    {
        LOGGER.info("Fetching wallet type with id - " + id);
        WalletTypeModel walletType = idToDataCache.get(id);
        
        if (null == walletType)
        {
            String queryString = 
                "from WalletTypeModel where id = :id and isDeleted = 0";
            
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", id);
            
            List<WalletTypeModel> result = 
                queryDatabase(queryString, queryParams);
            
            if (null != result && !result.isEmpty())
            {
                walletType = result.get(0);
                
                idToDataCache.put(id, walletType);
            }
        }
        
        LOGGER.debug("Fetched wallet type - " + walletType);
        
        return walletType;
    }
}
