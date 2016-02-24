httpQL Documentation
====================

*httpQL* is a small library for converting URL query arguments into SQL queries.

Given the following URL::

  http://example.com/?name=bob&age__gt=20&order=-age

And an annotated POJO:

.. code-block:: java

  @QueryConstraints(defaultLimit = 10, maxLimit = 100, maxOffset = 100)
  class Person implements QuerySpec {

     @FilterBy(Equal.class)
     String name;

     @FilterBy({Equal.class, GreaterThan.class})
     @OrderBy
     Integer age;

     public String tableName() {
       return "person";
     }

     // Getters/Setters elided
   }

*httpQL* will generate the query::

  SELECT * FROM person
    WHERE (`name` = 'bob' AND `age` > 20)
    ORDER BY `age` DESC LIMIT 10 OFFSET 0;

Which you can then execute using your favorite driver, library, etc.


Features!
---------

* Drop-in support for Jersey Resources via :java:type:`BindQuery`.
* Interopability with all Jackson_ and Rosetta_ annotations.
* 100% safe, escaped SQL.
* Can be customized for advanced usage (joins, global clauses, etc.)

Sound cool? :doc:`Get going, then! <usage>`


Table o' Contents
-----------------

.. toctree::
   :maxdepth: 2

   usage


Appendices and Tables
---------------------

.. toctree::
   :maxdepth: 1

   Appendix A: Complete Javadoc <javadoc/packages>



.. _Rosetta: https://github.com/HubSpot/Rosetta
.. _Jackson: http://wiki.fasterxml.com/JacksonHome
