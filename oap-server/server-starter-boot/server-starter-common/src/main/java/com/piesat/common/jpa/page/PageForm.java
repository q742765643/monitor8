/*
package com.piesat.common.jpa.page;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

*/
/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-11-18 22:27
 * <p>
 * 当前页
 * <p>
 * 分页大小
 *//*

@Data
public class PageForm<T> {
    //查询条件
    private T t;
    */
/**
 * 当前页
 *//*

    private int currentPage = 1;

    */
/**
 * 分页大小
 *//*

    private int pageSize = 10;

    private Sort sort = Sort.unsorted();

    public PageRequest buildPageRequest() {
        return PageRequest.of(currentPage - 1, pageSize,sort);
    }
}

*/
