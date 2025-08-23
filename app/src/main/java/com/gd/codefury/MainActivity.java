package com.gd.codefury;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Login Button
        Button login = findViewById(R.id.login); // make sure in XML it's "login"
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
            }
        });

        // Signup Button
        Button signIn = findViewById(R.id.signup); // <-- should match your signup button ID in XML
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
