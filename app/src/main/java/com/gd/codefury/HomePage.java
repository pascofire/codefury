package com.gd.codefury;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class HomePage extends AppCompatActivity {

    private RecyclerView homeRecyclerView;
    private ImageButton userprofilepicture;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> imageList;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        userprofilepicture=findViewById(R.id.userprofilepicture);
        userprofilepicture.setOnClickListener(v -> {
            startActivity(new Intent(HomePage.this, UserProfile.class));

        });

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Init Firestore
        db = FirebaseFirestore.getInstance();

        // Init RecyclerView
        homeRecyclerView = findViewById(R.id.homeRecyclerView);
        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageList, this);

        StaggeredGridLayoutManager layoutManager =
         new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        homeRecyclerView.setLayoutManager(layoutManager);
        homeRecyclerView.setAdapter(imageAdapter);

        // Fetch random images
        fetchRandomImages();
    }

    private void fetchRandomImages() {
        // assuming all users have "images" subcollection
        db.collectionGroup("images")  //  collectionGroup fetches from all users' "images"
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    imageList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Image item = doc.toObject(Image.class);
                        imageList.add(item);
                    }

                    // Shuffle to get random order
                    Collections.shuffle(imageList);

                    imageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(HomePage.this, "Failed to load images: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}
