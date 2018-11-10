package io.github.danielwii.buffs.spring.mapper;

public class ThrowableMapper {

    public String map(Throwable e) {
        return e != null ? e.toString() : null;
    }

}
