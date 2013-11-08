package jhelp.json;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ReflectedTest {

    static class FooBase {
        private String b0;
        public String b1;

        private String b0m() {
            return b1;
        }

        public String b1m() {
            return b1;
        }
    }
    static class FooClass extends FooBase {
        private String d0;
        public String d1;

        private String d0m() {
            return d0;
        }

        public String d1m() {
            return d1;
        }
    }

    @Test
    public void run() throws IOException {
        FooClass fc = new FooClass();
        ((FooBase)(fc)).b0 = "b0v";
        fc.b1 = "b1v";
        fc.d0 = "d0v";
        fc.d1 = "d1v";

        JSONFactory jf;

        jf = JSONFactory.predefined().forFields(FooClass.class).finish();

        assertEquals("test reflected encode",
                "{\"d1\":\"d1v\",\"b1\":\"b1v\"}",
                jf.encode(fc));

        jf = JSONFactory.predefined().forDeclaredFields(FooClass.class).finish();

        assertEquals("test reflected encode",
                "{\"d0\":\"d0v\",\"d1\":\"d1v\"}",
                jf.encode(fc));

        jf = JSONFactory.predefined().forClassMethods(FooClass.class, "d1m", "b1m", "d0m").finish();

        assertEquals("test reflected encode",
                "{\"d1m\":\"d1v\",\"b1m\":\"b1v\",\"d0m\":\"d0v\"}",
                jf.encode(fc));
    }
}
