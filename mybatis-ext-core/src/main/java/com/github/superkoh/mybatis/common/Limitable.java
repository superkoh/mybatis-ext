package com.github.superkoh.mybatis.common;

public interface Limitable {

  Integer getLimit();

  Integer getOffset();

  String getOrderBy();
}
