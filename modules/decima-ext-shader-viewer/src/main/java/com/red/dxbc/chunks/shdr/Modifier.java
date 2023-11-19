package com.red.dxbc.chunks.shdr;

import com.red.dxbc.decompiler.Absolute;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Negate;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.EnumValue;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotImplementedException;

public class Modifier {
    private final OperandModifier modifier;

    public Modifier(int value) {
        ExtendedOperandType type = IOUtils.getEnum(ExtendedOperandType.class, BitUnpacker.getIntRange(value, 0, 5));
        if (type == ExtendedOperandType.Modifier)
            modifier = IOUtils.getEnum(OperandModifier.class, BitUnpacker.getIntRange(value, 6, 13));
        else
            modifier = null;
    }

    public String wrap(String s) {
        if (modifier == null) {
            return s;
        }
        return switch (modifier) {

            case None -> s;
            case Neg -> '-' + s;
            case Abs -> '|' + s + '|';
            case AbsNeg -> throw new NotImplementedException();
        };
    }

    public Element wrap(Element s) {
        if (modifier == null) {
            return s;
        }
        return switch (modifier) {

            case None -> s;
            case Neg -> new Negate(s);
            case Abs -> new Absolute(s);
            case AbsNeg -> new Negate(new Absolute(s));
        };
    }

    public enum ExtendedOperandType implements EnumValue {
        // Might be used if this enum is full and further extended opcode is needed.
        Empty,

        Modifier;

        @Override
        public int value() {
            return ordinal();
        }

    }

    public enum OperandModifier implements EnumValue {
        None,
        Neg,
        Abs,
        AbsNeg;

        @Override
        public int value() {
            return ordinal();
        }
    }
}
