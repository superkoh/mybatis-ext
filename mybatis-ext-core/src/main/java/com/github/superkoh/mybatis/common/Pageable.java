package com.github.superkoh.mybatis.common;

public interface Pageable extends Limitable {

  int getPageNo();

  int getPageSize();

  Integer getLimit();

  Integer getOffset();
}
