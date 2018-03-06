package com.github.superkoh.mybatis.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BaseMapperWithBlobs<R, RE> extends BaseMapper<R, RE> {

  List<R> selectByExampleWithBLOBs(RE example);

  int updateByExampleWithBLOBs(@Param("record") R record, @Param("example") RE example);
}
