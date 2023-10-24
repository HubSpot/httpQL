package com.hubspot.httpql.lib.filter;

import com.hubspot.httpql.lib.internal.JoinFilter;
import java.util.Objects;

public abstract class FilterBase implements FilterImpl {

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FilterImpl)) {
      return false;
    }

    Class<?> filterClass = obj.getClass();

    if (obj instanceof JoinFilter) {
      filterClass = ((JoinFilter) obj).getFilter().getClass();
    }

    return getClass().equals(filterClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClass());
  }

}
