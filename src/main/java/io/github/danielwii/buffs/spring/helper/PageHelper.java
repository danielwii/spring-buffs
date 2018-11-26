package io.github.danielwii.buffs.spring.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.github.danielwii.buffs.spring.shared.PageableDTO;
import lombok.Setter;

public enum PageHelper {
    ;

    public static final Pageable DEFAULT = new PageableDTO().toPage();

    @Setter
    private static int maxSize = 1_000;

    private static int safeSize(int size) {
        return size > maxSize ? maxSize : size;
    }

    public static Pageable toPage(PageableDTO pageableDTO) {
        return PageRequest.of(pageableDTO.getPage(), safeSize(pageableDTO.getSize()), pageableDTO.getDirection(), pageableDTO.getSort());
    }

    public static Pageable toPage(Integer page, Integer size) {
        return PageRequest.of(page, safeSize(size));
    }

}
