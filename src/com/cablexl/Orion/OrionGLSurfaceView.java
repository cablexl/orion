package com.cablexl.Orion;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * User: frank
 * Date: 5/15/13
 * Time: 8:12 PM
 */
public class OrionGLSurfaceView extends GLSurfaceView {

    public OrionGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(new OrionGL20Renderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
