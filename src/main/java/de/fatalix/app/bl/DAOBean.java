/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fatalix.app.bl;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author felix.husse
 */
public abstract class DAOBean <T extends EntityIntf> {

    protected EntityManager entityManager;

    protected Class<T> theClass;

    public void init(EntityManager entityManager, Class<T> theClass) {
        this.entityManager = entityManager;
        this.theClass = theClass;
    }

    public T update(T entity) {
        return entityManager.merge(entity);
    }

    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public void delete(Integer id) {
        T entity = entityManager.getReference(theClass, id);
        if(entity != null) {
            entityManager.remove(entity);
        }
    }

    public void deleteAll() {
        List<T> objects = findAll();

        if(objects != null) {

            for(T object : objects) {
                if(object != null && object.getId() != null) {

                    delete(object.getId());
                }
            }
        }

    }

    public List<T> findAll() {
        return entityManager.createQuery("SELECT p FROM " + theClass.getSimpleName()+" p",theClass).getResultList();
    }


    public T findById(Integer id) {
        return entityManager.find(theClass, id);
    }

    public List<T> find(String namedQueryName) {
        return entityManager.createNamedQuery(namedQueryName, theClass).getResultList();
    }

    // helper methods

    /**
     * This helper method is used, if an {@code Number} is expected as return value as for aggregate functions.
     * 
     * @param parameter
     *            The query parameter.
     * @param queryName
     * @return In case, the result of the query is null, {@code null} is returned, otherwise the result.
     */
    protected Number getNumber(Map<String, ?> parameter, String queryName) {
        TypedQuery<Number> query = entityManager.createNamedQuery(queryName, Number.class);
        for(Map.Entry<String, ?> entry : parameter.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        List<Number> queryResult = query.getResultList();
        if(queryResult != null && queryResult.size() > 0) {
            Number result = queryResult.get(0);
            if(result == null) {
                return 0;
            } else {
                return result;
            }
        } else {
            return null;
        }
    }

    /**
     * This method is used, if a list is expected as return value.
     * 
     * @param parameter
     *            The query parameter.
     * @param queryName
     * @return The query result.
     */
    protected List<T> getList(Map<String, ?> parameter, String queryName) {
        TypedQuery<T> query = entityManager.createNamedQuery(queryName, theClass);
        for(Map.Entry<String, ?> entry : parameter.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    protected List<T> getTopList(Map<String, ?> parameter, String queryName, int numberOfMaxFirst) {
        TypedQuery<T> query = entityManager.createNamedQuery(queryName, theClass);
        for(Map.Entry<String, ?> entry : parameter.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        query.setFirstResult(0);
        query.setMaxResults(numberOfMaxFirst);
        return query.getResultList();
    }

    /**
     * Executes an update.
     * 
     * @param parameter
     *            The query parameter.
     * @param queryName
     * @return The number of updated objects.
     */
    protected int executeBatchUpdate(Map<String, ?> parameter, String queryName) {
        Query query = entityManager.createNamedQuery(queryName);
        for(Map.Entry<String, ?> entry : parameter.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.executeUpdate();
    }

    /**
     * Executes a batch delete.
     * 
     * @param parameter
     *            The query parameter.
     * @param queryName
     * @return The number of deleted objects.
     */
    protected int executeBatchDelete(Map<String, ?> parameter, String queryName) {
        Query query = entityManager.createNamedQuery(queryName);
        for(Map.Entry<String, ?> entry : parameter.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.executeUpdate();
    }

    /**
     * This method is used, if a single object is expected.
     * 
     * @param parameter
     *            The query parameter.
     * @param queryName
     * @return In case, the query result is greater then 0, the first object is returned, otherwise {@code null}
     */
    protected T getFirstEntity(Map<String, ?> parameter, String queryName) {
        List<T> list = getTopList(parameter, queryName,1);
        if(list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }    
}
