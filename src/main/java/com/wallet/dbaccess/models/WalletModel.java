package com.wallet.dbaccess.models;

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

import com.wallet.api.data.Wallet;
import com.wallet.api.data.enums.WalletStatus;
import com.wallet.api.exception.DatabaseException;
import com.wallet.api.exception.NoResourceFoundException;
import com.wallet.api.exception.UnableToQueryException;
import com.wallet.api.exception.UnableToSaveException;
import com.wallet.api.requests.PostWalletRequest;
import com.wallet.utils.Constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Entity
@Table(name = "wallets")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(exclude = { "walletType", "statusType"})
public class WalletModel extends BaseDbModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;
    
    @Column(name = "wallet_id")
    protected String walletId;
    
    @Column(name = "wallet_type_id")
    protected Long walletTypeId;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="wallet_type_id", insertable=false, 
                updatable=false, referencedColumnName="id")
    protected WalletTypeModel walletType;
    
    @Column(name = "status_type_id")
    protected Long statusTypeId;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="status_type_id", insertable=false, 
                updatable=false, referencedColumnName="id")
    protected WalletStatusTypeModel statusType;
    
    @Column(name = "balance", insertable=false, updatable=true)
    protected Double balance;
    
    public static WalletModel getById(Long id) 
        throws UnableToQueryException, DatabaseException, 
               NoResourceFoundException
    {
        WalletModel wallet = null;
        LOGGER.info("Fetching wallet with id - " + id);
        String queryString = 
            "from WalletModel where id = :id and isDeleted = 0";
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("id", id);
        
        List<WalletModel> result = 
            queryDatabase(queryString, queryParams);
        
        if (null != result && !result.isEmpty())
        {
            wallet = result.get(0);
        }
        else
        {
            String errorMessage = "No wallet exists with id - " + id;
            LOGGER.error(errorMessage);
            throw new NoResourceFoundException(errorMessage);
        }
        
        return wallet;
    }
    
    public static WalletModel getByWalletId(String walletId) 
        throws UnableToQueryException, DatabaseException, 
               NoResourceFoundException 
    {
        WalletModel wallet = null;
        LOGGER.info("Fetching wallet with walletId - " + wallet);
        String queryString = 
            "from WalletModel where walletId = :walletId and isDeleted = 0";
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("walletId", walletId);
        
        List<WalletModel> result = 
            queryDatabase(queryString, queryParams);
        
        if (null != result && !result.isEmpty())
        {
            wallet = result.get(0);
        }
        else
        {
            String errorMessage = "No wallet exists with walletId - " + walletId;
            LOGGER.error(errorMessage);
            throw new NoResourceFoundException(errorMessage);
        }
        
        return wallet;
    }

    public Wallet getWallet() 
        throws UnableToQueryException, DatabaseException, 
               NoResourceFoundException 
    {
        WalletModel walletModel = getById(id);
        
        String walletType = walletModel.getWalletType().getName();
        String statusType = walletModel.getStatusType().getName();
        
        return Wallet.builder().
                   id(walletModel.getWalletId()).
                   balance(walletModel.getBalance()).
                   type(walletType).
                   status(statusType).
                   build();
    }

    public static WalletModel create(PostWalletRequest createRequest) 
        throws UnableToQueryException, UnableToSaveException, 
               DatabaseException 
    {
        WalletModel wallet = null;
        LOGGER.info("Creating wallet on request - " + createRequest);
        WalletTypeModel walletTypeModel = 
            WalletTypeModel.getByName(createRequest.getType().name());
        
        WalletStatusTypeModel statusTypeModel = 
            WalletStatusTypeModel.
                getByName(Constants.DEFAULT_WALLET_STATUS_TYPE);
        
        wallet = createModel(walletTypeModel, statusTypeModel);
        
        saveData(wallet);
        
        return wallet;
    }
    
    protected static WalletModel createModel(
        WalletTypeModel walletTypeModel,
        WalletStatusTypeModel statusTypeModel
    )
    {
        WalletModel wallet = new WalletModel();
        wallet.setWalletTypeId(walletTypeModel.getId());
        wallet.setStatusTypeId(statusTypeModel.getId());
        
        return wallet;
    }

    public void deactivate() 
        throws UnableToQueryException, UnableToSaveException, 
               DatabaseException 
    {
        LOGGER.info("Deactivating wallet with id - " + this.id);
        
        WalletStatusTypeModel statusTypeModel = 
            WalletStatusTypeModel.
                getByName(WalletStatus.INACTIVE.name());
        
        this.setStatusTypeId(statusTypeModel.getId());
        updateData(this);
        
        LOGGER.debug("Deactivatated wallet with id - " + this.id);
    }
}
