package com.cablexl.orion.scene;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import com.cablexl.orion.OrionRenderer;
import com.cablexl.orion.R;

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
    private final int vertexShaderHandle;
    private final int fragmentShaderHandle;
    private final OrionRenderer renderer;

    private final float vertices[] = {
            -width, -height, depth, // 0 left-bottom-front
            -width, height, depth, // 1 left-top-front
            width, -height, depth, // 2 right-bottom-front
            width, height, depth, // 3 right-top-front

            -width, -height, -depth, // 4 left-bottom-back
            -width, height, -depth,  // 5 left-top-back
            width, -height, -depth, // 6 right-bottom-back
            width, height, -depth, // 7 right-top-back
    };

    private final short order[] = {
            1, 0, 2, 1, 2, 3 // front face
    };

    public Cube(OrionRenderer orionRenderer) {
        this.renderer = orionRenderer;

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

        vertexShaderHandle = renderer.loadVertexShader(R.raw.cubevertexshader);
        fragmentShaderHandle = renderer.loadFragmentShader(R.raw.cubefragmentshader);

        shaderProgramHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgramHandle, vertexShaderHandle);
        GLES20.glAttachShader(shaderProgramHandle, fragmentShaderHandle);
        GLES20.glLinkProgram(shaderProgramHandle);
    }

    public void draw(float[] view, float[] projection) {
        GLES20.glUseProgram(shaderProgramHandle);

        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);

        float[] model = new float[16];

        Matrix.setIdentityM(model, 0);

//        Matrix.translateM(model, 0, -0.5f, -0.5f, -0.5f);
//        Matrix.rotateM(model, 0, angle, 0.0f, 0.0f, 1.0f);
//        Matrix.translateM(model, 0, 0.5f, 0.5f, 0.5f);

        float[] modelView = new float[16];
        float[] viewProjection = new float[16];
        Matrix.multiplyMM(modelView, 0, view, 0, model, 0);
        Matrix.multiplyMM(viewProjection, 0, projection, 0, modelView, 0);

        // get handle to vertex shader's vPosition member
        int aVertex = GLES20.glGetAttribLocation(shaderProgramHandle, "aVertex");
        GLES20.glEnableVertexAttribArray(aVertex);
        GLES20.glVertexAttribPointer(aVertex, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);

        float color[] = {1.0f, 0.0f, 0.0f, 1.0f};

        // get handle to fragment shader's vColor member
        int uColor = GLES20.glGetUniformLocation(shaderProgramHandle, "uColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(uColor, 1, color, 0);

        int viewProjectionHandle = GLES20.glGetUniformLocation(shaderProgramHandle, "uViewProjection");

        // bind the viewProjection matrix to the shader program.
        GLES20.glUniformMatrix4fv(viewProjectionHandle, 1, false, viewProjection, 0);

        // draw the cube using triangle strips.
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, order.length, GLES20.GL_UNSIGNED_SHORT, orderBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(aVertex);
        // Draw the triangle
    }
}
