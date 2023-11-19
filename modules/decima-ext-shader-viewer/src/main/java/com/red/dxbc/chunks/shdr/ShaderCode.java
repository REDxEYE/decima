package com.red.dxbc.chunks.shdr;

import com.red.dxbc.Chunk;
import com.red.dxbc.chunks.shdr.enums.D3D10Opcode;
import com.red.dxbc.chunks.shdr.enums.ProgramType;
import com.red.dxbc.chunks.shdr.opcodes.*;
import com.red.dxbc.chunks.shdr.opcodes.bitwise.*;
import com.red.dxbc.chunks.shdr.opcodes.declarations.*;
import com.red.dxbc.chunks.shdr.opcodes.floatOpcodes.*;
import com.red.dxbc.chunks.shdr.opcodes.flowControl.*;
import com.red.dxbc.chunks.shdr.opcodes.intOpcodes.*;
import com.red.dxbc.chunks.shdr.opcodes.samplers.*;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.EnumValue;
import com.shade.platform.model.util.IOUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ShaderCode extends Chunk {
    public final ShaderVersion version;
    public final List<Opcode> opcodes;

    public ShaderCode(String name, byte[] data) {
        super(name);
        final ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        version = new ShaderVersion(buffer);
        opcodes = new ArrayList<>();


        if (version.programType == ProgramType.ComputeShader) {
            // TODO: Check if anything else is needed from the SHDR or SHEX blob.
            return;
        }
        final int size = buffer.getInt();
        if (size * 4 != data.length) {
            throw new IllegalArgumentException("Size does not match expected");
        }
        ByteBuffer opcodeBuffer = buffer.slice(buffer.position(), (size - 2) * 4).order(ByteOrder.LITTLE_ENDIAN);
        while (opcodeBuffer.remaining() > 0) {
            final int opcodeStart = opcodeBuffer.position();
            final int opcodeToken = opcodeBuffer.getInt();
            final D3D10Opcode opcode = IOUtils.getEnum(D3D10Opcode.class, BitUnpacker.getIntRange(opcodeToken, 0, 10));
            if (opcode == D3D10Opcode.CUSTOMDATA) {
                final CustomDataClass customDataClass = IOUtils.getEnum(CustomDataClass.class, BitUnpacker.getInt(opcodeToken, 11, 21));
                final int customSize = opcodeBuffer.getInt() - 2;
                final int[] customData = new int[customSize];
                for (int i = 0; i < customSize; i++) {
                    customData[i] = opcodeBuffer.getInt();
                }
            } else {
                Opcode opcodeC = switch (opcode) {
                    case DCL_GLOBAL_FLAGS -> new DeclareGlobalFlags(opcodeToken, opcodeBuffer);
                    case DCL_CONSTANT_BUFFER -> new DeclareConstantBuffer(opcodeToken, opcodeBuffer);
                    case DCL_RESOURCE -> new DeclareResource(opcodeToken, opcodeBuffer);
                    case DCL_RESOURCE_STRUCTURED -> new DeclareStructuredResource(opcodeToken, opcodeBuffer);
                    case DCL_UNORDERED_ACCESS_VIEW_TYPED -> new DeclareUnorderedAccessViewTyped(opcodeToken, opcodeBuffer);
                    case DCL_SAMPLER -> new DeclareSampler(opcodeToken, opcodeBuffer);
                    case DCL_INPUT -> new DeclareInput(opcodeToken, opcodeBuffer);
                    case DCL_INPUT_PS -> new DeclareInputFragment(opcodeToken, opcodeBuffer);
                    case DCL_INPUT_SGV -> new DeclareInputWithSystemGeneratedValue(opcodeToken, opcodeBuffer);
                    case DCL_INPUT_PS_SIV ->
                        new DeclareInputFragmentWithSystemInterpretedValue(opcodeToken, opcodeBuffer);
                    case DCL_INPUT_PS_SGV ->
                        new DeclareInputFragmentWithSystemGeneratedValue(opcodeToken, opcodeBuffer);
                    case DCL_OUTPUT -> new DeclareOutput(opcodeToken, opcodeBuffer);
                    case DCL_OUTPUT_SIV -> new DeclareOutputWithSystemInterpretedValue(opcodeToken, opcodeBuffer);
                    case DCL_TEMPS -> new DeclareTempRegisters(opcodeToken, opcodeBuffer);
                    case DCL_INDEXABLE_TEMP -> new DeclareIndexableTempRegisters(opcodeToken, opcodeBuffer);
                    case RESINFO -> new ResourceInfo(opcodeToken, opcodeBuffer);
                    case DERIV_RTX -> new DerivativeFdx(opcodeToken, opcodeBuffer);
                    case DERIV_RTX_FINE -> new DerivativeFdxFine(opcodeToken, opcodeBuffer);
                    case DERIV_RTX_COARSE -> new DerivativeFdxCoarse(opcodeToken, opcodeBuffer);
                    case DERIV_RTY -> new DerivativeFdy(opcodeToken, opcodeBuffer);
                    case DERIV_RTY_FINE -> new DerivativeFdyFine(opcodeToken, opcodeBuffer);
                    case DERIV_RTY_COARSE -> new DerivativeFdyCoarse(opcodeToken, opcodeBuffer);
                    case F16TOF32 -> new Float16ToFloat32(opcodeToken, opcodeBuffer);
                    case F32TOF16 -> new Float32ToFloat16(opcodeToken, opcodeBuffer);
                    case LD -> new Fetch(opcodeToken, opcodeBuffer);
                    case LD_STRUCTURED -> new FetchStructured(opcodeToken, opcodeBuffer);
                    case SAMPLE_B -> new SampleWithBias(opcodeToken, opcodeBuffer);
                    case SAMPLE_L -> new SampleLod(opcodeToken, opcodeBuffer);
                    case SAMPLE_C -> new SampleComparison(opcodeToken, opcodeBuffer);
                    case SAMPLE_C_LZ -> new SampleComparisonLevelZero(opcodeToken, opcodeBuffer);
                    case SAMPLE -> new Sample(opcodeToken, opcodeBuffer);
                    case LOD -> new Lod(opcodeToken, opcodeBuffer);
                    case GATHER4 -> new Gather4(opcodeToken, opcodeBuffer);
                    case GATHER4_C -> new Gather4Comparison(opcodeToken, opcodeBuffer);
                    case GATHER4_PO -> new Gather4WithOffset(opcodeToken, opcodeBuffer);
                    case GATHER4_PO_C -> new Gather4WithOffsetComparison(opcodeToken, opcodeBuffer);
                    case FTOI -> new FloatToInt(opcodeToken, opcodeBuffer);
                    case FTOU -> new FloatToUInt(opcodeToken, opcodeBuffer);
                    case ITOF -> new IntToFloat(opcodeToken, opcodeBuffer);
                    case UTOF -> new UIntToFloat(opcodeToken, opcodeBuffer);
                    case ROUND_NI -> new RoundToNegInf(opcodeToken, opcodeBuffer);
                    case ROUND_PI -> new RoundToInf(opcodeToken, opcodeBuffer);
                    case ROUND_NE -> new RoundToNearEven(opcodeToken, opcodeBuffer);
                    case ROUND_Z -> new RoundToZero(opcodeToken, opcodeBuffer);
                    case FRC -> new Fractional(opcodeToken, opcodeBuffer);
                    case LOG -> new Logarithm(opcodeToken, opcodeBuffer);
                    case EXP -> new Exponent(opcodeToken, opcodeBuffer);
                    case SQRT -> new SquareRoot(opcodeToken, opcodeBuffer);
                    case RSQ -> new ReciprocalSquareRoot(opcodeToken, opcodeBuffer);
                    case DP2 -> new DotProduct2(opcodeToken, opcodeBuffer);
                    case DP3 -> new DotProduct3(opcodeToken, opcodeBuffer);
                    case DP4 -> new DotProduct4(opcodeToken, opcodeBuffer);
                    case MOVC -> new MoveConditional(opcodeToken, opcodeBuffer);
                    case MOV -> new Move(opcodeToken, opcodeBuffer);
                    case INEG -> new IntegerNegative(opcodeToken, opcodeBuffer);
                    case ADD -> new Add(opcodeToken, opcodeBuffer);
                    case IADD -> new IntegerAdd(opcodeToken, opcodeBuffer);
                    case MUL -> new Multiply(opcodeToken, opcodeBuffer);
                    case IMUL -> new IntegerMultiply(opcodeToken, opcodeBuffer);
                    case UDIV -> new UIntegerDivide(opcodeToken, opcodeBuffer);
                    case DIV -> new Divide(opcodeToken, opcodeBuffer);
                    case MAD -> new MultiplyAdd(opcodeToken, opcodeBuffer);
                    case IMAD -> new IntegerMultiplyAdd(opcodeToken, opcodeBuffer);
                    case IMAX -> new IntegerMaximum(opcodeToken, opcodeBuffer);
                    case UMAX -> new UIntegerMaximum(opcodeToken, opcodeBuffer);
                    case MAX -> new Maximum(opcodeToken, opcodeBuffer);
                    case IMIN -> new IntegerMinimum(opcodeToken, opcodeBuffer);
                    case UMIN -> new UIntegerMinimum(opcodeToken, opcodeBuffer);
                    case MIN -> new Minimum(opcodeToken, opcodeBuffer);
                    case OR -> new Or(opcodeToken, opcodeBuffer);
                    case AND -> new And(opcodeToken, opcodeBuffer);
                    case NOT -> new Not(opcodeToken, opcodeBuffer);
                    case XOR -> new Xor(opcodeToken, opcodeBuffer);
                    case ISHL -> new ShiftLeft(opcodeToken, opcodeBuffer);
                    case ISHR -> new ShiftRight(opcodeToken, opcodeBuffer);
                    case USHR -> new UShiftRight(opcodeToken, opcodeBuffer);
                    case UBFE -> new UnsignedBitFieldExtract(opcodeToken, opcodeBuffer);
                    case BFI -> new BitFieldInsert(opcodeToken, opcodeBuffer);
                    case INE -> new IntegerNotEqual(opcodeToken, opcodeBuffer);
                    case NE -> new NotEqual(opcodeToken, opcodeBuffer);
                    case GE -> new GreaterOrEqual(opcodeToken, opcodeBuffer);
                    case IGE -> new IntegerGreaterOrEqual(opcodeToken, opcodeBuffer);
                    case UGE -> new UIntegerGreaterOrEqual(opcodeToken, opcodeBuffer);
                    case LT -> new LessThen(opcodeToken, opcodeBuffer);
                    case ILT -> new IntegerLessThen(opcodeToken, opcodeBuffer);
                    case ULT -> new UIntegerLessThen(opcodeToken, opcodeBuffer);
                    case EQ -> new Equal(opcodeToken, opcodeBuffer);
                    case IEQ -> new IntegerEqual(opcodeToken, opcodeBuffer);
                    case SINCOS -> new SinCos(opcodeToken, opcodeBuffer);
                    case LOOP -> new Loop(opcodeToken);
                    case DISCARD -> new DiscardConditional(opcodeToken, opcodeBuffer);
                    case SWITCH -> new Switch(opcodeToken, opcodeBuffer);
                    case CASE -> new SwitchCase(opcodeToken, opcodeBuffer);
                    case DEFAULT -> new SwitchDefault(opcodeToken);
                    case ENDSWITCH -> new SwitchEnd(opcodeToken);
                    case BREAK -> new Break(opcodeToken);
                    case IF -> new IfCase(opcodeToken, opcodeBuffer);
                    case BREAKC -> new BreakConditional(opcodeToken, opcodeBuffer);
                    case RET -> new Return(opcodeToken);
                    case ENDIF -> new EndIf(opcodeToken);
                    case ENDLOOP -> new EndLoop(opcodeToken);
                    case ELSE -> new Else(opcodeToken);
                    default -> {
                        System.out.printf("Unsuppored opcode %s%n", opcode.name());
                        yield new DummyOpcode(opcodeToken, opcodeBuffer);
                    }
                };
                final int opcodeSize = opcodeC.getSize() * 4;
                if (opcodeBuffer.position() != opcodeStart + opcodeSize) {
                    throw new IllegalStateException("Opcode %s read more/less data than expected".formatted(opcode));
                }
                opcodes.add(opcodeC);
            }
        }
    }

    public enum CustomDataClass implements EnumValue {
        COMMENT,
        DEBUGINFO,
        OPAQUE,
        DCL_IMMEDIATE_CONSTANT_BUFFER,
        SHADER_MESSAGE,
        SHADER_CLIP_PLANE_CONSTANT_MAPPINGS_FOR_DX9;

        @Override
        public int value() {
            return ordinal();
        }
    }


}
