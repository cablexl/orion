package com.cablexl.orion.scene;

import android.opengl.Matrix;

/**
 * User: frank
 * Date: 5/27/13
 * Time: 7:27 PM
 */
public class Camera {
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];

    public float[] getViewMatrix() {
        return viewMatrix;
    }

    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(int width, int height, float near, float far) {
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, near, far);
    }

    public void setViewMatrix() {
        Matrix.setLookAtM(viewMatrix, 0, 1.0f, 2.0f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
    }

    public Camera() {
    }
}
