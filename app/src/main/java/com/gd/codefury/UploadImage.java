package com.gd.codefury;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadImage extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 100;

    private ImageView image;
    private EditText titleField, descriptionField, linkField;
    private CheckBox artworkCheck, productCheck;
    private Button publishBtn;
    private ImageButton backButton;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        // Match XML IDs
        image = findViewById(R.id.image);
        titleField = findViewById(R.id.titlefield);
        descriptionField = findViewById(R.id.description);
        linkField = findViewById(R.id.link);
        artworkCheck = findViewById(R.id.artwork);
        productCheck = findViewById(R.id.product);
        publishBtn = findViewById(R.id.publishArtwork);
        backButton = findViewById(R.id.backbutton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Back button → finish activity
        backButton.setOnClickListener(v -> finish());

        // Pick image on ImageView click
        image.setOnClickListener(v -> openFileChooser());

        // Upload on publish
        publishBtn.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToFirestore();
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }

    private void uploadImageToFirestore() {
        try {
            // Convert image to Bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // ✅ Step 1: Resize (scale down to max 800x800)
            int maxWidth = 800;
            int maxHeight = 800;
            bitmap = Bitmap.createScaledBitmap(bitmap, maxWidth, maxHeight, true);

            // ✅ Step 2: Compress to JPEG with lower quality (60%)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] imageBytes = baos.toByteArray();

            // ✅ Step 3: Convert to Base64 string
            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            // Collect form values
            String title = titleField.getText().toString().trim();
            String description = descriptionField.getText().toString().trim();
            String link = linkField.getText().toString().trim();
            boolean isArtwork = artworkCheck.isChecked();
            boolean isProduct = productCheck.isChecked();

            // Save to Firestore
            String userId = mAuth.getCurrentUser().getUid();

            Map<String, Object> imageMap = new HashMap<>();
            imageMap.put("image", base64Image);
            imageMap.put("title", title);
            imageMap.put("description", description);
            imageMap.put("link", link);
            imageMap.put("isArtwork", isArtwork);
            imageMap.put("isProduct", isProduct);
            imageMap.put("timestamp", System.currentTimeMillis());

            db.collection("users")
                    .document(userId)
                    .collection("images")
                    .add(imageMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

}

