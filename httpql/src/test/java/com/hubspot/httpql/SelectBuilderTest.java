package com.hubspot.httpql;

import static org.assertj.core.api.Assertions.assertThat;

import org.jooq.Operator;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.hubspot.httpql.ann.FilterBy;
import com.hubspot.httpql.ann.OrderBy;
import com.hubspot.httpql.filter.Contains;
import com.hubspot.httpql.filter.Equal;
import com.hubspot.httpql.filter.GreaterThan;
import com.hubspot.httpql.filter.In;
import com.hubspot.httpql.filter.NotLike;
import com.hubspot.httpql.impl.PrefixingAliasFieldFactory;
import com.hubspot.httpql.impl.QueryParser;
import com.hubspot.httpql.impl.SelectBuilder;
import com.hubspot.httpql.impl.TableQualifiedFieldFactory;
import com.hubspot.httpql.internal.BoundFilterEntry;
import com.hubspot.httpql.internal.CombinedConditionCreator;
import com.hubspot.httpql.internal.MultiValuedBoundFilterEntry;
import com.hubspot.rosetta.annotations.RosettaNaming;

public class SelectBuilderTest {

  Multimap<String, String> query;
  QueryParser<Spec> parser;
  ParsedQuery<Spec> parsed;
  SelectBuilder<Spec> selectBuilder;
  String queryFormat;

  @Before
  public void setUp() {
    query = ArrayListMultimap.create();
    parser = QueryParser.newBuilder(Spec.class).build();

    query.putAll("id__in", ImmutableList.of("1", "2"));
    query.put("count__gt", "100");
    query.put("full_name__eq", "example");

    query.put("comments__nlike", "%John%");
    query.put("comments__nlike", "Jane%");

    query.put("offset", "5");

    parsed = parser.parse(query);
    queryFormat = "select * from example where (`comments` not like %s escape '!' and `comments` not like %s escape '!' and `id` in (%s, %s) and `count` > %s and `full_name` = %s) limit %s offset %s";
  }

  @Test
  public void simpleSelect() {
    selectBuilder = parser.newSelectBuilder(query).withDefaultPlaceholders();

    String sql = selectBuilder.build().toString();

    assertThat(sql).isEqualTo(String.format(queryFormat, "?", "?", "?", "?", "?", "?", "?", "?"));
  }

  @Test
  public void simpleSelectWithOr() {
    ParsedQuery<Spec> parsedQuery = parser.parse(query);
    BoundFilterEntry<Spec> idFilter1 = parsedQuery.getAllFiltersForFieldName("id").stream().findFirst().get();
    BoundFilterEntry<Spec> idFilter2 = new MultiValuedBoundFilterEntry<>(
        parsedQuery.getMetaData().getNewBoundFilterEntry("id", In.class), ImmutableList.of("4", "5"));
    parsedQuery.removeFiltersFor("id");
    parsedQuery.getCombinedConditionCreator().addConditionCreator(
        new CombinedConditionCreator<>(Operator.OR, Lists.newArrayList(idFilter1, idFilter2)));
    selectBuilder = SelectBuilder.forParsedQuery(parsedQuery);

    String sql = selectBuilder.build().toString();
    String localQueryFormat = "select * from example where (`comments` not like %s escape '!' and `comments` not like %s escape '!' and `count` > %s and `full_name` = %s and (`id` in (%s, %s) or `id` in (%s, %s))) limit %s offset %s";

    assertThat(sql).isEqualTo(
        String.format(localQueryFormat, "'%John%'", "'Jane%'", "100", "'example'", "1", "2", "4", "5", "10", "5"));
  }

  /**
   * TODO (tdavis): Named placeholder support is incomplete.
   * It doesn't work with limit/offset (because there are no fields for those) or multi-value params (because they don't use single-parameter conditions)
   */
  @Test
  public void namedPlaceholderSelect() {
    selectBuilder = parser.newSelectBuilder(query).withParamType(ParamType.NAMED);

    String sql = selectBuilder.build().toString();

    assertThat(sql).isEqualTo(String.format(queryFormat, ":1", ":2", ":3", ":4", ":count", ":full_name", ":7", ":8"));
  }

  @Test
  public void inlineSelect() {
    selectBuilder = parser.newSelectBuilder(query).withParamType(ParamType.INLINED);

    String sql = selectBuilder.build().toString();

    assertThat(sql).isEqualTo(String.format(queryFormat, "'%John%'", "'Jane%'", "1", "2", "100", "'example'", "10", "5"));
  }

  @Test
  public void withSpecificFields() {
    selectBuilder = parser.newSelectBuilder(query).withFields("id", "full_name");

    String sql = selectBuilder.build().toString();

    assertThat(sql).startsWith("select `id`, `full_name` from");
  }

  @Test
  public void withPrefixedFields() {
    parser = QueryParser.newBuilder(Spec.class).build();
    selectBuilder = parser.newSelectBuilder(query).withFields("id", "full_name").withFieldFactory(new PrefixingAliasFieldFactory(
        "a."));

    String sql = selectBuilder.build().toString();

    assertThat(sql).startsWith(
        "select id as `a.id`, full_name as `a.full_name` from example where (`a.comments` not like '%John%' escape '!' and `a.comments` not like 'Jane%' escape '!' and `a.id`");
  }

  @Test
  public void withTableQualifiedFields() {
    parser = QueryParser.newBuilder(Spec.class).build();
    selectBuilder = parser.newSelectBuilder(query).withFields("id", "full_name").withFieldFactory(
        new TableQualifiedFieldFactory());

    String sql = selectBuilder.build().toString();

    assertThat(sql).startsWith(
        "select `example`.`id`, `example`.`full_name` from example where (`example`.`comments` not like '%John%' escape '!' and `example`.`comments` not like 'Jane%' escape '!' and `example`.`id`");
  }

  @Test
  public void asCount() {
    query.put("order", "-count"); // Make sure we strip orderings, too.
    selectBuilder = parser.newSelectBuilder(query).withDefaultPlaceholders().withCountOnly();

    String sql = selectBuilder.build().toString();

    assertThat(sql).isEqualTo(
        "select count(*) from example where (`comments` not like ? escape '!' and `comments` not like ? escape '!' and `id` in (?, ?) and `count` > ? and `full_name` = ?)");
  }

  @Test
  public void withOrdering() {
    query.put("order", "-count");
    selectBuilder = parser.newSelectBuilder(query);

    String sql = selectBuilder.build().toString();

    assertThat(sql).contains("order by `count` desc");
  }

  @Test
  public void withSnakeCaseOrdering() {
    query.put("order", "-full_name");
    selectBuilder = parser.newSelectBuilder(query);

    String sql = selectBuilder.build().toString();

    assertThat(sql).contains("order by `full_name` desc");
  }

  @Test
  public void withCamelCaseOrdering() {
    query.put("order", "fullName");
    selectBuilder = parser.newSelectBuilder(query);

    String sql = selectBuilder.build().toString();

    assertThat(sql).contains("order by `full_name` asc");
  }

  @Test
  public void withJoin() {
    parser = QueryParser.newBuilder(Spec.class).build();
    selectBuilder = parser.newSelectBuilder(query).withJoinCondition(DSL.table("joined_table"), DSL.field("example.id").eq(DSL
        .field("joined_table.id")));

    String sql = selectBuilder.build().toString();

    assertThat(sql).contains("join joined_table on example.id = joined_table.id");
  }

  @Test
  public void withLeftJoin() {
    parser = QueryParser.newBuilder(Spec.class).build();
    selectBuilder = parser.newSelectBuilder(query).withLeftJoinCondition(DSL.table("joined_table"), DSL.field("example.id").eq(DSL
        .field("joined_table.id")));

    String sql = selectBuilder.build().toString();

    assertThat(sql).contains("left outer join joined_table on example.id = joined_table.id");
  }

  @Test
  public void withGroupBys() {
    parser = QueryParser.newBuilder(Spec.class).build();
    selectBuilder = parser.newSelectBuilder(query);

    selectBuilder.withAdditionalFields(DSL.field("COUNT(distinct(id))").as("total_things"));
    selectBuilder.withGroupBys(DSL.field("id"));

    String sql = selectBuilder.build().toString();

    assertThat(sql).contains("select *, COUNT(distinct(id)) as `total_things` from example");
    assertThat(sql).contains("group by id");
  }

  @Test
  public void withAdditionalFields() {
    parser = QueryParser.newBuilder(Spec.class).build();
    selectBuilder = parser.newSelectBuilder(query);

    selectBuilder.withAdditionalFields(DSL.field("IF(count > 0, 'true', 'false')").as("is_positive"));

    String sql = selectBuilder.build().toString();

    assertThat(sql).contains("select *, IF(count > 0, 'true', 'false') as `is_positive` from example");
  }

  @Test
  public void withSpecificAndAdditionalFields() {
    parser = QueryParser.newBuilder(Spec.class).build();
    selectBuilder = parser.newSelectBuilder(query);

    selectBuilder.withFields("id", "count");
    selectBuilder.withAdditionalFields(DSL.field("IF(count > 0, 'true', 'false')").as("is_positive"));

    String sql = selectBuilder.build().toString();

    assertThat(sql).contains("select `id`, `count`, IF(count > 0, 'true', 'false') as `is_positive` from example");
  }

  @Test
  public void withJoinAndOrderingByTableField() {
    query.put("order", "fullName");
    selectBuilder = parser.newSelectBuilder(query);

    selectBuilder.withJoinCondition(DSL.table("other_table"),
        DSL.field("example.full_name").eq(DSL.field("other_table.full_name")));

    String sql = selectBuilder.build().toString();
    assertThat(sql).contains("order by `example`.`full_name` asc");
  }

  @Test
  public void withJoinAndOrderingByGeneratedField() {
    query.put("order", "-name_length");
    selectBuilder = parser.newSelectBuilder(query);

    selectBuilder.withAdditionalFields(DSL.field("LENGTH(name)").as("name_length"));
    selectBuilder.withJoinCondition(DSL.table("other_table"),
        DSL.field("example.full_name").eq(DSL.field("other_table.full_name")));

    String sql = selectBuilder.build().toString();
    assertThat(sql).contains("order by `name_length` desc");

  }

  @QueryConstraints(defaultLimit = 10, maxLimit = 100, maxOffset = 100)
  @RosettaNaming(SnakeCaseStrategy.class)
  public static class Spec implements QuerySpec {

    @FilterBy({
        Equal.class, In.class
    })
    Integer id;

    @FilterBy(GreaterThan.class)
    @OrderBy
    Long count;

    @OrderBy(isGenerated = true)
    Integer nameLength;

    @FilterBy({
        Equal.class, In.class
    })
    @OrderBy
    String fullName;

    boolean secret;

    @FilterBy({
        Contains.class, NotLike.class
    })
    String comments;

    @Override
    public String tableName() {
      return "example";
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

    public Integer getNameLength() {
      return nameLength;
    }

    public void setNameLength(Integer nameLength) {
      this.nameLength = nameLength;
    }

    public String getComments() {
      return comments;
    }

    public void setComments(String comments) {
      this.comments = comments;
    }
  }

}
