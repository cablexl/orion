package com.cablexl.orion.scene;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * User: frank
 * Date: 5/27/13
 * Time: 7:27 PM
 */
public class SceneGraph {
    private final Camera camera = new Camera();
    private final List<Cube> cubes = new ArrayList<Cube>();
    private final float[] projView = new float[16];

    public SceneGraph() {
        for (int i = 0; i > -10; i--) {
            for (int j = 0; j < 10; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    Cube cube = new Cube(this);
                    cube.setPosition(new float[]{j, 0.0f, i});
                    cubes.add(cube);
                }
            }
        }
        Cube cube = new Cube(this);
        cube.setPosition(new float[]{5.0f, 2.0f, -7.0f});
        cubes.add(cube);

    }

    public Camera getCamera() {
        return camera;
    }

    public List<Cube> getCubes() {
        return cubes;
    }

    public void draw() {
        // Initialize camera matrix
        Matrix.setIdentityM(projView, 0);
        Matrix.multiplyMM(projView, 0, camera.getProjectionMatrix(), 0, camera.getViewMatrix(), 0);

        Cube.begin();
        for (Cube cube : cubes) {
            cube.draw(projView, camera.getViewMatrix());
        }
        Cube.end();
    }

}
