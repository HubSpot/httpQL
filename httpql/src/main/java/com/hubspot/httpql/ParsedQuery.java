package com.hubspot.httpql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.jooq.Operator;
import org.jooq.SortOrder;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.collect.Lists;
import com.hubspot.httpql.error.UnknownFieldException;
import com.hubspot.httpql.impl.Ordering;
import com.hubspot.httpql.impl.TableQualifiedFieldFactory;
import com.hubspot.httpql.internal.BoundFilterEntry;
import com.hubspot.httpql.internal.CombinedConditionCreator;
import com.hubspot.httpql.internal.FilterEntry;
import com.hubspot.httpql.internal.FilterEntryConditionCreator;
import com.hubspot.httpql.internal.MultiValuedBoundFilterEntry;

/**
 * The result of parsing query arguments.
 *
 * @author tdavis
 */
public class ParsedQuery<T extends QuerySpec> {

  private final Class<T> queryType;

  private final CombinedConditionCreator<T> combinedConditionCreator;
  @Deprecated
  private final T boundQuerySpec;

  private final MetaQuerySpec<T> meta;

  private Collection<Ordering> orderings;

  private Optional<Integer> limit;
  private Optional<Integer> offset;

  private boolean includeDeleted;

  public ParsedQuery(
                     T boundQuerySpec,
                     Class<T> queryType,
                     List<BoundFilterEntry<T>> boundFilterEntries,
                     MetaQuerySpec<T> meta,
                     Optional<Integer> limit,
                     Optional<Integer> offset,
                     Collection<Ordering> orderings,
                     boolean includeDeleted) {
    this(boundQuerySpec,
        queryType,
        new CombinedConditionCreator<>(Operator.AND, Lists.newArrayList(boundFilterEntries)),
        meta,
        limit,
        offset,
        orderings,
        includeDeleted);
  }

  public ParsedQuery(
                     T boundQuerySpec,
                     Class<T> queryType,
                     CombinedConditionCreator<T> combinedConditionCreator,
                     MetaQuerySpec<T> meta,
                     Optional<Integer> limit,
                     Optional<Integer> offset,
                     Collection<Ordering> orderings,
                     boolean includeDeleted) {

    this.boundQuerySpec = boundQuerySpec;
    this.queryType = queryType;
    this.combinedConditionCreator = combinedConditionCreator;
    this.meta = meta;

    this.limit = limit;
    this.offset = offset;
    this.orderings = orderings;

    this.includeDeleted = includeDeleted;
  }

  /**
   * Check to see if any filter exists for a given field.
   *
   * @param fieldName
   *          Name as seen in the query; not multi-value proxies ("id", not "ids")
   */
  public boolean hasFilter(String fieldName) {
    for (BoundFilterEntry<T> bfe : getBoundFilterEntries()) {
      if (bfe.getQueryName().equals(fieldName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check to see a specific filter type exists for a given field.
   *
   * @param fieldName
   *          Name as seen in the query; not multi-value proxies ("id", not "ids")
   */
  public boolean hasFilter(String fieldName, Class<? extends Filter> filterType) {
    for (BoundFilterEntry<T> bfe : getBoundFilterEntries()) {
      if (bfe.getQueryName().equals(fieldName) && bfe.getFilter().getClass().equals(filterType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Add the given filter to the query.
   *
   * @param fieldName
   *          Name as seen in the query; not multi-value proxies ("id", not "ids")
   * @throws UnknownFieldException
   *           When no field named {@code fieldName} exists
   * @throws IllegalArgumentException
   *           When {@code value} is of the wrong type
   */
  public void addFilter(String fieldName, Class<? extends Filter> filterType, Object value) {
    BeanPropertyDefinition filterProperty = meta.getFilterProperty(fieldName, filterType);
    BoundFilterEntry<T> boundColumn = meta.getNewBoundFilterEntry(fieldName, filterType);

    if (boundColumn.isMultiValue()) {
      Collection<?> values = (Collection<?>) value;
      boundColumn = new MultiValuedBoundFilterEntry<>(boundColumn, values);
    } else {
      filterProperty.getSetter().setValue(getBoundQuery(), value);
    }

    if (!combinedConditionCreator.getFlattenedBoundFilterEntries().contains(boundColumn)) {
      combinedConditionCreator.getConditionCreators().add(boundColumn);
    }
  }

  /**
   * Add the given order-by clause. The operation is not checked against allowed order-bys.
   *
   * @param fieldName
   *          Name as seen in the query; not multi-value proxies ("id", not "ids")
   */
  public void addOrdering(String fieldName, SortOrder order) {
    FilterEntry entry = new FilterEntry(null, fieldName, getQueryType());
    BeanPropertyDefinition prop = getMetaData().getFieldMap().get(entry.getQueryName());
    if (prop == null) {
      prop = getMetaData().getFieldMap().get(entry.getFieldName());
    }
    if (prop != null) {
      orderings.add(new Ordering(entry.getFieldName(), entry.getQueryName(), order));
    }
  }

  /**
   * Similar to {@link #addFilter} but removes all existing filters for {@code fieldName} first.
   *
   * @param fieldName
   *          Name as seen in the query; not multi-value proxies ("id", not "ids")
   */
  public void addFilterExclusively(String fieldName, Class<? extends Filter> filterType, Object value) {
    removeFiltersFor(fieldName);
    addFilter(fieldName, filterType, value);
  }

  /**
   * Similar to {@link #addFilter} but removes all existing filters for {@code fieldName} first.
   *
   * @param fieldName
   *          Name as seen in the query; not multi-value proxies ("id", not "ids")
   * @param filterEntryConditionCreator
   *          Either a BoundFilterEntry or a CombinedConditionCreator
   */
  public void addFilterEntryConditionCreatorExclusively(String fieldName,
                                                        FilterEntryConditionCreator<T> filterEntryConditionCreator) {
    removeFiltersFor(fieldName);
    combinedConditionCreator.addConditionCreator(filterEntryConditionCreator);
  }

  public boolean removeFiltersFor(String fieldName) {
    return combinedConditionCreator.removeAllFiltersFor(fieldName);
  }

  @Deprecated
  public BoundFilterEntry<T> getFirstFilterForFieldName(String fieldName) {
    return getAllFiltersForFieldName(fieldName).stream().findAny().orElse(null);
  }

  public List<BoundFilterEntry<T>> getAllFiltersForFieldName(String fieldName) {
    return combinedConditionCreator.getAllFiltersForFieldName(fieldName);
  }

  public void removeAllFilters() {
    combinedConditionCreator.getConditionCreators().clear();
  }

  public String getCacheKey() {
    List<Object> cacheKeyParts = new ArrayList<>();

    cacheKeyParts.add(combinedConditionCreator.getCondition(boundQuerySpec, new TableQualifiedFieldFactory()));

    for (Ordering o : orderings) {
      cacheKeyParts.add(o.getFieldName());
      cacheKeyParts.add(o.getOrder().ordinal());
    }

    cacheKeyParts.add(offset.orElse(0));
    cacheKeyParts.add(limit.orElse(0));
    cacheKeyParts.add(includeDeleted);

    return StringUtils.join(cacheKeyParts, ":");
  }

  public void setLimit(Optional<Integer> limit) {
    this.limit = limit;
  }

  public void setOffset(Optional<Integer> offset) {
    this.offset = offset;
  }

  public void setOrderings(Collection<Ordering> orderings) {
    this.orderings = orderings;
  }

  public Optional<Integer> getLimit() {
    return limit;
  }

  public Optional<Integer> getOffset() {
    return offset;
  }

  public Collection<Ordering> getOrderings() {
    return orderings;
  }

  public boolean getIncludeDeleted() {
    return includeDeleted;
  }

  @Deprecated
  public T getBoundQuery() {
    return boundQuerySpec;
  }

  public List<BoundFilterEntry<T>> getBoundFilterEntries() {
    return Lists.newArrayList(combinedConditionCreator.getFlattenedBoundFilterEntries());
  }

  public CombinedConditionCreator<T> getCombinedConditionCreator() {
    return combinedConditionCreator;
  }

  public Class<T> getQueryType() {
    return queryType;
  }

  public MetaQuerySpec<T> getMetaData() {
    return meta;
  }

  @Override
  public ParsedQuery<T> clone() {
    return new ParsedQuery<>(
        getBoundQuery(),
        getQueryType(),
        combinedConditionCreator,
        getMetaData(),
        getLimit(),
        getOffset(),
        new ArrayList<>(getOrderings()),
        getIncludeDeleted());
  }
}
