package restudio.reglass.client.runtime;

import java.util.Locale;

public enum HoverCurve {
    CLASSIC("Smooth (Classic)", Kind.CLASSIC, Family.LINEAR, Mode.IN, 0f),
    IOS_LIKE("iOS Like", Kind.SPRING, Family.LINEAR, Mode.IN, -1f),
    IOS_SNAPPY("iOS Snappy", Kind.SPRING, Family.LINEAR, Mode.IN, 0.15f),
    IOS_BOUNCY("iOS Bouncy", Kind.SPRING, Family.LINEAR, Mode.IN, 0.40f),
    INSTANT("Instant", Kind.INSTANT, Family.LINEAR, Mode.IN, 0f),
    LINEAR("Linear", Kind.EASING, Family.LINEAR, Mode.IN, 0f),

    EASE_IN_SINE("Ease In Sine", Kind.EASING, Family.SINE, Mode.IN, 0f),
    EASE_OUT_SINE("Ease Out Sine", Kind.EASING, Family.SINE, Mode.OUT, 0f),
    EASE_IN_OUT_SINE("Ease In/Out Sine", Kind.EASING, Family.SINE, Mode.IN_OUT, 0f),

    EASE_IN_QUAD("Ease In Quad", Kind.EASING, Family.QUAD, Mode.IN, 0f),
    EASE_OUT_QUAD("Ease Out Quad", Kind.EASING, Family.QUAD, Mode.OUT, 0f),
    EASE_IN_OUT_QUAD("Ease In/Out Quad", Kind.EASING, Family.QUAD, Mode.IN_OUT, 0f),

    EASE_IN_CUBIC("Ease In Cubic", Kind.EASING, Family.CUBIC, Mode.IN, 0f),
    EASE_OUT_CUBIC("Ease Out Cubic", Kind.EASING, Family.CUBIC, Mode.OUT, 0f),
    EASE_IN_OUT_CUBIC("Ease In/Out Cubic", Kind.EASING, Family.CUBIC, Mode.IN_OUT, 0f),

    EASE_IN_QUART("Ease In Quart", Kind.EASING, Family.QUART, Mode.IN, 0f),
    EASE_OUT_QUART("Ease Out Quart", Kind.EASING, Family.QUART, Mode.OUT, 0f),
    EASE_IN_OUT_QUART("Ease In/Out Quart", Kind.EASING, Family.QUART, Mode.IN_OUT, 0f),

    EASE_IN_QUINT("Ease In Quint", Kind.EASING, Family.QUINT, Mode.IN, 0f),
    EASE_OUT_QUINT("Ease Out Quint", Kind.EASING, Family.QUINT, Mode.OUT, 0f),
    EASE_IN_OUT_QUINT("Ease In/Out Quint", Kind.EASING, Family.QUINT, Mode.IN_OUT, 0f),

    EASE_IN_EXPO("Ease In Exponential", Kind.EASING, Family.EXPO, Mode.IN, 0f),
    EASE_OUT_EXPO("Ease Out Exponential", Kind.EASING, Family.EXPO, Mode.OUT, 0f),
    EASE_IN_OUT_EXPO("Ease In/Out Exponential", Kind.EASING, Family.EXPO, Mode.IN_OUT, 0f),

    EASE_IN_CIRC("Ease In Circular", Kind.EASING, Family.CIRC, Mode.IN, 0f),
    EASE_OUT_CIRC("Ease Out Circular", Kind.EASING, Family.CIRC, Mode.OUT, 0f),
    EASE_IN_OUT_CIRC("Ease In/Out Circular", Kind.EASING, Family.CIRC, Mode.IN_OUT, 0f),

    EASE_IN_BACK("Ease In Back", Kind.EASING, Family.BACK, Mode.IN, 0f),
    EASE_OUT_BACK("Ease Out Back", Kind.EASING, Family.BACK, Mode.OUT, 0f),
    EASE_IN_OUT_BACK("Ease In/Out Back", Kind.EASING, Family.BACK, Mode.IN_OUT, 0f);

    public enum Kind {
        CLASSIC,
        INSTANT,
        EASING,
        SPRING
    }

    public enum Family {
        LINEAR,
        SINE,
        QUAD,
        CUBIC,
        QUART,
        QUINT,
        EXPO,
        CIRC,
        BACK
    }

    public enum Mode {
        IN,
        OUT,
        IN_OUT
    }

    private static final HoverCurve[] VALUES = values();
    private static final double BACK_C1 = 1.70158;
    private static final double BACK_C3 = BACK_C1 + 1.0;

    private final String displayName;
    private final Kind kind;
    private final Family family;
    private final Mode mode;
    private final float bounce;

    HoverCurve(String displayName, Kind kind, Family family, Mode mode, float bounce) {
        this.displayName = displayName;
        this.kind = kind;
        this.family = family;
        this.mode = mode;
        this.bounce = bounce;
    }

    public String displayName() {
        return displayName;
    }

    public Kind kind() {
        return kind;
    }

    /**
     * Returns preset bounce factor, or a negative value when the curve uses the configurable hoverBounce slider (IOS_LIKE).
     */
    public float springBounce() {
        return bounce;
    }

    public HoverCurve next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public HoverCurve previous() {
        return VALUES[(this.ordinal() - 1 + VALUES.length) % VALUES.length];
    }

    public static HoverCurve fromId(String id, HoverCurve fallback) {
        if (id == null || id.isBlank()) {
            return fallback;
        }
        String normalized = id.trim().toUpperCase(Locale.ROOT);
        for (HoverCurve curve : VALUES) {
            if (curve.name().equals(normalized) || curve.displayName.equalsIgnoreCase(id.trim())) {
                return curve;
            }
        }
        return fallback;
    }

    public float apply(float t) {
        if (Float.isNaN(t) || t <= 0f) {
            return 0f;
        }
        if (t >= 1f) {
            return 1f;
        }
        if (family == Family.LINEAR) {
            return t;
        }
        return switch (mode) {
            case IN -> (float) easeIn(family, t);
            case OUT -> (float) (1.0 - easeIn(family, 1.0 - t));
            case IN_OUT -> (float) (t < 0.5f
                    ? 0.5 * easeIn(family, t * 2.0)
                    : 1.0 - 0.5 * easeIn(family, (1.0 - t) * 2.0));
        };
    }

    private static double easeIn(Family family, double t) {
        if (t <= 0.0) return 0.0;
        if (t >= 1.0) return 1.0;
        return switch (family) {
            case LINEAR -> t;
            case SINE -> 1.0 - Math.cos((t * Math.PI) * 0.5);
            case QUAD -> t * t;
            case CUBIC -> t * t * t;
            case QUART -> t * t * t * t;
            case QUINT -> t * t * t * t * t;
            case EXPO -> Math.pow(2.0, 10.0 * t - 10.0);
            case CIRC -> 1.0 - Math.sqrt(Math.max(0.0, 1.0 - t * t));
            case BACK -> BACK_C3 * t * t * t - BACK_C1 * t * t;
        };
    }
}
