package io.github.danielwii.wyf.mapper;

public class ThrowableMapper {

    public String map(Throwable e) {
        return e != null ? e.toString() : null;
    }

}
