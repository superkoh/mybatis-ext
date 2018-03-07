package com.github.superkoh.mybatis.utils;

import com.github.superkoh.mybatis.mapper.BaseMapperWithPK;
import java.lang.reflect.InvocationTargetException;
import lombok.val;
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
      val m = r.getClass().getMethod("getIsDeleted");
      val isDeleted = (Boolean) m.invoke(r);
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
