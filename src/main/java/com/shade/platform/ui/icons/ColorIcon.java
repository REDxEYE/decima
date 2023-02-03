package com.shade.platform.ui.icons;

import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.shade.util.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class ColorIcon extends FlatAbstractIcon {
    private static final Color COLOR_ODD_BACKGROUND = UIManager.getColor("ColorIcon.oddBackground");
    private static final Color COLOR_EVEN_BACKGROUND = UIManager.getColor("ColorIcon.evenBackground");

    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;

    private final Supplier<Color> supplier;

    public ColorIcon(@NotNull Supplier<Color> supplier) {
        super(WIDTH, HEIGHT, null);
        this.supplier = supplier;
    }

    @Override
    public void paintIcon(Component c, Graphics2D g) {
        final Color color = supplier.get();

        g.setColor(UIManager.getColor("Actions.Grey"));
        g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

        g.setColor(Color.WHITE);
        g.drawRect(1, 1, WIDTH - 3, HEIGHT - 3);

        for (int x0 = 0; x0 < 12; x0 += 3) {
            for (int y0 = 0; y0 < 12; y0 += 3) {
                final int i = (x0 + y0 % 6) % 6;
                g.setColor(getColor(color, i > 0));
                g.fillRect(x0 + 2, y0 + 2, 3, 3);
            }
        }
    }

    @NotNull
    public static Color getColor(@NotNull Color color, boolean odd) {
        return switch (color.getTransparency()) {
            case Transparency.OPAQUE -> color;
            case Transparency.TRANSLUCENT -> lerp(odd ? COLOR_ODD_BACKGROUND : COLOR_EVEN_BACKGROUND, color, color.getAlpha() / 255.0f);
            default -> odd ? COLOR_ODD_BACKGROUND : COLOR_EVEN_BACKGROUND;
        };
    }

    @NotNull
    private static Color lerp(@NotNull Color a, @NotNull Color b, float t) {
        return new Color(
            (int) (a.getRed() + t * (b.getRed() - a.getRed())),
            (int) (a.getGreen() + t * (b.getGreen() - a.getGreen())),
            (int) (a.getBlue() + t * (b.getBlue() - a.getBlue()))
        );
    }
}
