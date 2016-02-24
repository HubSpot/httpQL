.. java:import:: org.jooq Condition

.. java:import:: com.fasterxml.jackson.databind.introspect BeanPropertyDefinition

.. java:import:: com.google.common.base Strings

.. java:import:: com.hubspot.httpql ConditionProvider

.. java:import:: com.hubspot.httpql FieldFactory

.. java:import:: com.hubspot.httpql Filter

.. java:import:: com.hubspot.httpql QuerySpec

.. java:import:: com.hubspot.httpql.ann FilterBy

.. java:import:: com.hubspot.httpql.impl DefaultMetaQuerySpec

BoundFilterEntry
================

.. java:package:: com.hubspot.httpql.internal
   :noindex:

.. java:type:: public class BoundFilterEntry<T extends QuerySpec> extends FilterEntry

Constructors
------------
BoundFilterEntry
^^^^^^^^^^^^^^^^

.. java:constructor:: public BoundFilterEntry(Filter filter, String fieldName, String queryName, BeanPropertyDefinition prop, DefaultMetaQuerySpec<T> meta)
   :outertype: BoundFilterEntry

BoundFilterEntry
^^^^^^^^^^^^^^^^

.. java:constructor:: public BoundFilterEntry(Filter filter, BeanPropertyDefinition prop, DefaultMetaQuerySpec<T> meta)
   :outertype: BoundFilterEntry

BoundFilterEntry
^^^^^^^^^^^^^^^^

.. java:constructor:: public BoundFilterEntry(FilterEntry entry, BeanPropertyDefinition prop, DefaultMetaQuerySpec<T> meta)
   :outertype: BoundFilterEntry

Methods
-------
getCondition
^^^^^^^^^^^^

.. java:method:: public Condition getCondition(QuerySpec value, FieldFactory fieldFactory)
   :outertype: BoundFilterEntry

getConditionProvider
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public ConditionProvider<?> getConditionProvider(FieldFactory fieldFactory)
   :outertype: BoundFilterEntry

getFieldType
^^^^^^^^^^^^

.. java:method:: public Class<?> getFieldType()
   :outertype: BoundFilterEntry

getProperty
^^^^^^^^^^^

.. java:method:: public BeanPropertyDefinition getProperty()
   :outertype: BoundFilterEntry

setActualField
^^^^^^^^^^^^^^

.. java:method:: public void setActualField(BeanPropertyDefinition actualField)
   :outertype: BoundFilterEntry

