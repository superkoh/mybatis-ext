package com.github.superkoh.mybatis.common;

import com.github.superkoh.mybatis.annotation.QueryIgnored;

abstract public class AbstractPage implements Pageable {

  @QueryIgnored
  private int pageSize;

  @QueryIgnored
  private int pageNo;

  @QueryIgnored
  private String orderBy;

  public AbstractPage(int pageSize, int pageNo) {
    this.pageSize = pageSize;
    this.pageNo = pageNo;
  }

  public AbstractPage(int pageSize, int pageNo, String orderBy) {
    this(pageSize, pageNo);
    this.orderBy = orderBy;
  }

  public Integer getOffset() {
    return (this.pageNo - 1) * this.pageSize;
  }

  public Integer getLimit() {
    return this.pageSize;
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
  public String getOrderBy() {
    return orderBy;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setPageNo(int pageNo) {
    this.pageNo = pageNo;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }
}
