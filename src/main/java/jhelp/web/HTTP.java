package jhelp.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: g
 * Date: 13-10-25
 * Time: 上午7:15
 * To change this template use File | Settings | File Templates.
 */
public class HTTP {

    //src: play.data.Form

    public interface Para {
        String asString();
        Para get(String name);
        Para put(String name, Para child);
        Iterator<String> fields();
    }

    static private class ParaImpl implements Para {
        private final Object strOrMapstr;

        private ParaImpl() {
            this.strOrMapstr = new HashMap<String, Para>();
        }
        public ParaImpl(String value) {
            this.strOrMapstr = value;
        }

        @Override
        public String asString() {
            return strOrMapstr instanceof String ? (String)strOrMapstr : null;
        }

        @Override
        public Para get(String key) {
            return ((Map<String, Para>)strOrMapstr).get(key);
        }

        @Override
        public Iterator<String> fields() {
            return strOrMapstr instanceof Map ? ((Map)strOrMapstr).keySet().iterator()
                    : Collections.EMPTY_LIST.iterator();
        }

        @Override
        public Para put(String key, Para value) {
            Para old = ((Map<String, Para>)strOrMapstr).put(key, value);
            Preconditions.checkState(old == null);
            return value;
        }

        @Override
        public int hashCode() {
            return strOrMapstr.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof ParaImpl && strOrMapstr.equals(((ParaImpl)object).strOrMapstr);
        }

        @Override
        public String toString() {
            return strOrMapstr.toString();
        }
    }

    static private class ParaImplJson implements Para {
        private final JsonNode node;

        private ParaImplJson(JsonNode node) {
            this.node = node;
        }

        @Override
        public String asString() {
            return node.asText();
        }

        @Override
        public Para get(String name) {
            return new ParaImplJson(node.get(name));
        }

        @Override
        public Para put(String name, Para child) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<String> fields() {
            return node.fieldNames();
        }

        @Override
        public int hashCode() {
            return node.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof ParaImplJson && node.equals(((ParaImplJson)object).node);
        }

        @Override
        public String toString() {
            return node.toString();
        }
    }

    static private Para assure(Para parent, String key) {
        Para child = parent.get(key);
        if (child == null) {
            child = parent.put(key, new ParaImpl());
        }
        return child;
    }

    static public class Builder {
        private final Para para = new ParaImpl();

        public Builder forForm(Map<String, String[]> form) {
            if (form != null) {
                for (Map.Entry<String, String[]> ent : form.entrySet()) {
                    String encodedkey = ent.getKey();
                    String[] values = ent.getValue();
                    int k = encodedkey.indexOf('[');
                    if (k < 0) {
                        para.put(encodedkey, new ParaImpl(values[0]));
                    } else {
                        Para child = assure(para, encodedkey.substring(0, k));
                        int m = encodedkey.lastIndexOf('[');
                        int n = encodedkey.indexOf(']', m+1);
                        while (k < m) {
                            int j = encodedkey.indexOf(']', k);
                            child = assure(child, encodedkey.substring(k + 1, j));
                            k = encodedkey.indexOf('[', j+1);
                        }
                        if (n > m+1) {
                            child.put(encodedkey.substring(m + 1, n), new ParaImpl(values[0]));
                        } else if (n==m+1) { //else malformed
                            int seq = 0;
                            for (String v : values) {
                                child.put(Integer.toString(seq++), new ParaImpl(v));
                            }
                        }
                    }
                }
            }
            return this;
        }

        public Builder forJson(JsonNode json) {
            if(json != null) {
                Iterator<Map.Entry<String,JsonNode>> fields = json.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    para.put(field.getKey(), new ParaImplJson(field.getValue()));
                }
            }
            return this;
        }

        public Para finish() {
            return para;
        }
    }

    static public Builder builder() {
        return new Builder();
    }

    static public Builder builder(Builder builder) {
        return builder;
    }
}
