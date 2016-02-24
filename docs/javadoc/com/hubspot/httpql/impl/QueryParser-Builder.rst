.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util ServiceLoader

.. java:import:: java.util Set

.. java:import:: javax.ws.rs.core MultivaluedMap

.. java:import:: javax.ws.rs.core UriInfo

.. java:import:: com.fasterxml.jackson.databind.introspect BeanPropertyDefinition

.. java:import:: com.google.common.base Optional

.. java:import:: com.google.common.base Splitter

.. java:import:: com.google.common.base Strings

.. java:import:: com.google.common.collect ImmutableSet

.. java:import:: com.google.common.collect Iterables

.. java:import:: com.google.common.collect Multimap

.. java:import:: com.google.common.collect Table

.. java:import:: com.hubspot.httpql Filter

.. java:import:: com.hubspot.httpql MultiParamConditionProvider

.. java:import:: com.hubspot.httpql ParsedQuery

.. java:import:: com.hubspot.httpql QueryConstraints

.. java:import:: com.hubspot.httpql QuerySpec

.. java:import:: com.hubspot.httpql.ann FilterBy

.. java:import:: com.hubspot.httpql.ann OrderBy

.. java:import:: com.hubspot.httpql.error ConstraintType

.. java:import:: com.hubspot.httpql.error ConstraintViolation

.. java:import:: com.hubspot.httpql.error FilterViolation

.. java:import:: com.hubspot.httpql.filter Equal

.. java:import:: com.hubspot.httpql.internal BoundFilterEntry

.. java:import:: com.hubspot.httpql.internal FilterEntry

.. java:import:: com.hubspot.rosetta PropertyDefinition

.. java:import:: com.hubspot.rosetta Rosetta

.. java:import:: com.hubspot.rosetta RosettaMapper

.. java:import:: com.hubspot.rosetta RosettaMapperFactory

.. java:import:: com.hubspot.rosetta Tablet

.. java:import:: com.sun.jersey.core.util MultivaluedMapImpl

QueryParser.Builder
===================

.. java:package:: com.hubspot.httpql.impl
   :noindex:

.. java:type:: public static class Builder<T extends QuerySpec>
   :outertype: QueryParser

Constructors
------------
Builder
^^^^^^^

.. java:constructor:: public Builder(Class<T> spec)
   :outertype: QueryParser.Builder

Methods
-------
build
^^^^^

.. java:method:: public QueryParser<T> build()
   :outertype: QueryParser.Builder

withStrictMode
^^^^^^^^^^^^^^

.. java:method:: public Builder<T> withStrictMode()
   :outertype: QueryParser.Builder

   In Strict Mode, the parser will throw an Exception when an unknown query parameter is found, not only when a known field is not allowed to have the specified filter applied.

   Defaults to OFF.

