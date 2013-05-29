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

    private final float width = 0.5f;
    private final float height = 0.5f;
    private final float depth = 0.5f;
    private FloatBuffer vertexBuffer;
    private ShortBuffer orderBuffer;
    private final int shaderProgramHandle;
    private final OrionRenderer renderer;
    private final SceneGraph sceneGraph;
    private final int textureHandle;

    private final int aVertex;
    private final int aTex;
    private final int aNormal;
    private final int uColor;
    private final int uViewProjection;
    private final int uTexture;

    // Vertex format: x, y, z, u, v, nx, ny, nz
    private final float vertices[] = {
            // front
            -width,  height,  depth, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // 0 left-top-front
            -width, -height,  depth, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // 1 left-bottom-front
             width, -height,  depth, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // 2 right-bottom-front
             width,  height,  depth, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // 3 right-top-front

            // right
            width,  height,  depth, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // 4 right-top-front
            width, -height,  depth, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, // 5 right-bottom-front
            width, -height, -depth, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, // 6 right-bottom-back
            width,  height, -depth, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, // 7 right-top-back

            // back
            width,  height, -depth, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // 8 right-top-back
            width, -height, -depth, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // 9 right-bottom-back
            -width, -height, -depth, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // 10 left-bottom-back
            -width,  height, -depth, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // 11 left-top-back

            // left
            -width,  height, -depth, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // 12 left-top-back
            -width, -height, -depth, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, // 13 left-bottom-back
            -width, -height,  depth, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, // 14 left-bottom-front
            -width,  height,  depth, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, // 15 left-top-front

            // top
            width,  height,  depth, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // 16 right-top-front
            width,  height, -depth, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // 17 right-top-back
            -width,  height, -depth, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, // 18 left-top-back
            -width,  height,  depth, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // 19 left-top-front

            -width, -height,  depth, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // 20 left-bottom-front
            -width, -height, -depth, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // 21 left-bottom-back
            width, -height, -depth, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, // 22 right-bottom-back
            width, -height,  depth, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // 23 right-bottom-front

    };

    private static final int VERTEX_STRIDE = (3 * 4) + (2 * 4) + (3 * 4); // Pos, UV, Norm

    private static final int POSITION_INDEX = 0;
    private static final int TEXTURE_INDEX = 3;
    private static final int NORMAL_INDEX = 5;

    private float[] position = { 0.0f,0.0f,0.0f };

    private final short order[] = {
            0, 1, 2, 0, 2, 3,
            4, 5, 6, 4, 6, 7,
            8, 9, 10, 8, 10, 11,
            12, 13, 14, 12, 14, 15,
            16, 17, 18, 16, 18, 19,
            20, 21, 22, 20, 22, 23
    };

    public void setPosition(float[] position) {
        this.position = position;
    }

    public Cube(OrionRenderer orionRenderer, SceneGraph sceneGraph) {
        this.renderer = orionRenderer;
        this.sceneGraph = sceneGraph;

        // setup vertex buffer
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // setup vertex draw order buffer
        byteBuffer = ByteBuffer.allocateDirect(order.length * 2);
        byteBuffer.order(ByteOrder.nativeOrder());
        orderBuffer = byteBuffer.asShortBuffer();
        orderBuffer.put(order);
        orderBuffer.position(0);

        int vertexShaderHandle = renderer.loadVertexShader(R.raw.cubevertexshader);
        int fragmentShaderHandle = renderer.loadFragmentShader(R.raw.cubefragmentshader);

        shaderProgramHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgramHandle, vertexShaderHandle);
        GLES20.glAttachShader(shaderProgramHandle, fragmentShaderHandle);
        GLES20.glLinkProgram(shaderProgramHandle);

        // Load texture
        textureHandle = renderer.loadTexture(R.drawable.texture);

        // Cache attribute locations
        aVertex         = GLES20.glGetAttribLocation(shaderProgramHandle, "aVertex");
        aTex            = GLES20.glGetAttribLocation(shaderProgramHandle, "aTex");
        aNormal         = GLES20.glGetAttribLocation(shaderProgramHandle, "aNormal");
        uColor          = GLES20.glGetUniformLocation(shaderProgramHandle, "uColor");
        uViewProjection = GLES20.glGetUniformLocation(shaderProgramHandle, "uViewProjection");
        uTexture        = GLES20.glGetUniformLocation(shaderProgramHandle, "uTexture");
    }

    // Set states
    public void begin() {
        GLES20.glUseProgram(shaderProgramHandle);
        GLES20.glEnableVertexAttribArray(aVertex);
        GLES20.glEnableVertexAttribArray(aTex);
        GLES20.glEnableVertexAttribArray(aNormal);

        // Enable textures
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glEnable(GLES20.GL_TEXTURE);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_SRC_COLOR);

        // Activate texture unit 0 and bind the single texture to that unit
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
    }

    public void draw() {
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

        float[] modelView = new float[16];
        float[] viewProjection = new float[16];
       Matrix.multiplyMM(modelView, 0, sceneGraph.getCamera().getViewMatrix(), 0, modelRotation, 0);
//        Matrix.multiplyMM(modelView, 0, sceneGraph.getCamera().getViewMatrix(), 0, model, 0);
        Matrix.multiplyMM(viewProjection, 0, sceneGraph.getCamera().getProjectionMatrix(), 0, modelView, 0);

        // Set position
        vertexBuffer.position(POSITION_INDEX);
        GLES20.glVertexAttribPointer(aVertex, 3, GLES20.GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer);
        // Set texture coords
        vertexBuffer.position(TEXTURE_INDEX);
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer);
        // Set normal coords
        vertexBuffer.position(NORMAL_INDEX);
        GLES20.glVertexAttribPointer(aNormal, 3, GLES20.GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer);

        float color[] = {1.0f, 0.0f, 0.0f, 1.0f};

        // Bind texture unit 0 (prepared in the begin method) to 'uTexture' sampler
        GLES20.glUniform1i(uTexture, 0);

        // Set color for drawing the triangle
        GLES20.glUniform4fv(uColor, 1, color, 0);

        // bind the viewProjection matrix to the shader program.
        GLES20.glUniformMatrix4fv(uViewProjection, 1, false, viewProjection, 0);

        // draw the cube using triangle strips.
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, order.length, GLES20.GL_UNSIGNED_SHORT, orderBuffer);
    }

    public void end() {
        GLES20.glDisableVertexAttribArray(aVertex);
        GLES20.glDisableVertexAttribArray(aTex);
        GLES20.glDisableVertexAttribArray(aNormal);

        GLES20.glDisable(GLES20.GL_TEXTURE_2D);
        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
