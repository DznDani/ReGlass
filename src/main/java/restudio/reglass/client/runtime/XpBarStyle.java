package restudio.reglass.client.runtime;

import java.util.Locale;

/**
 * Display style for the Liquid Glass experience bar.
 */
public enum XpBarStyle {
    SMOOTH("Smooth"),
    SEGMENTED("Segmented");

    private static final XpBarStyle[] VALUES = values();

    private final String displayName;

    XpBarStyle(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public XpBarStyle next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public XpBarStyle previous() {
        return VALUES[(this.ordinal() - 1 + VALUES.length) % VALUES.length];
    }

    public static XpBarStyle fromId(String id, XpBarStyle fallback) {
        if (id == null || id.isBlank()) {
            return fallback;
        }
        String normalized = id.trim().toUpperCase(Locale.ROOT);
        for (XpBarStyle style : VALUES) {
            if (style.name().equals(normalized) || style.displayName.equalsIgnoreCase(id.trim())) {
                return style;
            }
        }
        return fallback;
    }
}
