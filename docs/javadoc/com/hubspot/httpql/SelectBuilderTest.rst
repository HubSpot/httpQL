.. java:import:: java.util Collection

.. java:import:: org.jooq.conf ParamType

.. java:import:: org.junit Before

.. java:import:: org.junit Test

.. java:import:: com.google.common.collect ArrayListMultimap

.. java:import:: com.google.common.collect ImmutableList

.. java:import:: com.google.common.collect Multimap

.. java:import:: com.hubspot.httpql ParsedQuery

.. java:import:: com.hubspot.httpql QueryConstraints

.. java:import:: com.hubspot.httpql QuerySpec

.. java:import:: com.hubspot.httpql.ann FilterBy

.. java:import:: com.hubspot.httpql.ann OrderBy

.. java:import:: com.hubspot.httpql.filter Equal

.. java:import:: com.hubspot.httpql.filter GreaterThan

.. java:import:: com.hubspot.httpql.filter In

.. java:import:: com.hubspot.httpql.impl PrefixingAliasFieldFactory

.. java:import:: com.hubspot.httpql.impl TableQualifiedFieldFactory

.. java:import:: com.hubspot.httpql.impl QueryParser

.. java:import:: com.hubspot.httpql.impl SelectBuilder

.. java:import:: com.hubspot.rosetta SnakeCase

SelectBuilderTest
=================

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: public class SelectBuilderTest

Fields
------
parsed
^^^^^^

.. java:field::  ParsedQuery<Spec> parsed
   :outertype: SelectBuilderTest

parser
^^^^^^

.. java:field::  QueryParser<Spec> parser
   :outertype: SelectBuilderTest

query
^^^^^

.. java:field::  Multimap<String, String> query
   :outertype: SelectBuilderTest

queryFormat
^^^^^^^^^^^

.. java:field::  String queryFormat
   :outertype: SelectBuilderTest

selectBuilder
^^^^^^^^^^^^^

.. java:field::  SelectBuilder<Spec> selectBuilder
   :outertype: SelectBuilderTest

Methods
-------
asCount
^^^^^^^

.. java:method:: @Test public void asCount()
   :outertype: SelectBuilderTest

inlineSelect
^^^^^^^^^^^^

.. java:method:: @Test public void inlineSelect()
   :outertype: SelectBuilderTest

namedPlaceholderSelect
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void namedPlaceholderSelect()
   :outertype: SelectBuilderTest

   TODO (tdavis): Named placeholder support is incomplete. It doesn't work with limit/offset (because there are no fields for those) or multi-value params (because they don't use single-parameter conditions)

setUp
^^^^^

.. java:method:: @Before public void setUp()
   :outertype: SelectBuilderTest

simpleSelect
^^^^^^^^^^^^

.. java:method:: @Test public void simpleSelect()
   :outertype: SelectBuilderTest

withOrdering
^^^^^^^^^^^^

.. java:method:: @Test public void withOrdering()
   :outertype: SelectBuilderTest

withPrefixedFields
^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void withPrefixedFields()
   :outertype: SelectBuilderTest

withSpecificFields
^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void withSpecificFields()
   :outertype: SelectBuilderTest

withTableQualifiedFields
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Test public void withTableQualifiedFields()
   :outertype: SelectBuilderTest

