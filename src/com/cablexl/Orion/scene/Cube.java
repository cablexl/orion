package com.cablexl.orion.scene;

import android.opengl.GLES20;
import com.cablexl.orion.OrionGL20Renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * User: frank
 * Date: 5/20/13
 * Time: 8:26 PM
 */
public class Cube extends SceneObject {
    private final String vertexShaderCode =
            "uniform mat4 uModelViewProjectionMatrix;" +
            "attribute vec4 modelVector;" +
            "void main() {" +
            "  gl_Position = modelVector * uModelViewProjectionMatrix;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 colorVector;" +
            "void main() {" +
            "  gl_FragColor = colorVector;" +
            "}";

    private final float width = 0.5f;
    private final float height = 0.5f;
    private final float depth = 0.5f;
    private FloatBuffer vertexBuffer;
    private ShortBuffer orderBuffer;
    private int shaderProgram;

    private final float vertices[] = {
            -width, -height, -depth, // 0 left-bottom-front
            -width,  height, -depth, // 1 left-top-front
            width, -height, -depth, // 2 right-bottom-front
            width,  height, -depth, // 3 right-top-front

            -width, -height,  depth, // 4 left-bottom-back
            -width,  height,  depth,  // 5 left-top-back
            width, -height,  depth, // 6 right-bottom-back
            width,  height,  depth, // 7 right-top-back
    };

    private final short order[] = {
            0,1,2,3,6,7,4,5,0,1
    };

    public Cube() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        byteBuffer = ByteBuffer.allocateDirect(order.length * 2);
        byteBuffer.order(ByteOrder.nativeOrder());
        orderBuffer = byteBuffer.asShortBuffer();
        orderBuffer.put(order);
        orderBuffer.position(0);

        int vertexShaderHandle = OrionGL20Renderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderHandle = OrionGL20Renderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShaderHandle);
        GLES20.glAttachShader(shaderProgram, fragmentShaderHandle);
        GLES20.glLinkProgram(shaderProgram);
    }

    @Override
    public void draw(float[] modelviewProjectionMatrix) {
        GLES20.glUseProgram(shaderProgram);

        // get handle to vertex shader's vPosition member
        int modelVectorHandle = GLES20.glGetAttribLocation(shaderProgram, "modelVector");
        GLES20.glEnableVertexAttribArray(modelVectorHandle);
        GLES20.glVertexAttribPointer(modelVectorHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

        // get handle to fragment shader's vColor member
        int colorVectorHandle = GLES20.glGetUniformLocation(shaderProgram, "colorVector");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorVectorHandle, 1, color, 0);

        int modelViewProjectionMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uModelViewProjectionMatrix");


        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(modelViewProjectionMatrixHandle, 1, false, modelviewProjectionMatrix, 0);
        OrionGL20Renderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, order.length, GLES20.GL_UNSIGNED_SHORT, orderBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(modelVectorHandle);
        // Draw the triangle
    }
}
