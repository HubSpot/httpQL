package com.hubspot.httpql.internal;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Operator;
import org.jooq.impl.DSL;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hubspot.httpql.FieldFactory;
import com.hubspot.httpql.QuerySpec;

public class CombinedConditionCreator<T extends QuerySpec> implements FilterEntryConditionCreator<T> {

  private Operator operator;

  private List<FilterEntryConditionCreator<T>> conditionCreators;

  public CombinedConditionCreator(Operator operator, Collection<FilterEntryConditionCreator<T>> conditionCreators) {
    this.operator = operator;
    this.conditionCreators = Lists.newArrayList(conditionCreators);
  }

  @Override
  public Condition getCondition(QuerySpec value, FieldFactory fieldFactory) {
    return DSL.condition(operator, conditionCreators.stream()
        .map(cc -> cc.getCondition(value, fieldFactory))
        .collect(Collectors.toList()));
  }

  @Override
  public Collection<BoundFilterEntry<T>> getFlattenedBoundFilterEntries() {
    return conditionCreators.stream()
        .map(FilterEntryConditionCreator::getFlattenedBoundFilterEntries)
        .flatMap(Collection::stream)
        .collect(ImmutableList.toImmutableList());
  }

  public void addConditionCreator(FilterEntryConditionCreator<T> conditionCreator) {
    if (!getFlattenedBoundFilterEntries().contains(conditionCreator)) {
      conditionCreators.add(conditionCreator);
    }
  }

  public boolean removeAllFiltersFor(String fieldName) {
    boolean removedFromThis = conditionCreators.removeIf(
        f -> f instanceof BoundFilterEntry && ((BoundFilterEntry<T>) f).getQueryName().equals(fieldName));
    boolean removedFromChildren = conditionCreators.stream()
        .filter(cc -> cc instanceof CombinedConditionCreator)
        .map(cc -> ((CombinedConditionCreator<T>) cc).removeAllFiltersFor(fieldName))
        .reduce(Boolean::logicalOr).orElse(false);

    return removedFromThis || removedFromChildren;
  }

  public List<BoundFilterEntry<T>> getAllFiltersForFieldName(String fieldName) {
    return getFlattenedBoundFilterEntries().stream()
        .filter(bfe -> bfe.getQueryName().equals(fieldName))
        .collect(Collectors.toList());
  }

  public List<FilterEntryConditionCreator<T>> getConditionCreators() {
    return conditionCreators;
  }

  public void addConditionCreators(FilterEntryConditionCreator<T> filterEntryConditionCreator) {
    conditionCreators.add(filterEntryConditionCreator);
  }
}
