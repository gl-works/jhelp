package jhelp.json;

import jhelp.lang.Pair;
import jhelp.lang.Triple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class PredifinedTest {

    @Test
    public void run() throws IOException {
        JSONFactory jf = JSONFactory.predefined().finish();

        assertEquals("test json encode", "123", jf.encode(123));

        assertEquals("test json encode", "123", jf.encode(123L));

        assertEquals("test json encode", "true", jf.encode(true));

        assertEquals("test json encode", "false", jf.encode(false));

        assertEquals("test json encode", "1.2", jf.encode(1.2));

        assertEquals("test json encode", "{\"foo\":\"bar\"}", jf.encode(forKeyValue("foo", "bar")));

        assertEquals("test json encode", "{\"foo\":123}", jf.encode(forKeyValue("foo", 123)));

        assertEquals("test json encode", "[true,false]", jf.encode(new boolean[]{true,false}));

        assertEquals("test json encode", "[12,34]", jf.encode(new byte[]{12,34}));

        assertEquals("test json encode", "[123,456]", jf.encode(new int[]{123, 456}));

        assertEquals("test json encode", "[123,456]", jf.encode(new Object[]{123, 456}));

        assertEquals("test json encode", "[123,\"foo\"]", jf.encode(new Object[]{123, "foo"}));

        assertEquals("test json encode", "[\"foo\",\"bar\"]", jf.encode(new String[]{"foo", "bar"}));

        assertEquals("test json encode", "[\"foo\",\"bar\"]", jf.encode(Pair.of("foo", "bar")));

        assertEquals("test json encode", "[\"foo\",\"bar\",\"baz\"]", jf.encode(Triple.of("foo", "bar", "baz")));
    }

    static private <K,V> Map<K,V> forKeyValue(K k, V v) {
        Map<K,V> m = new HashMap<K, V>();
        m.put(k, v);
        return m;
    }
}
