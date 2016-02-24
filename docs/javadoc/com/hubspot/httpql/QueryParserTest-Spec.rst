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

QueryParserTest.Spec
====================

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: @QueryConstraints @SnakeCase public static class Spec implements QuerySpec
   :outertype: QueryParserTest

Fields
------
count
^^^^^

.. java:field:: @FilterBy  Long count
   :outertype: QueryParserTest.Spec

fullName
^^^^^^^^

.. java:field:: @FilterBy  String fullName
   :outertype: QueryParserTest.Spec

id
^^

.. java:field:: @FilterBy  Integer id
   :outertype: QueryParserTest.Spec

ids
^^^

.. java:field:: @JsonView @FilterBy  Collection<Integer> ids
   :outertype: QueryParserTest.Spec

secret
^^^^^^

.. java:field::  boolean secret
   :outertype: QueryParserTest.Spec

Methods
-------
getCount
^^^^^^^^

.. java:method:: public Long getCount()
   :outertype: QueryParserTest.Spec

getFullName
^^^^^^^^^^^

.. java:method:: public String getFullName()
   :outertype: QueryParserTest.Spec

getId
^^^^^

.. java:method:: public Integer getId()
   :outertype: QueryParserTest.Spec

getIds
^^^^^^

.. java:method:: public Collection<Integer> getIds()
   :outertype: QueryParserTest.Spec

isSecret
^^^^^^^^

.. java:method:: public boolean isSecret()
   :outertype: QueryParserTest.Spec

setCount
^^^^^^^^

.. java:method:: public void setCount(Long count)
   :outertype: QueryParserTest.Spec

setFullName
^^^^^^^^^^^

.. java:method:: public void setFullName(String fullName)
   :outertype: QueryParserTest.Spec

setId
^^^^^

.. java:method:: public void setId(Integer id)
   :outertype: QueryParserTest.Spec

setIds
^^^^^^

.. java:method:: public void setIds(Collection<Integer> ids)
   :outertype: QueryParserTest.Spec

setSecret
^^^^^^^^^

.. java:method:: public void setSecret(boolean secret)
   :outertype: QueryParserTest.Spec

tableName
^^^^^^^^^

.. java:method:: @Override public String tableName()
   :outertype: QueryParserTest.Spec

