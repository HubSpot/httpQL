httpQL Documentation
====================

*httpQL* is a small library for converting URL query arguments into SQL queries.

Given the following URL::

  http://example.com/?name=bob&age__gt=20&order=-age

And an annotated POJO::

  @QueryConstraints(defaultLimit = 10, maxLimit = 100, maxOffset = 100)
  class Person {

     @FilterBy(Equal.class)
     String name;

     @FilterBy({Equal.class, GreaterThan.class})
     @OrderBy
     Integer age;

     // Getters/Setters elided
   }

*httpQL* will generate the query::

  SELECT * FROM person
    WHERE (`name` = 'bob' AND `age` > 20)
    ORDER BY `age` DESC LIMIT 10 OFFSET 0;

Which you can then execute using your favorite driver, library, etc.

Sound cool? `Get reading, then!`_

.. _Rosetta: https://github.com/HubSpot/Rosetta
.. _Jackson: http://wiki.fasterxml.com/JacksonHome
.. _Get reading, then!: http://github.hubspot.com/httpQL/
