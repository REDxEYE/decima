package com.shade.decima.model.archive;

import com.shade.util.NotNull;
import com.shade.util.Nullable;

import java.io.Closeable;
import java.nio.file.Path;

public interface Archive extends Closeable {
    @NotNull
    ArchiveManager getManager();

    @NotNull
    String getId();

    @NotNull
    String getName();

    @NotNull
    Path getPath();

    @Nullable
    ArchiveFile findFile(@NotNull String identifier);

    @NotNull
    ArchiveFile getFile(@NotNull String identifier);
}
