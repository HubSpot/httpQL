package com.hubspot.httpql.lib.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.primitives.Primitives;
import com.hubspot.rosetta.Rosetta;
import com.hubspot.rosetta.annotations.RosettaProperty;
import com.hubspot.rosetta.annotations.StoredAsJson;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanPropertyIntrospector {

  public static Map<String, BeanPropertyDefinition> getFields(Class<?> type) {
    return introspect(type).findProperties().stream()
        .collect(Collectors.toMap(BeanPropertyDefinition::getName, Function.identity()));
  }

  public static Map<String, Object> makeBoundMap(Object object) {
    Map<String, Object> propertyMap = new HashMap<>();

    for (Map.Entry<String, BeanPropertyDefinition> entry : getFields(object.getClass()).entrySet()) {
      BeanPropertyDefinition definition = entry.getValue();

      if (!definition.couldSerialize()) {
        continue;
      }

      Object value = definition.getAccessor().getValue(object);

      if (value != null) {
        if (hasAnnotation(definition, StoredAsJson.class) ||
            !Primitives.wrap(getFieldType(definition)).isAssignableFrom(value.getClass())) {

          try {
            value = Rosetta.getMapper().writeValueAsString(value);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
      }

      propertyMap.put(entry.getKey(), value);
    }

    return propertyMap;
  }

  public static Class<?> getFieldType(BeanPropertyDefinition definition) {
    Class<?> fieldType = null;

    if (definition.hasField()) {
      fieldType = definition.getField().getAnnotated().getType();
    } else if (definition.hasGetter()) {
      fieldType = definition.getGetter().getRawReturnType();
    } else if (definition.hasSetter()) {
      fieldType = definition.getSetter().getRawParameterType(0);
    }

    return fieldType;
  }

  public static boolean hasAnnotation(BeanPropertyDefinition definition, Class<? extends Annotation> type) {
    return getAnnotation(definition, type) != null;
  }

  public static <T extends Annotation> T getAnnotation(BeanPropertyDefinition definition, Class<T> type) {
    if (definition.hasField() && definition.getField().getAnnotation(type) != null) {
      return definition.getField().getAnnotation(type);
    } else if (definition.hasGetter() && definition.getGetter().getAnnotation(type) != null) {
      return definition.getGetter().getAnnotation(type);
    } else if (definition.hasSetter() && definition.getSetter().getAnnotation(type) != null) {
      return definition.getSetter().getAnnotation(type);
    }
    return null;
  }

  private static BeanDescription introspect(Class<?> type) {
    final JavaType javaType;
    SerializationConfig sc = Rosetta.getMapper().getSerializationConfig().withView(RosettaProperty.class);

    javaType = Rosetta.getMapper().getTypeFactory().constructType(type);
    return sc.introspect(javaType);
  }

}
