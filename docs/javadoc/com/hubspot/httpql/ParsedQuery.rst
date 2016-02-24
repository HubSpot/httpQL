.. java:import:: java.util Collection

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: com.fasterxml.jackson.databind.introspect BeanPropertyDefinition

.. java:import:: com.google.common.base Optional

.. java:import:: com.google.common.collect Table.Cell

.. java:import:: com.hubspot.httpql.error UnknownFieldException

.. java:import:: com.hubspot.httpql.impl DefaultMetaQuerySpec

.. java:import:: com.hubspot.httpql.impl Ordering

.. java:import:: com.hubspot.httpql.internal BoundFilterEntry

ParsedQuery
===========

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: public abstract class ParsedQuery<T extends QuerySpec>

   The result of parsing query arguments.

   :author: tdavis

Constructors
------------
ParsedQuery
^^^^^^^^^^^

.. java:constructor:: public ParsedQuery(Optional<Integer> limit, Optional<Integer> offset, Collection<Ordering> orderings)
   :outertype: ParsedQuery

Methods
-------
addFilter
^^^^^^^^^

.. java:method:: public void addFilter(String fieldName, Class<? extends Filter> filterType, Object value)
   :outertype: ParsedQuery

   Add the given filter to the query.

   :param fieldName: Name as seen in the query; not multi-value proxies ("id", not "ids")
   :throws UnknownFieldException: When no field named \ ``fieldName``\  exists
   :throws IllegalArgumentException: When \ ``value``\  is of the wrong type

addFilterExclusively
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addFilterExclusively(String fieldName, Class<? extends Filter> filterType, Object value)
   :outertype: ParsedQuery

   Similar to \ :java:ref:`addFilter`\  but removes all existing filters for \ ``fieldName``\  first.

   :param fieldName: Name as seen in the query; not multi-value proxies ("id", not "ids")

getBoundFilterEntries
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract List<BoundFilterEntry<T>> getBoundFilterEntries()
   :outertype: ParsedQuery

getBoundQuery
^^^^^^^^^^^^^

.. java:method:: public abstract T getBoundQuery()
   :outertype: ParsedQuery

getLimit
^^^^^^^^

.. java:method:: public Optional<Integer> getLimit()
   :outertype: ParsedQuery

getMetaData
^^^^^^^^^^^

.. java:method:: protected abstract DefaultMetaQuerySpec<T> getMetaData()
   :outertype: ParsedQuery

getOffset
^^^^^^^^^

.. java:method:: public Optional<Integer> getOffset()
   :outertype: ParsedQuery

getOrderings
^^^^^^^^^^^^

.. java:method:: public Collection<Ordering> getOrderings()
   :outertype: ParsedQuery

getQueryType
^^^^^^^^^^^^

.. java:method:: public abstract Class<T> getQueryType()
   :outertype: ParsedQuery

hasFilter
^^^^^^^^^

.. java:method:: public boolean hasFilter(String fieldName)
   :outertype: ParsedQuery

   Check to see if any filter exists for a given field.

   :param fieldName: Name as seen in the query; not multi-value proxies ("id", not "ids")

hasFilter
^^^^^^^^^

.. java:method:: public boolean hasFilter(String fieldName, Class<? extends Filter> filterType)
   :outertype: ParsedQuery

   Check to see a specific filter type exists for a given field.

   :param fieldName: Name as seen in the query; not multi-value proxies ("id", not "ids")

removeFiltersFor
^^^^^^^^^^^^^^^^

.. java:method:: public void removeFiltersFor(String fieldName)
   :outertype: ParsedQuery

setLimit
^^^^^^^^

.. java:method:: public void setLimit(Optional<Integer> limit)
   :outertype: ParsedQuery

setOffset
^^^^^^^^^

.. java:method:: public void setOffset(Optional<Integer> offset)
   :outertype: ParsedQuery

setOrderings
^^^^^^^^^^^^

.. java:method:: public void setOrderings(Collection<Ordering> orderings)
   :outertype: ParsedQuery

