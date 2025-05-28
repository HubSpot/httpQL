package com.hubspot.httpql;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.google.common.base.Enums;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.hubspot.httpql.ann.FilterBy;
import com.hubspot.httpql.ann.OrderBy;
import com.hubspot.httpql.core.OrderingIF;
import com.hubspot.httpql.filter.Contains;
import com.hubspot.httpql.filter.Equal;
import com.hubspot.httpql.filter.GreaterThan;
import com.hubspot.httpql.filter.In;
import com.hubspot.httpql.filter.IsDistinctFrom;
import com.hubspot.httpql.filter.IsNotDistinctFrom;
import com.hubspot.httpql.filter.NotIn;
import com.hubspot.httpql.filter.NotLike;
import com.hubspot.httpql.impl.DefaultFieldFactory;
import com.hubspot.httpql.impl.QueryParser;
import com.hubspot.httpql.internal.BoundFilterEntry;
import com.hubspot.httpql.internal.MultiValuedBoundFilterEntry;
import com.hubspot.rosetta.annotations.RosettaNaming;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.jooq.Param;
import org.junit.Before;
import org.junit.Test;

public class ParsedQueryTest {

  Multimap<String, String> query;
  QueryParser<Spec> parser;
  FieldFactory fieldFactory = new DefaultFieldFactory();

  @Before
  public void setUp() {
    query = ArrayListMultimap.create();
    parser = QueryParser.newBuilder(Spec.class).build();
  }

  @Test
  public void itContainsBoundFilterEntries() {
    query.put("id__eq", "1");
    query.put("count__gt", "100");

    ParsedQuery<Spec> parsed = parser.parse(query);

    assertThat(parsed.getBoundFilterEntries()).hasSize(2);
  }

  @Test
  public void itWorksWithConditionProvider() {
    query.put("count__gt", "100");

    ParsedQuery<Spec> parsed = parser.parse(query);
    BoundFilterEntry<Spec> bfe = parsed.getBoundFilterEntries().get(0);
    ConditionProvider<?> provider = bfe.getConditionProvider(fieldFactory);

    provider = bfe.getConditionProvider(fieldFactory);
    Param<?> value = provider.getParam(parsed.getBoundQuery().getCount(), "count");

    assertThat(provider.getField().getName()).isEqualTo("count");
    assertThat(provider.getField().getType()).isEqualTo(Long.class);
    assertThat(value.getValue()).isEqualTo(100L);
  }

  @Test
  public void itWorksWithMultiParamConditionProvider() {
    query.putAll("id__in", ImmutableList.of("1", "2", "3"));

    ParsedQuery<Spec> parsed = parser.parse(query);
    BoundFilterEntry<Spec> bfe = parsed.getBoundFilterEntries().get(0);
    ConditionProvider<?> provider = bfe.getConditionProvider(fieldFactory);

    assertThat(provider.getField().getName()).isEqualTo("id");
    assertThat(provider.getField().getType()).isEqualTo(Integer.class);
    // Wrong method call would result in RuntimeException...
    assertThat(provider.getCondition(ImmutableList.of(1, 2, 3), "id")).isNotNull();
  }

  @Test
  public void hasFilter() {
    query.putAll("id__in", ImmutableList.of("1", "2", "3"));
    ParsedQuery<Spec> parsed = parser.parse(query);

    assertThat(parsed.hasFilter("id")).isTrue();
    assertThat(parsed.hasFilter("ids")).isFalse();
    assertThat(parsed.hasFilter("count")).isFalse();
  }

  @Test
  public void hasEnumFilter() {
    query.putAll("specEnum__in", ImmutableList.of("1", "2", "3"));
    ParsedQuery<Spec> parsed = parser.parse(query);

    assertThat(parsed.hasFilter("spec_enum")).isTrue();
    assertThat(parsed.hasFilter("specEnum")).isFalse();
    assertThat(parsed.hasFilter("spec_enums")).isFalse();
    assertThat(parsed.hasFilter("count")).isFalse();
  }

  @Test
  public void addFilter() {
    query.put("id__eq", "1");
    ParsedQuery<Spec> parsed = parser.parse(query);

    parsed.addFilter("count", Equal.class, 15L);

    assertThat(parsed.getBoundFilterEntries()).hasSize(2);
    assertThat(parsed.getBoundQuery().getCount()).isEqualTo(15);

    // Should only update the value!
    parsed.addFilter("count", Equal.class, 20L);

    assertThat(parsed.getBoundFilterEntries()).hasSize(2);
    assertThat(parsed.getBoundQuery().getCount()).isEqualTo(20);
  }

  @Test
  public void addFilterExclusively() {
    query.put("id__in", "1");
    query.put("id__in", "2");
    query.put("id__eq", "3");
    ParsedQuery<Spec> parsed = parser.parse(query);

    parsed.addFilterExclusively("id", Equal.class, 15);

    assertThat(parsed.getBoundFilterEntries()).hasSize(1);
    assertThat(parsed.getBoundQuery().getId()).isEqualTo(15);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void addFilterEntryConditionCreatorExclusively() {
    query.put("id__in", "1");
    query.put("id__in", "2");
    query.put("id__eq", "3");
    ParsedQuery<Spec> parsed = parser.parse(query);

    BoundFilterEntry<Spec> idFilter = new MultiValuedBoundFilterEntry<>(
      parsed.getMetaData().getNewBoundFilterEntry("id", Equal.class),
      ImmutableSet.of(4)
    );

    parsed.addFilterEntryConditionCreatorExclusively("id", idFilter);

    assertThat(parsed.getBoundFilterEntries()).hasSize(1);
    assertThat(
      ((MultiValuedBoundFilterEntry<Spec>) parsed
          .getBoundFilterEntries()
          .get(0)).getValues()
    )
      .hasSize(1);
    assertThat(
      (Set<Integer>) ((MultiValuedBoundFilterEntry<Spec>) parsed
          .getBoundFilterEntries()
          .get(0)).getValues()
    )
      .containsExactly(4);
  }

  @Test
  public void addInFilter() {
    query.put("count__eq", "11");
    ParsedQuery<Spec> parsed = parser.parse(query);

    parsed.addFilter("id", In.class, Lists.newArrayList(1, 2));

    assertThat(parsed.getBoundFilterEntries()).hasSize(2);
    assertThat(parsed.hasFilter("id"));
    // assertThat(parsed.getBoundQuery().getIds()).containsExactly(1, 2);
  }

  @Test
  public void addNotLikeFilter() {
    query.put("count__eq", "11");
    ParsedQuery<Spec> parsed = parser.parse(query);

    parsed.addFilter("comments", NotLike.class, Lists.newArrayList("%John%", "Jane%"));

    assertThat(parsed.getBoundFilterEntries()).hasSize(2);
    assertThat(parsed.hasFilter("comments"));
  }

  @Test
  public void itAddsIsDistinctFromFilter() {
    query.put("count__distinct", "12,100");
    ParsedQuery<Spec> parsed = parser.parse(query);

    assertThat(parsed.getBoundFilterEntries()).hasSize(1);
  }

  @Test
  public void itAddsIsNotDistinctFromFilter() {
    query.put("count__ndistinct", "1");
    ParsedQuery<Spec> parsed = parser.parse(query);

    assertThat(parsed.getBoundFilterEntries()).hasSize(1);
  }

  @Test
  public void itaddsOrderBy() {
    query.put("order", "-count");
    ParsedQuery<Spec> parsed = parser.parse(query);

    assertThat(parsed.getOrderings()).hasSize(1);
    for (OrderingIF ordering : parsed.getOrderings()) {
      assertThat(ordering.getFieldName()).isEqualTo("count");
    }
  }

  @Test
  public void itSupportsMultipleOrders() {
    query.put("order", "-count");
    query.put("order", "createDate");
    ParsedQuery<Spec> parsed = parser.parse(query);

    assertThat(parsed.getOrderings()).hasSize(2);
  }

  @Test
  public void itSupportsSnakeCaseOrders() {
    query.put("order", "-count");
    query.put("order", "create_date");
    ParsedQuery<Spec> parsed = parser.parse(query);

    assertThat(parsed.getOrderings()).hasSize(2);
  }

  @Test
  public void itSupportsCamelForSnakeCaseModels() {
    query.put("order", "-count");
    query.put("order", "createDate");
    query.put("createDate", "100000");
    ParsedQuery<Spec> parsed = parser.parse(query);

    assertThat(parsed.getOrderings()).hasSize(2);
    assertThat(parsed.hasFilter("create_date"));
  }

  public static class SpecParent {

    Integer id;

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }
  }

  public enum SpecEnum {
    TEST_ONE,
    TEST_TWO,
    TEST_THREE;

    @Override
    public String toString() {
      return this.name().toLowerCase();
    }

    @JsonCreator
    public static SpecEnum forValue(String name) {
      if (StringUtils.isBlank(name)) {
        return null;
      }

      return Enums.getIfPresent(SpecEnum.class, name.toUpperCase()).orNull();
    }

    @JsonValue
    public String toValue() {
      return this.toString();
    }
  }

  @QueryConstraints(defaultLimit = 10, maxLimit = 100, maxOffset = 100)
  @RosettaNaming(SnakeCaseStrategy.class)
  public static class Spec extends SpecParent implements QuerySpec {

    @OrderBy
    @FilterBy(
      value = {
        GreaterThan.class, Equal.class, IsDistinctFrom.class, IsNotDistinctFrom.class,
      }
    )
    Long count;

    @OrderBy
    @FilterBy(value = { GreaterThan.class, Equal.class })
    private Long createDate;

    boolean secret;

    @FilterBy(value = { In.class, NotIn.class })
    private SpecEnum specEnum;

    @Override
    public String tableName() {
      return "";
    }

    @Override
    @FilterBy({ Equal.class, In.class })
    public Integer getId() {
      return super.getId();
    }

    public Long getCount() {
      return count;
    }

    public void setCount(Long count) {
      this.count = count;
    }

    /**
     * @return the secret
     */
    public boolean isSecret() {
      return secret;
    }

    @FilterBy({ Contains.class, NotLike.class })
    String comments;

    /**
     * @param secret
     *          the secret to set
     */
    public void setSecret(boolean secret) {
      this.secret = secret;
    }

    public Long getCreateDate() {
      return createDate;
    }

    public void setCreateDate(Long createDate) {
      this.createDate = createDate;
    }

    public SpecEnum getSpecEnum() {
      return specEnum;
    }

    public void setSpecEnum(SpecEnum specEnum) {
      this.specEnum = specEnum;
    }

    public String getComments() {
      return comments;
    }

    public void setComments(String comments) {
      this.comments = comments;
    }
  }
}
