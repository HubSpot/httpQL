.. java:import:: org.jooq Condition

.. java:import:: org.jooq Field

.. java:import:: org.jooq Param

.. java:import:: com.hubspot.httpql ConditionProvider

.. java:import:: com.hubspot.httpql Filter

NotEqual
========

.. java:package:: com.hubspot.httpql.filter
   :noindex:

.. java:type:: public class NotEqual implements Filter

Methods
-------
getConditionProvider
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T> ConditionProvider<T> getConditionProvider(Field<T> field)
   :outertype: NotEqual

names
^^^^^

.. java:method:: @Override public String[] names()
   :outertype: NotEqual

