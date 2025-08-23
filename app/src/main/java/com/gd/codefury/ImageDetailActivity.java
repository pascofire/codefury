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
        setContentView(R.layout.image_adapter_view); // ✅ reuse your layout

        // Init views
        imageView = findViewById(R.id.image_item_view);
        title = findViewById(R.id.title_ia);
        description = findViewById(R.id.description_ia);
        link = findViewById(R.id.link_ia);
        backButton = findViewById(R.id.backbutton);

        // Get data from intent
        String base64Image = getIntent().getStringExtra("imageBase64"); // ✅ now Base64
        String titleText = getIntent().getStringExtra("title");
        String descriptionText = getIntent().getStringExtra("description");
        String linkUrl = getIntent().getStringExtra("link");

        // Decode Base64 → Bitmap
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageView.setImageBitmap(bitmap);
        }

        // Set text
        title.setText(titleText);
        description.setText(descriptionText);
        link.setText(linkUrl);

        // Open link in browser
        link.setOnClickListener(v -> {
            if (linkUrl != null && !linkUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
                startActivity(browserIntent);
            }
        });

        // Back button → finish
        backButton.setOnClickListener(v -> finish());
    }
}
