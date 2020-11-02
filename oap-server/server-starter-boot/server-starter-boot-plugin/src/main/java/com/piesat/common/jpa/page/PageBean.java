/*
package com.piesat.common.jpa.page;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

*/
/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-11-18 22:09
 * <p>
 * 当前页
 * <p>
 * 分页大小
 *//*

@Data
@EqualsAndHashCode(callSuper = false)
public class PageBean {
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

    private List<?> pageData;

    private long totalCount;

    private Integer totalPage;

}

*/
