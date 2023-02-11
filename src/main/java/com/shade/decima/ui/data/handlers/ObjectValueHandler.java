package com.shade.decima.ui.data.handlers;

import com.shade.decima.model.rtti.RTTIClass;
import com.shade.decima.model.rtti.RTTIType;
import com.shade.decima.model.rtti.objects.RTTIObject;
import com.shade.decima.model.rtti.path.RTTIPathElement;
import com.shade.decima.ui.data.ValueHandlerCollection;
import com.shade.decima.ui.data.registry.Type;
import com.shade.decima.ui.data.registry.ValueHandlerRegistration;
import com.shade.util.NotNull;
import com.shade.util.Nullable;

import javax.swing.*;
import java.util.Arrays;

@ValueHandlerRegistration(value = @Type(type = RTTIObject.class), order = 1000)
public class ObjectValueHandler implements ValueHandlerCollection<RTTIObject, RTTIPathElement.Field> {
    @Nullable
    @Override
    public Decorator getDecorator(@NotNull RTTIType<?> type) {
        return null;
    }

    @NotNull
    @Override
    public RTTIPathElement.Field[] getElements(@NotNull RTTIType<?> type, @NotNull RTTIObject object) {
        return Arrays.stream(((RTTIClass) type).getFields())
            .filter(field -> field.get(object) != null)
            .map(RTTIPathElement.Field::new)
            .toArray(RTTIPathElement.Field[]::new);
    }

    @NotNull
    @Override
    public String getElementName(@NotNull RTTIType<?> type, @NotNull RTTIObject object, @NotNull RTTIPathElement.Field element) {
        final RTTIClass.Field<Object> field = element.get();

        if (field.getCategory() != null) {
            for (RTTIClass.Field<?> other : object.type().getFields()) {
                if (other != field && other.getName().equals(field.getName())) {
                    return field.getCategory() + '.' + field.getName();
                }
            }
        }

        return field.getName();
    }

    @NotNull
    @Override
    public RTTIType<?> getElementType(@NotNull RTTIType<?> type, @NotNull RTTIObject object, @NotNull RTTIPathElement.Field element) {
        if (element.get(object) instanceof RTTIObject obj) {
            return obj.type();
        } else {
            return element.get().getType();
        }
    }

    @Nullable
    @Override
    public Icon getIcon(@NotNull RTTIType<?> type) {
        return UIManager.getIcon("Node.objectIcon");
    }
}
