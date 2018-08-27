package com.wallet.dbaccess;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.api.exception.DatabaseException;
import com.wallet.api.exception.InternalError;

public class HibernateUtil
{
    private static final Logger LOGGER =
        LoggerFactory.getLogger(HibernateUtil.class);
    
    private SessionFactory factory;
    private static HibernateUtil hibernateUtil;
    
    private HibernateUtil() 
        throws DatabaseException
    {
        // This will get file from resource directory.
        StandardServiceRegistry registry = 
            new StandardServiceRegistryBuilder().configure().build();
        try
        {
            // Create SessionFactory from standard hbm file.
            factory = new MetadataSources(registry).buildMetadata().
                           buildSessionFactory();
        } 
        catch (Exception e)
        {
            // The registry would be destroyed by the SessionFactory,
            // but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw new DatabaseException(e.getMessage(), e);
        }
    }
    
    public static void init()
        throws DatabaseException
    {
        if (null == hibernateUtil)
        {
            hibernateUtil = new HibernateUtil();
        }
    }
    
    public synchronized SessionFactory getFactory()
    {
        return factory;
    }
    
    public static synchronized HibernateUtil getInstance() 
        throws InternalError
    {
        if (null == hibernateUtil)
        {
            String errorMessage = "hibernateUtil is not initialized yet.";
            LOGGER.debug(errorMessage);
            throw new InternalError(errorMessage);
        }
        
        return hibernateUtil;
    }
}
