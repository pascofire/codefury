package com.gd.codefury;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ImageDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView title, description, link;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.detail_image);
        title = findViewById(R.id.detail_title);
        description = findViewById(R.id.detail_description);
        link = findViewById(R.id.detail_link);
        backButton = findViewById(R.id.detail_backbutton);

        String base64Image = getIntent().getStringExtra("imageBase64");
        String titleText = getIntent().getStringExtra("title");
        String descriptionText = getIntent().getStringExtra("description");
        String linkUrl = getIntent().getStringExtra("link");

        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageView.setImageBitmap(bitmap);
        }

        title.setText(titleText);
        description.setText(descriptionText);
        link.setText(linkUrl);

        backButton.setOnClickListener(v -> finish());

        link.setOnClickListener(v -> {
            if (linkUrl != null && !linkUrl.isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl)));
            }
        });
    }
}

