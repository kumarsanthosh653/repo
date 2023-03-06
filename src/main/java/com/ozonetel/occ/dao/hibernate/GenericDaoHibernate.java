package com.ozonetel.occ.dao.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ozonetel.occ.dao.GenericDao;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * This class serves as the Base class for all other DAOs - namely to hold
 * common CRUD methods that they might all use. You should only need to extend
 * this class when your require custom CRUD logic.
 *
 * <p>
 * To register this class in your Spring context file, use the following XML.
 * <pre>
 *      &lt;bean id="fooDao" class="com.ozonetel.occ.dao.hibernate.GenericDaoHibernate"&gt;
 *          &lt;constructor-arg value="com.ozonetel.occ.model.Foo"/&gt;
 *          &lt;property name="sessionFactory" ref="sessionFactory"/&gt;
 *      &lt;/bean&gt;
 * </pre>
 *
 * @author <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 * @param <T> a type variable
 * @param <PK> the primary key for that type
 */
public class GenericDaoHibernate<T, PK extends Serializable> extends HibernateDaoSupport implements GenericDao<T, PK> {

    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass())
     * from Commons Logging
     */
    protected final Log log = LogFactory.getLog(getClass());
    private Class<T> persistentClass;

    /**
     * Constructor that takes in a class to see which type of entity to persist
     *
     * @param persistentClass the class type you'd like to persist
     */
    public GenericDaoHibernate(final Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        return super.getHibernateTemplate().loadAll(this.persistentClass);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> getAllDistinct() {
        Collection result = new LinkedHashSet(getAll());
        return new ArrayList(result);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T get(PK id) {
        T entity = (T) super.getHibernateTemplate().get(this.persistentClass, id);

        if (entity == null) {
            log.warn("Uh oh, '" + this.persistentClass + "' object with id '" + id + "' not found...");
//            throw new ObjectRetrievalFailureException(this.persistentClass, id);

        }

        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean exists(PK id) {
        T entity = (T) super.getHibernateTemplate().get(this.persistentClass, id);
        return entity != null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T save(T object) {
        return (T) super.getHibernateTemplate().merge(object);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(PK id) {
        super.getHibernateTemplate().delete(this.get(id));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByNamedQuery(
            String queryName,
            Map<String, Object> queryParams) {
        String[] params = new String[queryParams.size()];
        Object[] values = new Object[queryParams.size()];
        int index = 0;
        Iterator<String> i = queryParams.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            params[index] = key;
            values[index++] = queryParams.get(key);
        }
        return getHibernateTemplate().findByNamedQueryAndNamedParam(
                queryName,
                params,
                values);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByLimitQuery(
            final String query,
            final Map<String, Object> queryParams, final int limit) {
        log.debug("Query = " + query);
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            @Override
            public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery(query);
                Iterator<String> i = queryParams.keySet().iterator();
                while (i.hasNext()) {
                    String key = i.next();
                    q.setParameter(key, queryParams.get(key));
                }
                q.setMaxResults(limit);
                return q.list();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public List findByNativeQuery(String queryName,
            Map<String, Object> queryParams) {
        Query query = (Query) getSession().getNamedQuery(queryName);
        for (String key : queryParams.keySet()) {
            query.setParameter(key, queryParams.get(key));
        }
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByNamedParams(
            String queryString,
            Map<String, Object> queryParams) {
        String[] params = new String[queryParams.size()];
        Object[] values = new Object[queryParams.size()];
        int index = 0;
        Iterator<String> i = queryParams.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            params[index] = key;
            values[index++] = queryParams.get(key);
        }
        return getHibernateTemplate().findByNamedParam(
                queryString,
                params,
                values);
    }

    /**
     * {@inheritDoc}
     */
    public int bulkUpdate(String query, Object[] values) {
        return super.getHibernateTemplate().bulkUpdate(query, values);

    }

    /**
     * {@inheritDoc}
     */
    public void saveAll(Collection<T> entities) {
        super.getHibernateTemplate().saveOrUpdateAll(entities);

    }

    @Override
    public List<Map<String, Object>> executeProcedure(String query, Map<String, Object> params) {
        List list = new ArrayList();
        Connection con = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        try {
            con = getSession().connection();
            cstmt = con.prepareCall(query);
            int i = 1;
            for (String key : params.keySet()) {
                cstmt.setObject(i, params.get(key));
                i++;
            }
            rs = cstmt.executeQuery();
            if(!rs.next()){
                return list;
            }
            rs.beforeFirst();
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            Set<String> columns = new HashSet<String>();
            for (int j = 1; j <= count; j++) {
//		System.out.println(meta.getColumnName(j));
                columns.add(meta.getColumnLabel(j));
            }
            i = 1;
            while (rs.next()) {
                Map<String, Object> rows = new HashMap<String, Object>();
                rows.put("SNO", i);
                for (String key : columns) {
                    rows.put(key, rs.getString(key));
                }
                list.add(rows);
                i++;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (cstmt != null) {
                    cstmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return list;
    }

    public List executeSQLQuery(String query, Map<String, Object> params) {
//        log.info("Query :" + query);
//        log.info("Params :" + params);
        List list = new ArrayList();
        Connection con = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            con = getSession().connection();
            psmt = con.prepareStatement(query);
            int i = 1;
            for (String key : params.keySet()) {
                psmt.setObject(i, params.get(key));
                i++;
            }
            rs = psmt.executeQuery();

//            log.trace("------------------------------------------\n"
//                    + "Query:"+query+"\n"
//                    +"Params:"+params
//                    +"------------------------------------------");
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            Set<String> columns = new LinkedHashSet<String>();
            for (int j = 1; j <= count; j++) {
//		System.out.println(meta.getColumnName(j));
                columns.add(meta.getColumnLabel(j));
            }
//            i = 1;
            while (rs.next()) {
                Map<String, Object> rows = new LinkedHashMap<String, Object>();
//                rows.put("SNO", i);
                for (String key : columns) {
                    rows.put(key, rs.getString(key));
                }
                list.add(rows);
//                i++;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (psmt != null) {
                    psmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return list;
    }
}
