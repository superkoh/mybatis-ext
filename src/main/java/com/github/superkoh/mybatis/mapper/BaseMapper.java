package com.github.superkoh.mybatis.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BaseMapper<R, RE> {

  long countByExample(RE example);

  int deleteByExample(RE example);

  int insert(R record);

  int insertSelective(R record);

  List<R> selectByExample(RE example);

  int updateByExampleSelective(@Param("record") R record, @Param("example") RE example);

  int updateByExample(@Param("record") R record, @Param("example") RE example);
}
