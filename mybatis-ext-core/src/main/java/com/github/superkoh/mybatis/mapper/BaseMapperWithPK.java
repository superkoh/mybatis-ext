package com.github.superkoh.mybatis.mapper;

public interface BaseMapperWithPK<R, RE, K> extends BaseMapper<R, RE> {

  int deleteByPrimaryKey(K id);

  R selectByPrimaryKey(K id);

  int updateByPrimaryKeySelective(R record);

  int updateByPrimaryKey(R record);
}
