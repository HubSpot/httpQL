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

ParsedQueryTest.Spec
====================

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: @QueryConstraints public static class Spec implements QuerySpec
   :outertype: ParsedQueryTest

Fields
------
count
^^^^^

.. java:field:: @FilterBy  Long count
   :outertype: ParsedQueryTest.Spec

id
^^

.. java:field:: @FilterBy  Integer id
   :outertype: ParsedQueryTest.Spec

ids
^^^

.. java:field:: @FilterBy @JsonView  Collection<Integer> ids
   :outertype: ParsedQueryTest.Spec

secret
^^^^^^

.. java:field::  boolean secret
   :outertype: ParsedQueryTest.Spec

Methods
-------
getCount
^^^^^^^^

.. java:method:: public Long getCount()
   :outertype: ParsedQueryTest.Spec

getId
^^^^^

.. java:method:: public Integer getId()
   :outertype: ParsedQueryTest.Spec

getIds
^^^^^^

.. java:method:: public Collection<Integer> getIds()
   :outertype: ParsedQueryTest.Spec

isSecret
^^^^^^^^

.. java:method:: public boolean isSecret()
   :outertype: ParsedQueryTest.Spec

   :return: the secret

setCount
^^^^^^^^

.. java:method:: public void setCount(Long count)
   :outertype: ParsedQueryTest.Spec

setId
^^^^^

.. java:method:: public void setId(Integer id)
   :outertype: ParsedQueryTest.Spec

setIds
^^^^^^

.. java:method:: public void setIds(Collection<Integer> ids)
   :outertype: ParsedQueryTest.Spec

setSecret
^^^^^^^^^

.. java:method:: public void setSecret(boolean secret)
   :outertype: ParsedQueryTest.Spec

   :param secret: the secret to set

tableName
^^^^^^^^^

.. java:method:: @Override public String tableName()
   :outertype: ParsedQueryTest.Spec

