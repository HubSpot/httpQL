.. java:import:: org.jooq Field

.. java:import:: org.jooq Table

.. java:import:: org.jooq.impl DSL

.. java:import:: com.google.common.base Optional

.. java:import:: com.hubspot.httpql FieldFactory

DefaultFieldFactory
===================

.. java:package:: com.hubspot.httpql.impl
   :noindex:

.. java:type:: public class DefaultFieldFactory implements FieldFactory

   Basic field factory that creates fields by name.

   :author: tdavis

Methods
-------
createField
^^^^^^^^^^^

.. java:method:: @Override public <T> Field<T> createField(String name, Class<T> fieldType, Table<?> table)
   :outertype: DefaultFieldFactory

tableAlias
^^^^^^^^^^

.. java:method:: @Override public Optional<String> tableAlias(String name)
   :outertype: DefaultFieldFactory

