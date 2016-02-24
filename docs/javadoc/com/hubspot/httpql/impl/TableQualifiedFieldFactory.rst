.. java:import:: org.jooq Field

.. java:import:: org.jooq Table

.. java:import:: org.jooq.impl DSL

.. java:import:: com.google.common.base Optional

.. java:import:: com.hubspot.httpql FieldFactory

TableQualifiedFieldFactory
==========================

.. java:package:: com.hubspot.httpql.impl
   :noindex:

.. java:type:: public class TableQualifiedFieldFactory implements FieldFactory

   A field factory that prefixes fields for the purposes of disambiguation.

   For instance, a default field would look like \ ``select id from foo``\  whereas a prefixed field would look like \ ``select foo.id from foo``\ .

   :author: tdavis

Methods
-------
createField
^^^^^^^^^^^

.. java:method:: public <T> Field<T> createField(String name, Class<T> fieldType, Table<?> table)
   :outertype: TableQualifiedFieldFactory

   FIXME (tdavis): This uses some pretty serious hacks (string format + table.toString())

tableAlias
^^^^^^^^^^

.. java:method:: @Override public Optional<String> tableAlias(String name)
   :outertype: TableQualifiedFieldFactory

