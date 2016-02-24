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

SelectBuilderTest.Spec
======================

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: @QueryConstraints @SnakeCase public static class Spec implements QuerySpec
   :outertype: SelectBuilderTest

Fields
------
count
^^^^^

.. java:field:: @FilterBy @OrderBy  Long count
   :outertype: SelectBuilderTest.Spec

fullName
^^^^^^^^

.. java:field:: @FilterBy  String fullName
   :outertype: SelectBuilderTest.Spec

id
^^

.. java:field:: @FilterBy  Integer id
   :outertype: SelectBuilderTest.Spec

ids
^^^

.. java:field:: @FilterBy  Collection<Integer> ids
   :outertype: SelectBuilderTest.Spec

secret
^^^^^^

.. java:field::  boolean secret
   :outertype: SelectBuilderTest.Spec

Methods
-------
getCount
^^^^^^^^

.. java:method:: public Long getCount()
   :outertype: SelectBuilderTest.Spec

getFullName
^^^^^^^^^^^

.. java:method:: public String getFullName()
   :outertype: SelectBuilderTest.Spec

getId
^^^^^

.. java:method:: public Integer getId()
   :outertype: SelectBuilderTest.Spec

getIds
^^^^^^

.. java:method:: public Collection<Integer> getIds()
   :outertype: SelectBuilderTest.Spec

isSecret
^^^^^^^^

.. java:method:: public boolean isSecret()
   :outertype: SelectBuilderTest.Spec

setCount
^^^^^^^^

.. java:method:: public void setCount(Long count)
   :outertype: SelectBuilderTest.Spec

setFullName
^^^^^^^^^^^

.. java:method:: public void setFullName(String fullName)
   :outertype: SelectBuilderTest.Spec

setId
^^^^^

.. java:method:: public void setId(Integer id)
   :outertype: SelectBuilderTest.Spec

setIds
^^^^^^

.. java:method:: public void setIds(Collection<Integer> ids)
   :outertype: SelectBuilderTest.Spec

setSecret
^^^^^^^^^

.. java:method:: public void setSecret(boolean secret)
   :outertype: SelectBuilderTest.Spec

tableName
^^^^^^^^^

.. java:method:: @Override public String tableName()
   :outertype: SelectBuilderTest.Spec

