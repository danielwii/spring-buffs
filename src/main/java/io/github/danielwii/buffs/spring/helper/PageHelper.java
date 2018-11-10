package io.github.danielwii.buffs.spring.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.github.danielwii.buffs.spring.infrastructure.PageableDTO;

public enum PageHelper {
    ;

    public static final Pageable DEFAULT = new PageableDTO().toPage();

    public static Pageable toPage(PageableDTO pageableDto) {
        return PageRequest.of(pageableDto.getPage(), pageableDto.getSize(), pageableDto.getDirection(), pageableDto.getSort());
    }
}
