package com.red.dxbc.chunks.shdr;

import com.red.dxbc.chunks.shdr.enums.IndexRepresentation;
import com.red.dxbc.chunks.shdr.enums.OperandIndexDimension;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.IOUtils;

public class OperandIndexRepresentation {

    public final OperandIndexDimension dimension;
    public final IndexRepresentation[] indices;

    public OperandIndexRepresentation(int value) {
        dimension = IOUtils.getEnum(OperandIndexDimension.class, BitUnpacker.getIntRange(value, 20, 21));
        indices = new IndexRepresentation[dimension.ordinal()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = IOUtils.getEnum(IndexRepresentation.class, BitUnpacker.getIntRange(value, 22 + i * 3, 24 + i * 3));
        }

    }

}
