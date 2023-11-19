package com.red.dxbc;

import com.red.dxbc.chunks.rdef.ResourceDefinitions;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.ShaderCode;
import com.red.dxbc.chunks.xsgn.D3D11SignatureParameter;
import com.red.dxbc.chunks.xsgn.InputSignature;
import com.red.dxbc.chunks.xsgn.OutputSignature;
import com.red.dxbc.decompiler.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ShaderDecompiler {
    private final DXBC shader;
    private final ResourceDefinitions rdef;
    private final ShaderCode shex;
    private final InputSignature isgn;
    private final OutputSignature osgn;

    public ShaderDecompiler(Path path) throws IOException {
        final byte[] bytes = Files.readAllBytes(path);
        shader = new DXBC(bytes);
        rdef = (ResourceDefinitions) shader.getChunk("RDEF");
        shex = (ShaderCode) shader.getChunk("SHEX");
        isgn = (InputSignature) shader.getChunk("ISGN");
        osgn = (OutputSignature) shader.getChunk("OSGN");
    }

    public ShaderDecompiler(byte[] bytes) {
        shader = new DXBC(bytes);
        rdef = (ResourceDefinitions) shader.getChunk("RDEF");
        shex = (ShaderCode) shader.getChunk("SHEX");
        isgn = (InputSignature) shader.getChunk("ISGN");
        osgn = (OutputSignature) shader.getChunk("OSGN");
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    public String decompile(int optimizationsIterations) {
        if (shex == null) {
            return "// Failed to decompile. Missing SHEX chunk";
        }
        StringBuilder ss = new StringBuilder();
        ss.append("struct Input{\n");
        for (D3D11SignatureParameter parameter : isgn.parameters) {
            ss.append('\t');
            ss.append(parameter.componentType);
            ss.append(parameter.mask.componetCount());
            ss.append(" ");
            ss.append(parameter.formattedRepr());
            ss.append(" : ");
            ss.append(parameter.semanticName);
            ss.append('\n');
        }
        ss.append("struct Output{\n");
        for (D3D11SignatureParameter parameter : osgn.parameters) {
            ss.append('\t');
            ss.append(parameter.componentType);
            ss.append(parameter.mask.componetCount());
            ss.append(" ");
            ss.append(parameter.formattedRepr());
            ss.append(" : ");
            ss.append(parameter.semanticName);
            ss.append('\n');
        }
        ss.append("}\n");
        int ident = 0;
        List<Element> elements = new ArrayList<>(shex.opcodes.size());
        for (Opcode opcode : shex.opcodes) {
            elements.addAll(opcode.toExpressions(shader));
        }
        for (int i = 0; i < optimizationsIterations; i++) {
            inlinePass(elements);
        }
        boolean needIndent = true;
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            boolean needNewLine;
            boolean needSemicolon = false;
            switch (element) {
                case Inlined inlined -> {
                    continue;
                }
                case Commentary commentary -> {
                    needNewLine = true;
                }
                case Statement statement && statement.template.contains("{") -> {
                    ident++;
                    if (needIndent)
                        addIdent(ident, ss);
                    ss.append(' ');
                    needNewLine = true;
                }
                case Statement statement && statement.template.contains("}") -> {
                    ident--;
                    if (needIndent)
                        addIdent(ident, ss);
                    needNewLine = !(elements.get(i + 1) instanceof Statement s) || !s.template.contains("else");
                }
                case Expression statement && statement.template.contains("if") -> {
                    addIdent(ident, ss);
                    needNewLine = false;
                    needIndent = false;
                }
                case Statement statement && statement.template.contains("loop") -> {
                    addIdent(ident, ss);
                    needNewLine = false;
                    needIndent = false;
                }
                case Statement statement && statement.template.contains("else") -> {
                    addIdent(ident, ss);
                    needNewLine = false;
                    needIndent = false;
                }
                case default -> {
                    addIdent(ident, ss);
                    needNewLine = true;
                    needIndent = true;
                    needSemicolon = true;
                }
            }
            ss.append(element);
            if (needSemicolon)
                ss.append(';');
            if (needNewLine)
                ss.append('\n');
        }
        return ss.toString();
    }

    private void inlinePass(List<Element> elements) {
        for (int i = 0; i < elements.size(); i++) {
            final Element element = elements.get(i);
            if (element instanceof Assignment assignment) {
                Operand currentOutput = (Operand) assignment.outputToken;
                int totalUseCount = 0;
                List<Element> useCases = new ArrayList<>();
                for (int j = i + 1; j < elements.size(); j++) {
                    final Element nextElement = elements.get(j);
                    if (nextElement instanceof Statement nextStatement) {
                        if (nextStatement.template.contains("}"))
                            break;
                        // if (nextStatement.template.contains("{"))
                        //     break;
                    } else if (nextElement instanceof Assignment nextAssignment) {
                        Operand output = (Operand) nextAssignment.outputToken;
                        if (!currentOutput.equals(output)) {
                            if (currentOutput.name.equals(output.name))
                                break;
                        } else {
                            final int operandUseCount = nextElement.getOperandUseCount(currentOutput);
                            totalUseCount += operandUseCount;
                            if (operandUseCount > 0)
                                useCases.add(nextElement);
                            break;
                        }
                    }
                    final int operandUseCount = nextElement.getOperandUseCount(currentOutput);
                    if (operandUseCount > 0)
                        useCases.add(nextElement);

                    totalUseCount += operandUseCount;
                }
                if (totalUseCount == 1) {
                    {
                        boolean res = useCases.get(0).inline((Assignment) element);
                        if (res) {
                            elements.set(i, new Inlined(elements.get(i).toString()));
                            // System.out.printf("Inlined %s into %s%n", element, useCases.get(0));
                        } else {
                            System.err.printf("Inline failed at %s. Tried to inline %s%n", useCases.get(0), element);
                        }
                    }
                }
                // System.out.printf("%s %d%n", assignment.outputToken, totalUseCount);
                // System.out.println(useCases);
            }
        }
    }

    private void addIdent(int ident, StringBuilder ss) {
        ss.append("\t".repeat(Math.max(0, ident)));
    }

}
