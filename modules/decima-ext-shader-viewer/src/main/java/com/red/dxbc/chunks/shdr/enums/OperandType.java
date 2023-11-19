package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum OperandType implements EnumValue {
    TEMP,  // Temporary Register File
    INPUT,  // General Input Register File
    OUTPUT,  // General Output Register File
    INDEXABLE_TEMP,  // Temporary Register File (indexable)
    IMMEDIATE32,  // 32bit/component immediate value(s)
    IMMEDIATE64,  // 64bit/comp.imm.val(s)HI:LO(unused)
    SAMPLER,  // Reference to sampler state
    RESOURCE,  // Reference to memory resource (e.g. texture)
    CONSTANT_BUFFER,  // Reference to constant buffer
    IMMEDIATE_CONSTANT_BUFFER,  // Reference to immediate constant buffer
    LABEL, // Label
    INPUT_PRIMITIVEID, // Input primitive ID
    OUTPUT_DEPTH, // Output Depth
    NULL, // Null register, used to discard results of operations
    RASTERIZER, // DX10.1 Rasterizer register, used to denote the depth/stencil and render target resources
    OUTPUT_COVERAGE_MASK; // DX10.1 PS output MSAA coverage mask (scalar)

    @Override
    public int value() {
        return ordinal();
    }
}
