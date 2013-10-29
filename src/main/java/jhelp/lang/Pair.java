package jhelp.lang;

public class Pair<A, B> {
    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;
        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair otherPair = (Pair) other;
            if ((first == null && otherPair.first == null) ||
                    (first != null && first.equals(otherPair.first))) {
                if ((second == null && otherPair.second == null) ||
                        (second != null && second.equals(otherPair.second))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }

    public static <A,B> Pair<A,B> of(A a, B b) {
        return new Pair<A, B>(a, b);
    }
}
