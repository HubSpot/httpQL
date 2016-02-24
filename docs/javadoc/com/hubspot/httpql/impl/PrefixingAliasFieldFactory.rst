.. java:import:: org.jooq Field

.. java:import:: org.jooq Table

.. java:import:: org.jooq.impl DSL

.. java:import:: com.google.common.base Optional

.. java:import:: com.hubspot.httpql FieldFactory

PrefixingAliasFieldFactory
==========================

.. java:package:: com.hubspot.httpql.impl
   :noindex:

.. java:type:: public class PrefixingAliasFieldFactory implements FieldFactory

   A field factory selects fields "AS" something

   For instance, a default field would look like \ ``select id from foo``\  whereas an aliased field would look like \ ``select id as `my.id` from foo``\ , provided \ ``new PrefixingAliasFieldFactory("my.")``\ .

   :author: tdavis

Constructors
------------
PrefixingAliasFieldFactory
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PrefixingAliasFieldFactory(String alias)
   :outertype: PrefixingAliasFieldFactory

Methods
-------
createField
^^^^^^^^^^^

.. java:method:: public <T> Field<T> createField(String name, Class<T> fieldType, Table<?> table)
   :outertype: PrefixingAliasFieldFactory

tableAlias
^^^^^^^^^^

.. java:method:: @Override public Optional<String> tableAlias(String name)
   :outertype: PrefixingAliasFieldFactory

