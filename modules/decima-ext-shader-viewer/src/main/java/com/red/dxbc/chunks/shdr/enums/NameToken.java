package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum NameToken implements EnumValue {
    undefined,
    position,
    clip_distance,
    cull_distance,
    render_target_array_index,
    viewport_array_index,
    vertex_id,
    primitive_id,
    instance_id,
    is_front_face,
    sample_index;

    @Override
    public int value() {
        return ordinal();
    }
}
