package com.hubspot.httpql.lib.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.hubspot.httpql.core.QuerySpec;
import com.hubspot.httpql.core.ann.FilterBy;
import com.hubspot.httpql.core.ann.FilterJoin;
import com.hubspot.httpql.core.ann.QueryConstraints;
import com.hubspot.httpql.core.filter.Equal;
import com.hubspot.httpql.core.filter.In;
import com.hubspot.rosetta.annotations.RosettaNaming;

@RosettaNaming(SnakeCaseStrategy.class)
@QueryConstraints(defaultLimit = 10, maxLimit = 100, maxOffset = 1000)
public class EntityWithJoin implements QuerySpec {

  @FilterBy(Equal.class)
  private Long id;

  @FilterBy(value = { In.class, Equal.class })
  @FilterJoin(table = "join_tbl", on = "id", eq = "entity_id")
  private Long topicId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTopicId() {
    return topicId;
  }

  public void setTopicId(Long topicId) {
    this.topicId = topicId;
  }

  @Override
  public String tableName() {
    return "entity_table";
  }

}
