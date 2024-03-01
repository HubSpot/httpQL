package com.hubspot.httpql.filter;

import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.NotImplementedException;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.JSONValueOnStep;

public abstract class JsonFilterBase extends FilterBase {

  protected <T> JsonFilterParts getJsonFilterParts(Field<T> field, Collection<T> values) {
    throw new NotImplementedException("Implemented in Impl class");
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
