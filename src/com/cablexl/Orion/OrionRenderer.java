package com.cablexl.orion;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import com.cablexl.orion.scene.Cube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * User: frank
 * Date: 5/22/13
 * Time: 9:59 PM
 */
public class OrionRenderer implements GLSurfaceView.Renderer {
    private final int NEAR = 3;
    private final int FAR = 7;

    private final Context context;
    private float[] view = new float[16];
    private float[] projection = new float[16];
    private Cube cube;

    public OrionRenderer(final Context context) {
        this.context = context;
    }

    public void onSurfaceCreated(final GL10 gl10, final EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        cube = new Cube(this);
    }

    public void onSurfaceChanged(final GL10 gl10, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projection, 0, -ratio, ratio, -1, 1, NEAR, FAR);
    }

    public void onDrawFrame(final GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glFrontFace(GLES20.GL_CCW);

        Matrix.setLookAtM(view, 0, 0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        cube.draw(view, projection);
        // draw stuff.
    }

    public final int loadVertexShader(final int id) {
        return loadShader(GLES20.GL_VERTEX_SHADER, id);
    }

    public final int loadFragmentShader(final int id) {
        return loadShader(GLES20.GL_FRAGMENT_SHADER, id);
    }

    public final int loadShader(final int type, final int id) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, loadFromRaw(id, context));
        GLES20.glCompileShader(shader);
        return shader;
    }

    private static String loadFromRaw(final int id, final Context context) {
        InputStreamReader inputStreamReader = new InputStreamReader(context.getResources().openRawResource(id));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while (null != (line = bufferedReader.readLine())) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
        }
        return stringBuilder.toString();
    }
}
