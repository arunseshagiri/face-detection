package com.arunkumar.snapaselfie;

import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class FaceTracker extends Tracker<Face> {

    private UpdateUIFaceDetection detection;

    public FaceTracker(UpdateUIFaceDetection detection) {
        this.detection = detection;
    }

    @Override
    public void onNewItem(int faceId, Face face) {
        Log.d("", "onNewItem. Face Id => " + faceId);
    }

    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        if (face.getWidth() > 900 && face.getWidth() < 1000) {
            detection.onFaceDetected(true);
        } else {
            detection.onFaceDetected(false);
        }
    }

    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        detection.onFaceDetected(false);
    }

    @Override
    public void onDone() {

    }
}
