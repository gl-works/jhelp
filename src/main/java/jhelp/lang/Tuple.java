package jhelp.lang;

import java.util.Arrays;

public class Tuple {
    private final Object[] objects;

    public Tuple(Object[] objects) {
        this.objects = objects;
    }

    public Object get(int at) {
        return objects[at];
    }

    public int size() {
        return objects.length;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(objects);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Tuple ?
                Arrays.equals(objects, ((Tuple)object).objects) : false;
    }

    @Override
    public String toString() {
        return Arrays.toString(objects);
    }

    static public Tuple compose(Object... objects) {
        return new Tuple(objects);
    }
}
