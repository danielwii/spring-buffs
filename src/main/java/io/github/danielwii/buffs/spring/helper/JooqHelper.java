package io.github.danielwii.wyf.helper;

import org.jooq.SortField;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

public enum JooqHelper {
    ;

    public static Collection<SortField<?>> getSortFields(@NotNull TableImpl table, Sort sortSpecification) {
        if (sortSpecification == null) {
            return Collections.emptyList();
        }

        return sortSpecification.stream().map(order -> {
            TableField tableField = getTableField(table, order.getProperty());
            return convertTableFieldToSortField(tableField, order.getDirection());
        }).collect(Collectors.toList());
    }

    private static SortField<?> convertTableFieldToSortField(@NotNull TableField tableField, Sort.Direction direction) {
        if (direction == Sort.Direction.ASC) {
            return tableField.asc();
        } else {
            return tableField.desc();
        }
    }

    private static TableField getTableField(@NotNull TableImpl table, String fieldName) {
        try {
            Optional<Field> optionalField = Arrays.stream(table.getClass().getFields())
                .filter(current -> current.getName().equalsIgnoreCase(fieldName))
                .findFirst();
            if (optionalField.isPresent()) {
                return (TableField) optionalField.get().get(table);
            } else {
                throw new NoSuchFieldException(fieldName);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            String message = String.format("Could not find table field: [%s]", fieldName);
            throw new InvalidDataAccessApiUsageException(message, e);
        }
    }

}
