package com.ozonetel.occ.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Generic Manager that talks to GenericDao to CRUD POJOs.
 *
 * <p>Extend this interface if you want typesafe (no casting necessary) managers
 * for your domain objects.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * @param <T> a type variable
 * @param <PK> the primary key for that type
 */
public interface GenericManager<T, PK extends Serializable> {

    /**
     * Generic method used to get all objects of a particular type. This
     * is the same as lookup up all rows in a table.
     * @return List of populated objects
     */
    List<T> getAll();
    
    
    List<T> getAllDistinct();

    /**
     * Generic method to get an object based on class and identifier. An
     * ObjectRetrievalFailureException Runtime Exception is thrown if
     * nothing is found.
     *
     * @param id the identifier (primary key) of the object to get
     * @return a populated object
     * @see org.springframework.orm.ObjectRetrievalFailureException
     */
    T get(PK id);

    /**
     * Checks for existence of an object of type T using the id arg.
     * @param id the identifier (primary key) of the object to get
     * @return - true if it exists, false if it doesn't
     */
    boolean exists(PK id);

    /**
     * Generic method to save an object - handles both update and insert.
     * @param object the object to save
     * @return the updated object
     */
    T save(T object);

    /**
     * Generic method to delete an object based on class and id
     * @param id the identifier (primary key) of the object to remove
     */
    void remove(PK id);

    List<T> findByNamedQuery(String queryName, Map<String, Object> queryParams);
    List<T> findByLimitQuery(String queryName, Map<String, Object> queryParams,int limit);

    /**
     * Find a list of records by using a named params
     * @param queryString query of the sql params query
     * @param queryParams a map of the query names and the values
     * @return a list of the records found
     */
    List<T> findByNamedParams(String queryString, Map<String, Object> queryParams);

    /**
     * Generic method used to update bulk of objects of a particular query.
     * @param query is query of the bulk update
     * @param values is array of values and condition values
     * @return int no.of records updated
     */
    int bulkUpdate(String query, Object[] values);

    /**
     * Generic method used to save or update collection of objects of type T.
     * @param entities is  Collection of T Objects
     */
    void saveAll(Collection<T> entities);

    List<Map<String, Object>> executeProcedure(String query, Map<String,Object> queryParams);
}
