package com.hubspot.httpql;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.hubspot.httpql.ann.FilterBy;
import com.hubspot.httpql.ann.FilterJoin;
import com.hubspot.httpql.ann.FilterJoinByDescriptor;
import com.hubspot.httpql.ann.OrderBy;
import com.hubspot.httpql.ann.desc.JoinDescriptor;
import com.hubspot.httpql.impl.FilterJoinInfo;
import com.hubspot.httpql.impl.filter.FilterImpl;
import com.hubspot.rosetta.annotations.RosettaNaming;
import java.lang.annotation.Annotation;
import java.util.Optional;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class DefaultMetaUtils {

  private static final String LOWER_CASE_WITH_UNDERSCORES_STRATEGY_CLASS_NAME =
    "com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy";

  public static boolean hasOrderBy(BeanPropertyDefinition prop) {
    return (
      findAnnotation(prop, com.hubspot.httpql.core.ann.OrderBy.class) != null ||
      findAnnotation(prop, OrderBy.class) != null
    );
  }

  public static Class<? extends com.hubspot.httpql.core.filter.Filter>[] getFilterByClasses(
    BeanPropertyDefinition prop
  ) {
    com.hubspot.httpql.core.ann.FilterBy annotation = findAnnotation(
      prop,
      com.hubspot.httpql.core.ann.FilterBy.class
    );
    if (annotation != null) {
      return annotation.value();
    }

    FilterBy ann = findAnnotation(prop, FilterBy.class);
    if (ann != null) {
      return ann.value();
    }

    return new Class[] {};
  }

  @Nullable
  public static FilterJoinInfo findFilterJoin(BeanPropertyDefinition prop) {
    return Optional
      .ofNullable(FilterJoinInfo.of(findAnnotation(prop, FilterJoin.class)))
      .orElse(
        FilterJoinInfo.of(
          findAnnotation(prop, com.hubspot.httpql.core.ann.FilterJoin.class)
        )
      );
  }

  @Nullable
  private static FilterJoinByDescriptor findFilterJoinByDescriptor(
    BeanPropertyDefinition prop
  ) {
    return findAnnotation(prop, FilterJoinByDescriptor.class);
  }

  @Nullable
  private static com.hubspot.httpql.core.ann.FilterJoinByDescriptor findCoreFilterJoinByDescriptor(
    BeanPropertyDefinition prop
  ) {
    return findAnnotation(prop, com.hubspot.httpql.core.ann.FilterJoinByDescriptor.class);
  }

  @Nullable
  public static JoinDescriptor findJoinDescriptor(BeanPropertyDefinition prop) {
    try {
      FilterJoinByDescriptor filterJoinByDescriptor = findFilterJoinByDescriptor(prop);
      if (filterJoinByDescriptor != null) {
        return filterJoinByDescriptor.value().newInstance();
      }
      com.hubspot.httpql.core.ann.FilterJoinByDescriptor desc =
        findCoreFilterJoinByDescriptor(prop);
      if (desc != null) {
        return (JoinDescriptor) Class.forName(desc.value()).newInstance();
      }
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public static <T extends Annotation> T findAnnotation(
    BeanPropertyDefinition prop,
    Class<T> type
  ) {
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

  public static <T extends Annotation> T getAnnotation(
    Class<?> clazz,
    Class<T> annClazz
  ) {
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

    boolean snakeCasing =
      rosettaNaming != null &&
      (LOWER_CASE_WITH_UNDERSCORES_STRATEGY_CLASS_NAME.equals(
          rosettaNaming.value().getCanonicalName()
        ) ||
        rosettaNaming.value().equals(SnakeCaseStrategy.class) ||
        rosettaNaming.value().equals(PropertyNamingStrategies.SnakeCaseStrategy.class));

    if (snakeCasing && !name.contains("_")) {
      return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }
    return name;
  }

  public static FilterImpl getFilterInstance(
    Class<? extends com.hubspot.httpql.core.filter.Filter> filterType
  ) {
    return Filters.getFilterImpl(filterType).orElse(null);
  }

  public static String getFilterByAs(AnnotatedMember member) {
    com.hubspot.httpql.core.ann.FilterBy coreAnn = member.getAnnotation(
      com.hubspot.httpql.core.ann.FilterBy.class
    );
    if (coreAnn != null) {
      return Strings.emptyToNull(coreAnn.as());
    }

    FilterBy ann = member.getAnnotation(FilterBy.class);
    if (ann != null) {
      return Strings.emptyToNull(ann.as());
    }

    return null;
  }

  public static Class<?> getFilterByTypeOverride(AnnotatedMember member) {
    com.hubspot.httpql.core.ann.FilterBy coreAnn = member.getAnnotation(
      com.hubspot.httpql.core.ann.FilterBy.class
    );
    if (coreAnn != null) {
      return coreAnn.typeOverride();
    }

    FilterBy ann = member.getAnnotation(FilterBy.class);
    if (ann != null) {
      return ann.typeOverride();
    }

    return null;
  }

  public static String getFilterByAs(BeanPropertyDefinition prop) {
    com.hubspot.httpql.core.ann.FilterBy coreFilterBy = findAnnotation(
      prop,
      com.hubspot.httpql.core.ann.FilterBy.class
    );
    if (coreFilterBy != null) {
      return Strings.emptyToNull(coreFilterBy.as());
    }
    FilterBy filterBy = findAnnotation(prop, FilterBy.class);
    if (filterBy != null) {
      return Strings.emptyToNull(filterBy.as());
    }
    return null;
  }
}
