package com.piesat.util.page;


/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-11-18 22:27
 **/
public class PageForm<T> {
    //查询条件
    private T t;
    /**
     * 当前页
     */
    private int currentPage = 1;

    private String orderBy;
    private int pageSize = 10;

    /**
     * 分页大小
     */
    public PageForm() {

    }

    public PageForm(int currentPage, int pageSize, T t) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.t = t;
    }

    public PageForm(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}

