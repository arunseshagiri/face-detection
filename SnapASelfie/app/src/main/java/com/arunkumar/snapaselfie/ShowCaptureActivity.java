package com.arunkumar.snapaselfie;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.himanshurawat.imageworker.Extension;
import com.himanshurawat.imageworker.ImageWorker;

public class ShowCaptureActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_capture);

        imageView = findViewById(R.id.image_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bitmap bitmap = ImageWorker
                .Companion.from(getApplicationContext())
                .setFileName("capture_image")
                .withExtension(Extension.PNG)
                .load();
        imageView.setImageBitmap(bitmap);
    }
}
