package com.gd.codefury; // Assuming your package name

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonSignup;
    private ProgressBar progressBarSignup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // Optional: if you want edge-to-edge display
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextSignupEmail);
        editTextPassword = findViewById(R.id.editTextSignupPassword);
        editTextConfirmPassword = findViewById(R.id.editTextSignupConfirmPassword);
        buttonSignup = findViewById(R.id.buttonSignup);
        progressBarSignup = findViewById(R.id.progressBarSignup);

        // Optional: Apply window insets if using EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_signup_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.email_required_error)); // Use string resource
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.invalid_email_error)); // Use string resource
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(getString(R.string.password_required_error)); // Use string resource
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.password_too_short_error)); // Use string resource
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.setError(getString(R.string.confirm_password_error)); // Use string resource
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError(getString(R.string.password_mismatch_error)); // Use string resource
            editTextConfirmPassword.requestFocus();
            // Clear the password fields if they don't match for better UX
            editTextPassword.setText("");
            editTextConfirmPassword.setText("");
            return;
        }

        progressBarSignup.setVisibility(View.VISIBLE);
        buttonSignup.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBarSignup.setVisibility(View.GONE);
                        buttonSignup.setEnabled(true);

                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignupActivity.this, getString(R.string.signup_successful_toast) + (user != null ? user.getEmail() : ""), Toast.LENGTH_LONG).show();

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, getString(R.string.signup_failed_toast) + ": " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
