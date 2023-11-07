package com.hubspot.httpql.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.hubspot.httpql.QueryConstraints;
import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.ann.FilterBy;
import com.hubspot.httpql.core.ann.FilterJoinByDescriptor;
import com.hubspot.httpql.filter.Equal;
import com.hubspot.httpql.filter.In;
import com.hubspot.rosetta.annotations.RosettaNaming;

@RosettaNaming(SnakeCaseStrategy.class)
@QueryConstraints(defaultLimit = 10, maxLimit = 100, maxOffset = 1000)
public class EntityWithSimpleJoinDescriptor implements QuerySpec {

  @FilterBy(Equal.class)
  private Long id;

  @FilterBy(value = {In.class, Equal.class})
  @FilterJoinByDescriptor("com.hubspot.httpql.model.EntitySimpleJoinDescriptor")
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
