ConstraintViolation
===================

.. java:package:: com.hubspot.httpql.error
   :noindex:

.. java:type:: @SuppressWarnings public class ConstraintViolation extends RuntimeException

Constructors
------------
ConstraintViolation
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConstraintViolation(ConstraintType constraintType, int provided, int allowed)
   :outertype: ConstraintViolation

Methods
-------
getAllowed
^^^^^^^^^^

.. java:method:: public int getAllowed()
   :outertype: ConstraintViolation

getConstraintType
^^^^^^^^^^^^^^^^^

.. java:method:: public ConstraintType getConstraintType()
   :outertype: ConstraintViolation

getProvided
^^^^^^^^^^^

.. java:method:: public int getProvided()
   :outertype: ConstraintViolation

