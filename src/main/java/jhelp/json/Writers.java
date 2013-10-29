package jhelp.json;

import com.fasterxml.jackson.core.JsonGenerator;
import jhelp.lang.Pair;
import jhelp.lang.Triple;
import jhelp.lang.Tuple;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Writers {

    static public final Writer<Map<Object, Object>> MAP = new Writer<Map<Object, Object>>() {
        @Override
        public void write(Context context, JsonGenerator jg, Map<Object,Object> value) throws IOException {
            jg.writeStartObject();
            for (Map.Entry<Object, Object> e : value.entrySet()) {
                jg.writeFieldName(e.getKey().toString());
                context.writeObject(e.getValue(), jg);
            }
            jg.writeEndObject();
        }
    };

    static public final Writer<Iterable> ITERABLE = new Writer<Iterable>() {
        @Override
        public void write(Context context, JsonGenerator jg, Iterable value) throws IOException {
            jg.writeStartArray();
            for (Object item : (Iterable)value) {
                context.writeObject(item, jg);
            }
            jg.writeEndArray();
        }
    };

    static public final Writer<Object[]> ARRAY = new Writer<Object[]>() {
        @Override
        public void write(Context context, JsonGenerator jg, Object[] value) throws IOException {
            jg.writeStartArray();
            for (Object item : value) {
                context.writeObject(item, jg);
            }
            jg.writeEndArray();
        }
    };

    static public final Writer<Byte> BYTE = new Writer<Byte>() {
        @Override
        public void write(Context context, JsonGenerator jg, Byte value) throws IOException {
            jg.writeNumber(value);
        }
    };

    static public final Writer<Short> SHORT = new Writer<Short>() {
        @Override
        public void write(Context context, JsonGenerator jg, Short value) throws IOException {
            jg.writeNumber(value);
        }
    };

    static public final Writer<Integer> INTEGER = new Writer<Integer>() {
        @Override
        public void write(Context context, JsonGenerator jg, Integer value) throws IOException {
            jg.writeNumber(value);
        }
    };

    static public final Writer<Long> LONG = new Writer<Long>() {
        @Override
        public void write(Context context, JsonGenerator jg, Long value) throws IOException {
            jg.writeNumber(value);
        }
    };

    static public final Writer<Float> FLOAT = new Writer<Float>() {
        @Override
        public void write(Context context, JsonGenerator jg, Float value) throws IOException {
            jg.writeNumber(value);
        }
    };

    static public final Writer<Double> DOUBLE = new Writer<Double>() {
        @Override
        public void write(Context context, JsonGenerator jg, Double value) throws IOException {
            jg.writeNumber(value);
        }
    };

    static public final Writer<byte[]> ARRAY_BYTE = new Writer<byte[]>() {
        @Override
        public void write(Context context, JsonGenerator jg, byte[] value) throws IOException {
            jg.writeStartArray();
            for (byte v : value) {
                jg.writeNumber(v);
            }
            jg.writeEndArray();
        }
    };

    static public final Writer<short[]> ARRAY_SHORT = new Writer<short[]>() {
        @Override
        public void write(Context context, JsonGenerator jg, short[] value) throws IOException {
            jg.writeStartArray();
            for (short v : value) {
                jg.writeNumber(v);
            }
            jg.writeEndArray();
        }
    };

    static public final Writer<int[]> ARRAY_INT = new Writer<int[]>() {
        @Override
        public void write(Context context, JsonGenerator jg, int[] value) throws IOException {
            jg.writeStartArray();
            for (int v : value) {
                jg.writeNumber(v);
            }
            jg.writeEndArray();
        }
    };

    static public final Writer<long[]> ARRAY_LONG = new Writer<long[]>() {
        @Override
        public void write(Context context, JsonGenerator jg, long[] value) throws IOException {
            jg.writeStartArray();
            for (long v : value) {
                jg.writeNumber(v);
            }
            jg.writeEndArray();
        }
    };

    static public final Writer<float[]> ARRAY_FLOAT = new Writer<float[]>() {
        @Override
        public void write(Context context, JsonGenerator jg, float[] value) throws IOException {
            jg.writeStartArray();
            for (float v : value) {
                jg.writeNumber(v);
            }
            jg.writeEndArray();
        }
    };

    static public final Writer<double[]> ARRAY_DOUBLE = new Writer<double[]>() {
        @Override
        public void write(Context context, JsonGenerator jg, double[] value) throws IOException {
            jg.writeStartArray();
            for (double v : value) {
                jg.writeNumber(v);
            }
            jg.writeEndArray();
        }
    };

    static public final Writer<boolean[]> ARRAY_BOOLEAN = new Writer<boolean[]>() {
        @Override
        public void write(Context context, JsonGenerator jg, boolean[] value) throws IOException {
            jg.writeStartArray();
            for (boolean v : value) {
                jg.writeBoolean(v);
            }
            jg.writeEndArray();
        }
    };

    static public <T> Writer<T> named(final String name, final Writer<T> writer) {
        return new Writer<T>() {
            @Override
            public void write(Context context, JsonGenerator g, T value) throws IOException {
                g.writeFieldName(name);
                writer.write(context, g, value);
            }
        };
    }

    static public Writer<Pair> PAIR = new Writer<Pair>() {
        @Override
        public void write(Context context, JsonGenerator jg, Pair value) throws IOException {
            jg.writeStartArray();
            context.writeObject(value.first, jg);
            context.writeObject(value.second, jg);
            jg.writeEndArray();
        }
    };

    static public Writer<Triple> TRIPLE = new Writer<Triple>() {
        @Override
        public void write(Context context, JsonGenerator jg, Triple value) throws IOException {
            jg.writeStartArray();
            context.writeObject(value.first, jg);
            context.writeObject(value.second, jg);
            context.writeObject(value.third, jg);
            jg.writeEndArray();
        }
    };

    static public Writer<Tuple> TUPLE = new Writer<Tuple>() {
        @Override
        public void write(Context context, JsonGenerator jg, Tuple value) throws IOException {
            jg.writeStartArray();
            for (int i=0; i<value.size(); i++) {
                context.writeObject(value.get(i), jg);
            }
            jg.writeEndArray();
        }
    };

    static public final Map<Class, Writer> PREDIFINED = new HashMap<Class, Writer>();
    static {
        define(Byte.class, BYTE);
        define(Short.class, SHORT);
        define(Integer.class, INTEGER);
        define(Long.class, LONG);
        define(Float.class, FLOAT);
        define(Double.class, DOUBLE);

        define(byte[].class, ARRAY_BYTE);
        define(short[].class, ARRAY_SHORT);
        define(int[].class, ARRAY_INT);
        define(long[].class, ARRAY_LONG);
        define(float[].class, ARRAY_FLOAT);
        define(double[].class, ARRAY_DOUBLE);

        define(Object[].class, ARRAY);

        define(Pair.class, PAIR);
        define(Triple.class, TRIPLE);
        define(Tuple.class, TUPLE);
    }
    static private <T> void define(Class<T> cls, Writer<T> writer) {
        PREDIFINED.put(cls, writer);
    }
}
