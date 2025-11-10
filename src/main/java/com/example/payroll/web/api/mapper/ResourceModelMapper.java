package com.example.payroll.web.api.mapper;

import com.example.payroll.database.entity.BaseEntity;
import org.springframework.hateoas.EntityModel;

/**
 * Maps a REST resource type to their corresponding JPA entity and the JPA entity to HAL entity representation.
 *
 * @param <R> the REST API resource type
 * @param <E> the JPA entity type
 */
public interface ResourceModelMapper<R, E extends BaseEntity> {

  E toJpaEntity(R resource);

  EntityModel<R> toHalEntityModel(E entity);

}
