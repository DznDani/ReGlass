//#if MC >= 26.3
#version 450
//#else
#version 150
//#endif
uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;
uniform sampler2D Sampler4;
uniform sampler2D Sampler5;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform CustomUniforms {
    float Time;
    vec4 Mouse;
    float ScreenWantsBlur;
    vec3 RIM_LIGHT_VEC;
    vec4 RIM_LIGHT_COLOR;
    float EPS_PIX;
    float DebugStep;
    float Pixelated;
    float PixelGridSize;
    float HoverScalePx;
    float FocusScalePx;
    float FocusBorderWidthPx;
    float FocusBorderIntensity;
    float FocusBorderSpeed;
};

#define MAX_WIDGETS 64
layout(std140) uniform WidgetInfo {
    float Count;
    vec4 LayerRanges[16];
    vec4 LayerBounds[16];
    vec4 Rects[MAX_WIDGETS];
    vec4 Rads[MAX_WIDGETS];
    vec4 Tints[MAX_WIDGETS];
    vec4 Optics0[MAX_WIDGETS];
    vec4 Optics1[MAX_WIDGETS];
    vec4 Optics2[MAX_WIDGETS];
    vec4 Smoothings[MAX_WIDGETS];
    vec4 ScissorRects[MAX_WIDGETS];
    vec4 Shadow0[MAX_WIDGETS];
    vec4 ShadowColor[MAX_WIDGETS];
    vec4 Extra0[MAX_WIDGETS];
    vec4 ShapeBounds[MAX_WIDGETS];
    vec4 ShadowBounds[MAX_WIDGETS];
};

layout(std140) uniform BgConfig {
    float ShadowExpand;
    float ShadowFactor;
    vec2 ShadowOffset;
};

layout(std140) uniform StyleInfo {
    vec4 Glow0[MAX_WIDGETS];
    vec4 Glow1[MAX_WIDGETS];
    vec4 GlowColor[MAX_WIDGETS];
    vec4 Seg0[MAX_WIDGETS];
};

//#if MC >= 26.3
layout(location = 0) out vec4 fragColor;
//#else
out vec4 fragColor;
//#endif

struct SDFResult { float dist; vec2 normal; float aspect; int index; };

bool outsideBounds(vec2 coord, vec4 bounds) {
    return coord.x < bounds.x || coord.y < bounds.y || coord.x > bounds.z || coord.y > bounds.w;
}

vec2 screenToUV(vec2 screen, vec2 res) {
    return (screen.xy - 0.5 * res.xy) / res.y;
}

vec3 sdgShape(in vec2 p, in vec2 b, vec4 ra, float exponent) {
    ra.xy = (p.x > 0.0) ? ra.xy : ra.zw;
    float r = min((p.y > 0.0) ? ra.x : ra.y, min(b.x, b.y));
    vec2 w = abs(p) - (b - r);
    vec2 s = vec2(p.x < 0.0 ? -1.0 : 1.0, p.y < 0.0 ? -1.0 : 1.0);
    float g = max(w.x, w.y);
    vec2 q = max(w, 0.0);
    if (exponent <= 2.05 || g <= 0.0 || r <= 1e-6) {
        float l = length(q);
        float dist = (g > 0.0) ? l - r : g - r;
        vec2 n = (g > 0.0) ? (q / max(l, 1e-6)) : ((w.x > w.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0));
        return vec3(dist, s * n);
    }
    vec2 u = q / r;
    float m = max(u.x, u.y);
    if (m <= 1e-4) {
        vec2 n = (w.x > w.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
        return vec3(-r, s * n);
    }
    vec2 t = u / m;
    float tp = pow(t.x, exponent) + pow(t.y, exponent);
    float lp = m * pow(tp, 1.0 / exponent);
    vec2 grad = vec2(
        (t.x > 1e-6) ? pow(t.x, exponent - 1.0) : 0.0,
        (t.y > 1e-6) ? pow(t.y, exponent - 1.0) : 0.0
    ) * pow(tp, (1.0 - exponent) / exponent);
    float gl = length(grad);
    vec2 n = grad / max(gl, 1e-6);
    float corr = mix(1.0, max(gl, 0.5), smoothstep(0.0, 0.5, lp));
    float dist = (lp - 1.0) * r / corr;
    return vec3(dist, s * n);
}

vec3 sdgShapeSeg(in vec2 p, in vec2 b, vec4 ra, float exponent, vec4 seg, float invH) {
    if (seg.x < 1.5 || seg.z <= 1e-4) return sdgShape(p, b, ra, exponent);
    float nSeg = max(floor(seg.x + 0.5), 2.0);
    float barW = seg.z * invH;
    float gap = min(max(seg.y, 0.0) * invH, (barW / nSeg) * 0.6);
    float segW = max((barW - (nSeg - 1.0) * gap) / nSeg, 1e-5);
    float stride = segW + gap;
    float filledW = max(2.0 * b.x, 1e-5);
    float maxCi = clamp(floor(max(filledW - 1e-5, 0.0) / stride), 0.0, nSeg - 1.0);
    float xl = p.x + b.x;
    float i = clamp(floor(xl / stride), 0.0, maxCi);
    float side = (fract(xl / stride) > 0.5) ? 1.0 : -1.0;
    vec3 best = vec3(1e3, 0.0, 1.0);
    for (int k = 0; k < 2; k++) {
        float ci = clamp(i + (k == 0 ? 0.0 : side), 0.0, maxCi);
        float left = ci * stride;
        float right = min(left + segW, filledW);
        if (right - left <= 1e-6) continue;
        float segCenter = 0.5 * (left + right) - b.x;
        float segHalfW = 0.5 * (right - left);
        vec3 d = sdgShape(p - vec2(segCenter, 0.0), vec2(segHalfW, b.y), ra, exponent);
        if (d.x < best.x) best = d;
    }
    return best;
}

vec3 blendLayer(vec3 bg, vec3 src, float a, int mode) {
    a = clamp(a, 0.0, 1.0);
    if (mode == 1) return bg * mix(vec3(1.0), src, a);
    if (mode == 2) return 1.0 - (1.0 - bg) * (1.0 - src * a);
    if (mode == 3) return clamp(bg + src * a, 0.0, 1.0);
    if (mode == 4) {
        vec3 o = mix(2.0 * bg * src, 1.0 - 2.0 * (1.0 - bg) * (1.0 - src), step(vec3(0.5), bg));
        return mix(bg, o, a);
    }
    if (mode == 5) {
        vec3 s = (1.0 - 2.0 * src) * bg * bg + 2.0 * src * bg;
        return mix(bg, s, a);
    }
    return mix(bg, src, a);
}

vec3 lightTransmission(vec3 bg, vec3 src, float intensity, float wide, float core, float coreMix) {
    float kWide = clamp(intensity * wide * 0.85, 0.0, 1.0);
    float kCore = clamp(intensity * core * coreMix * 1.2, 0.0, 1.0);
    vec3 mul = bg * mix(vec3(1.0), clamp(src * 1.15 + vec3(0.08), 0.0, 1.0), kWide);
    return clamp(1.0 - (1.0 - mul) * (1.0 - src * kCore * 0.75), 0.0, 1.0);
}

SDFResult opSmoothUnion(in SDFResult a, in SDFResult b, in float k) {
    if (k == 0.0) return (a.dist < b.dist) ? a : b;
    float h = clamp(0.5 + 0.5 * (a.dist - b.dist) / k, 0.0, 1.0);
    float d = mix(a.dist, b.dist, h) - k * h * (1.0 - h);
    vec2 n = normalize(mix(a.normal, b.normal, h));
    float aspect = mix(a.aspect, b.aspect, h);
    int index = (a.dist < b.dist) ? a.index : b.index;
    return SDFResult(d, n, aspect, index);
}

SDFResult opHardUnion(SDFResult a, SDFResult b) { return (a.dist < b.dist) ? a : b; }

SDFResult opHardSubtract(SDFResult a, SDFResult b) {
    float d = max(a.dist, -b.dist);
    if (d == a.dist) return a;
    return SDFResult(d, -b.normal, a.aspect, a.index);
}

vec4 sampleBlur(int idx, vec2 uv) {
    if (idx <= 0) return texture(Sampler1, uv);
    if (idx == 1) return texture(Sampler2, uv);
    if (idx == 2) return texture(Sampler3, uv);
    if (idx == 3) return texture(Sampler4, uv);
    return texture(Sampler5, uv);
}

SDFResult fieldWidgets(vec2 p, vec2 inSize, vec2 fragCoord, float layer) {
    int n = int(Count + 0.5);
    if (n == 0) return SDFResult(1e6, vec2(0.0), 1.0, -1);
    int layerIndex = int(layer + 0.5);
    if (layerIndex < 0 || layerIndex >= 16) return SDFResult(1e6, vec2(0.0), 1.0, -1);
    vec4 layerRange = LayerRanges[layerIndex];
    int start = int(layerRange.x + 0.5);
    int count = int(layerRange.y + 0.5);
    if (count <= 0) return SDFResult(1e6, vec2(0.0), 1.0, -1);

    SDFResult pos = SDFResult(1e6, vec2(0.0), 1.0, -1);
    bool hasPos = false;

    for (int i = 0; i < MAX_WIDGETS; i++) {
        if (i >= count) break;
        int wi = start + i;
        if (wi >= n) break;
        if (Smoothings[wi].x < 0.0) continue;
        if (DebugStep > 1.5 && outsideBounds(fragCoord, ShapeBounds[wi])) continue;

        vec4 sc = ScissorRects[wi];
        if (fragCoord.x < sc.x || fragCoord.y < sc.y || fragCoord.x > sc.z || fragCoord.y > sc.w) continue;

        vec4 rc = Rects[wi];
        vec4 rr = Rads[wi];
        vec2 cPx = vec2(rc.x + 0.5 * rc.z, rc.y + 0.5 * rc.w);
        vec2 c = screenToUV(cPx, inSize);
        vec2 b = 0.5 * vec2(rc.z, rc.w) / inSize.y;
        vec4 rad = rr / inSize.y;

        vec3 g = sdgShapeSeg(p - c, b, rad, Smoothings[wi].y, Seg0[wi], 1.0 / inSize.y);

        vec4 extra = Extra0[wi];
        float scaleOff = (HoverScalePx * 0.18 * extra.y + min(FocusScalePx, 4.0) * 0.4 * extra.z) / inSize.y;
        float dist = g.x - scaleOff;

        float aspect = min(rc.z, rc.w) / max(rc.z, rc.w);
        SDFResult s = SDFResult(dist, g.yz, aspect, wi);

        if (!hasPos) { pos = s; hasPos = true; }
        else { pos = opSmoothUnion(pos, s, Smoothings[wi].x); }
    }

    SDFResult f = pos;

    for (int i = 0; i < MAX_WIDGETS; i++) {
        if (i >= count) break;
        int wi = start + i;
        if (wi >= n) break;
        if (Smoothings[wi].x >= 0.0) continue;

        vec4 sc = ScissorRects[wi];
        if (fragCoord.x < sc.x || fragCoord.y < sc.y || fragCoord.x > sc.z || fragCoord.y > sc.w) continue;

        vec4 rc = Rects[wi];
        vec4 rr = Rads[wi];
        vec2 cPx = vec2(rc.x + 0.5 * rc.z, rc.y + 0.5 * rc.w);
        vec2 c = screenToUV(cPx, inSize);
        vec2 b = 0.5 * vec2(rc.z, rc.w) / inSize.y;
        vec4 rad = rr / inSize.y;

        vec3 g = sdgShapeSeg(p - c, b, rad, Smoothings[wi].y, Seg0[wi], 1.0 / inSize.y);

        vec4 extra = Extra0[wi];
        float scaleOff = (HoverScalePx * extra.y + FocusScalePx * extra.z) / inSize.y;
        float dist = g.x - scaleOff;

        float aspect = min(rc.z, rc.w) / max(rc.z, rc.w);
        SDFResult s = SDFResult(dist, g.yz, aspect, wi);

        float repulsion = -Smoothings[wi].x;
        SDFResult se = SDFResult(s.dist - repulsion, s.normal, s.aspect, s.index);
        f = opHardSubtract(f, se);
        f = opHardUnion(f, s);
    }

    return f;
}

#define DISPERSION_TAPS 7
#define DISPERSION_SCALE 0.004

float edgeFactorForIor(float nmerged, float refThickness, float ior) {
    if (nmerged >= refThickness) return 0.0;
    float xR     = 1.0 - nmerged / max(refThickness, 1e-6);
    float thetaI = asin(pow(clamp(xR, 0.0, 1.0), 2.0));
    float sinT   = clamp(sin(thetaI) / max(ior, 1.0), -1.0, 1.0);
    float thetaT = asin(sinT);
    return -tan(thetaT - thetaI);
}

void main() {
    vec2 inSize = InSize;
    if (inSize.x <= 0.0 || inSize.y <= 0.0) inSize = vec2(textureSize(Sampler0, 0));

    vec2 coord = gl_FragCoord.xy;
    vec2 uv = coord / inSize;
    vec3 base = texture(Sampler0, uv).rgb;

    vec3 currentColor = base;
    int n = int(Count + 0.5);

    int STEP = int(DebugStep + 0.5);
    if (STEP <= 1) {
        float globalMerged = 1e6;
        for (int l = 0; l < 16; l++) {
            globalMerged = min(globalMerged, fieldWidgets(screenToUV(coord, inSize), inSize, coord, float(l)).dist);
        }
        vec3 col = (globalMerged > 0.0) ? vec3(0.9, 0.6, 0.3) : vec3(0.65, 0.85, 1.0);
        col *= 1.0 - exp(-0.03 * abs(globalMerged) * inSize.y);
        fragColor = vec4(col, 1.0);
        return;
    }

    for (int l = 0; l < 16; l++) {
        float fl = float(l);
        vec4 layerRange = LayerRanges[l];
        int start = int(layerRange.x + 0.5);
        int count = int(layerRange.y + 0.5);
        if (count <= 0) continue;
        if (outsideBounds(coord, LayerBounds[l])) continue;
        vec3 layerBase = currentColor;

        for (int i = 0; i < MAX_WIDGETS; i++) {
            if (i >= count) break;
            int wi = start + i;
            if (wi >= n) break;

            if (outsideBounds(coord, ShadowBounds[wi])) continue;

            vec4 sc = ScissorRects[wi];
            if (coord.x < sc.x || coord.y < sc.y || coord.x > sc.z || coord.y > sc.w) continue;

            vec4 rc = Rects[wi];
            vec4 rr = Rads[wi];
            vec2 cPx = vec2(rc.x + 0.5 * rc.z, rc.y + 0.5 * rc.w);

            vec2 pShadow = screenToUV(coord + Shadow0[wi].zw, inSize);
            vec2 c = screenToUV(cPx, inSize);
            vec2 b = 0.5 * vec2(rc.z, rc.w) / inSize.y;
            vec4 rad = rr / inSize.y;
            float invH = 1.0 / inSize.y;

            float expand = max(Shadow0[wi].x, 1e-4);
            float factor = Shadow0[wi].y;
            if (factor > 0.0001 && ShadowColor[wi].a > 0.0001) {
                vec3 g = sdgShapeSeg(pShadow - c, b, rad, Smoothings[wi].y, Seg0[wi], invH);
                float sh = exp(-abs(g.x) * inSize.y / expand) * 0.6 * factor;
                float sa = clamp(ShadowColor[wi].a * sh, 0.0, 1.0);
                int sMode = int(Glow1[wi].z + 0.5);
                vec3 scol = ShadowColor[wi].rgb;
                currentColor = (sMode == 0) ? mix(currentColor, scol, sa)
                        : blendLayer(currentColor, scol, sa, (sMode == 6) ? 1 : sMode);
            }

            vec4 gA = Glow0[wi];
            if (gA.x > 0.001) {
                vec2 pGlow = screenToUV(coord + vec2(-gA.z, gA.w), inSize);
                vec3 gG = sdgShapeSeg(pGlow - c, b, rad, Smoothings[wi].y, Seg0[wi], invH);
                float dG = max(gG.x * inSize.y, 0.0);
                float spread = max(gA.y, 1.0);
                float wide = exp(-dG / spread);
                float core = exp(-dG / max(spread * 0.30, 0.75));
                int gMode = int(Glow1[wi].x + 0.5);
                float cm = clamp(Glow1[wi].y, 0.0, 1.0);
                vec3 gCol = GlowColor[wi].rgb;
                if (gMode == 6) {
                    currentColor = lightTransmission(currentColor, gCol, gA.x, wide, core, cm);
                } else {
                    float weight = mix(wide, core, cm);
                    currentColor = blendLayer(currentColor, gCol, clamp(gA.x * weight, 0.0, 1.0), gMode);
                }
            }
        }

        vec2 p = screenToUV(coord, inSize);
        SDFResult f = fieldWidgets(p, inSize, coord, fl);
        float merged = f.dist;

        if (merged < 0.01) {
            vec2 coordPix = coord;
            if (Pixelated > 0.5) {
                coordPix = floor(coord / PixelGridSize) * PixelGridSize + 0.5 * PixelGridSize;
            }
            vec2 uvPix = coordPix / inSize;

            int idx = f.index;
            if (idx < 0) continue;

            vec4 o0 = Optics0[idx];
            vec4 o1 = Optics1[idx];
            vec4 tint = Tints[idx];
            vec4 extra = Extra0[idx];

            float refThickness = o0.x;
            float refFactor = o0.y;
            float refDisp = o0.z;
            float refFresRange = o0.w;

            float refFresHard = o1.x / 100.0;
            float refFresFac = o1.y / 100.0;
            float glareRange = o1.z;
            float glareHard = o1.w / 100.0;

            float hoverK = extra.y;
            float focusK = extra.z;
            int blurIndex = int(extra.x + 0.5);

            vec2 normal = f.normal;
            float nlen = length(normal);
            if (nlen > 1e-6) normal /= nlen;

            float nmerged = -merged * inSize.y;

            vec2 refrDir = -normal * 0.08 * vec2(inSize.y / inSize.x, 1.0);

            float edgeG = edgeFactorForIor(nmerged, refThickness, refFactor);
            vec4 blurred = sampleBlur(blurIndex, uvPix + refrDir * edgeG);

            vec4 dispPixel = vec4(blurred.rgb, 1.0);
            float spread = refDisp * DISPERSION_SCALE;
            if (spread > 1e-4 && nmerged < refThickness) {
                vec3 acc = vec3(0.0);
                vec3 wsum = vec3(0.0);
                for (int k = 0; k < DISPERSION_TAPS; ++k) {
                    float t = float(k) / float(DISPERSION_TAPS - 1);
                    float ior = refFactor + (t * 2.0 - 1.0) * spread;
                    vec3 w = max(vec3(1.0 - 2.0 * t, 1.0 - abs(2.0 * t - 1.0), 2.0 * t - 1.0), 0.0);
                    float e = edgeFactorForIor(nmerged, refThickness, ior);
                    acc += sampleBlur(blurIndex, uvPix + refrDir * e).rgb * w;
                    wsum += w;
                }
                dispPixel.rgb = acc / max(wsum, vec3(1e-4));
            }

            float depthBlend = clamp(Glow1[idx].w, 0.0, 1.0);
            if (depthBlend > 0.001) {
                dispPixel.rgb = mix(dispPixel.rgb, layerBase, depthBlend);
            }

            float tintedMode = clamp(Smoothings[idx].z, 0.0, 1.0);
            vec4 outColor = mix(dispPixel, vec4(tint.rgb, 1.0), tint.a * 0.8);
            if (tintedMode > 0.001) {
                float bgLuma = dot(dispPixel.rgb, vec3(0.299, 0.587, 0.114));
                vec3 absorbed = dispPixel.rgb * mix(vec3(1.0), tint.rgb * 1.45, tint.a);
                vec3 body = tint.rgb * (0.28 + 0.62 * bgLuma);
                vec3 tintedBody = mix(absorbed, body, clamp(tint.a * 0.65, 0.0, 0.72));
                outColor.rgb = mix(outColor.rgb, tintedBody, tintedMode);
            }

            float fresnelFactor = clamp(pow(1.0 + merged * inSize.y / 1500.0 * pow(500.0 / max(refFresRange, 1e-6), 2.0) + refFresHard, 5.0), 0.0, 1.0);
            vec3 fresTint = mix(vec3(1.0), tint.rgb, tint.a * 0.5);
            outColor = mix(outColor, vec4(fresTint, 1.0), fresnelFactor * refFresFac * 0.7 * nlen * (1.0 - 0.6 * tintedMode));

            if (tintedMode > 0.001) {
                vec2 rimLightDir = normalize(RIM_LIGHT_VEC.xy + vec2(1e-5));
                float rimDir = 0.55 + 0.45 * abs(dot(normal, rimLightDir));
                vec3 tintedRim = clamp(mix(tint.rgb * 1.35 + vec3(0.22), vec3(1.0), 0.35), 0.0, 1.0);
                float tintedFres = clamp(fresnelFactor * (0.55 + refFresFac * 0.85) * rimDir * nlen, 0.0, 1.0);
                outColor.rgb = mix(outColor.rgb, tintedRim, tintedFres * tintedMode);
            }

            float glareGeo = clamp(pow(1.0 + merged * inSize.y / 1500.0 * pow(500.0 / max(glareRange, 1e-6), 2.0) + glareHard, 5.0), 0.0, 1.0);
            vec3 glareMix = mix(blurred.rgb, tint.rgb, tint.a * 0.5);
            outColor.rgb = mix(outColor.rgb, glareMix, 0.25 * glareGeo * nlen);

            if (hoverK > 0.0001 || focusK > 0.0001) {
                vec4 rc = Rects[idx];
                vec2 pointer = clamp(Mouse.xy, rc.xy, rc.xy + rc.zw);
                float glowRadius = max(min(rc.z, rc.w) * 1.7, 1.0);
                vec2 pointerDistance = (coord - pointer) / glowRadius;
                float glow = exp(-dot(pointerDistance, pointerDistance) * 2.5);
                float rim = exp(-max(-merged * inSize.y, 0.0) / max(FocusBorderWidthPx, 0.8));
                vec2 lightDirection = normalize(RIM_LIGHT_VEC.xy + vec2(1e-5));
                float specular = pow(max(dot(normal, lightDirection), 0.0), 2.0);
                float strength = clamp(FocusBorderIntensity, 0.0, 1.0);
                float illumination = 0.025 * hoverK * glow
                        + strength * focusK * (0.065 * glow + 0.12 * rim * specular);
                vec3 lightTint = mix(vec3(1.0), tint.rgb, tint.a * 0.15);
                outColor.rgb = mix(outColor.rgb, lightTint, clamp(illumination, 0.0, 0.16));
            }

            float aa = smoothstep(0.001, -0.001, merged);
            currentColor = mix(currentColor, outColor.rgb, aa);
        }
    }

    fragColor = vec4(currentColor, 1.0);
}
