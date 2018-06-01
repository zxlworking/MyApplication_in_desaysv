package test.zxl.com.test_opengl;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.util.Log;

import com.zxl.common.DebugUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity {
    private String TAG = "MainActivity";

    private Context mContext;

    private GLSurfaceView mGlSurfaceView;
    private CustomRenderer mCustomRenderer;

    private List<FloatBuffer> mVertexs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DebugUtil.IS_DEBUG = DebugUtil.STATE_OPEN;

        iniVertexs();

        mGlSurfaceView = findViewById(R.id.gl_surface_view);

        mCustomRenderer = new CustomRenderer();
        mGlSurfaceView.setRenderer(mCustomRenderer);
    }

    private void iniVertexs(){
        float[] mVertexFront = new float[]{1,1,1, -1,1,1, -1,-1,1, 1,-1,1};
        float[] mVertexBack = new float[]{1,1,-1, -1,1,-1, -1,-1,-1, 1,-1,-1};
        float[] mVertexRight = new float[]{1,1,1, 1,-1,1, 1,-1,-1, 1,1,-1};
        float[] mVertexLeft = new float[]{-1,1,1, -1,-1,1, -1,-1,-1, -1,1,-1};
        float[] mVertexTop = new float[]{1,1,1, 1,1,-1, -1,1,-1, -1,1,1};
        float[] mVertexBottom = new float[]{1,-1,1, 1,-1,-1, -1,-1,-1, -1,-1,1};

        mVertexs.clear();

        mVertexs.add(getFloatBuffer(mVertexFront));
        mVertexs.add(getFloatBuffer(mVertexBack));
        mVertexs.add(getFloatBuffer(mVertexRight));
        mVertexs.add(getFloatBuffer(mVertexLeft));
        mVertexs.add(getFloatBuffer(mVertexTop));
        mVertexs.add(getFloatBuffer(mVertexBottom));
    }

    private FloatBuffer getFloatBuffer(float[] array){
        ByteBuffer mByteBuffer = ByteBuffer.allocateDirect(array.length * Float.SIZE);
        mByteBuffer.order(ByteOrder.nativeOrder());

        FloatBuffer mFloatBuffer = mByteBuffer.asFloatBuffer();
        mFloatBuffer.put(array);
        mFloatBuffer.position(0);
        return mFloatBuffer;
    }

    private void drawCube(GL10 gl){
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        int i = 0;
        for(FloatBuffer floatBuffer : mVertexs){
            switch (i){
                case 0:
                    gl.glColor4f(1.0f,0.0f,0.0f,0.0f);
                    //break;
                case 1:
                    //gl.glColor4f(0.0f,1.0f,0.0f,0.0f);
                    break;
                case 2:
                    gl.glColor4f(0.0f,0.0f,1.0f,0.0f);
                    //break;
                case 3:
                    //gl.glColor4f(1.0f,1.0f,0.0f,0.0f);
                    break;
                case 4:
                    gl.glColor4f(0.0f,1.0f,0.0f,0.0f);
                    //break;
                case 5:
                    //gl.glColor4f(1.0f,0.0f,1.0f,0.0f);
                    break;
            }
            gl.glVertexPointer(3, GL10.GL_FLOAT,0,floatBuffer);
            gl.glDrawArrays(GL10.GL_LINE_STRIP,0,4);
            i++;
        }

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    class CustomRenderer implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            DebugUtil.d(this,"CustomRenderer::onSurfaceCreated");
            gl.glClearColor(1.0f,1.0f,1.0f,1.0f);

            gl.glShadeModel(GL10.GL_SMOOTH);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.d(TAG,"CustomRenderer::onSurfaceChanged");
            gl.glViewport(0,0,width,height);

            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            GLU.gluPerspective(gl,60,width/height,0.1f,9f);

            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //Log.d(TAG,"CustomRenderer::onDrawFrame");
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();

            gl.glColor4f(0,0,1.0f,1.0f);
            GLU.gluLookAt(gl,0.0f,0.0f,3.0f,0.0f,0.0f,0.0f,0.0f,1.0f,0.0f);

            drawCube(gl);
        }
    }
}
