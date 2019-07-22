package com.arunkumar.snapaselfie;


import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

public class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

    private UpdateUIFaceDetection detection;

    public FaceTrackerFactory(UpdateUIFaceDetection detection) {
        this.detection = detection;
    }
    @Override
    public Tracker<Face> create(Face face) {
        return new FaceTracker(detection);
    }
}
