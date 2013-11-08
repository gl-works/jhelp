package jhelp.json;

import jhelp.lang.Pair;
import jhelp.lang.Triple;
import jhelp.lang.Tuple;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Writers {

    static public final Writer<Map> MAP =
            new Writer<Map>() {
                @Override
                public void write(JSONFactory.Session session,
                                  Map value) throws IOException {
                    session.startObject();
                    for (Object e : value.entrySet()) {
                        session.writeFieldName(((Map.Entry)e).getKey().toString());
                        session.writeObject(((Map.Entry)e).getValue());
                    }
                    session.endObject();
                }
            };

    static public final Writer<Map<? extends Object, ? extends Object>> MAP_REVERSED =
            new Writer<Map<? extends Object, ? extends Object>>() {
                @Override
                public void write(JSONFactory.Session session,
                                  Map<? extends Object,? extends Object> value) throws IOException {
                    session.startObject();
                    for (Map.Entry<?, ?> e : value.entrySet()) {
                        session.writeFieldName(e.getValue().toString());
                        session.writeObject(e.getKey());
                    }
                    session.endObject();
                }
            };

    static public final Writer<Iterable> ITERABLE = new Writer<Iterable>() {
        @Override
        public void write(JSONFactory.Session session, Iterable value) throws IOException {
            session.startArray();
            for (Object item : (Iterable)value) {
                session.writeObject(item);
            }
            session.endArray();
        }
    };

    static public final Writer<Object[]> ARRAY = new Writer<Object[]>() {
        @Override
        public void write(JSONFactory.Session session, Object[] value) throws IOException {
            session.startArray();
            for (Object item : value) {
                session.writeObject(item);
            }
            session.endArray();
        }
    };

    static public final Writer<Byte> BYTE = new Writer<Byte>() {
        @Override
        public void write(JSONFactory.Session session, Byte value) throws IOException {
            session.writeNumber(value);
        }
    };

    static public final Writer<Short> SHORT = new Writer<Short>() {
        @Override
        public void write(JSONFactory.Session session, Short value) throws IOException {
            session.writeNumber(value);
        }
    };

    static public final Writer<Integer> INTEGER = new Writer<Integer>() {
        @Override
        public void write(JSONFactory.Session session, Integer value) throws IOException {
            session.writeNumber(value);
        }
    };

    static public final Writer<Long> LONG = new Writer<Long>() {
        @Override
        public void write(JSONFactory.Session session, Long value) throws IOException {
            session.writeNumber(value);
        }
    };

    static public final Writer<Float> FLOAT = new Writer<Float>() {
        @Override
        public void write(JSONFactory.Session session, Float value) throws IOException {
            session.writeNumber(value);
        }
    };

    static public final Writer<Double> DOUBLE = new Writer<Double>() {
        @Override
        public void write(JSONFactory.Session session, Double value) throws IOException {
            session.writeNumber(value);
        }
    };

    static public final Writer<byte[]> ARRAY_BYTE = new Writer<byte[]>() {
        @Override
        public void write(JSONFactory.Session session, byte[] value) throws IOException {
            session.startArray();
            for (byte v : value) {
                session.writeNumber(v);
            }
            session.endArray();
        }
    };

    static public final Writer<short[]> ARRAY_SHORT = new Writer<short[]>() {
        @Override
        public void write(JSONFactory.Session session, short[] value) throws IOException {
            session.startArray();
            for (short v : value) {
                session.writeNumber(v);
            }
            session.endArray();
        }
    };

    static public final Writer<int[]> ARRAY_INT = new Writer<int[]>() {
        @Override
        public void write(JSONFactory.Session session, int[] value) throws IOException {
            session.startArray();
            for (int v : value) {
                session.writeNumber(v);
            }
            session.endArray();
        }
    };

    static public final Writer<long[]> ARRAY_LONG = new Writer<long[]>() {
        @Override
        public void write(JSONFactory.Session sessioin, long[] value) throws IOException {
            sessioin.startArray();
            for (long v : value) {
                sessioin.writeNumber(v);
            }
            sessioin.endArray();
        }
    };

    static public final Writer<float[]> ARRAY_FLOAT = new Writer<float[]>() {
        @Override
        public void write(JSONFactory.Session session, float[] value) throws IOException {
            session.startArray();
            for (float v : value) {
                session.writeNumber(v);
            }
            session.endArray();
        }
    };

    static public final Writer<double[]> ARRAY_DOUBLE = new Writer<double[]>() {
        @Override
        public void write(JSONFactory.Session session, double[] value) throws IOException {
            session.startArray();
            for (double v : value) {
                session.writeNumber(v);
            }
            session.endArray();
        }
    };

    static public final Writer<boolean[]> ARRAY_BOOLEAN = new Writer<boolean[]>() {
        @Override
        public void write(JSONFactory.Session session, boolean[] value) throws IOException {
            session.startArray();
            for (boolean v : value) {
                session.writeBoolean(v);
            }
            session.endArray();
        }
    };

    static public <T> Writer<T> named(final String name, final Writer<T> writer) {
        return new Writer<T>() {
            @Override
            public void write(JSONFactory.Session session, T value) throws IOException {
                session.writeFieldName(name);
                writer.write(session, value);
            }
        };
    }

    static public Writer<Pair> PAIR = new Writer<Pair>() {
        @Override
        public void write(JSONFactory.Session session, Pair value) throws IOException {
            session.startArray();
            session.writeObject(value.first);
            session.writeObject(value.second);
            session.endArray();
        }
    };

    static public Writer<Triple> TRIPLE = new Writer<Triple>() {
        @Override
        public void write(JSONFactory.Session session, Triple value) throws IOException {
            session.startArray();
            session.writeObject(value.first);
            session.writeObject(value.second);
            session.writeObject(value.third);
            session.endArray();
        }
    };

    static public Writer<Tuple> TUPLE = new Writer<Tuple>() {
        @Override
        public void write(JSONFactory.Session session, Tuple value) throws IOException {
            session.startArray();
            for (int i=0; i<value.size(); i++) {
                session.writeObject(value.get(i));
            }
            session.endArray();
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

        define(Iterable.class, ITERABLE);
        define(Map.class, MAP);
    }
    static private <T> void define(Class<T> cls, Writer<T> writer) {
        PREDIFINED.put(cls, writer);
    }
}
