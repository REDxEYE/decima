package com.shade.decima.ui.data.viewer.texture.controls;

import com.shade.platform.ui.util.UIUtils;
import com.shade.util.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.Objects;

public class ImagePanel extends JComponent implements Scrollable {
    private static final String PLACEHOLDER_TEXT = "Unsupported texture format";

    private ImageProvider provider;
    private BufferedImage image;
    private float zoom;
    private float exposure;
    private int mip;
    private int slice;

    public ImagePanel(@Nullable ImageProvider provider) {
        this.provider = provider;
        this.zoom = 1.0f;
        this.exposure = 1f;
        this.mip = 0;
        this.slice = 0;

        final Handler handler = new Handler();
        addMouseListener(handler);
        addMouseMotionListener(handler);
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();

        if (image != null) {
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            g2.scale(zoom, zoom);
            g2.drawImage(image, 0, 0, null);
        } else {
            final Font font = getFont();
            final FontMetrics metrics = getFontMetrics(font);

            UIUtils.setRenderingHints(g2);

            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(getForeground());
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g2.drawString(PLACEHOLDER_TEXT, 4, (getHeight() - metrics.getHeight() + 1) / 2 + metrics.getAscent());
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        if (image != null) {
            return new Dimension(Math.round(image.getWidth() * zoom), Math.round(image.getHeight() * zoom));
        } else {
            final Font font = getFont();
            final FontMetrics metrics = getFontMetrics(font);
            final Rectangle bounds = font.getStringBounds(PLACEHOLDER_TEXT, metrics.getFontRenderContext()).getBounds();
            return new Dimension(bounds.width + 8, bounds.height + 4);
        }
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return getScrollableBlockIncrement(visibleRect, orientation, direction) / 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width;
        } else {
            return visibleRect.height;
        }
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Nullable
    public BufferedImage getImage() {
        return image;
    }

    @Nullable
    public ImageProvider getProvider() {
        return provider;
    }

    public void setProvider(@Nullable ImageProvider provider) {
        if (this.provider != provider) {
            final ImageProvider oldProvider = this.provider;

            this.provider = provider;
            this.image = null;
            this.zoom = 1.0f;
            this.mip = 0;
            this.slice = 0;

            update();

            firePropertyChange("provider", oldProvider, provider);
        }
    }

    public int getMip() {
        return mip;
    }

    public void setMip(int mip) {
        if (this.mip != mip) {
            final int oldMip = this.mip;

            this.mip = Objects.checkIndex(mip, provider.getMipCount());
            this.image = null;

            setSlice(Math.min(slice, provider.getSliceCount(mip) - 1));

            update();
            fit();

            firePropertyChange("mip", oldMip, mip);
        }
    }

    public int getSlice() {
        return slice;
    }

    public void setSlice(int slice) {
        if (this.slice != slice) {
            final int oldSlice = this.slice;

            this.slice = Objects.checkIndex(slice, provider.getSliceCount(mip));
            this.image = null;

            update();

            firePropertyChange("slice", oldSlice, slice);
        }
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        if (this.zoom != zoom && zoom > 0.0f) {
            final float oldScale = this.zoom;

            this.zoom = zoom;

            update();

            firePropertyChange("zoom", oldScale, zoom);
        }
    }

    public float getExposure() {
        return exposure;
    }

    public void setExposure(float exposure) {
        if (this.exposure != exposure) {
            final float oldExposure = this.exposure;

            this.exposure = exposure;
            this.image = null;

            update();
            firePropertyChange("exposure", oldExposure, exposure);
        }
    }

    public void fit() {
        if (provider == null) {
            return;
        }

        final Container viewport = SwingUtilities.getAncestorOfClass(JViewport.class, this);
        final float rs = (float) viewport.getWidth() / viewport.getHeight();
        final float ri = (float) image.getWidth() / image.getHeight();

        if (rs > ri) {
            setZoom((float) viewport.getHeight() / image.getHeight());
        } else {
            setZoom((float) viewport.getWidth() / image.getWidth());
        }
    }

    public boolean isHDR() {
        return image != null && image.getData().getTransferType() == DataBuffer.TYPE_FLOAT;
    }

    private void update() {
        if (provider == null) {
            return;
        }

        if (image == null) {
            image = provider.getImage(mip, slice);

            if (image.getData().getTransferType() == DataBuffer.TYPE_FLOAT) {
                applyExposure();
            }
        }

        revalidate();
        repaint();
    }

    private void applyExposure() {
        final float[] pixel = new float[3];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.getRaster().getPixel(x, y, pixel);

                pixel[0] = gamma(pixel[0], exposure, 1f);
                pixel[1] = gamma(pixel[1], exposure, 1f);
                pixel[2] = gamma(pixel[2], exposure, 1f);

                image.getRaster().setPixel(x, y, pixel);
            }
        }
    }

    private static float gamma(float value, float exposure, float gamma) {
        return (float) Math.pow(1.0f - Math.exp(-value * exposure), 1.0f / gamma);
    }

    private class Handler extends MouseAdapter {
        private Point origin;

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                origin = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            origin = null;
            setCursor(null);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (origin == null) {
                return;
            }

            final JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, ImagePanel.this);
            final Rectangle view = viewport.getViewRect();

            view.x += origin.x - e.getX();
            view.y += origin.y - e.getY();

            scrollRectToVisible(view);
        }
    }
}
