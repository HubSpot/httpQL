package com.hubspot.httpql.lib.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.hubspot.httpql.core.ann.FilterBy;
import com.hubspot.httpql.core.ann.FilterJoinByDescriptor;
import com.hubspot.httpql.core.ann.QueryConstraints;
import com.hubspot.httpql.core.filter.Equal;
import com.hubspot.httpql.core.filter.In;
import com.hubspot.httpql.lib.QuerySpec;
import com.hubspot.rosetta.annotations.RosettaNaming;

@RosettaNaming(SnakeCaseStrategy.class)
@QueryConstraints(defaultLimit = 10, maxLimit = 100, maxOffset = 1000)
public class EntityWithSimpleJoinDescriptor implements QuerySpec {

  @FilterBy(Equal.class)
  private Long id;

  @FilterBy(value = {In.class, Equal.class})
  @FilterJoinByDescriptor("com.hubspot.httpql.lib.model.EntitySimpleJoinDescriptor")
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
