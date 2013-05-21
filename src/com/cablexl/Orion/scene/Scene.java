package com.cablexl.orion.scene;

import java.util.ArrayList;
import java.util.List;

/**
 * User: frank
 * Date: 5/20/13
 * Time: 8:19 PM
 */
@SuppressWarnings("unused")
public class Scene {
    List<SceneObject> sceneObjects = new ArrayList<>();

    private final void addSceneObject(final SceneObject sceneObject) {
        sceneObjects.add(sceneObject);
    }

    public final void draw() {
        for(SceneObject sceneObject: sceneObjects) {
            sceneObject.draw(null);
        }
    }

}
