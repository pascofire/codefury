package com.gd.codefury;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    private TextView username;
    private ImageButton backButton;
    private RecyclerView imageRecyclerView;
    private Button upload;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ArrayList<Image> imageList;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
        username = findViewById(R.id.username);
        backButton = findViewById(R.id.backbutton_up);
        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        upload = findViewById(R.id.upload);

        // Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        // Upload button → open upload screen
        upload.setOnClickListener(v -> {
            startActivity(new Intent(UserProfile.this, UploadImage.class));
            finish();
        });

        // Back button → return to homepage
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(UserProfile.this, HomePage.class));
            finish();
        });

        // Setup RecyclerView
        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageList, this);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        imageRecyclerView.setLayoutManager(layoutManager);
        imageRecyclerView.setAdapter(imageAdapter);

        // Fetch images
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
                    imageList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Image item = new Image(
                                doc.getString("imageUrl"),
                                doc.getString("title"),
                                doc.getString("description"),
                                doc.getString("link"),
                                doc.getString("user")
                        );
                        imageList.add(item);
                    }
                    imageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(UserProfile.this, "Failed to load images: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
