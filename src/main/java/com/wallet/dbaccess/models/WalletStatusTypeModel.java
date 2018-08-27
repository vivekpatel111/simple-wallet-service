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
@Table(name = "wallet_status_types")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WalletStatusTypeModel extends BaseDbModel
{
    private static final CacheUtils<String, WalletStatusTypeModel> nameToDataCache = 
        new CacheUtils<>(Constants.CACHE_TTL_IN_MS);
    private static final CacheUtils<Long, WalletStatusTypeModel> idToDataCache = 
        new CacheUtils<>(Constants.CACHE_TTL_IN_MS);
    
    @Id
    @GeneratedValue
    @Column(name = "id")
    protected Long id;
    
    @Column(name = "name")
    protected String name;
    
    public static WalletStatusTypeModel getByName(String name) 
        throws UnableToQueryException, DatabaseException
    {
        LOGGER.info("Fetching wallet status with name - " + name);
        WalletStatusTypeModel statusType = nameToDataCache.get(name);
        
        if (null == statusType)
        {
            String queryString = 
                "from WalletStatusTypeModel where name = :name and isDeleted = 0";
            
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("name", name);
            
            List<WalletStatusTypeModel> result = 
                queryDatabase(queryString, queryParams);
            
            if (null != result && !result.isEmpty())
            {
                statusType = result.get(0);
                
                nameToDataCache.put(name, statusType);
            }
        }
        
        LOGGER.debug("Fetched wallet status - " + statusType);
        
        return statusType;
    }

    public static WalletStatusTypeModel getById(Long statusTypeId) 
        throws UnableToQueryException, DatabaseException 
    {
        LOGGER.info("Fetching wallet status with id - " + statusTypeId);
        WalletStatusTypeModel statusType = idToDataCache.get(statusTypeId);
        
        if (null == statusType)
        {
            String queryString = 
                "from WalletStatusTypeModel where id = :id and isDeleted = 0";
            
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", statusTypeId);
            
            List<WalletStatusTypeModel> result = 
                queryDatabase(queryString, queryParams);
            
            if (null != result && !result.isEmpty())
            {
                statusType = result.get(0);
                
                idToDataCache.put(statusTypeId, statusType);
            }
        }
        
        LOGGER.debug("Fetched wallet status - " + statusType);
        
        return statusType;
    }
}
