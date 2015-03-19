package de.eatgate.placessearch.deprecated;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import de.eatgate.placessearch.R;
import de.eatgate.placessearch.global.AppGob;

public class CameraActivity extends Activity
        implements SurfaceHolder.Callback,
        Camera.PictureCallback {

    private Camera camera;
    private Camera.PictureCallback cameraCallbackVorschau;
    private Camera.ShutterCallback cameraCallbackVerschluss;
    private SurfaceHolder cameraViewHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            camera.takePicture(this.cameraCallbackVerschluss,
                    this.cameraCallbackVorschau, this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.stopPreview();
        camera.setDisplayOrientation(90);
        Camera.Parameters params = camera.getParameters();
        Camera.Size vorschauGroesse = params.getPreviewSize();
        params.setPreviewSize(vorschauGroesse.width,
                vorschauGroesse.height);
        camera.setParameters(params);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d("surfaceChanged", e.getMessage());
        }
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        // TODO Auto-generated method stub

  /*Bitmap bitmapPicture

   = BitmapFactory.decodeByteArray(arg0, 0, arg0.length); */
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getPackageName());
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e("onPictureTaken", "Failed to create storage directory.");
                return;
            }
        }

        Uri uriTarget = getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());


        OutputStream imageFileOS;

        try {

            // imageFileOS = getContentResolver().openOutputStream(uriTarget);
            imageFileOS = openFileOutput(directory.toString() + "test.jpg",
                    Context.MODE_PRIVATE);
            imageFileOS.write(data);

            imageFileOS.flush();

            imageFileOS.close();


            Toast.makeText(CameraActivity.this,

                    "Image saved: " + uriTarget.toString(),

                    Toast.LENGTH_LONG).show();


        } catch (FileNotFoundException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        } catch (IOException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }


        camera.startPreview();

    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        AppGob app = (AppGob) getApplication();
        String photoPath = app.mCurrentPhotoPath;
        if (photoPath.isEmpty() || photoPath == null) {
            Log.e("GalleryAddPic: ", "Photopath is empty");
        }
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
    }

    protected void onResume() {
        super.onResume();
        SurfaceView cameraView =
                (SurfaceView) findViewById(R.id.surfaceview1);
        cameraViewHolder = cameraView.getHolder();
        cameraViewHolder.addCallback(this);
// setType() ist ab Android 4.x überfl üssig und wird ignoriert
        cameraViewHolder.setType(
                SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraCallbackVorschau = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera c) {
            }
        };
        cameraCallbackVerschluss = new Camera.ShutterCallback() {
            public void onShutter() {
            }
        };
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            camera.takePicture(this.cameraCallbackVerschluss,
                    this.cameraCallbackVorschau, this);

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }
}
