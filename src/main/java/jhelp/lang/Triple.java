package jhelp.lang;

/**
 * Created with IntelliJ IDEA.
 * User: g
 * Date: 13-10-26
 * Time: 下午4:42
 * To change this template use File | Settings | File Templates.
 */
public class Triple<X, Y, Z> {
    public final X first;
    public final Y second;
    public final Z third;

    public Triple(X first, Y second, Z third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    static public <X, Y, Z> Triple<X, Y, Z> of(X first, Y second, Z third) {
        return new Triple(first, second, third);
    }
}
