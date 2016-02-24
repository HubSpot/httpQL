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

AdditionalCondition
===================

.. java:package:: com.hubspot.httpql.impl
   :noindex:

.. java:type::  class AdditionalCondition

Fields
------
condition
^^^^^^^^^

.. java:field:: public final Condition condition
   :outertype: AdditionalCondition

includeInCount
^^^^^^^^^^^^^^

.. java:field:: public final boolean includeInCount
   :outertype: AdditionalCondition

Constructors
------------
AdditionalCondition
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AdditionalCondition(Condition condition, boolean includeInCount)
   :outertype: AdditionalCondition

