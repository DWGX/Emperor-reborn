package dev.emperor.utils.render.img;

public class ImageMath {
    public static final float PI = (float)Math.PI;
    public static final float HALF_PI = 1.5707964f;
    public static final float QUARTER_PI = 0.7853982f;
    public static final float TWO_PI = (float)Math.PI * 2;
    private static final float m00 = -0.5f;
    private static final float m01 = 1.5f;
    private static final float m02 = -1.5f;
    private static final float m03 = 0.5f;
    private static final float m10 = 1.0f;
    private static final float m11 = -2.5f;
    private static final float m12 = 2.0f;
    private static final float m13 = -0.5f;
    private static final float m20 = -0.5f;
    private static final float m21 = 0.0f;
    private static final float m22 = 0.5f;
    private static final float m23 = 0.0f;
    private static final float m30 = 0.0f;
    private static final float m31 = 1.0f;
    private static final float m32 = 0.0f;
    private static final float m33 = 0.0f;

    public static float bias(float a, float b2) {
        return a / ((1.0f / b2 - 2.0f) * (1.0f - a) + 1.0f);
    }

    public static float gain(float a, float b2) {
        float c = (1.0f / b2 - 2.0f) * (1.0f - 2.0f * a);
        if ((double)a < 0.5) {
            return a / (c + 1.0f);
        }
        return (c - a) / (c - 1.0f);
    }

    public static float step(float a, float x2) {
        return x2 < a ? 0.0f : 1.0f;
    }

    public static float pulse(float a, float b2, float x2) {
        return x2 < a || x2 >= b2 ? 0.0f : 1.0f;
    }

    public static float smoothPulse(float a1, float a2, float b1, float b2, float x2) {
        if (x2 < a1 || x2 >= b2) {
            return 0.0f;
        }
        if (x2 >= a2) {
            if (x2 < b1) {
                return 1.0f;
            }
            x2 = (x2 - b1) / (b2 - b1);
            return 1.0f - x2 * x2 * (3.0f - 2.0f * x2);
        }
        x2 = (x2 - a1) / (a2 - a1);
        return x2 * x2 * (3.0f - 2.0f * x2);
    }

    public static float smoothStep(float a, float b2, float x2) {
        if (x2 < a) {
            return 0.0f;
        }
        if (x2 >= b2) {
            return 1.0f;
        }
        x2 = (x2 - a) / (b2 - a);
        return x2 * x2 * (3.0f - 2.0f * x2);
    }

    public static float circleUp(float x2) {
        x2 = 1.0f - x2;
        return (float)Math.sqrt(1.0f - x2 * x2);
    }

    public static float circleDown(float x2) {
        return 1.0f - (float)Math.sqrt(1.0f - x2 * x2);
    }

    public static float clamp(float x2, float a, float b2) {
        return x2 < a ? a : (x2 > b2 ? b2 : x2);
    }

    public static int clamp(int x2, int a, int b2) {
        return x2 < a ? a : (x2 > b2 ? b2 : x2);
    }

    public static double mod(double a, double b2) {
        int n;
        if ((a -= (double)(n = (int)(a / b2)) * b2) < 0.0) {
            return a + b2;
        }
        return a;
    }

    public static float mod(float a, float b2) {
        int n;
        if ((a -= (float)(n = (int)(a / b2)) * b2) < 0.0f) {
            return a + b2;
        }
        return a;
    }

    public static int mod(int a, int b2) {
        int n;
        if ((a -= (n = a / b2) * b2) < 0) {
            return a + b2;
        }
        return a;
    }

    public static float triangle(float x2) {
        float r2 = ImageMath.mod(x2, 1.0f);
        return 2.0f * ((double)r2 < 0.5 ? r2 : 1.0f - r2);
    }

    public static float lerp(float t2, float a, float b2) {
        return a + t2 * (b2 - a);
    }

    public static int lerp(float t2, int a, int b2) {
        return (int)((float)a + t2 * (float)(b2 - a));
    }

    public static int mixColors(float t2, int rgb1, int rgb2) {
        int a1 = rgb1 >> 24 & 0xFF;
        int r1 = rgb1 >> 16 & 0xFF;
        int g1 = rgb1 >> 8 & 0xFF;
        int b1 = rgb1 & 0xFF;
        int a2 = rgb2 >> 24 & 0xFF;
        int r2 = rgb2 >> 16 & 0xFF;
        int g2 = rgb2 >> 8 & 0xFF;
        int b2 = rgb2 & 0xFF;
        a1 = ImageMath.lerp(t2, a1, a2);
        r1 = ImageMath.lerp(t2, r1, r2);
        g1 = ImageMath.lerp(t2, g1, g2);
        b1 = ImageMath.lerp(t2, b1, b2);
        return a1 << 24 | r1 << 16 | g1 << 8 | b1;
    }

    public static int bilinearInterpolate(float x2, float y2, int nw, int ne, int sw, int se) {
        int a0 = nw >> 24 & 0xFF;
        int r0 = nw >> 16 & 0xFF;
        int g0 = nw >> 8 & 0xFF;
        int b0 = nw & 0xFF;
        int a1 = ne >> 24 & 0xFF;
        int r1 = ne >> 16 & 0xFF;
        int g1 = ne >> 8 & 0xFF;
        int b1 = ne & 0xFF;
        int a2 = sw >> 24 & 0xFF;
        int r2 = sw >> 16 & 0xFF;
        int g2 = sw >> 8 & 0xFF;
        int b2 = sw & 0xFF;
        int a3 = se >> 24 & 0xFF;
        int r3 = se >> 16 & 0xFF;
        int g3 = se >> 8 & 0xFF;
        int b3 = se & 0xFF;
        float cx = 1.0f - x2;
        float cy = 1.0f - y2;
        float m0 = cx * (float)a0 + x2 * (float)a1;
        float m1 = cx * (float)a2 + x2 * (float)a3;
        int a = (int)(cy * m0 + y2 * m1);
        m0 = cx * (float)r0 + x2 * (float)r1;
        m1 = cx * (float)r2 + x2 * (float)r3;
        int r4 = (int)(cy * m0 + y2 * m1);
        m0 = cx * (float)g0 + x2 * (float)g1;
        m1 = cx * (float)g2 + x2 * (float)g3;
        int g4 = (int)(cy * m0 + y2 * m1);
        m0 = cx * (float)b0 + x2 * (float)b1;
        m1 = cx * (float)b2 + x2 * (float)b3;
        int b4 = (int)(cy * m0 + y2 * m1);
        return a << 24 | r4 << 16 | g4 << 8 | b4;
    }

    public static int brightnessNTSC(int rgb) {
        int r2 = rgb >> 16 & 0xFF;
        int g2 = rgb >> 8 & 0xFF;
        int b2 = rgb & 0xFF;
        return (int)((float)r2 * 0.299f + (float)g2 * 0.587f + (float)b2 * 0.114f);
    }

    public static float spline(float x2, int numKnots, float[] knots) {
        int numSpans = numKnots - 3;
        if (numSpans < 1) {
            throw new IllegalArgumentException("Too few knots in spline");
        }
        int span = (int)(x2 = ImageMath.clamp(x2, 0.0f, 1.0f) * (float)numSpans);
        if (span > numKnots - 4) {
            span = numKnots - 4;
        }
        x2 -= (float)span;
        float k0 = knots[span];
        float k1 = knots[span + 1];
        float k2 = knots[span + 2];
        float k3 = knots[span + 3];
        float c3 = -0.5f * k0 + 1.5f * k1 + -1.5f * k2 + 0.5f * k3;
        float c2 = 1.0f * k0 + -2.5f * k1 + 2.0f * k2 + -0.5f * k3;
        float c1 = -0.5f * k0 + 0.0f * k1 + 0.5f * k2 + 0.0f * k3;
        float c0 = 0.0f * k0 + 1.0f * k1 + 0.0f * k2 + 0.0f * k3;
        return ((c3 * x2 + c2) * x2 + c1) * x2 + c0;
    }

    public static float spline(float x2, int numKnots, int[] xknots, int[] yknots) {
        int span;
        int numSpans = numKnots - 3;
        if (numSpans < 1) {
            throw new IllegalArgumentException("Too few knots in spline");
        }
        for (span = 0; span < numSpans && !((float)xknots[span + 1] > x2); ++span) {
        }
        if (span > numKnots - 3) {
            span = numKnots - 3;
        }
        float t2 = (x2 - (float)xknots[span]) / (float)(xknots[span + 1] - xknots[span]);
        if (--span < 0) {
            span = 0;
            t2 = 0.0f;
        }
        float k0 = yknots[span];
        float k1 = yknots[span + 1];
        float k2 = yknots[span + 2];
        float k3 = yknots[span + 3];
        float c3 = -0.5f * k0 + 1.5f * k1 + -1.5f * k2 + 0.5f * k3;
        float c2 = 1.0f * k0 + -2.5f * k1 + 2.0f * k2 + -0.5f * k3;
        float c1 = -0.5f * k0 + 0.0f * k1 + 0.5f * k2 + 0.0f * k3;
        float c0 = 0.0f * k0 + 1.0f * k1 + 0.0f * k2 + 0.0f * k3;
        return ((c3 * t2 + c2) * t2 + c1) * t2 + c0;
    }

    public static int colorSpline(float x2, int numKnots, int[] knots) {
        int numSpans = numKnots - 3;
        if (numSpans < 1) {
            throw new IllegalArgumentException("Too few knots in spline");
        }
        int span = (int)(x2 = ImageMath.clamp(x2, 0.0f, 1.0f) * (float)numSpans);
        if (span > numKnots - 4) {
            span = numKnots - 4;
        }
        x2 -= (float)span;
        int v = 0;
        for (int i = 0; i < 4; ++i) {
            int shift = i * 8;
            float k0 = knots[span] >> shift & 0xFF;
            float k1 = knots[span + 1] >> shift & 0xFF;
            float k2 = knots[span + 2] >> shift & 0xFF;
            float k3 = knots[span + 3] >> shift & 0xFF;
            float c3 = -0.5f * k0 + 1.5f * k1 + -1.5f * k2 + 0.5f * k3;
            float c2 = 1.0f * k0 + -2.5f * k1 + 2.0f * k2 + -0.5f * k3;
            float c1 = -0.5f * k0 + 0.0f * k1 + 0.5f * k2 + 0.0f * k3;
            float c0 = 0.0f * k0 + 1.0f * k1 + 0.0f * k2 + 0.0f * k3;
            int n = (int)(((c3 * x2 + c2) * x2 + c1) * x2 + c0);
            if (n < 0) {
                n = 0;
            } else if (n > 255) {
                n = 255;
            }
            v |= n << shift;
        }
        return v;
    }

    public static int colorSpline(int x2, int numKnots, int[] xknots, int[] yknots) {
        int span;
        int numSpans = numKnots - 3;
        if (numSpans < 1) {
            throw new IllegalArgumentException("Too few knots in spline");
        }
        for (span = 0; span < numSpans && xknots[span + 1] <= x2; ++span) {
        }
        if (span > numKnots - 3) {
            span = numKnots - 3;
        }
        float t2 = (float)(x2 - xknots[span]) / (float)(xknots[span + 1] - xknots[span]);
        if (--span < 0) {
            span = 0;
            t2 = 0.0f;
        }
        int v = 0;
        for (int i = 0; i < 4; ++i) {
            int shift = i * 8;
            float k0 = yknots[span] >> shift & 0xFF;
            float k1 = yknots[span + 1] >> shift & 0xFF;
            float k2 = yknots[span + 2] >> shift & 0xFF;
            float k3 = yknots[span + 3] >> shift & 0xFF;
            float c3 = -0.5f * k0 + 1.5f * k1 + -1.5f * k2 + 0.5f * k3;
            float c2 = 1.0f * k0 + -2.5f * k1 + 2.0f * k2 + -0.5f * k3;
            float c1 = -0.5f * k0 + 0.0f * k1 + 0.5f * k2 + 0.0f * k3;
            float c0 = 0.0f * k0 + 1.0f * k1 + 0.0f * k2 + 0.0f * k3;
            int n = (int)(((c3 * t2 + c2) * t2 + c1) * t2 + c0);
            if (n < 0) {
                n = 0;
            } else if (n > 255) {
                n = 255;
            }
            v |= n << shift;
        }
        return v;
    }

    public static void resample(int[] source, int[] dest, int length, int offset, int stride, float[] out) {
        float outSegment;
        int srcIndex = offset;
        int destIndex = offset;
        int lastIndex = source.length;
        float[] in = new float[length + 2];
        int i = 0;
        for (int j2 = 0; j2 < length; ++j2) {
            while (out[i + 1] < (float)j2) {
                ++i;
            }
            in[j2] = (float)i + ((float)j2 - out[i]) / (out[i + 1] - out[i]);
        }
        in[length] = length;
        in[length + 1] = length;
        float inSegment = 1.0f;
        float sizfac = outSegment = in[1];
        float bSum = 0.0f;
        float gSum = 0.0f;
        float rSum = 0.0f;
        float aSum = 0.0f;
        int rgb = source[srcIndex];
        int a = rgb >> 24 & 0xFF;
        int r2 = rgb >> 16 & 0xFF;
        int g2 = rgb >> 8 & 0xFF;
        int b2 = rgb & 0xFF;
        rgb = source[srcIndex += stride];
        int nextA = rgb >> 24 & 0xFF;
        int nextR = rgb >> 16 & 0xFF;
        int nextG = rgb >> 8 & 0xFF;
        int nextB = rgb & 0xFF;
        srcIndex += stride;
        i = 1;
        while (i <= length) {
            float aIntensity = inSegment * (float)a + (1.0f - inSegment) * (float)nextA;
            float rIntensity = inSegment * (float)r2 + (1.0f - inSegment) * (float)nextR;
            float gIntensity = inSegment * (float)g2 + (1.0f - inSegment) * (float)nextG;
            float bIntensity = inSegment * (float)b2 + (1.0f - inSegment) * (float)nextB;
            if (inSegment < outSegment) {
                aSum += aIntensity * inSegment;
                rSum += rIntensity * inSegment;
                gSum += gIntensity * inSegment;
                bSum += bIntensity * inSegment;
                outSegment -= inSegment;
                inSegment = 1.0f;
                a = nextA;
                r2 = nextR;
                g2 = nextG;
                b2 = nextB;
                if (srcIndex < lastIndex) {
                    rgb = source[srcIndex];
                }
                nextA = rgb >> 24 & 0xFF;
                nextR = rgb >> 16 & 0xFF;
                nextG = rgb >> 8 & 0xFF;
                nextB = rgb & 0xFF;
                srcIndex += stride;
                continue;
            }
            dest[destIndex] = (int)Math.min((aSum += aIntensity * outSegment) / sizfac, 255.0f) << 24 | (int)Math.min((rSum += rIntensity * outSegment) / sizfac, 255.0f) << 16 | (int)Math.min((gSum += gIntensity * outSegment) / sizfac, 255.0f) << 8 | (int)Math.min((bSum += bIntensity * outSegment) / sizfac, 255.0f);
            destIndex += stride;
            bSum = 0.0f;
            gSum = 0.0f;
            rSum = 0.0f;
            aSum = 0.0f;
            inSegment -= outSegment;
            sizfac = outSegment = in[i + 1] - in[i];
            ++i;
        }
    }

    public static void premultiply(int[] p, int offset, int length) {
        length += offset;
        for (int i = offset; i < length; ++i) {
            int rgb = p[i];
            int a = rgb >> 24 & 0xFF;
            int r2 = rgb >> 16 & 0xFF;
            int g2 = rgb >> 8 & 0xFF;
            int b2 = rgb & 0xFF;
            float f = (float)a * 0.003921569f;
            r2 = (int)((float)r2 * f);
            g2 = (int)((float)g2 * f);
            b2 = (int)((float)b2 * f);
            p[i] = a << 24 | r2 << 16 | g2 << 8 | b2;
        }
    }

    public static void unpremultiply(int[] p, int offset, int length) {
        length += offset;
        for (int i = offset; i < length; ++i) {
            int rgb = p[i];
            int a = rgb >> 24 & 0xFF;
            int r2 = rgb >> 16 & 0xFF;
            int g2 = rgb >> 8 & 0xFF;
            int b2 = rgb & 0xFF;
            if (a == 0 || a == 255) continue;
            float f = 255.0f / (float)a;
            r2 = (int)((float)r2 * f);
            g2 = (int)((float)g2 * f);
            b2 = (int)((float)b2 * f);
            if (r2 > 255) {
                r2 = 255;
            }
            if (g2 > 255) {
                g2 = 255;
            }
            if (b2 > 255) {
                b2 = 255;
            }
            p[i] = a << 24 | r2 << 16 | g2 << 8 | b2;
        }
    }
}

