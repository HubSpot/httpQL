.. java:import:: org.jooq Field

Filter
======

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: public interface Filter

   Effectively the operator in a WHERE clause condition.

   :author: tdavis

Methods
-------
getConditionProvider
^^^^^^^^^^^^^^^^^^^^

.. java:method::  <T> ConditionProvider<T> getConditionProvider(Field<T> field)
   :outertype: Filter

names
^^^^^

.. java:method::  String[] names()
   :outertype: Filter

   List of names the operator goes by in queries; the \ ``gt``\  in \ ``foo_gt=1``\

