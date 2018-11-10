package io.github.danielwii.buffs.spring.exception;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import io.github.danielwii.buffs.spring.mapper.ThrowableMapper;

@Mapper(uses = ThrowableMapper.class)
public interface OneErrorHolderMapper {

    OneErrorHolderMapper INSTANCE = Mappers.getMapper(OneErrorHolderMapper.class);

    @Mappings({
        @Mapping(source = "message", target = "error.message"),
        @Mapping(source = "exception", target = "error.exception"),
        @Mapping(source = "additional", target = "error.additional"),
        @Mapping(source = "error.code", target = "error.code"),
        @Mapping(source = "error", target = "error.type"),
        @Mapping(source = "errors", target = "error.errors"),
    })
    OneErrorHolderVo toVO(OneErrorHolder errorHolder);

}
