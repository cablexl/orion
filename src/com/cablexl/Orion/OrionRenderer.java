package com.cablexl.orion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.cablexl.orion.scene.Cube;
import com.cablexl.orion.scene.SceneGraph;
import com.cablexl.orion.util.OrionUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * User: frank
 * Date: 5/22/13
 * Time: 9:59 PM
 */
public class OrionRenderer implements GLSurfaceView.Renderer {
    private SceneGraph sceneGraph;
    private final Context context;

    public OrionRenderer(final Context context) {
        this.context = context;
    }

    public void onSurfaceCreated(final GL10 gl10, final EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glClearDepthf(1.0f);
        GLES20.glDepthMask(true);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glFrontFace(GLES20.GL_CCW);

        // TODO Probably not the best place to do this but it works for now
        Cube.initialize(this);

        setupSceneGraph();
    }

    private final void setupSceneGraph() {
        sceneGraph = new SceneGraph();
    }

    public void onSurfaceChanged(final GL10 gl10, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);
        sceneGraph.getCamera().setProjectionMatrix(width, height, 1, 100);
    }

    public void onDrawFrame(final GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        sceneGraph.getCamera().setViewMatrix();
        sceneGraph.draw();
    }

    public final int loadShaderProgram(final int vertex, final int frag) {
        int v = loadVertexShader(vertex);
        int f = loadFragmentShader(frag);

        int program = GLES20.glCreateProgram();
        OrionUtils.checkGLError("OrionRenderer", "glCreateProgram");
        GLES20.glAttachShader(program, v);
        OrionUtils.checkGLError("OrionRenderer", "glAttachShader [vertex]");
        GLES20.glAttachShader(program, f);
        OrionUtils.checkGLError("OrionRenderer", "glAttachShader [prog]");
        GLES20.glLinkProgram(program);
        OrionUtils.checkGLError("OrionRenderer", "glLinkProgram");

        return program;
    }

    public final int loadVertexShader(final int id) {
        return loadShader(GLES20.GL_VERTEX_SHADER, id);
    }

    public final int loadFragmentShader(final int id) {
        return loadShader(GLES20.GL_FRAGMENT_SHADER, id);
    }

    /**
     * Internal helper function for loading shaders
     */
    private final int loadShader(final int type, final int id) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, loadFromRaw(id, context));
        OrionUtils.checkGLError("OrionRenderer", "glShaderSource");
        GLES20.glCompileShader(shader);
        OrionUtils.checkGLError("OrionRenderer", "glCompileShader");

        // TODO This code could probably be cleaned up
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.nativeOrder());
        IntBuffer i = byteBuffer.asIntBuffer();

        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, i);
        if(i.get(0) == GLES20.GL_FALSE) {
            Log.e("OrionRenderer", "Shader failed to compile:\n" + GLES20.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    public final int loadTexture(final int id) {
        // TODO This code could probably be cleaned up, maybe just use int[]
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.nativeOrder());
        IntBuffer i = byteBuffer.asIntBuffer();

        // You could create multiple textures at once here, but I'm not
        GLES20.glGenTextures(1, i);
        OrionUtils.checkGLError("OrionRenderer", "glGenTextures");

        // Bind the texture we want to load into
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, i.get(0));
        OrionUtils.checkGLError("OrionRenderer", "glBindTexture");

        // TODO more options?  Check the CT Texture class
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

        // Load resource
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        OrionUtils.checkGLError("OrionRenderer", "texImage2D");

        return i.get(0);
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
