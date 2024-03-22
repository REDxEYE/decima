package com.shade.decima.model.app;

import com.shade.decima.model.base.GameType;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;
import com.shade.util.Nullable;

import java.nio.file.Path;
import java.util.UUID;
import java.util.prefs.Preferences;

public class ProjectContainer {
    private final UUID id;
    private String name;
    private GameType type;

    private Path executablePath;
    private Path packfilesPath;
    private Path compressorPath;

    private Path typeMetadataPath;
    private Path fileListingsPath;

    public ProjectContainer(
        @NotNull UUID id,
        @NotNull String name,
        @NotNull GameType type,
        @NotNull Path executablePath,
        @NotNull Path packfilesPath,
        @NotNull Path compressorPath,
        @NotNull Path typeMetadataPath,
        @Nullable Path fileListingsPath
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.executablePath = executablePath;
        this.packfilesPath = packfilesPath;
        this.compressorPath = compressorPath;
        this.typeMetadataPath = typeMetadataPath;
        this.fileListingsPath = fileListingsPath;
    }

    @Deprecated
    public ProjectContainer(@NotNull UUID id, @NotNull Preferences node) {
        this(
            id,
            IOUtils.getNotNull(node, "game_name"),
            IOUtils.getNotNull(node, "game_type", GameType::valueOf),
            IOUtils.getNotNull(node, "game_executable_path", Path::of),
            IOUtils.getNotNull(node, "game_archive_root_path", Path::of),
            IOUtils.getNotNull(node, "game_compressor_path", Path::of),
            IOUtils.getNotNull(node, "game_rtti_meta_path", Path::of),
            IOUtils.getNullable(node, "game_file_listings_path", Path::of)
        );
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public GameType getType() {
        return type;
    }

    public void setType(@NotNull GameType type) {
        this.type = type;
    }

    @NotNull
    public Path getExecutablePath() {
        return executablePath;
    }

    public void setExecutablePath(@NotNull Path executablePath) {
        this.executablePath = executablePath;
    }

    @NotNull
    public Path getCompressorPath() {
        return compressorPath;
    }

    public void setCompressorPath(@NotNull Path compressorPath) {
        this.compressorPath = compressorPath;
    }

    @NotNull
    public Path getPackfilesPath() {
        return packfilesPath;
    }

    public void setPackfilesPath(@NotNull Path packfilesPath) {
        this.packfilesPath = packfilesPath;
    }

    @NotNull
    public Path getTypeMetadataPath() {
        return typeMetadataPath;
    }

    public void setTypeMetadataPath(@NotNull Path typeMetadataPath) {
        this.typeMetadataPath = typeMetadataPath;
    }

    @Nullable
    public Path getFileListingsPath() {
        return fileListingsPath;
    }

    public void setFileListingsPath(@Nullable Path fileListingsPath) {
        this.fileListingsPath = fileListingsPath;
    }

    @Override
    public String toString() {
        return getName();
    }
}
