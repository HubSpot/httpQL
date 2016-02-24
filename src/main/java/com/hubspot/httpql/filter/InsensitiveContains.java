package com.hubspot.httpql.filter;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.Filter;

public class InsensitiveContains extends FilterBase implements Filter {
  private static final Escaper escaper = Escapers.builder()
      .addEscape('\\', "\\\\")
      .addEscape('%', "!%")
      .addEscape('_', "!_")
      .addEscape('!', "!!")
      .build();

  @Override
  public String[] names() {
    return new String[] {
        "icontains", "ilike"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new ConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Param<T> value) {
        String originalValue = (String) value.getValue();
        String escapedValue = escaper.escape(originalValue);
        return field.likeIgnoreCase('%' + escapedValue + '%', '!');
      }

    };
  }

}
