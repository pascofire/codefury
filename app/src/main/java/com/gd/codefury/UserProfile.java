package com.gd.codefury;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    private EditText username;
    private ImageButton backButton;
    private RecyclerView imageRecyclerView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private ArrayList<String> imageUrls;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
        username = findViewById(R.id.username);
        backButton = findViewById(R.id.backbutton_up);
        imageRecyclerView = findViewById(R.id.imageRecyclerView);

        // Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Show user email
        if (mAuth.getCurrentUser() != null) {
            username.setText(mAuth.getCurrentUser().getEmail());
        }

        // Back button functionality
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(UserProfile.this, HomePage.class));
            finish();
        });

        // Setup RecyclerView with Staggered Grid
        imageUrls = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUrls, this);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        imageRecyclerView.setLayoutManager(layoutManager);
        imageRecyclerView.setAdapter(imageAdapter);

        // Fetch images from Firestore
        fetchUserImages();
    }

    private void fetchUserImages() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("images")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    imageUrls.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String url = doc.getString("imageUrl");
                        if (url != null) imageUrls.add(url);
                    }
                    imageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(UserProfile.this, "Failed to load images: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
