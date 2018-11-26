package io.github.danielwii.buffs.spring.shared;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Data;

@Data
public class PageableDTO {
    private Integer        page      = 0;
    private Integer        size      = 25;
    private Sort.Direction direction = Sort.Direction.DESC;
    private String[]       sort      = { "id" };

    public Pageable toPage() {
        return PageRequest.of(page, size, direction, sort);
    }

}
