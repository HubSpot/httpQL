.. java:import:: org.jooq Condition

.. java:import:: org.jooq Field

.. java:import:: org.jooq Param

.. java:import:: org.jooq.impl DSL

ConditionProvider
=================

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: public abstract class ConditionProvider<T>

   Used by \ :java:ref:`Filter`\  implementations to create appropriately-typed \ :java:ref:`Condition`\  instances.

   Because Filters are (necessarily) un-typed and \ :java:ref:`Field`\  and \ :java:ref:`Param`\  instances must share the same type parameter, this class acts as a closure of sorts to make sure that's possible.

   :author: tdavis

Constructors
------------
ConditionProvider
^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConditionProvider(Field<T> field)
   :outertype: ConditionProvider

Methods
-------
getCondition
^^^^^^^^^^^^

.. java:method:: public Condition getCondition(Object value, String paramName)
   :outertype: ConditionProvider

getCondition
^^^^^^^^^^^^

.. java:method:: public abstract Condition getCondition(Param<T> value)
   :outertype: ConditionProvider

getField
^^^^^^^^

.. java:method:: public Field<T> getField()
   :outertype: ConditionProvider

getParam
^^^^^^^^

.. java:method:: public Param<T> getParam(Object value, String paramName)
   :outertype: ConditionProvider

