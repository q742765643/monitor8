package com.piesat.common.jpa;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/18 11:31
 */
public interface BaseMapper<D, E> {

    /**
     * DTO转Entity
     *
     * @param dto
     * @return
     */
    E toEntity(D dto);

    /**
     * Entity转DTO
     *
     * @param entity
     * @return
     */
    D toDto(E entity);

    /**
     * DTO集合转Entity集合
     *
     * @param dtoList
     * @return
     */
    List<E> toEntity(List<D> dtoList);

    /**
     * Entity集合转DTO集合
     *
     * @param entityList
     * @return
     */
    List<D> toDto(List<E> entityList);
}
