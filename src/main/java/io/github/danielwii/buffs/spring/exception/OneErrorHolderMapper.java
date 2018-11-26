package io.github.danielwii.buffs.spring.exception;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import io.github.danielwii.buffs.spring.mapper.IOneErrorFieldMapper;
import io.github.danielwii.buffs.spring.mapper.ThrowableFieldMapper;

@Mapper(uses = {
        ThrowableFieldMapper.class,
        IOneErrorFieldMapper.class,
})
public interface OneErrorHolderMapper {

    OneErrorHolderMapper INSTANCE = Mappers.getMapper(OneErrorHolderMapper.class);

    @Mappings({
            @Mapping(source = "message", target = "error.message"),
            @Mapping(source = "cause", target = "error.cause"),
            @Mapping(source = "additional", target = "error.additional"),
            @Mapping(source = "error.code", target = "error.code"),
            @Mapping(source = "error", target = "error.type"),
            @Mapping(source = "errors", target = "error.errors"),
    })
    OneErrorHolderVO toVO(OneErrorHolder errorHolder);

}
