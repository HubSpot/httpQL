.. java:import:: java.util Collection

.. java:import:: org.jooq Condition

.. java:import:: org.jooq Field

.. java:import:: org.jooq Param

MultiParamConditionProvider
===========================

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: public abstract class MultiParamConditionProvider<T> extends ConditionProvider<T>

   Like \ :java:ref:`ConditionProvider`\  for filters that require multi-value parameters.

   Filters utilizing these types of \ :java:ref:`Condition`\  are not currently compatible with \ :java:ref:`ParamType.NAMED`\ .

   :author: tdavis

Constructors
------------
MultiParamConditionProvider
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public MultiParamConditionProvider(Field<T> field)
   :outertype: MultiParamConditionProvider

Methods
-------
getCondition
^^^^^^^^^^^^

.. java:method:: @SuppressWarnings @Override public Condition getCondition(Object obj, String paramName)
   :outertype: MultiParamConditionProvider

getCondition
^^^^^^^^^^^^

.. java:method:: public Condition getCondition(Collection<T> values, String paramName)
   :outertype: MultiParamConditionProvider

getCondition
^^^^^^^^^^^^

.. java:method:: @Override public Condition getCondition(Param<T> value)
   :outertype: MultiParamConditionProvider

getCondition
^^^^^^^^^^^^

.. java:method:: public abstract Condition getCondition(Collection<T> values)
   :outertype: MultiParamConditionProvider

