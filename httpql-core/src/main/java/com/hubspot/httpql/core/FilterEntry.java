package com.hubspot.httpql.core;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.rosetta.annotations.RosettaNaming;
import java.lang.annotation.Annotation;
import java.util.Optional;

public class FilterEntry {

  private final String queryName;
  private final String fieldName;
  private final Filter filter;

  public FilterEntry(Filter filter, String queryName, Class<?> queryType) { // Only valid for comparison
    this(filter, queryName, queryName, queryType);
  }

  public FilterEntry(
    Filter filter,
    String fieldName,
    String queryName,
    Class<?> queryType
  ) {
    this.filter = filter;
    this.fieldName = fieldName;
    this.queryName = convertToSnakeCaseIfSupported(queryName, queryType);
  }

  public String getQueryName() {
    return queryName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public Filter getFilter() {
    return filter;
  }

  public Optional<Class<? extends Filter>> getFilterClassMaybe() {
    return Optional.ofNullable(filter).map(Filter::getClass);
  }

  @Override
  public boolean equals(Object other) {
    FilterEntry fe = (FilterEntry) other;

    return (
      Objects.equal(getQueryName(), fe.getQueryName()) &&
      Objects.equal(getFilterClassMaybe(), fe.getFilterClassMaybe())
    );
  }

  private static <T extends Annotation> T getAnnotation(
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

  private static String convertToSnakeCaseIfSupported(String name, Class<?> specType) {
    RosettaNaming rosettaNaming = getAnnotation(specType, RosettaNaming.class);

    boolean snakeCasing =
      rosettaNaming != null && rosettaNaming.value().equals(SnakeCaseStrategy.class);

    if (snakeCasing && !name.contains("_")) {
      return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getQueryName(), getFilterClassMaybe());
  }
}
