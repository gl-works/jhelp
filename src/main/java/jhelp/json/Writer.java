package jhelp.json;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public interface Writer<T> {
    void write(JSONFactory.Session session, T value) throws IOException;
}
