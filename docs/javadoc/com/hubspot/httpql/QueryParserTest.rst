.. java:import:: java.util Collection

.. java:import:: org.junit Before

.. java:import:: org.junit Test

.. java:import:: com.fasterxml.jackson.annotation JsonView

.. java:import:: com.google.common.collect ArrayListMultimap

.. java:import:: com.google.common.collect ImmutableList

.. java:import:: com.google.common.collect Multimap

.. java:import:: com.hubspot.httpql HTTPQLView

.. java:import:: com.hubspot.httpql QueryConstraints

.. java:import:: com.hubspot.httpql QuerySpec

.. java:import:: com.hubspot.httpql.ann FilterBy

.. java:import:: com.hubspot.httpql.error ConstraintViolation

.. java:import:: com.hubspot.httpql.error FilterViolation

.. java:import:: com.hubspot.httpql.filter Equal

.. java:import:: com.hubspot.httpql.filter GreaterThan

.. java:import:: com.hubspot.httpql.filter In

.. java:import:: com.hubspot.httpql.impl QueryParser

.. java:import:: com.hubspot.rosetta SnakeCase

QueryParserTest
===============

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: public class QueryParserTest

Fields
------
parser
^^^^^^

.. java:field::  QueryParser<Spec> parser
   :outertype: QueryParserTest

query
^^^^^

.. java:field::  Multimap<String, String> query
   :outertype: QueryParserTest

Methods
-------
itBindsMultipleValues
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void itBindsMultipleValues()
   :outertype: QueryParserTest

itBindsSingleValues
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void itBindsSingleValues()
   :outertype: QueryParserTest

itDisallowsMissingFilters
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void itDisallowsMissingFilters()
   :outertype: QueryParserTest

itDisallowsUnknownFilters
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void itDisallowsUnknownFilters()
   :outertype: QueryParserTest

itRespectsMaxLimit
^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void itRespectsMaxLimit()
   :outertype: QueryParserTest

itRespectsMaxOffset
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void itRespectsMaxOffset()
   :outertype: QueryParserTest

itUsesNamingStrategy
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void itUsesNamingStrategy()
   :outertype: QueryParserTest

setUp
^^^^^

.. java:method:: @Before public void setUp()
   :outertype: QueryParserTest

