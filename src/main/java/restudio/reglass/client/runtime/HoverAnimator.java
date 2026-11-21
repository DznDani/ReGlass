package restudio.reglass.client.runtime;

import restudio.reglass.client.api.ReGlassConfig;

public final class HoverAnimator {
    private static final float MIN_VALUE = -0.35f;
    private static final float MAX_VALUE = 1.45f;

    private float value;
    private float velocity;
    private float from;
    private float to;
    private float elapsed;
    private HoverCurve activeCurve = HoverCurve.CLASSIC;
    private float activeDuration = 0.48f;
    private boolean initialized;

    public float step(float rawTarget, double dtSeconds, ReGlassConfig cfg) {
        float target = Float.isNaN(rawTarget) ? 0f : Math.max(0f, Math.min(1f, rawTarget));
        double dt = Double.isNaN(dtSeconds) || dtSeconds < 0.0 ? 0.0 : dtSeconds;

        if (!initialized) {
            initialized = true;
            value = target;
            from = target;
            to = target;
            velocity = 0f;
            elapsed = 0f;
            return value;
        }

        if (Math.abs(target - to) > 1e-4f) {
            boolean rising = target >= value;
            HoverCurve nextCurve = (rising ? cfg.hoverInCurve : cfg.hoverOutCurve);
            if (nextCurve == null) {
                nextCurve = HoverCurve.CLASSIC;
            }
            float nextDuration = rising ? cfg.hoverInDurationSec : cfg.hoverOutDurationSec;
            if (Float.isNaN(nextDuration) || nextDuration < 0f) {
                nextDuration = 0f;
            }
            activeCurve = nextCurve;
            activeDuration = nextDuration;
            from = value;
            to = target;
            elapsed = 0f;
        } else {
            boolean rising = to >= from;
            HoverCurve configuredCurve = (rising ? cfg.hoverInCurve : cfg.hoverOutCurve);
            if (configuredCurve != null) {
                activeCurve = configuredCurve;
            }
            float configuredDuration = rising ? cfg.hoverInDurationSec : cfg.hoverOutDurationSec;
            if (!Float.isNaN(configuredDuration) && configuredDuration >= 0f) {
                activeDuration = configuredDuration;
            }
        }

        if (activeCurve == HoverCurve.INSTANT || activeDuration <= 1e-4f) {
            value = to;
            velocity = 0f;
            return value;
        }

        if (dt <= 0.0) {
            return clampValue(value);
        }

        switch (activeCurve.kind()) {
            case INSTANT -> {
                value = to;
                velocity = 0f;
            }
            case CLASSIC -> {
                float tau = Math.max(1e-4f, activeDuration * 0.25f);
                float a = (float) (1.0 - Math.exp(-dt / tau));
                value = value + (to - value) * a;
                velocity = 0f;
                if (Math.abs(value - to) < 1e-4f) {
                    value = to;
                }
            }
            case EASING -> {
                elapsed += (float) dt;
                float t = Math.min(1f, elapsed / activeDuration);
                float eased = activeCurve.apply(t);
                value = from + (to - from) * eased;
                velocity = 0f;
                if (t >= 1f || Math.abs(value - to) < 1e-4f && t > 0.99f) {
                    value = to;
                }
            }
            case SPRING -> {
                float rawBounce = activeCurve.springBounce() < 0f ? cfg.hoverBounce : activeCurve.springBounce();
                if (Float.isNaN(rawBounce)) {
                    rawBounce = 0.25f;
                }
                float bounce = Math.max(0f, Math.min(0.85f, rawBounce));
                stepSpring(dt, activeDuration, bounce);
            }
        }

        return clampValue(value);
    }

    private void stepSpring(double dt, float duration, float bounce) {
        double omega = (2.0 * Math.PI) / Math.max(0.02, duration);
        double zeta = Math.max(0.12, 1.0 - bounce);
        double y0 = value - to;
        double v0 = velocity;

        if (Math.abs(y0) < 1e-4 && Math.abs(v0) < 1e-3) {
            value = to;
            velocity = 0f;
            return;
        }

        double expTerm = Math.exp(-zeta * omega * dt);
        double y;
        double v;
        if (zeta < 1.0 - 1e-4) {
            double omegaD = omega * Math.sqrt(1.0 - zeta * zeta);
            double cos = Math.cos(omegaD * dt);
            double sin = Math.sin(omegaD * dt);
            double b = (v0 + zeta * omega * y0) / omegaD;
            y = expTerm * (y0 * cos + b * sin);
            v = expTerm * (v0 * cos - ((zeta * omega * v0 + omega * omega * y0) / omegaD) * sin);
        } else {
            double b = v0 + omega * y0;
            y = expTerm * (y0 + b * dt);
            v = expTerm * (v0 - omega * b * dt);
        }

        value = (float) (to + y);
        velocity = (float) v;
        if (Math.abs(y) < 5e-4 && Math.abs(v) < 5e-3) {
            value = to;
            velocity = 0f;
        }
    }

    private static float clampValue(float v) {
        if (Float.isNaN(v)) return 0f;
        return Math.max(MIN_VALUE, Math.min(MAX_VALUE, v));
    }
}
