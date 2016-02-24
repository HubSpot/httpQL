Usage
=====

Installation
------------

.. code-block:: xml

  <dependency>
    <groupId>com.hubspot.httpql</groupId>
    <artifactId>httpql</artifactId>
    <version>1.0</version>
  </dependency>


Exposing Fields
---------------

Out of the box, *httpQL* doesn't permit filtering by *anything*; we'll
need to use the :java:type:`FilterBy`, :java:type:`OrderBy`, and
(optionally) :java:type:`QueryConstraints` annotations to tell it
what's allowed.

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

.. note::

   We'll assume ``Person`` already existed and we're using it internally
   for other things: binding data parameters, person-related logic,
   etc. We could just have easily written a new ``PersonQuery`` class; do
   whatever makes you happy.

In English, we're saying:

| Allow requests for people by name, by specific age, by age range, or by any combination of these. Additionally, allow responses ordered by age. Finally, limit the result set to 10 people by default with an upper bound of 100 matches and 200 matches total (``maxLimit`` + ``maxOffset``).
|


Jersey For Great Justice
------------------------

Let's expose our new query endpoint via Jersey:

.. code-block:: java

   @GET
   @Path("/search")
   public List<Person> searchPeople(@BindQuery(Person.class) ParsedQuery<Person> query) {
     // Until next time...
   }

This snippet is all we need to process the request's query parameters. What do we do after that? I guess you'll have to keep reading...


Handling Naughty Requests
-------------------------

What happens if a request violates one of our rules above?

* If attempting to filter by a disallowed/missing *field*,
  :java:type:`FilterViolation` is thrown.
* If attempting to use a disallowed/missing *filter*,
  :java:type:`FilterViolation` is thrown.
* If constraints such as limit and offset are violated,
  :java:type:`ConstraintViolation` is thrown.

In each case, the exceptions provide human-readable error messages to
help callers clean up their act. You can find all the possible error
cases along with descriptions of where they're used in
:java:package:`httpql.error`.

If you don't want these error messages (potentially) returned in the
response, you'll want to implement ``ExceptionMapper``.


Building The Query
------------------

Back to our Resource method: what do we *do* with this
:java:type:`ParsedQuery` thing?

We can customize the bound values if we want to confuse the requestor
(or for a better reason):

.. code-block:: java

   Person p = query.getBoundQuery();
   if (p.getAge() < 21) {  // Bar crawl!
     p.setAge(21);
   }

Once we're satisfied, we can make a :java:type:`SelectBuilder`:

.. code-block:: java

   SelectBuilder<Person> selectBuilder = SelectBuilder.forParsedQuery(query);

.. note::

   :java:meth:`SelectBuilder.forParsedQuery(ParsedQuery, MetaQuerySpec)`
   is a more advanced, customized method of creating builders.

Like most builders it has various methods (prefixed by "with") used to
control the resulting query; you can specify fields to select, build a
count instead of a normal query, change how parameters are bound,
etc. For this example, we're just going to build the default query,
though:

.. code-block:: java

   BuiltSelect<Person> select = selectBuilder.build();
   String sql = select.toString();  // select * from person where `age` > 21 ...


Putting It Together
-------------------

Let's look at the final Resource, assuming we're using JDBI_ as our
database interface.

.. code-block:: java

   @GET
   @Path("/search")
   public List<Person> searchPeople(@BindQuery(Person.class) ParsedQuery<Person> query) {
     SelectBuilder<Person> selectBuilder = SelectBuilder.forParsedQuery(query);
     BuiltSelect<Person> select = selectBuilder.build();
     String sql = select.toString();  // select * from person where `age` > 21 ...
     return personDAO.getHandle()
       .createQuery(sql)
       .map(RosettaResultSetMapperFactory.mapperFor(Person.class))
       .list();
   }

Voila: a flexible, safe people-searching endpoint in four lines of code!


Advanced Usage
--------------

Multi-value Parameters
^^^^^^^^^^^^^^^^^^^^^^

Certain condition operators (aka Filters) require multiple values. For
instance, the ``IN`` operator takes a list of possible matches. For
cases such as these, we'll need to add the appropriately-typed fields
to our :java:type:`QuerySpec`:

.. code-block:: java

   // class Person ... {

   @FilterBy(In.class, as="age")
   @JsonView(HTTPQLView.class)
   Collection<Integer> ages;

We need a field to hold our collection of possible ages, but there's
no such ``ages`` column in the database; we use the ``as`` parameter
to make sure the condition goes against the correct field.

.. note::

   The ``JsonView`` annotation is not mandatory, but is encouraged
   when using *httpQL* annotations on a type that may also be
   serialized as output. Since ``ages`` isn't an actual column it's
   probably not something you want exposed in a JSON response, even as
   a null value.

.. warning::

   Using ``@JsonIgnore`` on properties such as these will also hide
   them from *httpQL*!

Modifying Filters Manually
^^^^^^^^^^^^^^^^^^^^^^^^^^

You can add your own filters (for global conditions, etc.) to an
already-parsed query.

.. code-block:: java

   if (! query.hasFilter("age")) {
     query.addFilter("age", GreaterThan.class, 15);
   }
   assert query.hasFilter("age");
   assert query.getBoundQuery().getAge() == 15

   // Replace zero or more "name"-related filters with this one
   query.addFilterExclusively("name", Equal.class, "bob")

.. note::

   There's an important distinction between a condition's *value* and
   its *existence* in the list of active filters. In the above
   example, if we called ``query.getBoundQuery().setAge(15)`` *instead
   of* adding the *Greater Than* filter via
   :java:meth:`ParsedQuery.addFilter`, we would have indeed *set* the
   age value but, **it would not have been used!**

   Logically this makes sense: merely binding a value doesn't add it
   as a condition. Because of the potential for confusion, we
   recommend using :java:meth:`ParsedQuery.addFilter` or
   :java:meth:`ParsedQuery.addFilterExclusively` in place of simply
   binding values, except in cases where you are *certain* a condition
   exists (or will exist) for that field.

Modifying a ``BuiltSelect``
^^^^^^^^^^^^^^^^^^^^^^^^^^^

*httpQL* exposes queries for mutation all the way up to the final
string generation process. With a little familiarity with JOOQ_, you
can modify a query even after its been built:

.. code-block:: java

   BuiltSelect<Person> built = selectBuilder.build();
   (SelectConditionStep<?>) select = built.getRawSelect();
   select.and("secret = false");
   String query = select.toString() // select * from person where ... and secret = false ...

Custom Field Naming
^^^^^^^^^^^^^^^^^^^

The :java:type:`FieldFactory` interface can be used to customize how
fields are represented in queries to, for instance, use aliases
(``SELECT id as `foo.id` ...``) or table prefixes (``SELECT `tbl`.`id`
from tbl ...``):

.. code-block:: java

  SelectBuilder<Person> selectBuilder = SelectBuilder.forParsedQuery(query)
    .withFieldFactory(new PrefixingAliasFieldFactory("foo.");

Going Nuts
^^^^^^^^^^

Want to allow filtering on every field that *doesn't* have an
annotation instead? Of course not, but you *could*!

Most of the logic around how things get created and interpreted by
*httpQL* is centralized in :java:type:`MetaQuerySpec`. While the
default implementation should be sufficient in 99.9% of cases, it is
possible to extend it and/or wholesale implement your own crazy
logic.

.. _JDBI: http://jdbi.org/
.. _JOOQ: http://www.jooq.org/
