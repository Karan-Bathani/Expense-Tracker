package com.example.expensestracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.expensestracker.databinding.ActivityAddExpenseBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

public class AddExpenseActivity extends AppCompatActivity {

    ActivityAddExpenseBinding binding;
    private int count;
    private String description, value;
    private DatabaseReference mDatabase;
    private FirebaseUser current_user;
    private String uid;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_expense);

        setTitle(getString(R.string.add_expense));

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        if (current_user != null) {
            uid = current_user.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child(getString(R.string.users)).child(uid);
        }

        mDatabase.child(getString(R.string.expenses)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                counter = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (isNetworkConnected()) {
            new FetchImageAsync().execute();
            Random random = new Random();

            final String image;
            image = "https://picsum.photos/600?image=" + random.nextInt(1000 + 1);

            Glide.with(AddExpenseActivity.this).load(image)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(binding.imageView);
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public void addExpense(View view) {

        description = binding.etDescription.getText().toString().trim();
        value = binding.etValue.getText().toString().trim();

        count = 0;
        checkValidation(description, value);

        if (count == 0) {
            HashMap<String, String> expenseMap = new HashMap<>();
            expenseMap.put(getString(R.string.description_low), description);
            expenseMap.put(getString(R.string.value_low), value);

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            mDatabase.child(getString(R.string.expenses)).child(String.valueOf(++counter)).setValue(expenseMap);
            mDatabase.child(getString(R.string.count)).setValue(counter);

            Toast.makeText(this, R.string.added, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }

    private void checkValidation(String description, String value) {

        if (TextUtils.isEmpty(description)) {
            binding.etDescription.setError(getString(R.string.add_description));
            count++;
        }
        if (TextUtils.isEmpty(value)) {
            binding.etValue.setError(getString(R.string.add_value));
            count++;
        }
    }

    public class FetchImageAsync extends AsyncTask<String, Void, String> {

        public FetchImageAsync() {
        }

        @Override
        protected String doInBackground(String... strings) {


            return null;
        }


    }

    public boolean isNetworkConnected() {
        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();

                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            } else {
                final Network n = cm.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
                }
            }
        }

        return false;
    }

}