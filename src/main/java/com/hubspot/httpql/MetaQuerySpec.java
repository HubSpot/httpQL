package com.hubspot.httpql;

import java.util.Map;

import org.jooq.Field;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.collect.Table;
import com.hubspot.httpql.internal.BoundFilterEntry;

/**
 * MetaQuerySpec provides implementations of metadata-related methods related to a given {@link QuerySpec}.
 * <p>
 * Instances of this interface are primarily used internally and instantiating it is expensive; if you need it, an instance is available via {@link ParsedQuery#getMetaData()}
 *
 * @author tdavis
 */
public interface MetaQuerySpec<T extends QuerySpec> {
  /**
   * Returns the type for a field as found on {@code T}.
   */
  Class<?> getFieldType(String name);

  /**
   * Returns a table of filters where rows are their internal representation, columns are a given string alias for the filter, and the value is the meta description of the annotated field.
   */
  Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> getFilterTable();

  /**
   * Provides a mapping of every known field on {@code T}.
   * Implementations should take care to respect all Rosetta and Jackson annotations regarding visibility, if implementing logic manually.
   */
  Map<String, BeanPropertyDefinition> getFieldMap();

  /**
   * Construct a {@link Field} instance for a named field on {@code T}.
   *
   * @param name
   * @param fieldType
   * @param fieldFactory
   * @return
   */
  <E> Field<E> createField(String name, FieldFactory fieldFactory);

  /**
   * Returns an array of filter types for a field based on its annotations.
   */
  Class<? extends Filter>[] getFiltersForField(String name);

  /**
   * Provides a narrowed view of the table provided by {@link #getFilterTable()} by looking at only a specific field and set of filters.
   * <p>
   * This method should *not* perform validation on the entries in {@code filters} to, e.g., check for existence of the filter on the field.
   */
  Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> tableFor(BeanPropertyDefinition field,
      @SuppressWarnings("unchecked") Class<? extends Filter>... filters);

  Class<T> getQueryType();
}
