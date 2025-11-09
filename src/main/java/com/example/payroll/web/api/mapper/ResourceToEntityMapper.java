package com.example.payroll.web.api.mapper;

import com.example.payroll.database.entity.BaseEntity;

/**
 * Implementations map REST resources to their corresponding application entity and vice versa.
 *
 * @param <R> the REST API resource type
 * @param <E> the application entity type
 */
public interface ResourceToEntityMapper<R, E extends BaseEntity> {

  E fromResource(R resource);

  R toResource(E entity);
}
