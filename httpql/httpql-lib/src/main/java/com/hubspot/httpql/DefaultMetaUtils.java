package com.hubspot.httpql;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.CaseFormat;
import com.google.common.base.Throwables;
import com.hubspot.httpql.ann.FilterBy;
import com.hubspot.httpql.ann.FilterJoin;
import com.hubspot.httpql.ann.FilterJoinByDescriptor;
import com.hubspot.httpql.ann.OrderBy;
import com.hubspot.httpql.filter.Filter;
import com.hubspot.httpql.impl.JoinDescriptorImpl;
import com.hubspot.rosetta.annotations.RosettaNaming;
import java.lang.annotation.Annotation;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class DefaultMetaUtils {
  @Nullable
  public static OrderBy findOrderBy(BeanPropertyDefinition prop) {
    return findAnnotation(prop, OrderBy.class);
  }

  @Nullable
  public static FilterBy findFilterBy(BeanPropertyDefinition prop) {
    return findAnnotation(prop, FilterBy.class);
  }

  @Nullable
  public static FilterJoin findFilterJoin(BeanPropertyDefinition prop) {
    return findAnnotation(prop, FilterJoin.class);
  }

  @Nullable
  public static FilterJoinByDescriptor findFilterJoinByDescriptor(BeanPropertyDefinition prop) {
    return findAnnotation(prop, FilterJoinByDescriptor.class);
  }

  @Nullable
  public static JoinDescriptorImpl findJoinDescriptor(BeanPropertyDefinition prop) {
    try {
      FilterJoinByDescriptor filterJoinByDescriptor = findFilterJoinByDescriptor(prop);
      if (filterJoinByDescriptor == null) {
        return null;
      }
      // I'm not in love with this, but I haven't found a better way to find these classes
      return (JoinDescriptorImpl) ClassLoader.getSystemClassLoader().loadClass(filterJoinByDescriptor.value()).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e
             ) {
      throw new RuntimeException(e);
    }
  }

  public static <T extends Annotation> T findAnnotation(BeanPropertyDefinition prop, Class<T> type) {
    T ann = null;
    if (prop.hasField()) {
      ann = prop.getField().getAnnotation(type);
    }
    if (ann == null && prop.hasGetter()) {
      ann = prop.getGetter().getAnnotation(type);
    }
    if (ann == null && prop.hasSetter()) {
      ann = prop.getSetter().getAnnotation(type);
    }
    return ann;
  }

  public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annClazz) {
    while (clazz != null) {
      T ann = clazz.getAnnotation(annClazz);
      if (ann != null) {
        return ann;
      }
      clazz = clazz.getSuperclass();
    }
    return null;
  }

  public static String convertToSnakeCaseIfSupported(String name, Class<?> specType) {
    RosettaNaming rosettaNaming = getAnnotation(specType, RosettaNaming.class);

    boolean snakeCasing = rosettaNaming != null &&
        (rosettaNaming.value().equals(LowerCaseWithUnderscoresStrategy.class) ||
            rosettaNaming.value().equals(SnakeCaseStrategy.class) ||
                rosettaNaming.value().equals(PropertyNamingStrategies.SnakeCaseStrategy.class));

    if (snakeCasing && !name.contains("_")) {
      return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }
    return name;
  }

  public static Filter getFilterInstance(Class<? extends Filter> filterType) {
    try {
      return filterType.getDeclaredConstructor().newInstance();
    } catch (ReflectiveOperationException e) {
      throw Throwables.propagate(e);
    }
  }
}
