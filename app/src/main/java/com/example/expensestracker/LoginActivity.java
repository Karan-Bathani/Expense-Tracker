package com.example.expensestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.expensestracker.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private String emailid, password;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        String temp = getIntent().getStringExtra(getString(R.string.email));
        if (temp != null) {
            binding.etEmail.setText(temp);
        }

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

    }

    public void signupClick(View view) {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
    }

    public void loginClick(View view) {
        emailid = binding.etEmail.getText().toString().trim();
        password = binding.etPassword.getText().toString().trim();

        count = 0;
        checkValidation(emailid, password);

        if (count == 0) {
            login_user(emailid, password);
        }
    }

    private void checkValidation(final String emailid, final String password) {

        if (TextUtils.isEmpty(emailid)) {
            binding.etEmail.setError(getString(R.string.enter_email));
            count++;
        } else if (!emailid.matches("[a-zA-Z0-9._]+@[a-z]+\\.[a-z]+")) {
            binding.etEmail.setError(getString(R.string.invalid_email));
            count++;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
            count++;
        } else if (password.length() < 6) {
            Toast.makeText(LoginActivity.this, R.string.password_too_short, Toast.LENGTH_SHORT).show();
            count++;
        }

    }

    private void login_user(final String emailid, final String password) {

        mAuth.signInWithEmailAndPassword(emailid, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    if (count == 0) {
                        Toast.makeText(LoginActivity.this, R.string.login_successful, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, R.string.authentication_failed, Toast.LENGTH_LONG).show();
                }

            }
        });

    }


}
