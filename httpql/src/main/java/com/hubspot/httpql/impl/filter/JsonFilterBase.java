package com.hubspot.httpql.impl.filter;

import static org.jooq.impl.DSL.jsonValue;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.JSONValueOnStep;

public abstract class JsonFilterBase implements FilterImpl {

  protected <T> JsonFilterParts getJsonFilterParts(Field<T> field, Collection<T> values) {
    Preconditions.checkArgument(
      field.getDataType().isJSON() || field.getDataType().isString(),
      "Field for JSON filters must be JSON"
    );

    Field<JSON> jsonField = field.cast(JSON.class);

    List<T> valueList = new ArrayList<>(values);

    String path = valueList.get(0).toString();
    List<JSON> jsonValues = valueList
      .stream()
      .skip(1)
      .map(
        value -> value instanceof JSON ? (JSON) value : JSON.jsonOrNull((String) value)
      )
      .collect(Collectors.toList());
    Preconditions.checkArgument(
      jsonValues.stream().noneMatch(Objects::isNull),
      "Values for JSON filters must be valid JSON"
    );

    JSONValueOnStep<JSON> fieldValue = jsonValue(jsonField, path);
    return new JsonFilterParts(fieldValue, jsonValues);
  }

  protected static class JsonFilterParts {
    public final JSONValueOnStep<JSON> fieldValue;
    public final List<JSON> filterValues;

    public JsonFilterParts(JSONValueOnStep<JSON> fieldValue, List<JSON> filterValues) {
      this.fieldValue = fieldValue;
      this.filterValues = filterValues;
    }
  }
}
