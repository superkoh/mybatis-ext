package com.github.superkoh.mybatis.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BaseMapperWithPKAndBlobs<R, RE, K> extends BaseMapperWithPK<R, RE, K> {

  List<R> selectByExampleWithBLOBs(RE example);

  int updateByExampleWithBLOBs(@Param("record") R record, @Param("example") RE example);

  int updateByPrimaryKeyWithBLOBs(R record);
}
