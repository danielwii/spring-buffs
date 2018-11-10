package io.github.danielwii.wyf.infrastructure;

import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;

class CaseInsensitivePropertyEditor<T extends Enum<T>> extends PropertyEditorSupport {

    private final Class<T> typeParameterClass;

    CaseInsensitivePropertyEditor(Class<T> typeParameterClass) {
        super();
        this.typeParameterClass = typeParameterClass;
    }

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (StringUtils.isNotEmpty(text)) {
            T value = T.valueOf(typeParameterClass, text.toUpperCase());
            setValue(value);
        }
    }
}
