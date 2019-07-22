package com.arunkumar.snapaselfie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements UpdateUIFaceDetection {

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private View faceFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.preview);
        faceFrame = findViewById(R.id.face_frame);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
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
        if (requestCode == REQUEST_CAMERA_PERMISSION && resultCode == RESULT_OK) {
            createCameraSource();
        }
    }

    private void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(new MultiProcessor.Builder<>(new FaceTrackerFactory(this)).build());

        cameraSource = new CameraSource.Builder(this, detector)
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
            faceFrame.setBackground(ContextCompat.getDrawable(this, R.drawable.frame_face_detected));
        } else {
            faceFrame.setBackground(ContextCompat.getDrawable(this, R.drawable.frame_face));
        }
    }
}
