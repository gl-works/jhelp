package jhelp.lang;

/*
 * allow pass of value reference
 */
public class Refer<T> {
	private T value;

	public T reference(T value) {
		return this.value = value;
	}
	public T reference() {
		return this.value;
	}

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Refer) {
            Refer that = (Refer)object;
            return value == that.value ||
                    (value != null && value.equals(that.value)) ||
                    (that.value != null && that.value.equals(value));
        }
        return false;
    }

    static public <T> void refer(Refer<T> r, T v) {
        if (r != null) {
            r.reference(v);
        }
    }
}
