package com.github.superkoh.mybatis.utils;

import com.github.superkoh.mybatis.mapper.BaseMapperWithPK;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.lang3.ClassUtils;

public class MapperUtils {

  public static <R, RE, K, M extends BaseMapperWithPK<R, RE, K>> R selectByPrimaryKeyGuaranteed(
      M mapper, K id) {
    R r = mapper.selectByPrimaryKey(id);
    if (null == r) {
      throw new IllegalArgumentException(
          ClassUtils.getAllInterfaces(mapper.getClass()).get(0).getSimpleName()
              .replace("Mapper", "") + " not found");
    }
    try {
      Method m = r.getClass().getMethod("getIsDeleted");
      Boolean isDeleted = (Boolean) m.invoke(r);
      if (isDeleted) {
        throw new IllegalArgumentException(
            ClassUtils.getAllInterfaces(mapper.getClass()).get(0).getSimpleName()
                .replace("Mapper", "") + " not found");
      }
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
        ignored) {
    }
    return r;
  }
}
