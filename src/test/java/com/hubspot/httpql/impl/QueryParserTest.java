package com.hubspot.httpql.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.hubspot.httpql.ParsedQuery;
import com.hubspot.httpql.QueryConstraints;
import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.ann.FilterBy;
import com.hubspot.httpql.error.ConstraintViolation;
import com.hubspot.httpql.error.FilterViolation;
import com.hubspot.httpql.filter.Equal;
import com.hubspot.httpql.filter.GreaterThan;
import com.hubspot.httpql.filter.In;
import com.hubspot.rosetta.annotations.RosettaNaming;

public class QueryParserTest {

  private Multimap<String, String> query;
  private QueryParser<Spec> parser;

  @Before
  public void setUp() {
    query = ArrayListMultimap.create();
    parser = QueryParser.newBuilder(Spec.class).build();
  }

  @Test
  public void itBindsSingleValues() {
    query.put("id__eq", "1");
    query.put("count__gt", "100");

    Spec spec = parser.parse(query).getBoundQuery();

    assertThat(spec.getId()).isEqualTo(1);
    assertThat(spec.getCount()).isEqualTo(100L);
  }

  @Test
  public void itBindsMultipleValues() {
    query.putAll("id__in", ImmutableList.of("1", "2", "3"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(StringUtils.normalizeSpace(SelectBuilder.forParsedQuery(parsedQuery).build().getRawSelect().toString()))
        .isEqualTo("select * from where `id` in ( 1, 2, 3 ) limit 10");
  }

  @Test
  public void itBindsCommaDelimitedMultipleValues() {
    query.put("id__in", "1,2,3");

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(StringUtils.normalizeSpace(SelectBuilder.forParsedQuery(parsedQuery).build().getRawSelect().toString()))
        .isEqualTo("select * from where `id` in ( 1, 2, 3 ) limit 10");
  }

  @Test
  public void itChecksCamelCaseStrategy() {
    query.put("fullName", "example");
    query.put("middle_name", "middle");

    Spec spec = parser.parse(query).getBoundQuery();

    assertThat(spec.getFullName()).isEqualTo("example");
    assertThat(spec.getMiddleName()).isEqualTo("middle");
  }

  @Test
  public void itUsesNamingStrategy() {
    query.put("full_name", "example");

    Spec spec = parser.parse(query).getBoundQuery();

    assertThat(spec.getFullName()).isEqualTo("example");
  }

  @Test(expected = FilterViolation.class)
  public void itDisallowsUnknownFilters() {
    query.put("secret__eq", "true");

    parser.parse(query);
  }

  @Test(expected = FilterViolation.class)
  public void itDisallowsMissingFilters() {
    query.put("count__eq", "1");

    parser.parse(query);
  }

  @Test(expected = FilterViolation.class)
  public void itDisallowsInvalidFilters() {
    query.put("count__something", "1");
    parser.parse(query);
  }

  @Test(expected = FilterViolation.class)
  public void itDisallowsInvalidFilterValues() {
    query.put("count__gt", "blah");
    parser.parse(query);
  }

  @Test(expected = ConstraintViolation.class)
  public void itRespectsMinLimit() {
    query.put("limit", "-1");
    parser.parse(query);
  }

  @Test(expected = ConstraintViolation.class)
  public void itRespectsMinOffset() {
    query.put("offset", "-1");
    parser.parse(query);
  }

  @Test
  public void itGetsLimitAndOffset() {
    Optional<Integer> limit = parser.getLimit(Optional.of(20));
    assertThat(limit.get()).isEqualTo(20);
    Optional<Integer> offset = parser.getOffset(Optional.of(10));
    assertThat(offset.get()).isEqualTo(10);
  }

  @Test
  public void itUsesDefaultsWhenHigherThanMaxValues() {
    Optional<Integer> limit = parser.getLimit(Optional.of(1000));
    assertThat(limit.get()).isEqualTo(100);
    Optional<Integer> offset = parser.getOffset(Optional.of(200));
    assertThat(offset.get()).isEqualTo(100);
  }

  @QueryConstraints(defaultLimit = 10, maxLimit = 100, maxOffset = 100)
  @RosettaNaming(SnakeCaseStrategy.class)
  public static class Spec implements QuerySpec {

    @FilterBy({
        Equal.class, In.class
    })
    Integer id;

    @FilterBy(GreaterThan.class)
    Long count;

    @FilterBy(Equal.class)
    String fullName;
    @FilterBy(Equal.class)
    String middleName;
    boolean secret;

    @Override
    public String tableName() {
      return "";
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public Long getCount() {
      return count;
    }

    public void setCount(Long count) {
      this.count = count;
    }

    public String getFullName() {
      return fullName;
    }

    public void setFullName(String fullName) {
      this.fullName = fullName;
    }

    public boolean isSecret() {
      return secret;
    }

    public void setSecret(boolean secret) {
      this.secret = secret;
    }

    public String getMiddleName() {
      return middleName;
    }

    public void setMiddleName(String middleName) {
      this.middleName = middleName;
    }
  }

}
