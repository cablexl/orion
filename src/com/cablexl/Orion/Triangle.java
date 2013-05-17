package com.cablexl.Orion;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * User: frank
 * Date: 5/15/13
 * Time: 9:51 PM
 */
class Triangle {
    private static int FLOAT_BYTES = 4;

    private FloatBuffer vertexBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    private int shaderProgram, shaderPositionHandle, shaderColorHandle, modelViewProjectionMatrixHandle;

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uModelViewProjectionMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                "  gl_Position = vPosition * uModelViewProjectionMatrix;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private final int vertexCount = coordinates.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex

    static float coordinates[] = { // in counterclockwise order:
            0.0f, 0.622008459f, 0.0f,   // top
            -0.5f, -0.311004243f, 0.0f,   // bottom left
            0.5f, -0.311004243f, 0.0f    // bottom right
    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    public Triangle() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(coordinates.length * FLOAT_BYTES);

        // use the device hardware's native byte order
        byteBuffer.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = byteBuffer.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(coordinates);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        /* create the shader program used for this object */
        int vertexShaderHandle = OrionGL20Renderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderHandle = OrionGL20Renderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        shaderProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(shaderProgram, vertexShaderHandle);   // add the vertex shader to program
        GLES20.glAttachShader(shaderProgram, fragmentShaderHandle); // add the fragment shader to program
        GLES20.glLinkProgram(shaderProgram);
    }

    public void draw(float[] modelViewProjectionMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(shaderProgram);

        // get handle to vertex shader's vPosition member
        shaderPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(shaderPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(shaderPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        shaderColorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(shaderColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        modelViewProjectionMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uModelViewProjectionMatrix");
        OrionGL20Renderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(modelViewProjectionMatrixHandle, 1, false, modelViewProjectionMatrix, 0);
        OrionGL20Renderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(shaderPositionHandle);
    }
}