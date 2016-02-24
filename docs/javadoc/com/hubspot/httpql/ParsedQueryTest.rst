.. java:import:: java.util Collection

.. java:import:: org.jooq Param

.. java:import:: org.junit Before

.. java:import:: org.junit Test

.. java:import:: com.fasterxml.jackson.annotation JsonView

.. java:import:: com.google.common.collect ArrayListMultimap

.. java:import:: com.google.common.collect ImmutableList

.. java:import:: com.google.common.collect Multimap

.. java:import:: com.hubspot.httpql ConditionProvider

.. java:import:: com.hubspot.httpql FieldFactory

.. java:import:: com.hubspot.httpql HTTPQLView

.. java:import:: com.hubspot.httpql ParsedQuery

.. java:import:: com.hubspot.httpql QueryConstraints

.. java:import:: com.hubspot.httpql QuerySpec

.. java:import:: com.hubspot.httpql.ann FilterBy

.. java:import:: com.hubspot.httpql.filter Equal

.. java:import:: com.hubspot.httpql.filter GreaterThan

.. java:import:: com.hubspot.httpql.filter In

.. java:import:: com.hubspot.httpql.impl DefaultFieldFactory

.. java:import:: com.hubspot.httpql.impl QueryParser

.. java:import:: com.hubspot.httpql.internal BoundFilterEntry

ParsedQueryTest
===============

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: public class ParsedQueryTest

Fields
------
fieldFactory
^^^^^^^^^^^^

.. java:field::  FieldFactory fieldFactory
   :outertype: ParsedQueryTest

parser
^^^^^^

.. java:field::  QueryParser<Spec> parser
   :outertype: ParsedQueryTest

query
^^^^^

.. java:field::  Multimap<String, String> query
   :outertype: ParsedQueryTest

Methods
-------
addFilter
^^^^^^^^^

.. java:method:: @Test public void addFilter()
   :outertype: ParsedQueryTest

addFilterExclusively
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void addFilterExclusively()
   :outertype: ParsedQueryTest

hasFilter
^^^^^^^^^

.. java:method:: @Test public void hasFilter()
   :outertype: ParsedQueryTest

itContainsBoundFilterEntries
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void itContainsBoundFilterEntries()
   :outertype: ParsedQueryTest

itWorksWithConditionProvider
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void itWorksWithConditionProvider()
   :outertype: ParsedQueryTest

itWorksWithMultiParamConditionProvider
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void itWorksWithMultiParamConditionProvider()
   :outertype: ParsedQueryTest

setUp
^^^^^

.. java:method:: @Before public void setUp()
   :outertype: ParsedQueryTest

