.. java:import:: org.jooq Condition

.. java:import:: org.jooq Field

.. java:import:: org.jooq Param

.. java:import:: com.hubspot.httpql ConditionProvider

.. java:import:: com.hubspot.httpql Filter

LessThan
========

.. java:package:: com.hubspot.httpql.filter
   :noindex:

.. java:type:: public class LessThan implements Filter

Methods
-------
getConditionProvider
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T> ConditionProvider<T> getConditionProvider(Field<T> field)
   :outertype: LessThan

names
^^^^^

.. java:method:: @Override public String[] names()
   :outertype: LessThan

