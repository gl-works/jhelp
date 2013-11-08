package jhelp.json;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class CustomizedTest {

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

        Writer<FooClass> writer = new Writer<FooClass>() {
            @Override
            public void write(JSONFactory.Session session, FooClass value) throws IOException {
                session.startObject();
                session.writeNamedObject("b1", value.b1);
                session.writeNamedObject("d0", value.d0);
                session.writeNamedObject("d1", value.d1);
                session.endObject();
            }
        };

        jf = JSONFactory.predefined().forClass(FooClass.class, writer).finish();

        assertEquals("test customized encode",
                "{\"b1\":\"b1v\",\"d0\":\"d0v\",\"d1\":\"d1v\"}",
                jf.encode(fc));
    }
}
