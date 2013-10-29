package jhelp.json;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public interface Writer<T> {
    void write(Context context, JsonGenerator jg, T value) throws IOException;
}
