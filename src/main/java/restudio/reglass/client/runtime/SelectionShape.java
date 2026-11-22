package restudio.reglass.client.runtime;

import java.util.Locale;

/**
 * Selectable SDF corner geometry for slot and hotbar highlights.
 */
public enum SelectionShape {
    SQUIRCLE("Squircle", 4.2f),
    CIRCLE("Circle", 2.0f);

    private static final SelectionShape[] VALUES = values();

    private final String displayName;
    private final float cornerExponent;

    SelectionShape(String displayName, float cornerExponent) {
        this.displayName = displayName;
        this.cornerExponent = cornerExponent;
    }

    public String displayName() {
        return displayName;
    }

    public float cornerExponent() {
        return cornerExponent;
    }

    public SelectionShape next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public SelectionShape previous() {
        return VALUES[(this.ordinal() - 1 + VALUES.length) % VALUES.length];
    }

    public static SelectionShape fromId(String id, SelectionShape fallback) {
        if (id == null || id.isBlank()) {
            return fallback;
        }
        String normalized = id.trim().toUpperCase(Locale.ROOT);
        for (SelectionShape shape : VALUES) {
            if (shape.name().equals(normalized) || shape.displayName.equalsIgnoreCase(id.trim())) {
                return shape;
            }
        }
        return fallback;
    }
}
