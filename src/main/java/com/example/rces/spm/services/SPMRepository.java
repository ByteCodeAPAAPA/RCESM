package com.example.rces.spm.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(transactionManager = "spmTransactionManager")
public class SPMRepository {
    private final EntityManager entityManager;

    @Autowired
    public SPMRepository(@Qualifier("spmEntityManager") EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> T findById(Class<T> entityClass, Object id) {
        return entityManager.find(entityClass, id);
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return entityManager.createQuery(cq).getResultList();
    }

    public <T> List<T> findByField(Class<T> entityClass, String fieldName, Object fieldValue) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);

        if (fieldValue instanceof List<?> || fieldValue.getClass().isArray()) {
            Object[] arrayValues;
            if (fieldValue instanceof List<?>) {
                arrayValues = ((List<?>) fieldValue).toArray();
            } else {
                arrayValues = (Object[]) fieldValue;
            }
            cq.select(root).where(root.get(fieldName).in(arrayValues));
        } else {
            cq.select(root).where(cb.equal(root.get(fieldName), fieldValue));
        }

        return entityManager.createQuery(cq).getResultList();
    }

    public <T> T findSingleByField(Class<T> entityClass, String fieldName, Object fieldValue) {
        return findByField(entityClass, fieldName, fieldValue).stream().findFirst().orElse(null);
    }

    public <T> Page<T> getPageByEntity(Class<T> entityClass, int page, int pageSize, Sort sort, String conditions) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        String orderByClause = "ORDER BY " + sort.stream()
                .map(order -> String.format("e.%s %s", order.getProperty(), order.getDirection()))
                .collect(Collectors.joining(", "));
        List<T> content = entityManager.createQuery(
                        "SELECT e FROM " + entityClass.getSimpleName() + " e " + conditions + " " + orderByClause, entityClass)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        TypedQuery<Long> countQuery = entityManager.createQuery(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::getSingleResult);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
