package com.hubspot.httpql.impl;

import static org.assertj.core.api.Assertions.assertThat;

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

}
