package com.hubspot.httpql.impl;

import com.hubspot.httpql.FieldFactory;

import java.util.Optional;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

/**
 * Basic field factory that creates fields by name.
 *
 * @author tdavis
 */
public class DefaultFieldFactory implements FieldFactory {

  @Override
  public <T> Field<T> createField(String name, Class<T> fieldType, Table<?> table) {
    return DSL.field(DSL.name(name), fieldType);
  }

  @Override
  public String getFieldName(String name, Table<?> table) {
    return DSL.field(DSL.name(name)).toString();
  }

  @Override
  public Optional<String> tableAlias(String name) {
    return Optional.empty();
  }

}
