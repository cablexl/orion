package com.cablexl.orion.util;

/**
 * User: frank
 * Date: 5/27/13
 * Time: 7:25 PM
 */
public class OrionUtils {
    public static void setRotateEulerM(float[] rm, int rmOffset, float x,
                                       float y, float z) {
        x = x * 0.01745329f;
        y = y * 0.01745329f;
        z = z * 0.01745329f;
        float sx = (float) Math.sin(x);
        float sy = (float) Math.sin(y);
        float sz = (float) Math.sin(z);
        float cx = (float) Math.cos(x);
        float cy = (float) Math.cos(y);
        float cz = (float) Math.cos(z);
        float cxsy = cx * sy;
        float sxsy = sx * sy;

        rm[rmOffset] = cy * cz;
        rm[rmOffset + 1] = -cy * sz;
        rm[rmOffset + 2] = sy;
        rm[rmOffset + 3] = 0.0f;

        rm[rmOffset + 4] = sxsy * cz + cx * sz;
        rm[rmOffset + 5] = -sxsy * sz + cx * cz;
        rm[rmOffset + 6] = -sx * cy;
        rm[rmOffset + 7] = 0.0f;

        rm[rmOffset + 8] = -cxsy * cz + sx * sz;
        rm[rmOffset + 9] = cxsy * sz + sx * cz;
        rm[rmOffset + 10] = cx * cy;
        rm[rmOffset + 11] = 0.0f;

        rm[rmOffset + 12] = 0.0f;
        rm[rmOffset + 13] = 0.0f;
        rm[rmOffset + 14] = 0.0f;
        rm[rmOffset + 15] = 1.0f;
    }
}
