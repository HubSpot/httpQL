.. java:import:: org.jooq Field

.. java:import:: org.jooq Table

.. java:import:: com.google.common.base Optional

.. java:import:: com.hubspot.httpql.impl DefaultFieldFactory

FieldFactory
============

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: public interface FieldFactory

   A provider of JOOQ fields.

   Custom implementations are most useful for queries with table aliases and similara situations where field names may need to be munged.

   :author: tdavis

   **See also:** :java:ref:`DefaultFieldFactory`

Methods
-------
createField
^^^^^^^^^^^

.. java:method::  <T> Field<T> createField(String name, Class<T> fieldType, Table<?> table)
   :outertype: FieldFactory

tableAlias
^^^^^^^^^^

.. java:method::  Optional<String> tableAlias(String name)
   :outertype: FieldFactory

