package com.github.superkoh.mybatis.common;

public interface Pageable extends Limitable {

  int getPageNo();

  int getPageSize();

  default Integer getLimit() {
    return getPageSize();
  }

  default Integer getOffset() {
    return getPageSize() * (getPageNo() - 1);
  }
}
