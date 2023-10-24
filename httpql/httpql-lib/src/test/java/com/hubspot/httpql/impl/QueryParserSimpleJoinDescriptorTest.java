package com.hubspot.httpql.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hubspot.httpql.ParsedQuery;
import com.hubspot.httpql.model.EntityWithSimpleJoinDescriptor;

import org.apache.commons.lang.StringUtils;
import org.jooq.SelectFinalStep;
import org.junit.Before;
import org.junit.Test;

public class QueryParserSimpleJoinDescriptorTest {

  private Multimap<String, String> query;
  private QueryParser<EntityWithSimpleJoinDescriptor> parser;

  @Before
  public void setup() {
    query = ArrayListMultimap.create();
    parser = QueryParser.newBuilder(EntityWithSimpleJoinDescriptor.class).build();
  }

  @Test
  public void joinWithSingleBoundValue() {
    query.put("topicId__eq", "123");

    ParsedQuery<EntityWithSimpleJoinDescriptor> parsedQuery = parser.parse(query);
    assertThat(parsedQuery.getBoundQuery().getTopicId()).isEqualTo(123L);

    SelectBuilder<EntityWithSimpleJoinDescriptor> selectBuilder = SelectBuilder.forParsedQuery(parsedQuery);

    SelectFinalStep<?> sql = selectBuilder.build().getRawSelect();

    assertThat(StringUtils.normalizeSpace(sql.toString()))
        .isEqualTo("select distinct entity_table.* from entity_table "
            + "join `join_tbl` on `entity_table`.`id` = `join_tbl`.`entity_id` "
            + "where `join_tbl`.`topic_id` = '123' limit 10");
  }

  @Test
  public void joinWithMultipleBoundValues() {
    query.put("topicId__in", "123");
    query.put("topicId__in", "456");

    ParsedQuery<EntityWithSimpleJoinDescriptor> parsedQuery = parser.parse(query);

    SelectBuilder<EntityWithSimpleJoinDescriptor> selectBuilder = SelectBuilder.forParsedQuery(parsedQuery);

    SelectFinalStep<?> sql = selectBuilder.build().getRawSelect();

    assertThat(StringUtils.normalizeSpace(sql.toString()))
        .isEqualTo("select distinct entity_table.* from entity_table "
            + "join `join_tbl` on `entity_table`.`id` = `join_tbl`.`entity_id` "
            + "where `join_tbl`.`topic_id` in ( 123, 456 ) limit 10");
  }

}
