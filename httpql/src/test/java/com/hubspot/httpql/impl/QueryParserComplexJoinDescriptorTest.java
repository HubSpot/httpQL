package com.hubspot.httpql.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.jooq.SelectFinalStep;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hubspot.httpql.ParsedQuery;
import com.hubspot.httpql.model.EntityWithComplexJoinDescriptor;

public class QueryParserComplexJoinDescriptorTest {

  private Multimap<String, String> query;
  private QueryParser<EntityWithComplexJoinDescriptor> parser;

  @Before
  public void setup() {
    query = ArrayListMultimap.create();
    parser = QueryParser.newBuilder(EntityWithComplexJoinDescriptor.class).build();
  }

  @Test
  public void joinWithSingleBoundValue() {
    query.put("topicId__eq", "123");

    ParsedQuery<EntityWithComplexJoinDescriptor> parsedQuery = parser.parse(query);
    assertThat(parsedQuery.getBoundQuery().getTopicId()).isEqualTo(123L);

    SelectBuilder<EntityWithComplexJoinDescriptor> selectBuilder = SelectBuilder.forParsedQuery(parsedQuery);

    SelectFinalStep<?> sql = selectBuilder.build().getRawSelect();

    assertThat(StringUtils.normalizeSpace(sql.toString()))
        .isEqualTo("select distinct entity_table.* from entity_table "
            + "left outer join `join_tbl` on ( "
            + "`entity_table`.`group_id` = `join_tbl`.`id` "
            + "and `entity_table`.`tag` = `join_tbl`.`id` "
            + "and `join_tbl`.`meta_type` = 'joinObjects' ) "
            + "where ifnull(`join_tbl`.`topic_id`, 0) = '123' "
            + "limit 10");
  }

  @Test
  public void joinWithMultipleBoundValues() {
    query.put("topicId__in", "123");
    query.put("topicId__in", "456");

    ParsedQuery<EntityWithComplexJoinDescriptor> parsedQuery = parser.parse(query);

    SelectBuilder<EntityWithComplexJoinDescriptor> selectBuilder = SelectBuilder.forParsedQuery(parsedQuery);

    SelectFinalStep<?> sql = selectBuilder.build().getRawSelect();

    assertThat(StringUtils.normalizeSpace(sql.toString()))
        .isEqualTo("select distinct entity_table.* from entity_table "
            + "left outer join `join_tbl` on ( "
            + "`entity_table`.`group_id` = `join_tbl`.`id` "
            + "and `entity_table`.`tag` = `join_tbl`.`id` "
            + "and `join_tbl`.`meta_type` = 'joinObjects' ) "
            + "where ifnull(`join_tbl`.`topic_id`, 0) in ( 123, 456 ) "
            + "limit 10");
  }

}
