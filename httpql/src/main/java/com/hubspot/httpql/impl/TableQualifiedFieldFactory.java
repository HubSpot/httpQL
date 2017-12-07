package com.hubspot.httpql.impl;

import java.util.Optional;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.hubspot.httpql.FieldFactory;

/**
 * A field factory that prefixes fields for the purposes of disambiguation.
 * <p>
 * For instance, a default field would look like {@code select id from foo} whereas a prefixed field would look like {@code select foo.id from foo}.
 *
 * @author tdavis
 */
public class TableQualifiedFieldFactory implements FieldFactory {

  /**
   * FIXME (tdavis): This uses some pretty serious hacks (string format + table.toString())
   */
  @Override
  public <T> Field<T> createField(String name, Class<T> fieldType, Table<?> table) {
    return DSL.field(DSL.name(table.toString(), name), fieldType);
  }

  @Override
  public String getFieldName(String name, Table<?> table) {
    return DSL.name(table.toString(), name).toString();
  }

  @Override
  public Optional<String> tableAlias(String name) {
    return Optional.empty();
  }

}
