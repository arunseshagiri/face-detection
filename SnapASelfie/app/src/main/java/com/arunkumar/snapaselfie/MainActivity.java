package com.arunkumar.snapaselfie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;
import com.himanshurawat.imageworker.Extension;
import com.himanshurawat.imageworker.ImageWorker;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements UpdateUIFaceDetection, View.OnClickListener {

    private static final int REQUEST_PERMISSION = 1;

    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private View faceFrame;
    private Button takePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.preview);
        faceFrame = findViewById(R.id.face_frame);
        takePicture = findViewById(R.id.take_picture);
        takePicture.setOnClickListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            this,
                            new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            REQUEST_PERMISSION
                    );
        } else {
            createCameraSource();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PERMISSION && resultCode == RESULT_OK) {
            createCameraSource();
        } else {
            Toast.makeText(this, "Permission needed to proceed", Toast.LENGTH_LONG).show();
        }
    }

    private void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(new MultiProcessor.Builder<>(new FaceTrackerFactory(this)).build());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Log.d("", "**** Metrics Preview width and height=" + width + " " + height);
        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(width, height)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    private void startCameraSource() {
        if (cameraSource != null) {
            surfaceView.getHolder().addCallback(
                    new SurfaceHolder.Callback() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void surfaceCreated(SurfaceHolder surfaceHolder) {
                            try {
                                cameraSource.start(surfaceView.getHolder());
                            } catch (IOException e) {
                                Log.e("", "Exception in capturing preview: " + e.getMessage());
                            }
                        }

                        @Override
                        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                        }

                        @Override
                        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                        }
                    }
            );
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraSource.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
    }

    @Override
    public void onFaceDetected(boolean faceCaptured) {
        if (faceCaptured) {
            takePicture.setEnabled(true);
            faceFrame.setBackground(ContextCompat.getDrawable(this, R.drawable.frame_face_detected));
        } else {
            takePicture.setEnabled(false);
            faceFrame.setBackground(ContextCompat.getDrawable(this, R.drawable.frame_face));
        }
    }

    @Override
    public void onClick(View view) {
        cameraSource.takePicture(null, new CameraSource.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] byteArr) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
                ImageWorker
                        .Companion.to(getApplicationContext())
                        .setFileName("capture_image")
                        .withExtension(Extension.PNG)
                        .save(bitmap, 85);

                startActivity(new Intent(getApplicationContext(), ShowCaptureActivity.class));
            }
        });
    }
}
