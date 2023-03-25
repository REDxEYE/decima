package com.shade.decima.ui.data.viewer.wwise;

import com.shade.decima.model.app.Project;
import com.shade.decima.model.rtti.objects.RTTIObject;
import com.shade.decima.model.rtti.types.java.HwDataSource;
import com.shade.decima.ui.Application;
import com.shade.decima.ui.controls.audio.AudioPlayer;
import com.shade.decima.ui.data.viewer.wwise.WwiseBank.Chunk.Type;
import com.shade.decima.ui.data.viewer.wwise.data.AkBankSourceData;
import com.shade.decima.ui.data.viewer.wwise.data.AkHircNode;
import com.shade.decima.ui.data.viewer.wwise.data.AkMusicTrack;
import com.shade.decima.ui.data.viewer.wwise.data.AkSound;
import com.shade.decima.ui.settings.WwiseSettingsPage;
import com.shade.platform.model.runtime.ProgressMonitor;
import com.shade.platform.model.util.IOUtils;
import com.shade.platform.ui.controls.ColoredListCellRenderer;
import com.shade.platform.ui.controls.TextAttributes;
import com.shade.platform.ui.dialogs.ProgressDialog;
import com.shade.platform.ui.util.UIUtils;
import com.shade.util.NotNull;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class WwiseViewerPanel extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(WwiseViewerPanel.class);

    private final PlaylistList list = new PlaylistList();
    private final AudioPlayer player;

    private Project project;
    private Playlist playlist;

    public WwiseViewerPanel() {
        player = new AudioPlayer() {
            @Override
            protected boolean previousTrackRequested() {
                final int index = list.getSelectedIndex();

                if (index >= 0) {
                    final int wrapped = IOUtils.wrapAround(index - 1, list.getModel().getSize());
                    list.setSelectedIndex(wrapped);
                    list.scrollRectToVisible(list.getCellBounds(wrapped, wrapped));
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected boolean nextTrackRequested() {
                final int index = list.getSelectedIndex();

                if (index >= 0) {
                    final int wrapped = IOUtils.wrapAround(index + 1, list.getModel().getSize());
                    list.setSelectedIndex(wrapped);
                    list.scrollRectToVisible(list.getCellBounds(wrapped, wrapped));
                    return true;
                } else {
                    return false;
                }
            }
        };
        player.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.shadow")));

        final JScrollPane playlistPane = new JScrollPane(list);
        playlistPane.setBorder(null);

        setLayout(new MigLayout("ins 0,gap 0", "[grow,fill]", "[grow,fill][]"));
        add(playlistPane, "wrap");
        add(player);
        setPreferredSize(new Dimension(250, 0));
    }

    public void setInput(@NotNull Project project, @NotNull RTTIObject object) {
        this.project = project;
        this.playlist = switch (object.type().getTypeName()) {
            case "WwiseBankResource" -> new BankPlaylist(object);
            case "WwiseWemResource" -> new WemPlaylist(object);
            case "WwiseWemLocalizedResource" -> new WemLocalizedPlaylist(object);
            default -> throw new IllegalArgumentException("Unsupported type: " + object.type().getTypeName());
        };

        this.list.refresh();
        this.player.setClip(null);
    }

    public void close() {
        player.close();
    }

    private void setTrack(int index) {
        final var pref = WwiseSettingsPage.getPreferences();
        final var codebooks = pref.get(WwiseSettingsPage.PROP_WW2OGG_CODEBOOKS_PATH, "");
        final var ww2ogg = pref.get(WwiseSettingsPage.PROP_WW2OGG_PATH, "");
        final var revorb = pref.get(WwiseSettingsPage.PROP_REVORB_PATH, "");
        final var ffmpeg = pref.get(WwiseSettingsPage.PROP_FFMPEG_PATH, "");

        if (codebooks.isEmpty() || ww2ogg.isEmpty() || revorb.isEmpty() || ffmpeg.isEmpty()) {
            JOptionPane.showMessageDialog(
                Application.getFrame(),
                "<html>One or more native tools required for audio playback are missing.<br><br>You can specify them in <kbd>File</kbd> &rArr; <kbd>Settings</kbd> &rArr; <kbd>Wwise Audio</kbd></html>",
                "Can't play audio",
                JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        ProgressDialog.showProgressDialog(Application.getFrame(), "Prepare to play audio", monitor -> {
            try (ProgressMonitor.Task task = monitor.begin("Prepare to play audio", 4)) {
                final byte[] data;

                try (ProgressMonitor.Task ignored = task.split(1).begin("Extract track data", 1)) {
                    data = playlist.getData(index);
                }

                final var wemPath = Files.createTempFile(null, ".wem");
                final var oggPath = Path.of(IOUtils.getBasename(wemPath.toString()) + ".ogg");
                final var wavPath = Path.of(IOUtils.getBasename(wemPath.toString()) + ".wav");

                try {
                    Files.write(wemPath, data);

                    try (ProgressMonitor.Task ignored = task.split(1).begin("Invoke 'ww2ogg'", 1)) {
                        IOUtils.exec(ww2ogg, wemPath, "-o", oggPath, "--pcb", codebooks);
                    }

                    try (ProgressMonitor.Task ignored = task.split(1).begin("Invoke 'revorb'", 1)) {
                        IOUtils.exec(revorb, oggPath);
                    }

                    try (ProgressMonitor.Task ignored = task.split(1).begin("Invoke 'ffmpeg'", 1)) {
                        IOUtils.exec(ffmpeg, "-acodec", "libvorbis", "-i", oggPath, "-ac", "2", wavPath, "-y");
                    }

                    try (AudioInputStream is = AudioSystem.getAudioInputStream(wavPath.toFile())) {
                        final var format = is.getFormat();
                        final var info = new DataLine.Info(Clip.class, format);
                        final var clip = (Clip) AudioSystem.getLine(info);

                        clip.open(is);
                        player.setClip(clip);
                    }
                } finally {
                    Files.deleteIfExists(wemPath);
                    Files.deleteIfExists(oggPath);
                    Files.deleteIfExists(wavPath);
                }
            } catch (Exception e) {
                UIUtils.showErrorDialog(Application.getFrame(), e, "Error playing audio");
            }

            return null;
        });
    }

    private class PlaylistList extends JList<String> {
        public PlaylistList() {
            super(new PlaylistModel());
            setCellRenderer(new ColoredListCellRenderer<>() {
                @Override
                protected void customizeCellRenderer(@NotNull JList<? extends String> list, @NotNull String value, int index, boolean selected, boolean focused) {
                    append("[%d] ".formatted(index), TextAttributes.GRAYED_ATTRIBUTES);
                    append(value, TextAttributes.REGULAR_ATTRIBUTES);
                }
            });
            addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && getSelectedIndex() >= 0) {
                    setTrack(getSelectedIndex());
                }
            });
        }

        @Override
        public PlaylistModel getModel() {
            return (PlaylistModel) super.getModel();
        }

        public void refresh() {
            clearSelection();
            getModel().refresh();
        }
    }

    private class PlaylistModel extends AbstractListModel<String> {
        @Override
        public int getSize() {
            return playlist.size();
        }

        @Override
        public String getElementAt(int index) {
            return playlist.getName(index);
        }

        public void refresh() {
            fireContentsChanged(this, -1, -1);
        }
    }

    private interface Playlist {
        @NotNull
        String getName(int index);

        @NotNull
        byte[] getData(int index) throws IOException;

        int size();
    }

    private class BankPlaylist implements Playlist {
        private final RTTIObject object;
        private final WwiseBank bank;
        private final AkHircNode[] nodes;

        public BankPlaylist(@NotNull RTTIObject object) {
            final var size = object.i32("BankSize");
            final var data = object.<byte[]>get("BankData");
            final var buffer = ByteBuffer.wrap(data, 0, size).order(ByteOrder.LITTLE_ENDIAN);

            this.object = object;
            this.bank = WwiseBank.read(buffer);

            if (bank.has(Type.HIRC)) {
                this.nodes = Arrays.stream(bank.get(Type.HIRC).nodes())
                    .filter(BankPlaylist::isPlayable)
                    .toArray(AkHircNode[]::new);
            } else {
                this.nodes = new AkHircNode[0];
            }
        }

        @NotNull
        @Override
        public String getName(int index) {
            return "%d.wem".formatted(Integer.toUnsignedLong(nodes[index].id()));
        }

        @NotNull
        @Override
        public byte[] getData(int index) throws IOException {
            final AkHircNode node = nodes[index];
            final AkBankSourceData source;

            if (node instanceof AkSound sound) {
                source = sound.source();
            } else if (node instanceof AkMusicTrack track) {
                if (track.sources().length > 1) {
                    log.warn("Track {} has {} sources, using the first one", track.id(), track.sources().length);
                }

                source = track.sources()[0];
            } else {
                throw new IllegalStateException();
            }

            return switch (source.type()) {
                case STREAMING, PREFETCH_STREAMING -> {
                    final var dataSourceIndex = IOUtils.indexOf(object.get("WemIDs"), source.info().sourceId());
                    final var dataSource = object.objs("DataSources")[dataSourceIndex].<HwDataSource>cast();
                    yield dataSource.getData(project.getPackfileManager());
                }
                case DATA -> {
                    final var header = bank.get(Type.DIDX).get(source.info().sourceId());
                    yield Arrays.copyOfRange(bank.get(Type.DATA).data(), header.offset(), header.offset() + header.length());
                }
            };
        }

        @Override
        public int size() {
            return nodes.length;
        }

        private static boolean isPlayable(@NotNull AkHircNode node) {
            final AkBankSourceData source;

            if (node instanceof AkMusicTrack track && track.sources().length > 0) {
                source = track.sources()[0];
            } else if (node instanceof AkSound sound) {
                source = sound.source();
            } else {
                return false;
            }

            return source.info().inMemoryMediaSize() != 0;
        }
    }

    private class WemPlaylist implements Playlist {
        private final RTTIObject object;

        public WemPlaylist(@NotNull RTTIObject object) {
            this.object = object;
        }

        @NotNull
        @Override
        public String getName(int index) {
            final var dataSource = object.obj("DataSource").<HwDataSource>cast();
            return IOUtils.getFilename(dataSource.getLocation());
        }

        @NotNull
        @Override
        public byte[] getData(int index) throws IOException {
            final var dataSource = object.obj("DataSource").<HwDataSource>cast();
            return Arrays.copyOfRange(dataSource.getData(project.getPackfileManager()), dataSource.getOffset(), dataSource.getOffset() + dataSource.getLength());
        }

        @Override
        public int size() {
            return 1;
        }
    }

    private class WemLocalizedPlaylist implements Playlist {
        private final RTTIObject object;

        public WemLocalizedPlaylist(@NotNull RTTIObject object) {
            this.object = object;
        }

        @NotNull
        @Override
        public String getName(int index) {
            final var dataSource = object.objs("Entries")[index].obj("DataSource").<HwDataSource>cast();
            return IOUtils.getFilename(dataSource.getLocation());
        }

        @NotNull
        @Override
        public byte[] getData(int index) throws IOException {
            final var dataSource = object.objs("Entries")[index].obj("DataSource").<HwDataSource>cast();
            return Arrays.copyOfRange(dataSource.getData(project.getPackfileManager()), dataSource.getOffset(), dataSource.getOffset() + dataSource.getLength());
        }

        @Override
        public int size() {
            return object.objs("Entries").length;
        }
    }
}