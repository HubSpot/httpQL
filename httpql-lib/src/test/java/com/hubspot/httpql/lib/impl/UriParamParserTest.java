package com.hubspot.httpql.lib.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hubspot.httpql.lib.error.FilterViolation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

public class UriParamParserTest {
  UriParamParser uriParamParser;

  @Before
  public void setup() {
    uriParamParser = UriParamParser.newBuilder().build();
  }

  @Test
  public void itRemovesReservedParams() {

    Map<String, List<String>> query = new HashMap<>();
    query.put("offset", Lists.newArrayList("1"));
    query.put("limit", Lists.newArrayList("1"));
    query.put("includeDeleted", Lists.newArrayList("1"));
    query.put("order", Lists.newArrayList("1"));
    query.put("orderBy", Lists.newArrayList("1", "2"));

    final ParsedUriParams parsedUriParams = uriParamParser.parseUriParams(query);
    assertThat(parsedUriParams.getOrderBys()).hasSize(3);
    assertThat(parsedUriParams.getFieldFilters()).isEmpty();
  }

  @Test
  public void itPreservesCommasInEqValues() {
    Map<String, List<String>> query = Collections.singletonMap("name", Collections.singletonList("1,2,3"));

    final ParsedUriParams parsedUriParams = uriParamParser.parseUriParams(query);
    assertThat(parsedUriParams.getFieldFilters().get(0).getValue()).isEqualTo("1,2,3");
  }

  @Test
  public void itAllowsDoubleUnderscoresInFieldNames() {
    Map<String, List<String>> query = new HashMap<>();
    query.put("foo1__bar1__eq", Lists.newArrayList("1"));
    query.put("foo2__bar2__contains", Lists.newArrayList("1"));
    query.put("foo3__eq", Lists.newArrayList("1"));
    query.put("foo4", Lists.newArrayList("1"));

    final ParsedUriParams parsedUriParams = uriParamParser.parseUriParams(query, true);

    assertThat(parsedUriParams.getFieldFilters().get(0).getField()).isEqualTo("foo2__bar2");
    assertThat(parsedUriParams.getFieldFilters().get(0).getFilterName()).isEqualTo("contains");

    assertThat(parsedUriParams.getFieldFilters().get(1).getField()).isEqualTo("foo3");
    assertThat(parsedUriParams.getFieldFilters().get(1).getFilterName()).isEqualTo("eq");

    assertThat(parsedUriParams.getFieldFilters().get(2).getField()).isEqualTo("foo1__bar1");
    assertThat(parsedUriParams.getFieldFilters().get(2).getFilterName()).isEqualTo("eq");

    assertThat(parsedUriParams.getFieldFilters().get(3).getField()).isEqualTo("foo4");
    assertThat(parsedUriParams.getFieldFilters().get(3).getFilterName()).isEqualTo("eq");
  }

  @Test
  public void itRequiresExplicitEqualFilterWithDoubleUnderscoreInFieldNames() {
    Map<String, List<String>> query = new HashMap<>();
    query.put("foo2__bar2", Lists.newArrayList("1"));

    assertThatThrownBy(
        () -> uriParamParser.parseUriParams(query, true)
    )
        .isInstanceOf(FilterViolation.class);
  }
}
