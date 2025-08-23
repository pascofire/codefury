package com.gd.codefury;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadImage extends AppCompatActivity {

    private EditText Title;
    private EditText Description;
    private EditText link;
    private CheckBox artwork;
    private CheckBox product;
    private Button publish;

    private Uri imageUri; // You will get this from gallery/camera intent
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_upload_image);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);

        ImageButton backButton = findViewById(R.id.backbutton);
        backButton.setOnClickListener(v -> {
            // Go back to UserProfile activity
            Intent intent1 = new Intent(UploadImage.this, UserProfile.class);
            startActivity(intent1);
            finish(); // optional, if you want to remove UploadImage from back stack
        });
        Title = findViewById(R.id.titlefield);
        Description = findViewById(R.id.description);
        link = findViewById(R.id.link);
        artwork = findViewById(R.id.artwork);
        product = findViewById(R.id.product);
        publish = findViewById(R.id.publishArtwork);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        publish.setOnClickListener(v -> uploadImageToFirestore());
    }

    private void uploadImageToFirestore() {
        if (imageUri == null) {
            Toast.makeText(this, "Select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String titleText = Title.getText().toString().trim();
        String descriptionText = Description.getText().toString().trim();
        String linkText = link.getText().toString().trim();

        boolean isArtwork = artwork.isChecked();
        boolean isProduct = product.isChecked();

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference fileRef = storageRef.child(userId + "/" + fileName);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            // Store metadata in Firestore
                            Map<String, Object> imageData = new HashMap<>();
                            imageData.put("title", titleText);
                            imageData.put("description", descriptionText);
                            imageData.put("link", linkText);
                            imageData.put("isArtwork", isArtwork);
                            imageData.put("isProduct", isProduct);
                            imageData.put("imageUrl", downloadUri.toString());
                            imageData.put("timestamp", System.currentTimeMillis());

                            db.collection("users")
                                    .document(userId)
                                    .collection("images")
                                    .add(imageData)
                                    .addOnSuccessListener(documentReference -> {
                                        pd.dismiss();
                                        checkImageCount(userId);
                                        Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        pd.dismiss();
                                        Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                )
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(this, "Storage error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkImageCount(String userId) {
        db.collection("users")
                .document(userId)
                .collection("images")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    if (count == 3) {
                        showVerifiedArtistPopup();
                    }
                });
    }

    private void showVerifiedArtistPopup() {
        new AlertDialog.Builder(this)
                .setTitle("Congratulations!")
                .setMessage("You've uploaded 3 artworks! You're now a verified artist.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // You need to add code to open gallery and set imageUri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }
}
