package de.eatgate.placessearch.deprecated;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.eatgate.placessearch.R;

public class MyCameraActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_TAKE_PHOTO = 1;

    private ImageView imageView;
    private String mCurrentPhotoPath;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);
        this.imageView = (ImageView) findViewById(R.id.imageView);
        Button photoButton = (Button) findViewById(R.id.buttonU);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                dispatchTakePictureIntent();
                //startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            galleryAddPic();
        /*    try {
                // bimatp factory
                BitmapFactory.Options options = new BitmapFactory.Options();
                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;
                final Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath,
                        options);
                imageView.setImageBitmap(bitmap);
                Toast.makeText(getBaseContext(),mCurrentPhotoPath, Toast.LENGTH_LONG).show();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }*/
            // Bitmap photo = (Bitmap) data.getExtras().get("data");
            // TextView txtFilePath = (TextView) findViewById(R.id.editTextU);
            // txtFilePath.setText(data.getData().getPath());
            // imageView.setImageBitmap(photo);
            // galleryAddPic();
        }
    }

    /**
     * Startet den Intent zum Aufnehmen des Bildes via Handy Camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                // erzeugt leere File im Pfad
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                // der Intent zur Aufnahme des Fotos bekommt die Uri von createImageFile() fuer
                // Pfad und Dateiname
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                // ruft die OnActivityResult auf
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    /**
     * Hier wird Verzeichnis und Name fuer die Bilddatei erzeugt
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
        // Verzeichnis fuer das Bild
        //   File storageDir = Environment.getDataDirectory();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        storageDir.mkdirs();
        //new File(storageDir,imageFileName+".jpg");
        // File imageFile = File.createTempFile(
        //      imageFileName,  /* prefix */
        //        ".jpg",         /* suffix */
        //       storageDir      /* directory */
        //  );
        File imageFile = new File(storageDir, imageFileName);
        Toast.makeText(getBaseContext(), imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    /**
     * Speichert das Bild auf dem Handy unter mCurrentPhotoPath
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}