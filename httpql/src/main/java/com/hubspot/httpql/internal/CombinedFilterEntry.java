package com.hubspot.httpql.internal;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Operator;
import org.jooq.impl.DSL;

import com.google.common.collect.ImmutableList;
import com.hubspot.httpql.FieldFactory;
import com.hubspot.httpql.QuerySpec;

public class CombinedFilterEntry<T extends QuerySpec> implements FilterEntryConditionCreator<T> {

  private Operator operator;

  private List<FilterEntryConditionCreator<T>> conditionCreators;

  public CombinedFilterEntry(Operator operator, List<FilterEntryConditionCreator<T>> conditionCreators) {
    this.operator = operator;
    this.conditionCreators = conditionCreators;
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
    boolean removedFromThis = conditionCreators.removeIf(f -> f instanceof BoundFilterEntry && ((BoundFilterEntry<T>) f)
        .getQueryName()
        .equals(fieldName));
    boolean removedFromChildren = conditionCreators.stream()
        .filter(cc -> cc instanceof CombinedFilterEntry)
        .map(cc -> ((CombinedFilterEntry<T>) cc).removeAllFiltersFor(fieldName))
        .reduce(Boolean::logicalOr).orElse(false);

    return removedFromThis || removedFromChildren;
  }

  public Optional<BoundFilterEntry<T>> removeFirstFilterForFieldName(String fieldName) {
    Optional<BoundFilterEntry<T>> maybeBfe = conditionCreators.stream()
        .filter(cc -> cc instanceof BoundFilterEntry)
        .map(cc -> ((BoundFilterEntry<T>) cc))
        .filter(bfe -> bfe.getQueryName().equals(fieldName))
        .findFirst();

    if (maybeBfe.isPresent()) {
      conditionCreators.remove(maybeBfe.get());
      return maybeBfe;
    } else {
      List<CombinedFilterEntry<T>> combinedFilterEntries = conditionCreators.stream()
          .filter(cc -> cc instanceof CombinedFilterEntry)
          .map(cc -> ((CombinedFilterEntry<T>) cc))
          .collect(Collectors.toList());
      for (CombinedFilterEntry<T> combinedFilterEntry : combinedFilterEntries) {
        maybeBfe = combinedFilterEntry.removeFirstFilterForFieldName(fieldName);
        if (maybeBfe.isPresent()) {
          return maybeBfe;
        }
      }
    }
    return maybeBfe;
  }

  public List<FilterEntryConditionCreator<T>> getConditionCreators() {
    return conditionCreators;
  }

  public void addConditionCreators(FilterEntryConditionCreator<T> filterEntryConditionCreator) {
    conditionCreators.add(filterEntryConditionCreator);
  }
}
