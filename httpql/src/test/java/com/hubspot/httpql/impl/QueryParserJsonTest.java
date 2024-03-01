package com.hubspot.httpql.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.hubspot.httpql.ParsedQuery;
import com.hubspot.httpql.QueryConstraints;
import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.ann.FilterBy;
import com.hubspot.httpql.ann.OrderBy;
import com.hubspot.httpql.core.filter.JsonContains;
import com.hubspot.httpql.core.filter.JsonEqual;
import com.hubspot.httpql.core.filter.JsonGreaterThan;
import com.hubspot.httpql.core.filter.JsonGreaterThanOrEqual;
import com.hubspot.httpql.core.filter.JsonIn;
import com.hubspot.httpql.core.filter.JsonInsensitiveContains;
import com.hubspot.httpql.core.filter.JsonIsDistinctFrom;
import com.hubspot.httpql.core.filter.JsonIsNotDistinctFrom;
import com.hubspot.httpql.core.filter.JsonLessThan;
import com.hubspot.httpql.core.filter.JsonLessThanOrEqual;
import com.hubspot.httpql.core.filter.JsonNotEqual;
import com.hubspot.httpql.core.filter.JsonNotIn;
import com.hubspot.httpql.core.filter.JsonNotLike;
import com.hubspot.httpql.core.filter.JsonNotNull;
import com.hubspot.httpql.core.filter.JsonNull;
import com.hubspot.httpql.core.filter.JsonRange;
import com.hubspot.httpql.core.filter.JsonStartsWith;
import com.hubspot.httpql.filter.Equal;
import com.hubspot.httpql.filter.In;
import com.hubspot.httpql.filter.NotNull;
import com.hubspot.httpql.filter.Null;
import com.hubspot.rosetta.annotations.RosettaNaming;
import org.jooq.JSON;
import org.jooq.conf.Settings;
import org.junit.Before;
import org.junit.Test;

public class QueryParserJsonTest {
  private Multimap<String, String> query;
  private QueryParser<Spec> parser;
  private Settings settings = new Settings().withRenderFormatted(true);

  @Before
  public void setUp() {
    query = ArrayListMultimap.create();
    parser = QueryParser.newBuilder(Spec.class).build();
  }

  @Test
  public void itBindsJsonContains() {
    query.putAll("jsonBody__json_contains", ImmutableList.of("$.a", "abc"));
    query.putAll("jsonStringBody__json_contains", ImmutableList.of("$.a", "def"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
          .replace("(", " (")
          .replace(")", ")")
      )
      .startsWith("select *\n" + "from \n" + "where  (")
      .contains(
        "json_extract (cast (`json_body` as json), '$.a') like concat (\n" +
        "    '%',\n" +
        "    cast ('abc' as char),\n" +
        "    '%'\n" +
        "  ) escape '!'"
      )
      .contains(
        "and json_extract (cast (`json_string_body` as json), '$.a') like concat (\n" +
        "    '%',\n" +
        "    cast ('def' as char),\n" +
        "    '%'\n" +
        "  ) escape '!'"
      )
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonEqual() {
    query.putAll("jsonBody__json_eq", ImmutableList.of("$.a", "1"));
    query.putAll("jsonStringBody__json_eq", ImmutableList.of("$.a", "2"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains("cast(json_extract(cast(`json_body` as json), '$.a') as decimal) = 1")
      .contains(
        "cast(json_extract(cast(`json_string_body` as json), '$.a') as decimal) = 2"
      )
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonGreaterThan() {
    query.putAll("jsonBody__json_gt", ImmutableList.of("$.a", "1"));
    query.putAll("jsonStringBody__json_gt", ImmutableList.of("$.a", "2"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains("cast(json_extract(cast(`json_body` as json), '$.a') as decimal) > 1")
      .contains(
        "cast(json_extract(cast(`json_string_body` as json), '$.a') as decimal) > 2"
      )
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonGreaterThanOrEqual() {
    query.putAll("jsonBody__json_gte", ImmutableList.of("$.a", "1"));
    query.putAll("jsonStringBody__json_gte", ImmutableList.of("$.a", "2"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains("cast(json_extract(cast(`json_body` as json), '$.a') as decimal) >= 1")
      .contains(
        "cast(json_extract(cast(`json_string_body` as json), '$.a') as decimal) >= 2"
      )
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonIn() {
    query.putAll("jsonBody__json_in", ImmutableList.of("$.a", "1", "2"));
    query.putAll("jsonStringBody__json_in", ImmutableList.of("$.a", "3", "4"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains(
        "json_extract(cast(`json_body` as json), '$.a') in (\n" + "    '1', '2'\n" + "  )"
      )
      .contains(
        "json_extract(cast(`json_string_body` as json), '$.a') in (\n" +
        "    '3', '4'\n" +
        "  )"
      )
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonInsensitiveContains() {
    query.putAll("jsonBody__json_icontains", ImmutableList.of("$.a", "abc"));
    query.putAll("jsonStringBody__json_icontains", ImmutableList.of("$.a", "def"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains(
        "lower(cast(json_extract(cast(`json_body` as json), '$.a') as char)) like lower('%abc%') escape '!'"
      )
      .contains(
        "lower(cast(json_extract(cast(`json_string_body` as json), '$.a') as char)) like lower('%def%') escape '!'"
      )
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonDistinctFrom() {
    query.putAll("jsonBody__json_distinct", ImmutableList.of("$.a", "1", "2"));
    query.putAll("jsonStringBody__json_distinct", ImmutableList.of("$.a", "3", "4"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains("(not(json_extract(cast(`json_body` as json), '$.a') <=> '1'))")
      .contains("(not(json_extract(cast(`json_body` as json), '$.a') <=> '2'))")
      .contains("(not(json_extract(cast(`json_string_body` as json), '$.a') <=> '3'))")
      .contains("(not(json_extract(cast(`json_string_body` as json), '$.a') <=> '4'))")
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonNotDistinctFrom() {
    query.putAll("jsonBody__json_ndistinct", ImmutableList.of("$.a", "abc"));
    query.putAll("jsonStringBody__json_ndistinct", ImmutableList.of("$.a", "def"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains("json_extract(cast(`json_body` as json), '$.a') <=> 'abc'")
      .contains("json_extract(cast(`json_string_body` as json), '$.a') <=> 'def'")
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonLessThan() {
    query.putAll("jsonBody__json_lt", ImmutableList.of("$.a", "1"));
    query.putAll("jsonStringBody__json_lt", ImmutableList.of("$.a", "2"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains("cast(json_extract(cast(`json_body` as json), '$.a') as decimal) < 1")
      .contains(
        "cast(json_extract(cast(`json_string_body` as json), '$.a') as decimal) < 2"
      )
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonLessThanOrEqual() {
    query.putAll("jsonBody__json_lte", ImmutableList.of("$.a", "1"));
    query.putAll("jsonStringBody__json_lte", ImmutableList.of("$.a", "2"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains("cast(json_extract(cast(`json_body` as json), '$.a') as decimal) <= 1")
      .contains(
        "cast(json_extract(cast(`json_string_body` as json), '$.a') as decimal) <= 2"
      )
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonNotEqual() {
    query.putAll("jsonBody__json_ne", ImmutableList.of("$.a", "1"));
    query.putAll("jsonStringBody__json_ne", ImmutableList.of("$.a", "2"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains("json_extract(cast(`json_body` as json), '$.a') <> '1'")
      .contains("json_extract(cast(`json_string_body` as json), '$.a') <> '2'")
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonNotIn() {
    query.putAll("jsonBody__json_nin", ImmutableList.of("$.a", "1", "2"));
    query.putAll("jsonStringBody__json_nin", ImmutableList.of("$.a", "3", "4"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains(
        "json_extract(cast(`json_body` as json), '$.a') not in (\n" +
        "    '1', '2'\n" +
        "  )"
      )
      .contains(
        "json_extract(cast(`json_string_body` as json), '$.a') not in (\n" +
        "    '3', '4'\n" +
        "  )"
      )
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonNotLike() {
    query.putAll("jsonBody__json_nlike", ImmutableList.of("$.a", "%abc"));
    query.putAll("jsonStringBody__json_nlike", ImmutableList.of("$.a", "def%"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains(
        "cast(json_extract(cast(`json_body` as json), '$.a') as char) not like '%abc' escape '!'"
      )
      .contains(
        "cast(json_extract(cast(`json_string_body` as json), '$.a') as char) not like 'def%' escape '!'"
      )
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonNotNull() {
    query.put("jsonBody__json_not_null", "$.a");
    query.put("jsonStringBody__json_not_null", "$.a");

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains("json_extract(cast(`json_body` as json), '$.a') is not null")
      .contains("json_extract(cast(`json_string_body` as json), '$.a') is not null")
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonNull() {
    query.put("jsonBody__json_is_null", "$.a");
    query.put("jsonStringBody__json_is_null", "$.a");

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains("json_extract(cast(`json_body` as json), '$.a') is null")
      .contains("json_extract(cast(`json_string_body` as json), '$.a') is null")
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonRange() {
    query.putAll("jsonBody__json_range", ImmutableList.of("$.a", "1", "3"));
    query.putAll("jsonStringBody__json_range", ImmutableList.of("$.a", "4", "6"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains(
        "cast(json_extract(cast(`json_body` as json), '$.a') as decimal) between 1 and 3"
      )
      .contains(
        "cast(json_extract(cast(`json_string_body` as json), '$.a') as decimal) between 4 and 6"
      )
      .endsWith(")\nlimit 20");
  }

  @Test
  public void itBindsJsonStartsWith() {
    query.putAll("jsonBody__json_startswith", ImmutableList.of("$.a", "abc"));
    query.putAll("jsonStringBody__json_startswith", ImmutableList.of("$.a", "def"));

    ParsedQuery<Spec> parsedQuery = parser.parse(query);

    // This looks wild, but all those escapes happen for the non-json version too
    assertThat(
        SelectBuilder
          .forParsedQuery(parsedQuery)
          .build(settings)
          .getRawSelect()
          .toString()
      )
      .startsWith("select *\nfrom \nwhere (")
      .contains(
        "cast(json_extract(cast(`json_body` as json), '$.a') as char) like concat(\n" +
        "    replace(\n" +
        "      replace(\n" +
        "        replace('abc', '!', '!!'),\n" +
        "        '%',\n" +
        "        '!%'\n" +
        "      ),\n" +
        "      '_',\n" +
        "      '!_'\n" +
        "    ),\n" +
        "    '%'\n" +
        "  ) escape '!'"
      )
      .contains(
        "cast(json_extract(cast(`json_string_body` as json), '$.a') as char) like concat(\n" +
        "    replace(\n" +
        "      replace(\n" +
        "        replace('def', '!', '!!'),\n" +
        "        '%',\n" +
        "        '!%'\n" +
        "      ),\n" +
        "      '_',\n" +
        "      '!_'\n" +
        "    ),\n" +
        "    '%'\n" +
        "  ) escape '!'"
      )
      .endsWith(")\nlimit 20");
  }

  @com.hubspot.httpql.core.ann.QueryConstraints(
    defaultLimit = 20,
    maxLimit = 200,
    maxOffset = 200
  )
  @QueryConstraints(defaultLimit = 10, maxLimit = 100, maxOffset = 100)
  @RosettaNaming(SnakeCaseStrategy.class)
  public static class Spec implements QuerySpec {
    @FilterBy({ Equal.class, In.class, Null.class, NotNull.class })
    @OrderBy
    Integer id;

    @com.hubspot.httpql.core.ann.OrderBy
    @com.hubspot.httpql.core.ann.FilterBy(
      {
        com.hubspot.httpql.core.filter.GreaterThan.class,
        com.hubspot.httpql.core.filter.Null.class,
        com.hubspot.httpql.core.filter.Range.class
      }
    )
    Long count;

    @FilterBy(Equal.class)
    String fullName;

    @com.hubspot.httpql.core.ann.FilterBy(
      {
        com.hubspot.httpql.core.filter.Equal.class,
        com.hubspot.httpql.core.filter.StartsWith.class
      }
    )
    String middleName;

    boolean secret;

    @com.hubspot.httpql.core.ann.FilterBy(
      {
        JsonEqual.class,
        JsonContains.class,
        JsonEqual.class,
        JsonGreaterThan.class,
        JsonGreaterThanOrEqual.class,
        JsonIn.class,
        JsonInsensitiveContains.class,
        JsonIsDistinctFrom.class,
        JsonIsNotDistinctFrom.class,
        JsonLessThan.class,
        JsonLessThanOrEqual.class,
        JsonNotEqual.class,
        JsonNotIn.class,
        JsonRange.class,
        JsonStartsWith.class,
        JsonNull.class,
        JsonNotNull.class,
        JsonNotLike.class
      }
    )
    JSON jsonBody;

    @com.hubspot.httpql.core.ann.FilterBy(
      {
        JsonEqual.class,
        JsonContains.class,
        JsonEqual.class,
        JsonGreaterThan.class,
        JsonGreaterThanOrEqual.class,
        JsonIn.class,
        JsonInsensitiveContains.class,
        JsonIsDistinctFrom.class,
        JsonIsNotDistinctFrom.class,
        JsonLessThan.class,
        JsonLessThanOrEqual.class,
        JsonNotEqual.class,
        JsonNotIn.class,
        JsonRange.class,
        JsonStartsWith.class,
        JsonNull.class,
        JsonNotNull.class,
        JsonNotLike.class
      }
    )
    String jsonStringBody;

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

    public JSON getJsonBody() {
      return jsonBody;
    }

    public String getJsonStringBody() {
      return jsonStringBody;
    }
  }
}
