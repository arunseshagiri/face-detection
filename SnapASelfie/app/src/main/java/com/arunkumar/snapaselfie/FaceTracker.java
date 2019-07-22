package com.arunkumar.snapaselfie;

import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class FaceTracker extends Tracker<Face> {

    public FaceTracker() {
    }

    @Override
    public void onNewItem(int faceId, Face face) {
        Log.d("", "onNewItem. Face Id => " + faceId);
    }

    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
    }

    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {

    }

    @Override
    public void onDone() {

    }
}
