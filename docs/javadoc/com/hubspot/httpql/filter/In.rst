.. java:import:: java.util Collection

.. java:import:: org.jooq Condition

.. java:import:: org.jooq Field

.. java:import:: com.hubspot.httpql ConditionProvider

.. java:import:: com.hubspot.httpql Filter

.. java:import:: com.hubspot.httpql MultiParamConditionProvider

In
==

.. java:package:: com.hubspot.httpql.filter
   :noindex:

.. java:type:: public class In implements Filter

Methods
-------
getConditionProvider
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T> ConditionProvider<T> getConditionProvider(Field<T> field)
   :outertype: In

names
^^^^^

.. java:method:: @Override public String[] names()
   :outertype: In

