package com.gd.codefury;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ImageDetailActivity extends AppCompatActivity {

        private ImageView imageView;
        private TextView title, description, link;
        private ImageButton backButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.image_adapter_view); // reuse your layout

            // Init views
            imageView = findViewById(R.id.image_item_view);
            title = findViewById(R.id.title_ia);
            description = findViewById(R.id.description_ia);
            link = findViewById(R.id.link_ia);
            backButton = findViewById(R.id.backbutton);

            // Get data from intent
            String imageUrl = getIntent().getStringExtra("imageUrl");
            String titleText = getIntent().getStringExtra("title");
            String descriptionText = getIntent().getStringExtra("description");
            String linkUrl = getIntent().getStringExtra("link");

            // Set data
            Glide.with(this).load(imageUrl).into(imageView);
            title.setText(titleText);
            description.setText(descriptionText);
            link.setText(linkUrl);

            // Open link in browser
            link.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
                startActivity(browserIntent);
            });

            // Back button → finish
            backButton.setOnClickListener(v -> finish());
        }
    }


