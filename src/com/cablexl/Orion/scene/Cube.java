package com.cablexl.orion.scene;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.cablexl.orion.OrionRenderer;
import com.cablexl.orion.R;
import com.cablexl.orion.util.OrionUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * User: frank
 * Date: 5/20/13
 * Time: 8:26 PM
 */
public class Cube {

    private static final float WIDTH = 0.5f;
    private static final float HEIGHT = 0.5f;
    private static final float DEPTH = 0.5f;

    private static FloatBuffer VERTEX_BUFFER;
    private static ShortBuffer INDEX_BUFFER;

    private static int CUBE_SHADER_HANDLE;
    private static int TEXTURE_HANDLE;

    private static int aVERTEX;
    private static int aTEX;
    private static int aNORMAL;
    private static int uCOLOR;
    private static int uVIEW_PROJ;
    private static int uTEXTURE;

    // Vertex format: x, y, z, u, v, nx, ny, nz
    private static float VERTICES[] = {
            // front
            -WIDTH,  HEIGHT,  DEPTH, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // 0 left-top-front
            -WIDTH, -HEIGHT,  DEPTH, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // 1 left-bottom-front
            WIDTH, -HEIGHT,  DEPTH, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // 2 right-bottom-front
            WIDTH,  HEIGHT,  DEPTH, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // 3 right-top-front

            // right
            WIDTH,  HEIGHT,  DEPTH, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // 4 right-top-front
            WIDTH, -HEIGHT,  DEPTH, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, // 5 right-bottom-front
            WIDTH, -HEIGHT, -DEPTH, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, // 6 right-bottom-back
            WIDTH,  HEIGHT, -DEPTH, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, // 7 right-top-back

            // back
            WIDTH,  HEIGHT, -DEPTH, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // 8 right-top-back
            WIDTH, -HEIGHT, -DEPTH, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // 9 right-bottom-back
            -WIDTH, -HEIGHT, -DEPTH, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // 10 left-bottom-back
            -WIDTH,  HEIGHT, -DEPTH, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // 11 left-top-back

            // left
            -WIDTH,  HEIGHT, -DEPTH, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // 12 left-top-back
            -WIDTH, -HEIGHT, -DEPTH, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, // 13 left-bottom-back
            -WIDTH, -HEIGHT,  DEPTH, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, // 14 left-bottom-front
            -WIDTH,  HEIGHT,  DEPTH, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, // 15 left-top-front

            // top
            WIDTH,  HEIGHT,  DEPTH, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // 16 right-top-front
            WIDTH,  HEIGHT, -DEPTH, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // 17 right-top-back
            -WIDTH,  HEIGHT, -DEPTH, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, // 18 left-top-back
            -WIDTH,  HEIGHT,  DEPTH, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // 19 left-top-front

            -WIDTH, -HEIGHT,  DEPTH, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // 20 left-bottom-front
            -WIDTH, -HEIGHT, -DEPTH, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // 21 left-bottom-back
            WIDTH, -HEIGHT, -DEPTH, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, // 22 right-bottom-back
            WIDTH, -HEIGHT,  DEPTH, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // 23 right-bottom-front

    };

    private static final int VERTEX_STRIDE = (3 * 4) + (2 * 4) + (3 * 4); // Pos, UV, Norm

    private static final int POSITION_INDEX = 0;
    private static final int TEXTURE_INDEX = 3;
    private static final int NORMAL_INDEX = 5;

    private float[] position = { 0.0f,0.0f,0.0f };

    private static final short INDICES[] = {
            0, 1, 2, 0, 2, 3,
            4, 5, 6, 4, 6, 7,
            8, 9, 10, 8, 10, 11,
            12, 13, 14, 12, 14, 15,
            16, 17, 18, 16, 18, 19,
            20, 21, 22, 20, 22, 23
    };

    private final SceneGraph sceneGraph;

    public Cube(OrionRenderer orionRenderer, SceneGraph sceneGraph) {
        // TODO remove
        // this.renderer = orionRenderer;
        this.sceneGraph = sceneGraph;
    }

    public static void initialize(OrionRenderer renderer) {
        // setup vertex buffer
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(VERTICES.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        VERTEX_BUFFER = byteBuffer.asFloatBuffer();
        VERTEX_BUFFER.put(VERTICES);
        VERTEX_BUFFER.position(0);

        // setup index buffer
        byteBuffer = ByteBuffer.allocateDirect(INDICES.length * 2);
        byteBuffer.order(ByteOrder.nativeOrder());
        INDEX_BUFFER = byteBuffer.asShortBuffer();
        INDEX_BUFFER.put(INDICES);
        INDEX_BUFFER.position(0);

        CUBE_SHADER_HANDLE = renderer.loadShaderProgram(R.raw.cubevertexshader, R.raw.cubefragmentshader);

        // Load texture
        TEXTURE_HANDLE = renderer.loadTexture(R.drawable.texture);

        aVERTEX     = GLES20.glGetAttribLocation(CUBE_SHADER_HANDLE, "aVertex");
        aTEX        = GLES20.glGetAttribLocation(CUBE_SHADER_HANDLE, "aTex");
        aNORMAL     = GLES20.glGetAttribLocation(CUBE_SHADER_HANDLE, "aNormal");
        uCOLOR      = GLES20.glGetUniformLocation(CUBE_SHADER_HANDLE, "uColor");
        uVIEW_PROJ  = GLES20.glGetUniformLocation(CUBE_SHADER_HANDLE, "uViewProjection");
        uTEXTURE    = GLES20.glGetUniformLocation(CUBE_SHADER_HANDLE, "uTexture");
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

    // Set states
    public static void begin() {
        GLES20.glUseProgram(CUBE_SHADER_HANDLE);
        GLES20.glEnableVertexAttribArray(aVERTEX);
        GLES20.glEnableVertexAttribArray(aTEX);
        GLES20.glEnableVertexAttribArray(aNORMAL);

        // Enable textures
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glEnable(GLES20.GL_TEXTURE);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_SRC_COLOR);

        // Activate texture unit 0 and bind the single texture to that unit
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TEXTURE_HANDLE);

        // Bind texture unit 0 (prepared in the begin method) to 'uTexture' sampler
        GLES20.glUniform1i(uTEXTURE, 0);

        // Set position
        VERTEX_BUFFER.position(POSITION_INDEX);
        GLES20.glVertexAttribPointer(aVERTEX, 3, GLES20.GL_FLOAT, false, VERTEX_STRIDE, VERTEX_BUFFER);
        // Set texture coords
        VERTEX_BUFFER.position(TEXTURE_INDEX);
        GLES20.glVertexAttribPointer(aTEX, 2, GLES20.GL_FLOAT, false, VERTEX_STRIDE, VERTEX_BUFFER);
        // Set normal coords
        VERTEX_BUFFER.position(NORMAL_INDEX);
        GLES20.glVertexAttribPointer(aNORMAL, 3, GLES20.GL_FLOAT, false, VERTEX_STRIDE, VERTEX_BUFFER);
    }

    public void draw(final float[] projView) {
        long time = SystemClock.uptimeMillis() % 64000L;
        float angle = 0.090f * ((int) time);

        float[] model = new float[16];
        float[] rotation = new float[16];

        Matrix.setIdentityM(model, 0);
        Matrix.setIdentityM(rotation, 0);

        // create rotation matrix.
        OrionUtils.setRotateEulerM(rotation, 0, angle, angle, 0.0f);
        float[] modelRotation = new float[16];

        // translate the model matrix to the right position.
        Matrix.translateM(model, 0, position[0], position[1], position[2]);

        // apply the rotation to the translated model matrix.
        Matrix.multiplyMM(modelRotation, 0, model, 0, rotation, 0);

//        Matrix.rotateM(model, 0, angle, 1.0f, 1.0f, 0.0f);

        float[] projViewModel = new float[16];

        Matrix.multiplyMM(projViewModel, 0, projView, 0, modelRotation, 0);

        float color[] = {1.0f, 0.0f, 0.0f, 1.0f};

        // Set color for drawing the triangle
        GLES20.glUniform4fv(uCOLOR, 1, color, 0);

        // bind the viewProjection matrix to the shader program.
        GLES20.glUniformMatrix4fv(uVIEW_PROJ, 1, false, projViewModel, 0);

        // draw the cube using triangle strips.
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES.length, GLES20.GL_UNSIGNED_SHORT, INDEX_BUFFER);
    }

    public static void end() {
        GLES20.glDisableVertexAttribArray(aVERTEX);
        GLES20.glDisableVertexAttribArray(aTEX);
        GLES20.glDisableVertexAttribArray(aNORMAL);

        GLES20.glDisable(GLES20.GL_TEXTURE_2D);
        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
