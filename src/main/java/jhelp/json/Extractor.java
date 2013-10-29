package jhelp.json;

public interface Extractor<F> {
    Object[] extract(F object);
}
