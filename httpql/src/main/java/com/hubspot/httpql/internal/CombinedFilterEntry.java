package com.hubspot.httpql.internal;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Operator;
import org.jooq.impl.DSL;

import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.FieldFactory;
import com.hubspot.httpql.QuerySpec;

public class CombinedFilterEntry<T extends QuerySpec> implements FilterEntryConditionCreator<T> {

  private Operator operator;

  private Set<FilterEntryConditionCreator<T>> conditionCreators;

  public CombinedFilterEntry(Operator operator, Set<FilterEntryConditionCreator<T>> conditionCreators) {
    this.operator = operator;
    this.conditionCreators = conditionCreators;
  }

  @Override
  public Condition getCondition(QuerySpec value, FieldFactory fieldFactory) {
    return DSL.condition(operator, conditionCreators.stream()
        .map(cc -> cc.getCondition(value, fieldFactory))
        .collect(Collectors.toSet()));
  }

  @Override
  public Collection<BoundFilterEntry<T>> getFlattenedBoundFilterEntries() {
    return conditionCreators.stream()
        .map(FilterEntryConditionCreator::getFlattenedBoundFilterEntries)
        .flatMap(Collection::stream)
        .collect(ImmutableSet.toImmutableSet());
  }

  public void addConditionCreator(FilterEntryConditionCreator<T> conditionCreator) {
    if (!getFlattenedBoundFilterEntries().contains(conditionCreator)) {
      conditionCreators.add(conditionCreator);
    }
  }

  public boolean removeAllFiltersFor(String fieldName) {
    boolean removedFromThis = conditionCreators.removeIf(f -> f instanceof BoundFilterEntry && ((BoundFilterEntry<T>) f)
        .getQueryName()
        .equals(fieldName));
    boolean removedFromChildren = conditionCreators.stream()
        .filter(cc -> cc instanceof CombinedFilterEntry)
        .map(cc -> ((CombinedFilterEntry<T>) cc).removeAllFiltersFor(fieldName))
        .reduce(Boolean::logicalOr).orElse(false);

    return removedFromThis || removedFromChildren;
  }
}
