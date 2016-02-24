.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util Collection

.. java:import:: java.util List

.. java:import:: org.jooq Condition

.. java:import:: org.jooq DSLContext

.. java:import:: org.jooq Field

.. java:import:: org.jooq SQLDialect

.. java:import:: org.jooq Select

.. java:import:: org.jooq SelectFinalStep

.. java:import:: org.jooq SelectFromStep

.. java:import:: org.jooq SelectLimitStep

.. java:import:: org.jooq SelectOffsetStep

.. java:import:: org.jooq SelectOrderByStep

.. java:import:: org.jooq SortField

.. java:import:: org.jooq Table

.. java:import:: org.jooq.conf ParamType

.. java:import:: org.jooq.impl DSL

.. java:import:: com.hubspot.httpql FieldFactory

.. java:import:: com.hubspot.httpql ParsedQuery

.. java:import:: com.hubspot.httpql QuerySpec

.. java:import:: com.hubspot.httpql.internal BoundFilterEntry

SelectBuilder.BuiltSelect
=========================

.. java:package:: com.hubspot.httpql.impl
   :noindex:

.. java:type:: public static class BuiltSelect<T>
   :outertype: SelectBuilder

Constructors
------------
BuiltSelect
^^^^^^^^^^^

.. java:constructor:: public BuiltSelect(SelectFinalStep<?> select, ParamType paramType)
   :outertype: SelectBuilder.BuiltSelect

Methods
-------
getRawSelect
^^^^^^^^^^^^

.. java:method:: public SelectFinalStep<?> getRawSelect()
   :outertype: SelectBuilder.BuiltSelect

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: SelectBuilder.BuiltSelect

