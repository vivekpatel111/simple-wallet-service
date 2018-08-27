package com.wallet.dbaccess.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.api.exception.DatabaseException;
import com.wallet.api.exception.UnableToQueryException;
import com.wallet.api.exception.UnableToSaveException;
import com.wallet.dbaccess.HibernateUtil;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter(AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BaseDbModel implements Serializable
{
    protected static final Logger LOGGER =
        LoggerFactory.getLogger(BaseDbModel.class);
    
    @Column(name="is_deleted", insertable=false, updatable=false)
    protected Boolean isDeleted;
    
    @Column(name="creation_date", insertable=false, updatable=false)
    protected Date creationDate;
    
    @Column(name="last_update", insertable=false, updatable=false)
    protected Date lastUpdate;
    
    @Column(name="last_updated_by", insertable=false, updatable=false)
    protected String lastUpdatedBy;
    
    @SuppressWarnings("rawtypes")
    protected static List queryDatabase(String queryString, 
                                        Map<String, Object> queryParams) 
        throws UnableToQueryException, DatabaseException
    {
        return queryDatabase(queryString, queryParams, 
                             null /*offset*/, null /*limit*/);
    }
    
    @SuppressWarnings("rawtypes")
    protected static List queryDatabase(String queryString, 
                                        Map<String, Object> queryParams,
                                        Integer offset, Integer limit) 
        throws UnableToQueryException, DatabaseException
    {
        List result = null;
        LOGGER.debug(
            String.format(
                "Fetching data from query string - [%s] with " + 
                    "params - %s at offset %s with limit %s", 
                queryString, queryParams, offset, limit
            )
        );
        Session session = null;
        try
        {
            session = 
                HibernateUtil.getInstance().getFactory().openSession();
            
            session.beginTransaction();
            Query query = session.createQuery(queryString);
            
            if (null != offset)
            {
                query.setFirstResult(offset);
            }
            
            if (null != limit)
            {
                query.setMaxResults(limit);
            }
            
            if (null != queryParams)
            {
                for (Entry<String, Object> entry : queryParams.entrySet())
                {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            
            result = query.list();
            
            session.getTransaction().commit();
            session.close();
        }
        catch(HibernateException e)
        {
            LOGGER.error("HibernateException while fetching data", e);
            throw new DatabaseException(e.getMessage(), e);
        }
        catch(Exception e)
        {
            LOGGER.error("Exception while fetching data", e);
            if (null != session)
            {
                session.getTransaction().rollback();
            }
            
            throw new UnableToQueryException(e.getMessage(), e);
        }
        finally 
        {
            if (null != session)
            {
                session.close();
            }
        }
        
        return result;
    }
    
    protected static void saveData(Object object) 
        throws UnableToSaveException, DatabaseException
    {
        Session session = null;
        LOGGER.debug("Saving object to database - " + object);
        try
        {
            session = 
                HibernateUtil.getInstance().getFactory().openSession();
            
            session.beginTransaction();
            session.save(object);
            
            session.getTransaction().commit();
            session.close();
        }
        catch(HibernateException e)
        {
            LOGGER.error("HibernateException while saving data", e);
            throw new DatabaseException(e.getMessage(), e);
        }
        catch(Exception e)
        {
            LOGGER.error("Exception while saving data", e);
            if (null != session)
            {
                session.getTransaction().rollback();
            }
            
            throw new UnableToSaveException(e.getMessage(), e);
        }
        finally 
        {
            if (null != session)
            {
                session.close();
            }
        }
    }
    
    protected static void updateData(Object object) 
        throws UnableToSaveException, DatabaseException
    {
        Session session = null;
        LOGGER.debug("Updating object to database - " + object);
        try
        {
            session = 
                HibernateUtil.getInstance().getFactory().openSession();
            
            session.beginTransaction();
            session.update(object);
            
            session.getTransaction().commit();
            session.close();
        }
        catch(HibernateException e)
        {
            LOGGER.error("HibernateException while updating data", e);
            throw new DatabaseException(e.getMessage(), e);
        }
        catch(Exception e)
        {
            LOGGER.error("Exception while updating data", e);
            if (null != session)
            {
                session.getTransaction().rollback();
            }
            
            throw new UnableToSaveException(e.getMessage(), e);
        }
        finally 
        {
            if (null != session)
            {
                session.close();
            }
        }
    }
}
