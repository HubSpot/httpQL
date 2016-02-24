.. java:import:: com.hubspot.httpql ParsedQuery

.. java:import:: com.hubspot.httpql QuerySpec

.. java:import:: com.hubspot.httpql.impl QueryParser

.. java:import:: com.sun.jersey.api.core HttpContext

.. java:import:: com.sun.jersey.api.model Parameter

.. java:import:: com.sun.jersey.core.spi.component ComponentContext

.. java:import:: com.sun.jersey.core.spi.component ComponentScope

.. java:import:: com.sun.jersey.server.impl.inject AbstractHttpContextInjectable

.. java:import:: com.sun.jersey.spi.inject Injectable

.. java:import:: com.sun.jersey.spi.inject InjectableProvider

BindQueryInjectableProvider
===========================

.. java:package:: com.hubspot.httpql.jersey
   :noindex:

.. java:type:: public class BindQueryInjectableProvider implements InjectableProvider<BindQuery, Parameter>

Methods
-------
getInjectable
^^^^^^^^^^^^^

.. java:method:: @Override public Injectable<ParsedQuery<? extends QuerySpec>> getInjectable(ComponentContext ic, BindQuery a, Parameter c)
   :outertype: BindQueryInjectableProvider

getScope
^^^^^^^^

.. java:method:: @Override public ComponentScope getScope()
   :outertype: BindQueryInjectableProvider

