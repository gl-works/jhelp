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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class JSONFactory {
    private final Map<Class, Writer> writers;
    private final JsonFactory factory;

    public class Session<OS extends OutputStream> {
        private final OS stream;
        private final JsonGenerator generator;

        public Session(OS stream) throws IOException {
            this.stream = stream;
            this.generator = factory.createGenerator(stream);
        }

        public Session<OS> writeObject(Object data) throws IOException {
            if (data == null) {
                generator.writeNull();
            } else {
                Class cls = data.getClass();
                Writer w = writers.get(cls);
                if (w == null) {
                    if (cls.isArray()) {
                        w = Writers.ARRAY; //primitive arrays should have been shot ahead
                    } else {
                        w = searchInterfaces(cls);
                        if (w == null) {
                            w = searchAncestors(cls);
                            Preconditions.checkState(w != null,
                                    "Don't know how to write class %s as json",
                                    cls.getName());
                        }
                        writers.put(cls, w);
                    }
                }
                w.write(this, data);
            }
            return this;
        }

        public Session<OS> writeFieldName(String name) throws IOException {
            generator.writeFieldName(name);
            return this;
        }

        public Session<OS> writeNamedObject(String name, Object o) throws IOException {
            writeFieldName(name);
            writeObject(o);
            return this;
        }

        public Session<OS> startObject() throws IOException {
            generator.writeStartObject();
            return this;
        }

        public Session<OS> endObject() throws IOException {
            generator.writeEndObject();
            return this;
        }

        public Session<OS> startArray() throws IOException {
            generator.writeStartArray();
            return this;
        }

        public Session<OS> endArray() throws IOException {
            generator.writeEndArray();
            return this;
        }

        public Session<OS> writeNumberField(String name, BigDecimal bigDecimal) throws IOException {
            generator.writeNumberField(name, bigDecimal);
            return this;
        }

        public Session<OS> writeNumberField(String name, int x) throws IOException {
            generator.writeNumberField(name, x);
            return this;
        }

        public Session<OS> writeNumberField(String name, long x) throws IOException {
            generator.writeNumberField(name, x);
            return this;
        }

        public Session<OS> writeNumberField(String name, float x) throws IOException {
            generator.writeNumberField(name, x);
            return this;
        }

        public Session<OS> writeNumberField(String name, double x) throws IOException {
            generator.writeNumberField(name, x);
            return this;
        }

        public Session<OS> writeStringField(String name, String value) throws IOException {
            generator.writeStringField(name, value);
            return this;
        }

        public Session<OS> writeNumber(Byte value) throws IOException {
            generator.writeNumber(value);
            return this;
        }

        public Session<OS> writeNumber(Short value) throws IOException {
            generator.writeNumber(value);
            return this;
        }

        public Session<OS> writeNumber(Integer value) throws IOException {
            generator.writeNumber(value);
            return this;
        }

        public Session<OS> writeNumber(Long value) throws IOException {
            generator.writeNumber(value);
            return this;
        }

        public Session<OS> writeNumber(Float value) throws IOException {
            generator.writeNumber(value);
            return this;
        }

        public Session<OS> writeNumber(Double value) throws IOException {
            generator.writeNumber(value);
            return this;
        }

        public Session<OS> writeBoolean(boolean value) throws IOException {
            generator.writeBoolean(value);
            return this;
        }

        public Session<OS> writeString(String value) throws IOException {
            generator.writeString(value);
            return this;
        }

        public OS finish() throws IOException {
            generator.flush();
            return stream;
        }
    }

    private JSONFactory(Map<Class, Writer> writers) {
        this.writers = writers;
        this.factory = new com.fasterxml.jackson.core.JsonFactory();
    }

    public <OS extends OutputStream> Session<OS> prepare(OS os) throws IOException {
        return new Session<OS>(os);
    }

    public String encode(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return encode(object, baos).toString();
    }

    public <OS extends OutputStream> OS encode(Object object, OS os) throws IOException {
        return prepare(os)
                .writeObject(object)
                .finish();
    }

    private Writer searchAncestors(Class cls) {
        for (Class p = cls.getSuperclass(); p!=null; p = p.getSuperclass()) {
            Writer w = writers.get(p);
            if (w != null) {
                return w;
            }
            if (null != (w = searchInterfaces(p))) {
                return w;
            }
        }
        return null;
    }

    private Writer searchInterfaces(Class cls) {
        for (Class p : cls.getInterfaces()) {
            Writer w = writers.get(p);
            if (w != null) {
                return w;
            }
            if (null != (w = searchInterfaces(p))) {
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

        public JSONFactory finish() {
            return new JSONFactory(writers);
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
            for (Field f : fields) {
                f.setAccessible(true);
            }
            writers.put(cls, new Writer<Object>() {
                @Override
                public void write(Session session, Object value) throws IOException {
                    session.startObject();
                    for (Field field : fields) {
                        try {
                            session.writeNamedObject(field.getName(), field.get(value));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    session.endObject();
                }
            });
            return this;
        }

        public Builder forClassMethods(Class cls, final String... names) {
            Method[] methods = new Method[names.length];
            for (int i=0; i<methods.length; i++) {
                try {
                    methods[i] = cls.getMethod(names[i]);
                } catch (NoSuchMethodException e) {
                    try {
                        methods[i] = cls.getDeclaredMethod(names[i]);
                    } catch (NoSuchMethodException e1) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return forClassMethods(cls, methods);
        }

        public Builder forClassMethods(Class cls, final Method[] methods) {
            for (Method m : methods) {
                m.setAccessible(true);
            }
            writers.put(cls, new Writer<Object>() {
                @Override
                public void write(Session session, Object value) throws IOException {
                    session.startObject();
                    for (Method method : methods) {
                        try {
                            session.writeNamedObject(method.getName(), method.invoke(value));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    session.endObject();
                }
            });
            return this;
        }

        public <T> Builder extratAsArray(Class<T> cls, final Extractor<T> extractor) {
            writers.put(cls, new Writer<T>() {
                @Override
                public void write(Session session, T value) throws IOException {
                    session.startArray();
                    for (Object obj : extractor.extract(value)) {
                        session.writeObject(obj);
                    }
                    session.endArray();
                }
            });
            return this;
        }

        public <T> Builder extratAsObject(Class<T> cls, final Extractor<T> extractor) {
            writers.put(cls, new Writer<T>() {
                @Override
                public void write(Session session, T value) throws IOException {
                    session.startObject();
                    for (Object obj : extractor.extract(value)) {
                        session.writeObject(obj);
                    }
                    session.endObject();
                }
            });
            return this;
        }

        public <F, T> Builder forTransform(Class<F> cls, final Function<F, T> transform) {
            writers.put(cls, new Writer<F>() {
                @Override
                public void write(Session session, F value) throws IOException {
                    session.writeObject(transform.apply(value));
                }
            });
            return this;
        }
    }

}
