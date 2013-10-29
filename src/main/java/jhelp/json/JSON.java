package jhelp.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JSON {
    static public <T> String toString(T data, Writer<T> writer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonFactory f = new JsonFactory();
        JsonGenerator g = f.createGenerator(baos);
        writer.write(null, g, data);
        g.flush();
        return baos.toString();
    }

    static public String toString(Object data, Context context) {
        return null;
    }
}
