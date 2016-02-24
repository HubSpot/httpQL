.. java:import:: java.util Map

.. java:import:: org.jooq Field

.. java:import:: com.fasterxml.jackson.databind.introspect BeanPropertyDefinition

.. java:import:: com.google.common.collect Table

.. java:import:: com.hubspot.httpql.internal BoundFilterEntry

MetaQuerySpec
=============

.. java:package:: com.hubspot.httpql
   :noindex:

.. java:type:: public interface MetaQuerySpec<T extends QuerySpec>

   MetaQuerySpec provides implementations of metadata-related methods related to a given \ :java:ref:`QuerySpec`\ .

   Instances of this interface are primarily used internally and instantiating it is expensive; if you need it, an instance is available via \ :java:ref:`ParsedQuery.getMetaData()`\

   :author: tdavis

Methods
-------
createField
^^^^^^^^^^^

.. java:method::  <E> Field<E> createField(String name, FieldFactory fieldFactory)
   :outertype: MetaQuerySpec

   Construct a \ :java:ref:`Field`\  instance for a named field on \ ``T``\ .

   :param name:
   :param fieldType:
   :param fieldFactory:

getFieldMap
^^^^^^^^^^^

.. java:method::  Map<String, BeanPropertyDefinition> getFieldMap()
   :outertype: MetaQuerySpec

   Provides a mapping of every known field on \ ``T``\ . Implementations should take care to respect all Rosetta and Jackson annotations regarding visibility, if implementing logic manually.

getFieldType
^^^^^^^^^^^^

.. java:method::  Class<?> getFieldType(String name)
   :outertype: MetaQuerySpec

   Returns the type for a field as found on \ ``T``\ .

getFilterTable
^^^^^^^^^^^^^^

.. java:method::  Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> getFilterTable()
   :outertype: MetaQuerySpec

   Returns a table of filters where rows are their internal representation, columns are a given string alias for the filter, and the value is the meta description of the annotated field.

getFiltersForField
^^^^^^^^^^^^^^^^^^

.. java:method::  Class<? extends Filter>[] getFiltersForField(String name)
   :outertype: MetaQuerySpec

   Returns an array of filter types for a field based on its annotations.

tableFor
^^^^^^^^

.. java:method::  Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> tableFor(BeanPropertyDefinition field, Class<? extends Filter>... filters)
   :outertype: MetaQuerySpec

   Provides a narrowed view of the table provided by \ :java:ref:`getFilterTable()`\  by looking at only a specific field and set of filters.

   This method should *not* perform validation on the entries in \ ``filters``\  to, e.g., check for existence of the filter on the field.

