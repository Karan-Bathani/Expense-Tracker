package com.example.expensestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.expensestracker.databinding.ActivitySignupBinding;
import com.example.expensestracker.databinding.ActivityStartBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.HttpCookie;
import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "ExpensesTracker";
    private String emailid, password, name;
    private ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    private int count;
    private FirebaseUser current_user;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
    }

    public void loginClick(View view) {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }

    public void signupClick(View view) {

        name = binding.etName.getText().toString().trim();
        emailid = binding.etEmail.getText().toString().trim();
        password = binding.etPassword.getText().toString().trim();

        count = 0;
        checkValidation(emailid, password, name);

        if (count == 0) {
            signin_user(emailid, password, name);
        }
    }

    private void signin_user(final String emailid, String password, final String name) {
        mAuth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    current_user = mAuth.getCurrentUser();
                    String uid;
                    if (current_user != null) {
                        uid = current_user.getUid();
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    }

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("username", name);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "SignupActivity: Data Uploaded");
                        }
                    });

                    mDatabase.child("count").setValue(0);

                    Toast.makeText(SignupActivity.this, getString(R.string.successful), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class).putExtra(getString(R.string.email), emailid));
                    finish();

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(SignupActivity.this, R.string.signup_failed, Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    private void checkValidation(final String emailid, final String password, String name) {

        if (TextUtils.isEmpty(emailid)) {
            binding.etEmail.setError(getString(R.string.enter_email));
            count++;
        } else if (!emailid.matches("[a-zA-Z0-9._]+@[a-z]+\\.[a-z]+")) {
            binding.etEmail.setError(getString(R.string.invalid_email));
            count++;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignupActivity.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
            count++;
        } else if (password.length() < 6) {
            Toast.makeText(SignupActivity.this, R.string.password_too_short, Toast.LENGTH_SHORT).show();
            count++;
        }
        if (TextUtils.isEmpty(name)){
            binding.etName.setError(getString(R.string.enter_email));
            count++;
        }

    }
}