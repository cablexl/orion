package com.cablexl.Orion;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class OrionMainActivity extends Activity {

    private GLSurfaceView glSurfaceView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new OrionGLSurfaceView(this);
        setContentView(glSurfaceView);
    }
}
