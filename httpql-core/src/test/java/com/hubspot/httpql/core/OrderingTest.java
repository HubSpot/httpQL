package com.hubspot.httpql.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

public class OrderingTest {

  private ObjectMapper mapper;

  @Before
  public void setup() {
    mapper = new ObjectMapper();
  }

  @Test
  public void itGetsOrderString() {
    assertThat(new Ordering("foo", SortOrder.ASC).getOrderString())
      .isEqualTo(SortOrder.ASC.toSQL());
    assertThat(new Ordering("foo", SortOrder.DESC).getOrderString())
      .isEqualTo(SortOrder.DESC.toSQL());
  }

  @Test
  public void testJsonWithField() throws Exception {
    Ordering o1 = new Ordering("foo", SortOrder.ASC);
    String json = mapper.writeValueAsString(o1);

    Ordering o2 = mapper.readValue(json, Ordering.class);
    assertThat(o2).isEqualToComparingFieldByField(o1);
    assertThat(o2.equals(o1)).isTrue();

    o1 = new Ordering("foo", SortOrder.DESC);
    json = mapper.writeValueAsString(o1);

    o2 = mapper.readValue(json, Ordering.class);
    assertThat(o2).isEqualToComparingFieldByField(o1);
  }

  @Test
  public void testJsonToIF() throws JsonProcessingException {
    Ordering o1 = new Ordering("foo", SortOrder.ASC);
    String json = mapper.writeValueAsString(o1);
    OrderingIF o2 = mapper.readValue(json, OrderingIF.class);
    assertThat(o2).isEqualToComparingFieldByField(o1);
  }

  @Test
  public void testJsonWithQueryField() throws Exception {
    Ordering o1 = new Ordering("foo", "bar", SortOrder.ASC);
    String json = mapper.writeValueAsString(o1);

    Ordering o2 = mapper.readValue(json, Ordering.class);
    assertThat(o2).isEqualToComparingFieldByField(o1);
    assertThat(o2.equals(o1)).isTrue();

    o1 = new Ordering("foo", "bar", SortOrder.DESC);
    json = mapper.writeValueAsString(o1);

    o2 = mapper.readValue(json, Ordering.class);
    assertThat(o2).isEqualToComparingFieldByField(o1);
  }
}
