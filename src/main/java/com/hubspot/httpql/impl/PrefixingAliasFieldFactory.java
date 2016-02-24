package com.hubspot.httpql.impl;

import java.util.Optional;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.hubspot.httpql.FieldFactory;

/**
 * A field factory selects fields "AS" something
 * <p>
 * For instance, a default field would look like {@code select id from foo} whereas an aliased field would look like {@code select id as `my.id` from foo}, provided {@code new PrefixingAliasFieldFactory("my.")}.
 *
 * @author tdavis
 */
public class PrefixingAliasFieldFactory implements FieldFactory {

  private final String alias;

  public PrefixingAliasFieldFactory(String alias) {
    this.alias = alias;
  }

  @Override
  public <T> Field<T> createField(String name, Class<T> fieldType, Table<?> table) {
    return DSL.field(name, fieldType).as(alias + name);
  }

  @Override
  public Optional<String> tableAlias(String name) {
    return Optional.empty();
  }

}
