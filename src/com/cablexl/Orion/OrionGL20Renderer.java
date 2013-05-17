package com.cablexl.Orion;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * User: frank
 * Date: 5/15/13
 * Time: 8:14 PM
 */
public class OrionGL20Renderer implements OrionGLSurfaceView.Renderer {
    private static final String TAG = OrionGL20Renderer.class.getName();

    // define our 4x4 matrices.
    private float[] projectionMatrix = new float[16];
    private float[] modelViewMatrix = new float[16];
    private float[] modelViewProjectionMatrix = new float[16];
    private float[] rotationMatrix = new float[16];



    private Triangle triangle;
    private Square square;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        triangle = new Triangle();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // Create a projection matrix for our viewport.
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // clear the buffer with the clearcolor specified in the onSurfaceCreated method.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Create the viewModel matrix for the desired camera position of this frame.
        Matrix.setLookAtM(modelViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Create the modelViewProjection matrix by multiplying the ViewModel matrix with the Projection Matrix.
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(rotationMatrix, 0, angle, 0, 1.0f, 0);

        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, rotationMatrix, 0, modelViewProjectionMatrix, 0);

        triangle.draw(modelViewProjectionMatrix);

    }

    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}
