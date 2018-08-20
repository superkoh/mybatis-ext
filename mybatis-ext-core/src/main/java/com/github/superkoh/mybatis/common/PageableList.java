package com.github.superkoh.mybatis.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageableList<T> extends ArrayList<T> implements Pageable {

  private long totalCount;
  private String orderBy;
  private int pageNo;
  private int pageSize;

  public PageableList(List<T> list, Pageable page, long totalCount) {
    super(list);
    this.pageNo = page.getPageNo();
    this.pageSize = page.getPageSize();
    this.orderBy = page.getOrderBy();
    this.totalCount = totalCount;
  }

  public <I> PageableList(PageableList<I> pageList, Function<? super I, ? extends T> mapper) {
    super(pageList.stream().map(mapper).collect(Collectors.toList()));
    this.pageSize = pageList.getPageSize();
    this.pageNo = pageList.getPageNo();
    this.orderBy = pageList.getOrderBy();
    this.totalCount = pageList.getTotalCount();
  }

  @Override
  public int getPageNo() {
    return pageNo;
  }

  @Override
  public int getPageSize() {
    return pageSize;
  }

  @Override
  public Integer getLimit() {
    return pageSize;
  }

  @Override
  public Integer getOffset() {
    return (pageNo - 1) * pageSize;
  }

  @Override
  public String getOrderBy() {
    return orderBy;
  }

  public long getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(long totalCount) {
    this.totalCount = totalCount;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public void setPageNo(int pageNo) {
    this.pageNo = pageNo;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }
}
