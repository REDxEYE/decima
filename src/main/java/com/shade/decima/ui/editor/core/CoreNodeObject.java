package com.shade.decima.ui.editor.core;

import com.shade.decima.model.rtti.RTTIType;
import com.shade.decima.model.rtti.path.RTTIPath;
import com.shade.decima.model.rtti.path.RTTIPathElement;
import com.shade.decima.model.rtti.types.RTTITypeArray;
import com.shade.decima.ui.data.ValueHandler;
import com.shade.decima.ui.data.ValueHandlerCollection;
import com.shade.decima.ui.data.registry.ValueRegistry;
import com.shade.platform.model.runtime.ProgressMonitor;
import com.shade.platform.ui.controls.tree.TreeNode;
import com.shade.platform.ui.controls.tree.TreeNodeLazy;
import com.shade.util.NotNull;
import com.shade.util.Nullable;

import javax.swing.*;
import java.util.Objects;

public class CoreNodeObject extends TreeNodeLazy {
    private final RTTIType<?> type;
    private final String name;
    private final RTTIPathElement element;
    private final RTTIPath path;
    private ValueHandler handler;
    private State state;

    public CoreNodeObject(@NotNull TreeNode parent, @NotNull RTTIType<?> type, @NotNull String name, @NotNull RTTIPathElement element) {
        super(parent);
        this.type = type;
        this.name = name;
        this.element = element;
        this.path = new RTTIPath(getPathToRoot(this, 0));
        this.handler = ValueRegistry.getInstance().findHandler(getValue(), type, getParentOfType(CoreNodeBinary.class).getGameType());
        this.state = State.UNCHANGED;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    protected TreeNode[] loadChildren(@NotNull ProgressMonitor monitor) {
        if (handler instanceof ValueHandlerCollection<?, ?>) {
            final var value = getValue();
            final var handler = (ValueHandlerCollection<Object, RTTIPathElement>) this.handler;
            final var elements = handler.getElements(type, value);
            final var children = new CoreNodeObject[elements.length];

            for (int i = 0; i < children.length; i++) {
                final RTTIPathElement element = elements[i];

                children[i] = new CoreNodeObject(
                    this,
                    handler.getElementType(type, value, element),
                    handler.getElementName(type, value, element),
                    element
                );
            }

            return children;
        }

        return EMPTY_CHILDREN;
    }

    @Override
    protected boolean allowsChildren() {
        return handler instanceof ValueHandlerCollection;
    }

    @Override
    public boolean loadChildrenInBackground() {
        return !(type instanceof RTTITypeArray);
    }

    @NotNull
    @Override
    public String getLabel() {
        return name;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return handler.getIcon(type);
    }

    @NotNull
    public ValueHandler getHandler() {
        return handler;
    }

    public void setHandler(@NotNull ValueHandler handler) {
        this.handler = handler;
    }

    @NotNull
    public RTTIType<?> getType() {
        return type;
    }

    @NotNull
    public Object getValue() {
        return path.get(getParentOfType(CoreNodeBinary.class).getBinary());
    }

    public void setValue(@NotNull Object value) {
        path.set(getParentOfType(CoreNodeBinary.class).getBinary(), value);
    }

    @NotNull
    public State getState() {
        return state;
    }

    public void setState(@NotNull State state) {
        this.state = state;
    }

    @NotNull
    public RTTIPath getPath() {
        return path;
    }

    @NotNull
    public RTTIPathElement getElement() {
        return element;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoreNodeObject that = (CoreNodeObject) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @NotNull
    protected static RTTIPathElement[] getPathToRoot(@NotNull TreeNode node, int depth) {
        final TreeNode parent = node.getParent();
        final RTTIPathElement[] elements;

        if (parent instanceof CoreNodeObject) {
            elements = getPathToRoot(parent, depth + 1);
        } else {
            elements = new RTTIPathElement[depth + 1];
        }

        elements[elements.length - depth - 1] = ((CoreNodeObject) node).element;

        return elements;
    }

    public enum State {
        UNCHANGED,
        CHANGED,
        NEW
    }
}
