package jhelp.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.reflect.FieldUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class Context {
    private final Map<Class, Writer> writers;
    private final JsonFactory factory;

    private Context(Map<Class, Writer> writers) {
        this.writers = writers;
        this.factory = new JsonFactory();
    }

    public String encode(Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            encode(object, baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toString();
    }

    public void encode(Object object, OutputStream os) throws IOException {
        JsonGenerator jg = factory.createGenerator(os);
        writeObject(object, jg);
        jg.flush();
    }

    public void writeNamedObject(String name, Object data, JsonGenerator jg) throws IOException {
        jg.writeFieldName(name);
        writeObject(data, jg);
    }

    public void writeObject(Object data, JsonGenerator jg) throws IOException {
        if (data == null) {
            jg.writeNull();
        } else {
            Class cls = data.getClass();
            Writer w = writers.get(cls);
            if (w == null) {
                if (data instanceof Map) {
                    w = Writers.MAP;
                } else if (data instanceof Iterable) {
                    w = Writers.ITERABLE;
                } else if (cls.isArray()) {
                    w = Writers.ARRAY; //primitive arrays should have been shot ahead
                } else {
                    w = searchUpward(cls);
                    Preconditions.checkState(w != null,
                            "Don't know how to write class %s as json");
                    writers.put(cls, w);
                }
            }
            w.write(this, jg, data);
        }
    }

    private Writer searchUpward(Class cls) {
        for (Class p = cls.getSuperclass(); p!=null; p = p.getSuperclass()) {
            Writer w = writers.get(p);
            if (w != null) {
                return w;
            }
        }
        for (Class p : cls.getInterfaces()) {
            Writer w = writers.get(p);
            if (w != null) {
                return w;
            }
        }
        return null;
    }

    static public Builder predefined() {
        Builder b = new Builder();
        b.writers.putAll(Writers.PREDIFINED);
        return b;
    }

    static public Builder empty() {
        return new Builder();
    }

    static public class Builder {
        private final Map<Class, Writer> writers = new HashMap<Class, Writer>();

        public Context finish() {
            return new Context(writers);
        }

        public <T> Builder forClass(Class<T> cls, Writer<T> writer) {
            writers.put(cls, writer);
            return this;
        }

        /**
         * Writer reflectively write all <code>Field</code> objects of the class or
         * interface represented by the <code>Class</code> object. This includes
         * public, protected, default (package) access, and private fields, but
         * excludes inherited fields.
         * @param cls
         * @param <T>
         * @return
         */
        public <T> Builder forDeclaredFields(Class<T> cls) {
            return (forClassFields(cls, cls.getDeclaredFields()));
        }

        /**
         * Writer reflectively write all <code>Field</code> objects  of the class or
         * interface represented by the <code>Class</code> object with inheritances
         * included.
         * @param cls
         * @param <T>
         * @return
         */
        public <T> Builder forFields(Class<T> cls) {
            return forClassFields(cls, cls.getFields());
        }

        public <T> Builder forFields(Class<T> clz, final String... fields) {
            final Field[] accessors = new Field[fields.length];
            for (int i=0; i<accessors.length; i++) {
                accessors[i] = FieldUtils.getField(clz, fields[i], true);
            }
            return (forClassFields(clz, accessors));
        }

        public Builder forClassFields(Class cls, final Field[] fields) {
            writers.put(cls, new Writer<Object>() {
                @Override
                public void write(Context context, JsonGenerator jg, Object value) throws IOException {
                    jg.writeStartObject();
                    for (Field field : fields) {
                        try {
                            context.writeNamedObject(field.getName(), field.get(value), jg);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    jg.writeEndObject();
                }
            });
            return this;
        }

        public <T> Builder extratAsArray(Class<T> cls, final Extractor<T> extractor) {
            writers.put(cls, new Writer<T>() {
                @Override
                public void write(Context context, JsonGenerator jg, T value) throws IOException {
                    jg.writeStartArray();
                    for (Object obj : extractor.extract(value)) {
                        context.writeObject(obj, jg);
                    }
                    jg.writeEndArray();
                }
            });
            return this;
        }

        public <T> Builder extratAsObject(Class<T> cls, final Extractor<T> extractor) {
            writers.put(cls, new Writer<T>() {
                @Override
                public void write(Context context, JsonGenerator jg, T value) throws IOException {
                    jg.writeStartObject();
                    for (Object obj : extractor.extract(value)) {
                        context.writeObject(obj, jg);
                    }
                    jg.writeEndObject();
                }
            });
            return this;
        }

        public <F, T> Builder forTransform(Class<F> cls, final Function<F, T> transform) {
            writers.put(cls, new Writer<F>() {
                @Override
                public void write(Context context, JsonGenerator jg, F value) throws IOException {
                    context.writeObject(transform.apply(value), jg);
                }
            });
            return this;
        }
    }

}
