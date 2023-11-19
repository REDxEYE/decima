package com.red.dxbc.chunks.shdr;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.enums.OperandNumComponents;
import com.red.dxbc.chunks.shdr.enums.OperandType;
import com.red.dxbc.decompiler.Element;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotImplementedException;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;

public class OperandToken0 {
    public final OperandNumComponents numComponents;
    public final ComponentSelection componentSelection;
    public final OperandType operandType;
    public final OperandIndexRepresentation operandIndexRepresentation;
    public final Index[] indices;
    public final boolean isExtended;
    public final OperandData operandData;
    public final Modifier modifier;

    public OperandToken0(@NotNull ByteBuffer buffer) {
        this(buffer, false);
    }

    public OperandToken0(@NotNull ByteBuffer buffer, boolean useInteger) {
        final int operandToken = buffer.getInt();
        numComponents = IOUtils.getEnum(OperandNumComponents.class, BitUnpacker.getIntRange(operandToken, 0, 1));
        if (numComponents == OperandNumComponents.OPERAND_4_COMPONENT) {
            componentSelection = new ComponentSelection(operandToken);
        } else {
            componentSelection = null;
        }
        operandType = IOUtils.getEnum(OperandType.class, BitUnpacker.getIntRange(operandToken, 12, 19));
        operandIndexRepresentation = new OperandIndexRepresentation(operandToken);
        isExtended = BitUnpacker.getBool(operandToken, 31);
        if (isExtended) {
            modifier = new Modifier(buffer.getInt());
        } else {
            modifier = null;
        }
        indices = new Index[operandIndexRepresentation.dimension.ordinal()];
        for (int i = 0; i < indices.length; i++) {

            indices[i] = switch (operandIndexRepresentation.indices[i]) {
                case IMMEDIATE32 -> new Index.Immediate32Index(buffer);
                case IMMEDIATE64 -> new Index.Immediate64Index(buffer);
                case IMMEDIATE32_PLUS_RELATIVE -> new Index.Immediate32PRelativeIndex(buffer);
                case RELATIVE -> new Index.RelativeIndex(buffer);
                default -> throw new NotImplementedException();
            };
        }
        operandData = switch (operandType) {
            case NULL -> new OperandData.NullOperandData();
            case TEMP -> new OperandData.RegisterOperandData();
            case INPUT -> new OperandData.InputOperandData();
            case OUTPUT -> new OperandData.OutputOperandData();
            case IMMEDIATE32 ->
                useInteger ? new OperandData.Immediate32OperandData(buffer, numComponents.componentCount()) : new OperandData.ImmediateFloat32OperandData(buffer, numComponents.componentCount());
            case IMMEDIATE64 ->
                useInteger ? new OperandData.Immediate64OperandData(buffer, numComponents.componentCount()) : new OperandData.ImmediateFloat64OperandData(buffer, numComponents.componentCount());
            case CONSTANT_BUFFER -> new OperandData.ConstantBufferOperandData();
            case IMMEDIATE_CONSTANT_BUFFER-> new OperandData.ImmediateConstantBufferOperandData();
            case RESOURCE -> new OperandData.ResourceOperandData();
            case SAMPLER -> new OperandData.SamplerOperandData();
            case INDEXABLE_TEMP -> new OperandData.IndexableTemp();
            default -> throw new IllegalArgumentException("Unhandled %s".formatted(operandType.name()));
        };

    }

    @Override
    public String toString() {
        String op = switch (operandType) {
            case NULL, IMMEDIATE32 -> operandData.toString();
            case TEMP, INPUT, OUTPUT -> "%s%s%s".formatted(operandData, indices[0], componentSelection);
            case SAMPLER -> "%s%s[%s:%s]".formatted(operandData, indices[0], indices[1], indices[1]);
            case RESOURCE -> "%s%s[%s]%s".formatted(operandData, indices[0], indices[1], componentSelection);
            case CONSTANT_BUFFER ->
                "%s%s[%s][%s]%s".formatted(operandData, indices[0], indices[1], indices[2], componentSelection);
            default -> "FIX_ME";
        };
        if (modifier == null) {
            return op;
        }
        return modifier.wrap(op);
    }


    public Element toExpression(@NotNull DXBC shader) {
        Element element = operandData.toExpression(shader, componentSelection, indices, componentSelection);
        return modifier != null ? modifier.wrap(element) : element;
    }

    public Element toExpression(@NotNull DXBC shader, ComponentSelection components) {
        Element element = operandData.toExpression(shader, components, indices, componentSelection);
        return modifier != null ? modifier.wrap(element) : element;
    }

    public Element toExpression(@NotNull DXBC shader, int compCount) {
        Element element = operandData.toExpression(shader, compCount, indices, componentSelection);
        return modifier != null ? modifier.wrap(element) : element;
    }
}
