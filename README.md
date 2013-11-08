jhelp
=====

Some Java helping utilities

json - streaming based mapping using [jackson](http://wiki.fasterxml.com/JacksonHome)
----

###Why this is needed?###

Jackson [Streaming API](http://wiki.fasterxml.com/JacksonStreamingApi) provides an efficient way encoding Java into JSON. Yet the efficiency comes with inconvience. On the other hand, [Data Binding](http://wiki.fasterxml.com/JacksonDataBinding) is more convinent while being less efficient. Still, there are cases where Jackson Data Binding might not work at all.

By this utility we introduce streaming based mapping allow application to wire their custom JSON encoding scheme easily and flexibly. All one has to do is to
1. define [Writer](https://github.com/gl-works/jhelp/blob/master/src/main/java/jhelp/json/Writer.java) for your custom data type
2. instantialize a encoding [Context](https://github.com/gl-works/jhelp/blob/master/src/main/java/jhelp/json/Context.java) with your Writer defined
3. call encode(object) on the context

###Get Started###

The Test Cases are good for get started.
1. Check what [DEFAUTL TYPES](https://github.com/gl-works/jhelp/blob/master/src/test/java/jhelp/json/PredifinedTest.java) are supported.
2. Check how to enable [REFLECTED ENCODING](https://github.com/gl-works/jhelp/blob/master/src/test/java/jhelp/json/ReflectedTest.java)
3. Check how to [CUSTOMIZE](https://github.com/gl-works/jhelp/blob/master/src/test/java/jhelp/json/CustomizedTest.java)

Quantile Estimator
---------------------

A simple & fast quantile estimation utility for arbitrary any data size. It was orginally designed to estimate [HBase](http://hbase.apache.org/) row-key distribution. Ideally, it can be used with any [Dividable](https://github.com/gl-works/jhelp/blob/master/src/main/java/jhelp/math/quantile/Estimator.java) types.


Check how to use [Estimator](https://github.com/gl-works/jhelp/blob/master/src/main/java/jhelp/math/quantile/Estimator.java)
