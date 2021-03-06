package com.cablexl.orion.scene;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.cablexl.orion.OrionRenderer;
import com.cablexl.orion.R;
import com.cablexl.orion.util.Matrix4;
import com.cablexl.orion.util.OrionUtils;
import com.cablexl.orion.util.Pool;

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
    private static int aTEXTURE;
    private static int aNORMAL;
    private static int aLIGHT_POSITION;
    private static int uVIEW_PROJ;
    private static int uVIEW;
    private static int uVIEW_INV_TRANS;
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
            WIDTH,  HEIGHT, -DEPTH, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, // 8 right-top-back
            WIDTH, -HEIGHT, -DEPTH, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, // 9 right-bottom-back
            -WIDTH, -HEIGHT, -DEPTH, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f, // 10 left-bottom-back
            -WIDTH,  HEIGHT, -DEPTH, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, // 11 left-top-back

            // left
            -WIDTH,  HEIGHT, -DEPTH, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // 12 left-top-back
            -WIDTH, -HEIGHT, -DEPTH, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, // 13 left-bottom-back
            -WIDTH, -HEIGHT,  DEPTH, 1.0f, 1.0f, -1.0f, 0.0f, 0.0f, // 14 left-bottom-front
            -WIDTH,  HEIGHT,  DEPTH, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f, // 15 left-top-front

            // top
            WIDTH,  HEIGHT,  DEPTH, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // 16 right-top-front
            WIDTH,  HEIGHT, -DEPTH, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // 17 right-top-back
            -WIDTH,  HEIGHT, -DEPTH, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, // 18 left-top-back
            -WIDTH,  HEIGHT,  DEPTH, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // 19 left-top-front

            // bottom
            -WIDTH, -HEIGHT,  DEPTH, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, // 20 left-bottom-front
            -WIDTH, -HEIGHT, -DEPTH, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, // 21 left-bottom-back
            WIDTH, -HEIGHT, -DEPTH, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f, // 22 right-bottom-back
            WIDTH, -HEIGHT,  DEPTH, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, // 23 right-bottom-front

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

    // TODO tweak this size
    private static final int MATRIX_POOL_COUNT = 10;
    private final Pool<Matrix4> MATRIX_POOL;


    public Cube(SceneGraph sceneGraph) {
        this.sceneGraph = sceneGraph;
        this.MATRIX_POOL = new Pool<Matrix4>(MATRIX_POOL_COUNT, Matrix4.class);
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
        TEXTURE_HANDLE = renderer.loadTexture("textures/texture.png");

        aVERTEX         = GLES20.glGetAttribLocation(CUBE_SHADER_HANDLE, "aVertex");
        aLIGHT_POSITION = GLES20.glGetAttribLocation(CUBE_SHADER_HANDLE, "aLightPosition");
        aTEXTURE        = GLES20.glGetAttribLocation(CUBE_SHADER_HANDLE, "aTexture");
        aNORMAL         = GLES20.glGetAttribLocation(CUBE_SHADER_HANDLE, "aNormal");
        uVIEW           = GLES20.glGetUniformLocation(CUBE_SHADER_HANDLE, "uView");
        uVIEW_PROJ      = GLES20.glGetUniformLocation(CUBE_SHADER_HANDLE, "uViewProjection");
        uTEXTURE        = GLES20.glGetUniformLocation(CUBE_SHADER_HANDLE, "uTexture");
        uVIEW_INV_TRANS = GLES20.glGetUniformLocation(CUBE_SHADER_HANDLE, "uViewInvTrans");
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

    // Set states
    public static void begin() {
        GLES20.glUseProgram(CUBE_SHADER_HANDLE);
        GLES20.glEnableVertexAttribArray(aVERTEX);
        GLES20.glEnableVertexAttribArray(aTEXTURE);
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
        GLES20.glVertexAttribPointer(aTEXTURE, 2, GLES20.GL_FLOAT, false, VERTEX_STRIDE, VERTEX_BUFFER);
        // Set normal coords
        VERTEX_BUFFER.position(NORMAL_INDEX);
        GLES20.glVertexAttribPointer(aNORMAL, 3, GLES20.GL_FLOAT, false, VERTEX_STRIDE, VERTEX_BUFFER);


    }

    public void draw(final float[] projView, final float[] view) {

        Matrix4 modelToWorld = MATRIX_POOL.take();
        calculateModelToWorld(modelToWorld);
        Matrix4 projViewModel = MATRIX_POOL.take();

        // Calculate and bind ModelViewProj
        Matrix.multiplyMM(projViewModel.m, 0, projView, 0, modelToWorld.m, 0);
        // bind the viewProjection matrix to the shader program.
        GLES20.glUniformMatrix4fv(uVIEW_PROJ, 1, false, projViewModel.m, 0);

        // Calculate and bind ModelView
        Matrix4 modelView = MATRIX_POOL.take();
        Matrix.multiplyMM(modelView.m, 0, view, 0, modelToWorld.m, 0);
        GLES20.glUniformMatrix4fv(uVIEW, 1, false, modelView.m, 0);

        // Calculate and bind Inverse Transpose of ModelView
        Matrix4 scratch = MATRIX_POOL.take();
        Matrix4 invTrans = MATRIX_POOL.take();

        Matrix.invertM(scratch.m, 0, modelView.m, 0);
        Matrix.transposeM(invTrans.m, 0, scratch.m, 0);
        GLES20.glUniformMatrix4fv(uVIEW_INV_TRANS, 1, false, invTrans.m, 0);

        float lightPosition[] = {5.0f, 2.0f, -7.0f, 1.0f};
        float[] lightInView = new float[4];
        Matrix.multiplyMV(lightInView, 0, view, 0, lightPosition, 0);
        // Set the position of the light (static)
        GLES20.glVertexAttrib4fv(aLIGHT_POSITION, lightInView, 0);

        // draw the cube using triangle strips.
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES.length, GLES20.GL_UNSIGNED_SHORT, INDEX_BUFFER);

        MATRIX_POOL.release(modelToWorld);
        MATRIX_POOL.release(projViewModel);
        MATRIX_POOL.release(modelView);
        MATRIX_POOL.release(scratch);
        MATRIX_POOL.release(invTrans);
    }

    private void calculateModelToWorld(Matrix4 out) {
        Matrix4 model = MATRIX_POOL.take();
        Matrix.setIdentityM(model.m, 0);
        Matrix.translateM(model.m, 0, position[0], position[1], position[2]);

        // create rotation matrix
        long time = SystemClock.uptimeMillis() % 64000L;
        float angle = 0.090f * ((int) time);

        // Allows you to 'name' a pre-allocated temporary object
        Matrix4 rotation = MATRIX_POOL.take();
        Matrix.setIdentityM(rotation.m, 0);
        OrionUtils.setRotateEulerM(rotation.m, 0, angle, angle, 0.0f);

        // apply rotation to model matrix
        Matrix.multiplyMM(out.m, 0, model.m, 0, rotation.m, 0);

        MATRIX_POOL.release(model);
        MATRIX_POOL.release(rotation);
    }

    public static void end() {
        GLES20.glDisableVertexAttribArray(aVERTEX);
        GLES20.glDisableVertexAttribArray(aTEXTURE);
        GLES20.glDisableVertexAttribArray(aNORMAL);

        GLES20.glDisable(GLES20.GL_TEXTURE_2D);
        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
