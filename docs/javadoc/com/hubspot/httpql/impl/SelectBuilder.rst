.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util Collection

.. java:import:: java.util List

.. java:import:: org.jooq Condition

.. java:import:: org.jooq DSLContext

.. java:import:: org.jooq Field

.. java:import:: org.jooq SQLDialect

.. java:import:: org.jooq Select

.. java:import:: org.jooq SelectFinalStep

.. java:import:: org.jooq SelectFromStep

.. java:import:: org.jooq SelectLimitStep

.. java:import:: org.jooq SelectOffsetStep

.. java:import:: org.jooq SelectOrderByStep

.. java:import:: org.jooq SortField

.. java:import:: org.jooq Table

.. java:import:: org.jooq.conf ParamType

.. java:import:: org.jooq.impl DSL

.. java:import:: com.hubspot.httpql FieldFactory

.. java:import:: com.hubspot.httpql ParsedQuery

.. java:import:: com.hubspot.httpql QuerySpec

.. java:import:: com.hubspot.httpql.internal BoundFilterEntry

SelectBuilder
=============

.. java:package:: com.hubspot.httpql.impl
   :noindex:

.. java:type:: public class SelectBuilder<T extends QuerySpec>

   Translates the high-level parsed query into a JOOQ Select and/or String representation.

   :author: tdavis

Methods
-------
build
^^^^^

.. java:method:: public BuiltSelect<T> build()
   :outertype: SelectBuilder

forParsedQuery
^^^^^^^^^^^^^^

.. java:method:: public static <T extends QuerySpec> SelectBuilder<T> forParsedQuery(ParsedQuery<T> parsed, DefaultMetaQuerySpec<T> context)
   :outertype: SelectBuilder

forParsedQuery
^^^^^^^^^^^^^^

.. java:method:: public static <T extends QuerySpec> SelectBuilder<T> forParsedQuery(ParsedQuery<T> parsed)
   :outertype: SelectBuilder

getConditions
^^^^^^^^^^^^^

.. java:method:: public Collection<Condition> getConditions()
   :outertype: SelectBuilder

   Get just the list of Conditions (WHERE clauses) associated with the query.

getSourceQuery
^^^^^^^^^^^^^^

.. java:method:: public ParsedQuery<T> getSourceQuery()
   :outertype: SelectBuilder

orderingsToSortFields
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Collection<SortField<?>> orderingsToSortFields()
   :outertype: SelectBuilder

withCondition
^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withCondition(Condition condition, boolean includeInCount)
   :outertype: SelectBuilder

withConditions
^^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withConditions(Collection<Condition> conditions, boolean includeInCount)
   :outertype: SelectBuilder

   Add custom JOOQ Conditions to the query.

withConditions
^^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withConditions(Condition... conditions)
   :outertype: SelectBuilder

withCountOnly
^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withCountOnly()
   :outertype: SelectBuilder

   Produce only a simple aggregate count query, ``SELECT COUNT(*) ...`` Using this mode automatically disables the LIMIT, OFFSET, and ORDER BY clauses.

withDefaultPlaceholders
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withDefaultPlaceholders()
   :outertype: SelectBuilder

   Switch back to default, \ ``?``\  placeholders.

withDialect
^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withDialect(SQLDialect dialect)
   :outertype: SelectBuilder

   Change the SQL dialect used to build the SQL string.

withFieldFactory
^^^^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withFieldFactory(FieldFactory factory)
   :outertype: SelectBuilder

withFields
^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withFields(Collection<String> fieldNames)
   :outertype: SelectBuilder

   Name specific fields (columns) to select

withFields
^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withFields(String... fieldNames)
   :outertype: SelectBuilder

withLimitAndOffset
^^^^^^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withLimitAndOffset()
   :outertype: SelectBuilder

   Include the ``LIMIT ... OFFSET`` clause in the query, if applicable.

withOrderings
^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withOrderings()
   :outertype: SelectBuilder

   Include the ``ORDER BY`` clause in the query, if applicable.

withParamType
^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withParamType(ParamType paramType)
   :outertype: SelectBuilder

   Change the parameter type (placeholder/value style) of the resulting SQL string.

withoutLimitAndOffset
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withoutLimitAndOffset()
   :outertype: SelectBuilder

   Exclude the ``LIMIT ... OFFSET`` clause from the query, if applicable.

withoutOrderings
^^^^^^^^^^^^^^^^

.. java:method:: public SelectBuilder<T> withoutOrderings()
   :outertype: SelectBuilder

   Exclude the ``ORDER BY`` clause from the query, if applicable.

