package com.red.dxbc.chunks.shdr;

import com.red.dxbc.chunks.shdr.enums.ComponentName;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.EnumValue;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentSelection {
    public final Map<Integer, ComponentName> components;

    public ComponentSelection(@NotNull List<ComponentName> components) {
        this.components = new HashMap<>(components.size());
        for (int i = 0; i < components.size(); i++) {
            this.components.put(i, components.get(i));
        }
    }

    public ComponentSelection(int value) {
        components = new HashMap<>(1);
        final SelectionMode mode = IOUtils.getEnum(SelectionMode.class, BitUnpacker.getIntRange(value, 2, 3));
        switch (mode) {
            case MASK_MODE -> {
                for (int i = 0; i < 4; i++) {
                    if (BitUnpacker.getBool(value, 4 + i))
                        components.put(i, IOUtils.getEnum(ComponentName.class, i));
                }
            }
            case SWIZZLE_MODE -> {
                components.put(0, IOUtils.getEnum(ComponentName.class, BitUnpacker.getIntRange(value, 4, 5)));
                components.put(1, IOUtils.getEnum(ComponentName.class, BitUnpacker.getIntRange(value, 6, 7)));
                components.put(2, IOUtils.getEnum(ComponentName.class, BitUnpacker.getIntRange(value, 8, 9)));
                components.put(3, IOUtils.getEnum(ComponentName.class, BitUnpacker.getIntRange(value, 10, 11)));
            }
            case SELECT_1_MODE -> {
                components.put(0, IOUtils.getEnum(ComponentName.class, BitUnpacker.getIntRange(value, 4, 5)));
            }
            default -> throw new IllegalArgumentException("Unknown selection mode: " + mode);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (components.containsKey(i))
                sb.append(components.get(i).name());
        }
        return sb.toString().toLowerCase();
    }

    public int size() {
        return components.size();
    }

    public ComponentName get(int i) {
        return getComponents().get(i);
    }

    public List<ComponentName> getComponents() {
        List<ComponentName> comps = new ArrayList<>(components.size());
        for (int i = 0; i < 4; i++) {
            if (components.containsKey(i))
                comps.add(components.get(i));
        }
        return comps;
    }

    public List<ComponentName> getComponents(int compCount) {
        if (compCount == -1) {
            return getComponents();
        }
        List<ComponentName> comps = new ArrayList<>(components.size());
        for (int i = 0; i < compCount; i++) {
            if (components.containsKey(i))
                comps.add(components.get(i));
        }
        return comps;
    }

    public List<ComponentName> getComponents(ComponentSelection components) {
        if (components.size() > size()) {
            throw new IllegalStateException();
        }
        if (this.components.size() == 1) {
            return this.getComponents();
        }

        List<ComponentName> comps = new ArrayList<>(components.size());
        for (ComponentName comp : components.getComponents()) {
            comps.add(this.components.get(comp.value()));
        }
        return comps;
    }

    public enum SelectionMode implements EnumValue {
        MASK_MODE,
        SWIZZLE_MODE,
        SELECT_1_MODE;

        @Override
        public int value() {
            return ordinal();
        }
    }

}
