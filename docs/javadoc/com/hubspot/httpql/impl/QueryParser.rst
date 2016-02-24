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

QueryParser
===========

.. java:package:: com.hubspot.httpql.impl
   :noindex:

.. java:type:: public class QueryParser<T extends QuerySpec>

   Primary entry point into httpQL.

   The parser's job is to take a set of query arguments (string key/value pairs) and turn it into a high-level query representation, assuming it is valid according to the defined filtering rules (provided via \ :java:ref:`FilterBy`\  and \ :java:ref:`OrderBy`\  annotations).

   :author: tdavis

Fields
------
FILTER_PARAM_SPLITTER
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Splitter FILTER_PARAM_SPLITTER
   :outertype: QueryParser

Methods
-------
buildOrderableFields
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void buildOrderableFields(Map<String, PropertyDefinition> fields)
   :outertype: QueryParser

createEmptyQuery
^^^^^^^^^^^^^^^^

.. java:method:: public ParsedQuery<T> createEmptyQuery()
   :outertype: QueryParser

getQueryType
^^^^^^^^^^^^

.. java:method:: public Class<T> getQueryType()
   :outertype: QueryParser

named
^^^^^

.. java:method:: public static Filter named(String name)
   :outertype: QueryParser

newBuilder
^^^^^^^^^^

.. java:method:: public static <T extends QuerySpec> Builder<T> newBuilder(Class<T> spec)
   :outertype: QueryParser

newSelectBuilder
^^^^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> newSelectBuilder(UriInfo query)
   :outertype: QueryParser

newSelectBuilder
^^^^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> newSelectBuilder(Multimap<String, String> query)
   :outertype: QueryParser

newSelectBuilder
^^^^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> newSelectBuilder(MultivaluedMap<String, String> query)
   :outertype: QueryParser

parse
^^^^^

.. java:method:: public ParsedQuery<T> parse(UriInfo uriInfo)
   :outertype: QueryParser

parse
^^^^^

.. java:method:: public ParsedQuery<T> parse(Multimap<String, String> uriParams)
   :outertype: QueryParser

parse
^^^^^

.. java:method:: public ParsedQuery<T> parse(MultivaluedMap<String, String> uriParams)
   :outertype: QueryParser

setConstraints
^^^^^^^^^^^^^^

.. java:method:: protected void setConstraints(Class<T> spec)
   :outertype: QueryParser

