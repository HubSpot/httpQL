.. java:import:: java.lang.annotation ElementType

.. java:import:: java.lang.annotation Retention

.. java:import:: java.lang.annotation RetentionPolicy

.. java:import:: java.lang.annotation Target

.. java:import:: com.hubspot.httpql Filter

.. java:import:: com.hubspot.httpql MultiParamConditionProvider

FilterBy
========

.. java:package:: com.hubspot.httpql.ann
   :noindex:

.. java:type:: @Retention @Target public @interface FilterBy

   Annotate a field to indicate it may be filtered/queried.

   :author: tdavis
   :param value: One or more {@ Filter} (operators) to permit
   :param as: Real name of the column/field, if we're annotating something for a \ :java:ref:`MultiParamConditionProvider`\

