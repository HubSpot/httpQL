package com.hubspot.httpql.impl;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class UriParamParserTest {
  UriParamParser uriParamParser;

  @Before
  public void setup() {
    uriParamParser = UriParamParser.newBuilder().build();
  }

  @Test
  public void itRemovesReservedParams() {

    MultivaluedMap<String, String> query = new MultivaluedMapImpl();
    query.add("offset", "1");
    query.add("limit", "1");
    query.add("includeDeleted", "1");
    query.add("order", "1");
    query.add("orderBy", "1");
    query.add("orderBy", "2");

    final ParsedUriParams parsedUriParams = uriParamParser.parseUriParams(query);
    assertThat(parsedUriParams.getOrderBys()).hasSize(3);
    assertThat(parsedUriParams.getFieldFilters()).isEmpty();
  }

  @Test
  public void itPreservesCommasInEqValues() {
    MultivaluedMap<String, String> query = new MultivaluedMapImpl();
    query.add("name", "1,2,3");

    final ParsedUriParams parsedUriParams = uriParamParser.parseUriParams(query);
    assertThat(parsedUriParams.getFieldFilters().get(0).getValue()).isEqualTo("1,2,3");
  }

}
