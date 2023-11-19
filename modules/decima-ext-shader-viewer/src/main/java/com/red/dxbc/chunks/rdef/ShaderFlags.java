package com.red.dxbc.chunks.rdef;

import com.shade.platform.model.util.EnumSetValue;

public enum ShaderFlags implements EnumSetValue {
    None(0),
    Debug(1),
    SkipValidation(2),
    SkipOptimization(4),
    PackMatrixRowMajor(8),
    PackMatrixColumnMajor(16),
    PartialPrecision(32),
    ForceVsSoftwareNoOpt(64),
    ForcePsSoftwareNoOpt(128),
    NoPreshader(256),
    AvoidFlowControl(512),
    PreferFlowControl(1024),
    EnableStrictness(2048),
    EnableBackwardsCompatibility(4096),
    IeeeStrictness(8192),
    OptimizationLevel0(16384),
    OptimizationLevel1(0),
    OptimizationLevel2(49152),
    OptimizationLevel3(32768),
    Reserved16(65536),
    Reserved17(131072),
    WarningsAreErrors(262144);

    private final int value;

    ShaderFlags(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return value;
    }
}
