package com.cablexl.orion;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class OrionMainActivity extends Activity {

    private GLSurfaceView glSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new OrionGLSurfaceView(this);
        setContentView(glSurfaceView);
    }


}
