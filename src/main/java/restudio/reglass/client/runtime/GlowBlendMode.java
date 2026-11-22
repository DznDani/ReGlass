package restudio.reglass.client.runtime;

import java.util.Locale;

/**
 * Shader blend modes for Liquid Glass outer glow and inter-layer shadows.
 */
public enum GlowBlendMode {
    NORMAL("Normal", 0),
    MULTIPLY("Multiply", 1),
    SCREEN("Screen", 2),
    ADD("Add", 3),
    OVERLAY("Overlay", 4),
    SOFT_LIGHT("Soft Light", 5),
    LIGHT_TRANSMISSION("Light Transmission", 6);

    private static final GlowBlendMode[] VALUES = values();

    private final String displayName;
    private final int shaderId;

    GlowBlendMode(String displayName, int shaderId) {
        this.displayName = displayName;
        this.shaderId = shaderId;
    }

    public String displayName() {
        return displayName;
    }

    public int shaderId() {
        return shaderId;
    }

    public GlowBlendMode next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public GlowBlendMode previous() {
        return VALUES[(this.ordinal() - 1 + VALUES.length) % VALUES.length];
    }

    public static GlowBlendMode fromId(String id, GlowBlendMode fallback) {
        if (id == null || id.isBlank()) {
            return fallback;
        }
        String normalized = id.trim().toUpperCase(Locale.ROOT);
        for (GlowBlendMode mode : VALUES) {
            if (mode.name().equals(normalized) || mode.displayName.equalsIgnoreCase(id.trim())) {
                return mode;
            }
        }
        return fallback;
    }
}
