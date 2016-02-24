package com.hubspot.httpql;

import java.util.Optional;

import org.jooq.Field;
import org.jooq.Table;

import com.hubspot.httpql.impl.DefaultFieldFactory;

/**
 * A provider of JOOQ fields.
 * <p>
 * Custom implementations are most useful for queries with table aliases and similar situations where field names may need to be munged.
 *
 * @see DefaultFieldFactory
 * @author tdavis
 */
public interface FieldFactory {

  <T> Field<T> createField(String name, Class<T> fieldType, Table<?> table);

  Optional<String> tableAlias(String name);

}
