package io.github.danielwii.buffs.spring.mapper;

import io.github.danielwii.buffs.spring.exception.IOneError;

public class IOneErrorFieldMapper {

    public String map(IOneError e) {
        return e != null ? e.toString() : null;
    }

}
