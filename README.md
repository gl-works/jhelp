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

###An Example###

In the following we show how to encoding java object containing JTS [Geometry](http://www.vividsolutions.com/jts/javadoc/com/vividsolutions/jts/geom/Geometry.html)/[Coordinate](http://www.vividsolutions.com/jts/javadoc/com/vividsolutions/jts/geom/Coordinate.html) types. (Note - these type will not work with simple Jackson DataBinding)

    import com.vividsolutions.jts.geom.*;
    
    #Define Writer for [Geometry]
    Writer<Geometry> GEOMETRY_WRITER = new Writer<Geometry>() {
        @Override
        public void write(Context context, JsonGenerator jg, Geometry geometry) throws IOException {
            jg.writeStartObject();
            context.writeNamedObject("centroid", geometry.getCentroid().getCoordinate(), jg);
            context.writeNamedObject("path", geometry.getCoordinates(), jg);
            jg.writeEndObject();
        }
    };

    #define Writer for Coordinate
    Writer<Coordinate> COORDINATE_WRITER = new Writer<Coordinate>() {
        @Override
        public void write(Context context, JsonGenerator jg, Coordinate value) throws IOException {
            jg.writeStartObject();
            jg.writeNumberField("lng", value.x);
            jg.writeNumberField("lat", value.y);
            jg.writeEndObject();
        }
    };
    Context context = Context.predefined()
            .forClass(Geometry.class, GEOMETRY_WRITER)
            .forClass(Coordinate.class, COORDINATE_WRITER)
            .finish();
     
     #
     List<Geometry> geometries = ...;
     String json = context.encode(geometries);
     ...

Quantile Estimator
---------------------

A simple & fast quantile estimation utility for arbitrary any data size. It was orginally designed to estimate [HBase](http://hbase.apache.org/) row-key distribution. Ideally, it can be used with any [Dividable](https://github.com/gl-works/jhelp/blob/master/src/main/java/jhelp/math/quantile/Estimator.java) types.


Check how to use [Estimator](https://github.com/gl-works/jhelp/blob/master/src/main/java/jhelp/math/quantile/Estimator.java)
