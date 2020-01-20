package de.hs_kl.gatav.gles05colorcube;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;


public class MainActivity extends AppCompatActivity {
    private GLSurfaceView touchableGLSurfaceView;
    public static AssetManager assetManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Debug.waitForDebugger();
        MainActivity.assetManager = getAssets();

        touchableGLSurfaceView = new TouchableGLSurfaceView(this);
        setContentView(touchableGLSurfaceView);
        touchableGLSurfaceView.setFocusableInTouchMode(true);
        touchableGLSurfaceView.requestFocus();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        touchableGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        touchableGLSurfaceView.onPause();
    }
}