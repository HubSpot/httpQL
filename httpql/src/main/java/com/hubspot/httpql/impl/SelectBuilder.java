package com.hubspot.httpql.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SelectFinalStep;
import org.jooq.SelectFromStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectOffsetStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SelectSelectStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.conf.ParamType;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.hubspot.httpql.FieldFactory;
import com.hubspot.httpql.MetaQuerySpec;
import com.hubspot.httpql.ParsedQuery;
import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.ann.OrderBy;
import com.hubspot.httpql.internal.BoundFilterEntry;
import com.hubspot.httpql.internal.JoinFilter;

/**
 * Translates the high-level parsed query into a JOOQ Select and/or String representation.
 *
 * @author tdavis
 */
public class SelectBuilder<T extends QuerySpec> {

  private final List<String> includedFieldNames = new ArrayList<>();
  private final List<Field<?>> additionalFields = new ArrayList<>();
  private final List<AdditionalCondition> additionalConditions = new ArrayList<>();
  private final List<JoinCondition> joinConditions = new ArrayList<>();
  private final List<Field<?>> groupByFields = new ArrayList<>();
  private final MetaQuerySpec<T> meta;
  private final ParsedQuery<T> sourceQuery;

  private FieldFactory factory = new DefaultFieldFactory();
  private SQLDialect dialect = SQLDialect.MYSQL;
  private ParamType paramType = ParamType.INLINED;
  private RenderNameStyle renderNameStyle = RenderNameStyle.QUOTED;
  private boolean includeOrderings = true;
  private boolean includeConstraints = true;
  private boolean asCount = false;

  private SelectBuilder(ParsedQuery<T> parsed, MetaQuerySpec<T> context) {
    this.sourceQuery = parsed;
    this.meta = context;

    for (BoundFilterEntry<T> bfe : sourceQuery.getBoundFilterEntries()) {
      if (bfe.getFilter() instanceof JoinFilter) {
        JoinFilter joinFilter = (JoinFilter) bfe.getFilter();
        joinConditions.add(joinFilter.getJoin());
        if (factory instanceof DefaultFieldFactory) {
          factory = new TableQualifiedFieldFactory();
        }
      }
    }
  }

  /**
   * Change the SQL dialect used to build the SQL string.
   */
  public SelectBuilder<T> withDialect(SQLDialect dialect) {
    this.dialect = dialect;
    return this;
  }

  /**
   * Change the parameter type (placeholder/value style) of the resulting SQL string.
   */
  public SelectBuilder<T> withParamType(ParamType paramType) {
    this.paramType = paramType;
    return this;
  }

  public SelectBuilder<T> withRenderNameStyle(RenderNameStyle renderNameStyle) {
    this.renderNameStyle = renderNameStyle;
    return this;
  }

  /**
   * Include the ``ORDER BY`` clause in the query, if applicable.
   */
  public SelectBuilder<T> withOrderings() {
    this.includeOrderings = true;
    return this;
  }

  /**
   * Exclude the ``ORDER BY`` clause from the query, if applicable.
   */
  public SelectBuilder<T> withoutOrderings() {
    this.includeOrderings = false;
    return this;
  }

  /**
   * Include the ``LIMIT ... OFFSET`` clause in the query, if applicable.
   */
  public SelectBuilder<T> withLimitAndOffset() {
    this.includeConstraints = true;
    return this;
  }

  /**
   * Exclude the ``LIMIT ... OFFSET`` clause from the query, if applicable.
   */
  public SelectBuilder<T> withoutLimitAndOffset() {
    this.includeConstraints = false;
    return this;
  }

  /**
   * Produce only a simple aggregate count query, ``SELECT COUNT(*) ...``
   * <p/>
   * Using this mode automatically disables the LIMIT, OFFSET, and ORDER BY clauses.
   */
  public SelectBuilder<T> withCountOnly() {
    this.asCount = true;
    withoutLimitAndOffset();
    withoutOrderings();
    return this;
  }

  /**
   * Name specific fields (columns) to select
   */
  public SelectBuilder<T> withFields(Collection<String> fieldNames) {
    this.asCount = false;
    includedFieldNames.addAll(fieldNames);
    return this;
  }

  /**
   * Name specific fields (columns) to select
   */
  public SelectBuilder<T> withAdditionalFields(Field<?>... fields) {
    this.asCount = false;
    for (Field<?> field : fields) {
      additionalFields.add(field);
    }
    return this;
  }

  /**
   * Add custom JOOQ Conditions to the query.
   */
  public SelectBuilder<T> withConditions(Collection<Condition> conditions, boolean includeInCount) {
    for (Condition c : conditions) {
      additionalConditions.add(new AdditionalCondition(c, includeInCount));
    }
    return this;
  }

  public SelectBuilder<T> withFieldFactory(FieldFactory factory) {
    this.factory = factory;
    return this;
  }

  public SelectBuilder<T> withConditions(Condition... conditions) {
    withConditions(Arrays.asList(conditions), true);
    return this;
  }

  public SelectBuilder<T> withCondition(Condition condition, boolean includeInCount) {
    additionalConditions.add(new AdditionalCondition(condition, includeInCount));
    return this;
  }

  public SelectBuilder<T> withJoinCondition(TableLike<?> table, Condition condition) {
    joinConditions.add(new JoinCondition(table, condition));
    if (factory instanceof DefaultFieldFactory) {
      factory = new TableQualifiedFieldFactory();
    }
    return this;
  }

  public SelectBuilder<T> withLeftJoinCondition(TableLike<?> table, Condition condition) {
    joinConditions.add(new JoinCondition(table, condition, true));
    if (factory instanceof DefaultFieldFactory) {
      factory = new TableQualifiedFieldFactory();
    }
    return this;
  }

  public SelectBuilder<T> withFields(String... fieldNames) {
    return withFields(Arrays.asList(fieldNames));
  }

  public SelectBuilder<T> withNoGroupBys() {
    groupByFields.clear();
    return this;
  }

  public SelectBuilder<T> withGroupBys(Field<?>... fields) {
    for (Field<?> field : fields) {
      groupByFields.add(field);
    }
    return this;
  }

  public BuiltSelect<T> build() {
    return new BuiltSelect<>(buildSelect(), paramType);
  }

  private Table<?> getTable() {
    String name = sourceQuery.getBoundQuery().tableName();
    return DSL.table(factory.tableAlias(name).orElse(name));
  }

  private List<Field<?>> fieldNamesToFields() {
    final List<Field<?>> includedFields = new ArrayList<>();
    for (String name : includedFieldNames) {
      includedFields.add(factory.createField(name, meta.getFieldType(name), getTable()));
    }
    return includedFields;
  }

  public <F> Field<F> getFieldByName(String name, Class<F> type) {
    return factory.createField(name, type, getTable());
  }

  public String getFieldName(String name) {
    return factory.getFieldName(name, getTable());
  }

  private SelectFinalStep<?> buildSelect() {
    Select<?> select;
    SelectFromStep<?> selectFrom;
    Settings settings = new Settings();
    settings.setRenderNameStyle(renderNameStyle);
    DSLContext ctx = DSL.using(dialect, settings);
    Table<?> table = getTable();

    if (asCount) {
      if (includedFieldNames.size() > 0) {
        List<Field<?>> distinctFields = fieldNamesToFields();
        selectFrom = ctx.select(DSL.countDistinct(distinctFields.toArray(new Field[distinctFields.size()])));
      } else {
        selectFrom = ctx.selectCount();
      }
    } else {
      SelectSelectStep<?> selectStep;
      if (includedFieldNames.size() > 0) {
        selectStep = ctx.select(fieldNamesToFields());
      } else {
        String tableName = sourceQuery.getBoundQuery().tableName();

        if (joinConditions.size() > 0) {
          selectStep = ctx.selectDistinct(DSL.field(tableName + ".*"));
        } else {
          selectStep = ctx.select(DSL.field("*"));
        }
      }
      if (additionalFields.size() > 0) {
        selectFrom = selectStep.select(additionalFields);
      } else {
        selectFrom = selectStep;
      }
    }
    select = selectFrom.from(table);
    for (JoinCondition joinCondition : joinConditions) {
      if (joinCondition.isLeftJoin()) {
        ((SelectJoinStep<?>) select).leftOuterJoin(joinCondition.getTable()).on(joinCondition.getCondition());
      } else {
        ((SelectJoinStep<?>) select).join(joinCondition.getTable()).on(joinCondition.getCondition());
      }
    }
    select = ((SelectJoinStep<?>) select).where(getConditions());

    if (groupByFields.size() > 0) {
      select = ((SelectJoinStep<?>) select).groupBy(groupByFields);
    }

    if (includeOrderings) {
      select = ((SelectOrderByStep<?>) select).orderBy(orderingsToSortFields());
    }
    if (includeConstraints) {
      if (sourceQuery.getLimit().isPresent()) {
        select = ((SelectLimitStep<?>) select).limit(sourceQuery.getLimit().get());
      }
      if (sourceQuery.getOffset().isPresent()) {
        select = ((SelectOffsetStep<?>) select).offset(sourceQuery.getOffset().get());
      }
    }
    return (SelectFinalStep<?>) select;
  }

  /**
   * Get just the list of Conditions (WHERE clauses) associated with the query.
   */
  public Collection<Condition> getConditions() {
    Collection<Condition> conditions = new ArrayList<>();
    conditions.add(sourceQuery.getCombinedFilterEntry().getCondition(sourceQuery.getBoundQuery(), factory));

    for (AdditionalCondition c : additionalConditions) {
      if (!asCount || c.includeInCount) {
        conditions.add(c.condition);
      }
    }
    return conditions;
  }

  public ParsedQuery<T> getSourceQuery() {
    return sourceQuery;
  }

  public static <T extends QuerySpec> SelectBuilder<T> forParsedQuery(ParsedQuery<T> parsed, MetaQuerySpec<T> context) {
    return new SelectBuilder<>(parsed, context);
  }

  public static <T extends QuerySpec> SelectBuilder<T> forParsedQuery(ParsedQuery<T> parsed) {
    return new SelectBuilder<>(parsed, new DefaultMetaQuerySpec<>(parsed.getQueryType()));
  }

  public Collection<SortField<?>> orderingsToSortFields() {
    ArrayList<SortField<?>> sorts = new ArrayList<>(sourceQuery.getOrderings().size());
    for (Ordering order : sourceQuery.getOrderings()) {
      sorts.add(getSortField(order).sort(order.getOrder()));
    }
    return sorts;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Field getSortField(Ordering order) {
    Map<String, BeanPropertyDefinition> fieldMap = meta.getFieldMap();
    BeanPropertyDefinition bpd = fieldMap.get(order.getQueryName());
    String fieldName = order.getQueryName();
    Class fieldType = meta.getFieldType(order.getFieldName());
    if (bpd.getField().getAnnotation(OrderBy.class).isGenerated()) {
      // it's possible to sort by generated fields
      // but we shouldn't qualify them with table name in the ORDER BY clause
      return DSL.field(DSL.name(fieldName), fieldType);
    } else {
      return factory.createField(fieldName, fieldType, getTable());
    }
  }

  public boolean containsSortField(String fieldName) {
    Collection<SortField<?>> sortFields = orderingsToSortFields();
    for (SortField<?> field : sortFields) {
      if (field.getName().equals(fieldName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Switch back to default, {@code ?} placeholders.
   */
  public SelectBuilder<T> withDefaultPlaceholders() {
    this.paramType = null;
    return this;
  }

  public static class BuiltSelect<T> {

    private final SelectFinalStep<?> select;
    private final ParamType paramType;

    public BuiltSelect(SelectFinalStep<?> select, ParamType paramType) {
      this.select = select;
      this.paramType = paramType;
    }

    @Override
    public String toString() {
      return paramType != null ? select.getSQL(paramType) : select.getSQL();
    }

    public SelectFinalStep<?> getRawSelect() {
      return select;
    }

  }

}

class AdditionalCondition {
  public final Condition condition;
  public final boolean includeInCount;

  public AdditionalCondition(Condition condition, boolean includeInCount) {
    this.condition = condition;
    this.includeInCount = includeInCount;
  }
}
