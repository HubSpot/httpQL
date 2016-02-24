.. java:import:: com.google.common.base Objects

.. java:import:: com.hubspot.httpql Filter

FilterEntry
===========

.. java:package:: com.hubspot.httpql.internal
   :noindex:

.. java:type:: public class FilterEntry

Constructors
------------
FilterEntry
^^^^^^^^^^^

.. java:constructor:: public FilterEntry(Filter filter, String queryName)
   :outertype: FilterEntry

FilterEntry
^^^^^^^^^^^

.. java:constructor:: public FilterEntry(Filter filter, String fieldName, String queryName)
   :outertype: FilterEntry

Methods
-------
equals
^^^^^^

.. java:method:: @Override public boolean equals(Object other)
   :outertype: FilterEntry

getFieldName
^^^^^^^^^^^^

.. java:method:: public String getFieldName()
   :outertype: FilterEntry

getFilter
^^^^^^^^^

.. java:method:: public Filter getFilter()
   :outertype: FilterEntry

getQueryName
^^^^^^^^^^^^

.. java:method:: public String getQueryName()
   :outertype: FilterEntry

hashCode
^^^^^^^^

.. java:method:: @Override public int hashCode()
   :outertype: FilterEntry

