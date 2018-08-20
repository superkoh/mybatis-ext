package com.github.superkoh.mybatis.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LimitableList<T> extends ArrayList<T> implements Limitable {

  private Integer limit;
  private Integer offset;
  private String orderBy;

  public LimitableList(List<T> list, Limitable limit) {
    super(list);
    this.limit = limit.getLimit();
    this.offset = limit.getOffset();
    this.orderBy = limit.getOrderBy();
  }

  public <I> LimitableList(LimitableList<I> limitableList,
      Function<? super I, ? extends T> mapper) {
    super(limitableList.stream().map(mapper).collect(Collectors.toList()));
    this.limit = limitableList.getLimit();
    this.offset = limitableList.getOffset();
    this.orderBy = limitableList.getOrderBy();
  }

  @Override
  public Integer getLimit() {
    return limit;
  }

  @Override
  public Integer getOffset() {
    return offset;
  }

  @Override
  public String getOrderBy() {
    return orderBy;
  }


  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }
}
