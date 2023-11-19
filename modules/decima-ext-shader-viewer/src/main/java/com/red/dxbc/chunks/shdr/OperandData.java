package com.red.dxbc.chunks.shdr;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.rdef.ConstantBuffer;
import com.red.dxbc.chunks.rdef.ResourceBinding;
import com.red.dxbc.chunks.rdef.ResourceDefinitions;
import com.red.dxbc.chunks.shdr.enums.ComponentName;
import com.red.dxbc.chunks.xsgn.D3D11SignatureParameter;
import com.red.dxbc.chunks.xsgn.InputSignature;
import com.red.dxbc.chunks.xsgn.OutputSignature;
import com.red.dxbc.decompiler.*;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;

public sealed abstract class OperandData {

    public abstract Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection);

    public abstract Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection);

    public static final class OutputOperandData extends OperandData {

        public OutputOperandData() {
        }

        @Override
        public String toString() {
            return "o";
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            if (indices.length != 1) {
                throw new IllegalArgumentException("Unexpected count if indices for Output register");
            }
            OutputSignature osgn = (OutputSignature) shader.getChunk("OSGN");
            if (indices[0] instanceof Index.Immediate32Index i && osgn != null) {
                D3D11SignatureParameter sig = osgn.getByRegister((int) i.index);
                return new Operand(sig.formattedRepr(), componentSelection.getComponents(compCount));
            }
            return new Operand(toString() + indices[0], componentSelection.getComponents(compCount));
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            if (indices.length != 1) {
                throw new IllegalArgumentException("Unexpected count if indices for Output register");
            }
            OutputSignature osgn = (OutputSignature) shader.getChunk("OSGN");
            if (indices[0] instanceof Index.Immediate32Index i && osgn != null) {
                D3D11SignatureParameter sig = osgn.getByRegister((int) i.index);
                return new Operand(sig.formattedRepr(), componentSelection.getComponents(components));
            }
            return new Operand(toString() + indices[0], componentSelection.getComponents(components));
        }
    }

    public static final class RegisterOperandData extends OperandData {

        public RegisterOperandData() {
        }

        @Override
        public String toString() {
            return "r";
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            if (indices.length != 1) {
                throw new IllegalArgumentException("Unexpected count if indices for Temp register");
            }
            return new Operand(toString() + indices[0], componentSelection.getComponents(compCount));
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            if (indices.length != 1) {
                throw new IllegalArgumentException("Unexpected count if indices for Output register");
            }
            return new Operand(toString() + indices[0], componentSelection.getComponents(components));
        }
    }

    public static final class ResourceOperandData extends OperandData {

        public ResourceOperandData() {
        }

        @Override
        public String toString() {
            return "T";
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            ResourceDefinitions rdef = (ResourceDefinitions) shader.getChunk("RDEF");
            if (indices[0] instanceof Index.Immediate32Index i && rdef != null) {
                ResourceBinding resource = rdef.resourceBindings.get((int) i.index);
                return new Operand(resource.name);
            }
            return new Operand(toString() + indices[0]);
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            ResourceDefinitions rdef = (ResourceDefinitions) shader.getChunk("RDEF");
            if (indices[0] instanceof Index.Immediate32Index i && rdef != null) {
                ResourceBinding resource = rdef.resourceBindings.get((int) i.index);
                return new Operand(resource.name);
            }
            return new Operand(toString() + indices[0]);
        }
    }

    public static final class SamplerOperandData extends OperandData {

        public SamplerOperandData() {
        }

        @Override
        public String toString() {
            return "S";
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            ResourceDefinitions rdef = (ResourceDefinitions) shader.getChunk("RDEF");
            if (indices[0] instanceof Index.Immediate32Index i && rdef != null) {
                ResourceBinding resource = rdef.resourceBindings.get((int) i.index);
                return new Operand(resource.name);
            }
            return new Operand(toString() + indices[0]);
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            ResourceDefinitions rdef = (ResourceDefinitions) shader.getChunk("RDEF");
            if (indices[0] instanceof Index.Immediate32Index i && rdef != null) {
                ResourceBinding resource = rdef.resourceBindings.get((int) i.index);
                return new Operand(resource.name);
            }
            return new Operand(toString() + indices[0]);
        }
    }

    public static final class NullOperandData extends OperandData {
        @Override
        public String toString() {
            return "null";
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            return new NullOperand();
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            return new NullOperand();
        }
    }

    public static final class InputOperandData extends OperandData {

        public InputOperandData() {
        }

        @Override
        public String toString() {
            return "v";
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            if (indices.length != 1) {
                throw new IllegalArgumentException("Unexpected count if indices for Input register");
            }
            InputSignature isgn = (InputSignature) shader.getChunk("ISGN");
            if (indices[0] instanceof Index.Immediate32Index i && isgn != null) {
                D3D11SignatureParameter sig = isgn.getByRegister((int) i.index);
                return new Operand(sig.formattedRepr(), componentSelection.getComponents(compCount));
            }
            return new Operand(toString() + indices[0], componentSelection.getComponents(compCount));
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            if (indices.length != 1) {
                throw new IllegalArgumentException("Unexpected count if indices for Output register");
            }
            InputSignature isgn = (InputSignature) shader.getChunk("ISGN");
            if (indices[0] instanceof Index.Immediate32Index i && isgn != null) {
                D3D11SignatureParameter sig = isgn.getByRegister((int) i.index);
                return new Operand(sig.formattedRepr(), componentSelection.getComponents(components));
            }
            return new Operand(toString() + indices[0], componentSelection.getComponents(components));
        }
    }

    public static final class ImmediateFloat64OperandData extends OperandData {
        public final double[] values;

        public ImmediateFloat64OperandData(@NotNull ByteBuffer buffer, int compCount) {
            values = new double[compCount];
            for (int i = 0; i < compCount; i++) {
                values[i] = buffer.getDouble();
            }
        }

        @Override
        public String toString() {
            StringBuilder ss = new StringBuilder("(");
            for (int i = 0; i < values.length; i++) {
                ss.append(values[i]);
                if (i != values.length - 1) {
                    ss.append(", ");
                }
            }
            ss.append(")");
            return ss.toString();
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            if (compCount != values.length && compCount != -1) {
                System.err.printf("Component count on immediate input does not match requested: %d!=%d%n", values.length, compCount);
            }
            if (compCount == 1) {
                return new ImmediateFloat64Value(values[0]);
            }
            double[] tmp = new double[compCount];
            System.arraycopy(values, 0, tmp, 0, compCount);
            return new ImmediateFloat64Value(tmp);
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            if (components.size() > values.length) {
                throw new IllegalArgumentException();
            }

            if (components.size() == 1) {
                if (values.length == 1) {
                    return new ImmediateFloat64Value(values[0]);
                }
                return new ImmediateFloat64Value(values[components.get(0).value()]);
            } else {
                double[] tmp = new double[components.size()];
                for (int i = 0; i < components.size(); i++) {
                    ComponentName component = components.get(i);
                    tmp[i] = values[component.value()];
                }
                return new ImmediateFloat64Value(tmp);
            }
        }
    }

    public static final class ImmediateFloat32OperandData extends OperandData {
        public final float[] values;

        public ImmediateFloat32OperandData(@NotNull ByteBuffer buffer, int compCount) {
            values = new float[compCount];
            for (int i = 0; i < compCount; i++) {
                values[i] = buffer.getFloat();
            }
        }

        @Override
        public String toString() {
            StringBuilder ss = new StringBuilder("(");
            for (int i = 0; i < values.length; i++) {
                ss.append(values[i]);
                if (i != values.length - 1) {
                    ss.append(", ");
                }
            }
            ss.append(")");
            return ss.toString();
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            if (compCount != values.length && compCount != -1) {
                System.err.printf("Component count on immediate input does not match requested: %d!=%d%n", values.length, compCount);
            }
            if (compCount == 1) {
                return new ImmediateFloat64Value(values[0]);
            }
            float[] tmp = new float[compCount];
            System.arraycopy(values, 0, tmp, 0, compCount);
            return new ImmediateFloat32Value(tmp);
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            if (components.size() > values.length) {
                throw new IllegalArgumentException();
            }

            if (components.size() == 1) {
                if (values.length == 1) {
                    return new ImmediateFloat32Value(values[0]);
                }
                return new ImmediateFloat32Value(values[components.get(0).value()]);
            } else {
                float[] tmp = new float[components.size()];
                for (int i = 0; i < components.size(); i++) {
                    ComponentName component = components.get(i);
                    tmp[i] = values[component.value()];
                }
                return new ImmediateFloat32Value(tmp);
            }
        }
    }

    public static final class Immediate64OperandData extends OperandData {
        public final long[] values;

        public Immediate64OperandData(@NotNull ByteBuffer buffer, int compCount) {
            values = new long[compCount];
            for (int i = 0; i < compCount; i++) {
                values[i] = buffer.getLong();
            }
        }

        @Override
        public String toString() {
            StringBuilder ss = new StringBuilder("(");
            for (int i = 0; i < values.length; i++) {
                ss.append(values[i]);
                if (i != values.length - 1) {
                    ss.append(", ");
                }
            }
            ss.append(")");
            return ss.toString();
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            if (compCount != values.length && compCount != -1) {
                System.err.printf("Component count on immediate input does not match requested: %d!=%d%n", values.length, compCount);
            }
            if (compCount == 1) {
                return new ImmediateFloat64Value(values[0]);
            }
            long[] tmp = new long[compCount];
            System.arraycopy(values, 0, tmp, 0, compCount);
            return new ImmediateInt64Value(tmp);
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            if (components.size() > values.length) {
                throw new IllegalArgumentException();
            }

            if (components.size() == 1) {
                if (values.length == 1) {
                    return new ImmediateInt64Value(values[0]);
                }
                return new ImmediateInt64Value(values[components.get(0).value()]);
            } else {
                long[] tmp = new long[components.size()];
                for (int i = 0; i < components.size(); i++) {
                    ComponentName component = components.get(i);
                    tmp[i] = values[component.value()];
                }
                return new ImmediateInt64Value(tmp);
            }
        }
    }

    public static final class Immediate32OperandData extends OperandData {
        public final int[] values;

        public Immediate32OperandData(@NotNull ByteBuffer buffer, int compCount) {
            values = new int[compCount];
            for (int i = 0; i < compCount; i++) {
                values[i] = buffer.getInt();
            }
        }

        @Override
        public String toString() {
            StringBuilder ss = new StringBuilder("(");
            for (int i = 0; i < values.length; i++) {
                ss.append(values[i]);
                if (i != values.length - 1) {
                    ss.append(", ");
                }
            }
            ss.append(")");
            return ss.toString();
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            if (compCount != values.length && compCount != -1) {
                System.err.printf("Component count on immediate input does not match requested: %d!=%d%n", values.length, compCount);
            }
            if (compCount == 1) {
                return new ImmediateInt32Value(values[0]);
            }
            int[] tmp = new int[compCount];
            System.arraycopy(values, 0, tmp, 0, compCount);
            return new ImmediateInt32Value(tmp);
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            if (components.size() > values.length) {
                throw new IllegalArgumentException();
            }

            if (components.size() == 1) {
                if (values.length == 1) {
                    return new ImmediateInt32Value(values[0]);
                }
                return new ImmediateInt32Value(values[components.get(0).value()]);
            } else {
                int[] tmp = new int[components.size()];
                for (int i = 0; i < components.size(); i++) {
                    ComponentName component = components.get(i);
                    tmp[i] = values[component.value()];
                }
                return new ImmediateInt32Value(tmp);
            }
        }
    }

    public static final class ConstantBufferOperandData extends OperandData {
        public ConstantBufferOperandData() {
        }

        @Override
        public String toString() {
            return "CB";
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            ResourceDefinitions rdef = (ResourceDefinitions) shader.getChunk("RDEF");
            if (indices[1] instanceof Index.Immediate32Index i && i.index != 0) {
                throw new IllegalArgumentException("AAAAAAAAAAA");
            }
            if (indices[0] instanceof Index.Immediate32Index cbId && rdef != null) {
                ConstantBuffer resource = rdef.constantBuffers.get((int) cbId.index);
                return new Operand(resource.buildVariableLookupFromRegisterId(indices[2]), componentSelection.getComponents(compCount));

            }
            return new Operand(toString() + indices[0], componentSelection.getComponents(compCount));
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            ResourceDefinitions rdef = (ResourceDefinitions) shader.getChunk("RDEF");
            if (indices[1] instanceof Index.Immediate32Index i && i.index != 0) {
                throw new IllegalArgumentException("AAAAAAAAAAA");
            }
            if (indices[0] instanceof Index.Immediate32Index cbId && rdef != null) {
                ConstantBuffer resource = rdef.constantBuffers.get((int) cbId.index);
                return new Operand(resource.buildVariableLookupFromRegisterId(indices[2]), componentSelection.getComponents(components));

            }
            return new Operand(toString() + indices[0], componentSelection.getComponents(components));
        }
    }

    public static final class IndexableTemp extends OperandData {

        @Override
        public String toString() {
            return "x";
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            if (indices.length != 2) {
                throw new IllegalArgumentException("Unexpected count if indices for Indexable register");
            }
            return new Operand(toString() + indices[0] + "[%s]".formatted(indices[1].formattedRepr(shader)), componentSelection.getComponents(compCount));
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            if (indices.length != 2) {
                throw new IllegalArgumentException("Unexpected count if indices for Indexable register");
            }
            return new Operand(toString() + indices[0] + "[%s]".formatted(indices[1].formattedRepr(shader)), componentSelection.getComponents(components));
        }
    }

    public static final class ImmediateConstantBufferOperandData extends OperandData {
        @Override
        public Element toExpression(@NotNull DXBC shader, int compCount, Index[] indices, ComponentSelection componentSelection) {
            return new Operand("icb[%s]".formatted(indices[0].formattedRepr(shader)), componentSelection.getComponents(compCount));
        }

        @Override
        public Element toExpression(@NotNull DXBC shader, ComponentSelection components, Index[] indices, ComponentSelection componentSelection) {
            return new Operand("icb[%s]".formatted(indices[0].formattedRepr(shader)), componentSelection.getComponents(components));
        }
    }
}
