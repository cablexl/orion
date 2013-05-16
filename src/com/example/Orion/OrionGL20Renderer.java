package com.example.Orion;

import android.opengl.GLES20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * User: frank
 * Date: 5/15/13
 * Time: 8:14 PM
 */
public class OrionGL20Renderer implements OrionGLSurfaceView.Renderer {
    private Square square;

    private final String vertexShaderCode = "";


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig eglConfig) {
        GLES20.glClearColor(0.5f,0.5f,0.5f,1.0f);
        square = new Square();

    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0,0,width,height);

    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
