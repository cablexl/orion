package com.cablexl.orion.scene;

import com.cablexl.orion.OrionRenderer;

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

    public Camera getCamera() {
        return camera;
    }

    public List<Cube> getCubes() {
        return cubes;
    }

    public SceneGraph(OrionRenderer renderer) {
        Cube cube1 = new Cube(renderer, this);
        Cube cube2 = new Cube(renderer, this);
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j > -10; j--) {
                Cube cube = new Cube(renderer, this);
                cube.setPosition(new float[] { i, 0.0f, j });
                getCubes().add(cube);
            }
        }
    }

    public void draw() {
        for(Cube cube: cubes) {
            cube.begin();
            cube.draw();
            cube.end();
        }

    }

}
