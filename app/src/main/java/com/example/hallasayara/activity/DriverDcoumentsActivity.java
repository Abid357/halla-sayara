package com.example.hallasayara.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hallasayara.BuildConfig;
import com.example.hallasayara.R;
import com.example.hallasayara.database.User;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.io.IOException;
import java.util.UUID;

public class DriverDcoumentsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Documents";
    private static String[] DOCUMENT_TYPES = new String[]{"National ID Card", "Driver's License", "Vehicle Registration", "Vehicle Plate Number"};

    //Declaring views
    private Button selectButton;
    private Button uploadButton;
    private ImageView image;
    private Spinner spinner;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_upload_document);

        //Requesting storage permission
        requestStoragePermission();


        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        //Initializing views
        selectButton = (Button) findViewById(R.id.select_document_button);
        uploadButton = (Button) findViewById(R.id.upload_document_button);
        image = (ImageView) findViewById(R.id.document_image_view);
        spinner = (Spinner) findViewById(R.id.document_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, DOCUMENT_TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Setting clicklistener
        selectButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
    }

    /*
     * This is the method responsible for image upload
     * We need the full image path and the name for the image in this method
     * */
    public void uploadMultipart() {
        //getting name for the image
        String caption = spinner.getSelectedItem().toString();

        //getting the actual path of the image
        String path = getPath(filePath);

        //current user ID
        SharedPreferences sp = getSharedPreferences("com.example.hallasayara", MODE_PRIVATE);
        int id = sp.getInt("id", -1);

        Log.d(TAG, "uploadMultipart(): caption=" + caption);
        Log.d(TAG, "uploadMultipart(): id=" + Integer.toString(id));

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            MultipartUploadRequest uploadRequest = new MultipartUploadRequest(this, uploadId, User.PHP_UPLOAD_IMAGE)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", caption) //Adding text parameter to the request
                    .addParameter("user_id", Integer.toString(id))
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                            Log.d("TEST", serverResponse.getBodyAsString());
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            Log.d("TEST", serverResponse.getBodyAsString());
                            Toast.makeText(getApplicationContext(), "Document uploaded.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    });

            // For Android > 8, we need to set an Channel to the UploadNotificationConfig.
            // So, here, we create the channel and set it to the MultipartUploadRequest
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel("Upload", "Upload service", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);

                UploadNotificationConfig notificationConfig = new UploadNotificationConfig();
                notificationConfig.setNotificationChannelId("Upload");

                uploadRequest.setNotificationConfig(notificationConfig);
            } else {
                // If android < Oreo, just set a simple notification (or remove if you don't wanna any notification
                // Notification is mandatory for Android > 8
                uploadRequest.setNotificationConfig(new UploadNotificationConfig());
            }
            uploadRequest.startUpload();


        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == selectButton) {
            showFileChooser();
        }
        if (v == uploadButton) {
            uploadMultipart();
        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}
