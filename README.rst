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

Class Loader StackOverflow Issue
-------
* Sometimes when including ``httpQL`` in your project you will run into intermittent issues where ``java.lang.ClassLoader.loadClass`` will throw a ``StackOverflow`` error. This is because your thread runs out of stack when trying to include all of the necessary classes. You can fix this by increasing your thread stack size with this deploy config option: ``JVM_STACK_SIZE: 384k``. It is only recommended to do this if you run into this issue.

.. |BlazarShield| image:: https://private.hubapi.com/blazar/v2/branches/state/9954/shield
.. _BlazarShield: https://private.hubteam.com/blazar/builds/branch/9954
