.. java:import:: java.util HashMap

.. java:import:: java.util Map

.. java:import:: org.jooq Field

.. java:import:: org.jooq.impl DSL

.. java:import:: com.fasterxml.jackson.databind.introspect BeanPropertyDefinition

.. java:import:: com.google.common.base Throwables

.. java:import:: com.google.common.collect HashBasedTable

.. java:import:: com.google.common.collect Table

.. java:import:: com.hubspot.httpql FieldFactory

.. java:import:: com.hubspot.httpql Filter

.. java:import:: com.hubspot.httpql HTTPQLView

.. java:import:: com.hubspot.httpql MetaQuerySpec

.. java:import:: com.hubspot.httpql QuerySpec

.. java:import:: com.hubspot.httpql.ann FilterBy

.. java:import:: com.hubspot.httpql.internal BoundFilterEntry

.. java:import:: com.hubspot.rosetta PropertyDefinition

.. java:import:: com.hubspot.rosetta Rosetta

.. java:import:: com.hubspot.rosetta Tablet

DefaultMetaQuerySpec
====================

.. java:package:: com.hubspot.httpql.impl
   :noindex:

.. java:type:: public class DefaultMetaQuerySpec<T extends QuerySpec> implements MetaQuerySpec<T>

Constructors
------------
DefaultMetaQuerySpec
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DefaultMetaQuerySpec(Class<T> specType)
   :outertype: DefaultMetaQuerySpec

Methods
-------
createField
^^^^^^^^^^^

.. java:method:: @SuppressWarnings public <E> Field<E> createField(String name, FieldFactory fieldFactory)
   :outertype: DefaultMetaQuerySpec

getFieldMap
^^^^^^^^^^^

.. java:method:: public Map<String, BeanPropertyDefinition> getFieldMap()
   :outertype: DefaultMetaQuerySpec

getFieldType
^^^^^^^^^^^^

.. java:method:: public Class<?> getFieldType(String name)
   :outertype: DefaultMetaQuerySpec

getFilterTable
^^^^^^^^^^^^^^

.. java:method:: public Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> getFilterTable()
   :outertype: DefaultMetaQuerySpec

getFiltersForField
^^^^^^^^^^^^^^^^^^

.. java:method:: @SuppressWarnings public Class<? extends Filter>[] getFiltersForField(String name)
   :outertype: DefaultMetaQuerySpec

tableFor
^^^^^^^^

.. java:method:: @SafeVarargs public final Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> tableFor(BeanPropertyDefinition field, Class<? extends Filter>... filters)
   :outertype: DefaultMetaQuerySpec

