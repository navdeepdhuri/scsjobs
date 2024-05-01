package com.scsjobspunjab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import androidx.fragment.app.Fragment;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.Continue;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity {

    ImageView logout;
    ImageView whatsappchat;
    private TextView welcomeMessage;
    private ProgressBar progressBar;
    private RecyclerView jobsRecyclerView;
    private JobsAdapter jobsAdapter;
    private static final String ONESIGNAL_APP_ID = "34e61b9b-1eab-4fbf-87c2-cfd7b5c4f2f9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });
        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);

        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
        OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
            if (r.isSuccess()) {
                if (r.getData()) {
                    // `requestPermission` completed successfully and the user has accepted permission
                } else {
                    // `requestPermission` completed successfully but the user has rejected permission
                }
            } else {
                // `requestPermission` completed unsuccessfully, check `r.getThrowable()` for more info on the failure reason
            }
        }));

        initViewElements();
        authenticateUserAndFetchData();
        setupLogout();
        setupWhatsAppChat();
        fetchJobs();
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Check if the intent contains a OneSignal notification
        if (intent.hasExtra("onesignalData")) {
            // Handle the notification
            // For example, you can extract data and perform appropriate actions
            Bundle dataBundle = intent.getBundleExtra("onesignalData");
            if (dataBundle != null) {
                String customKey = dataBundle.getString("key");
                if (customKey != null) {
                    // Handle custom key
                }
            }
        }
    }


    private void initViewElements() {
        welcomeMessage = findViewById(R.id.welcomeMessage);
        logout = findViewById(R.id.logoutbutton);
        whatsappchat = findViewById(R.id.whatsapp);
        progressBar = findViewById(R.id.progressBar);
        jobsRecyclerView = findViewById(R.id.jobs_recycler_view);
    }

    private void authenticateUserAndFetchData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            progressBar.setVisibility(View.VISIBLE);
            String email = currentUser.getEmail();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            welcomeMessage.setText("Welcome " + user.getName());
                            setupRecyclerView(user.getName());
                        }
                    } else {
                        welcomeMessage.setText("Welcome TO SCS Jobs");
                        setupRecyclerView("Guest");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainPageActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            welcomeMessage.setText("Welcome Guest");
            setupRecyclerView("Guest");
        }

    }

    private void setupLogout() {
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupWhatsAppChat() {
        whatsappchat.setOnClickListener(v -> {
            // WhatsApp chat code or any necessary handling
        });
    }

    private void setupRecyclerView(String userName) {
        jobsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobsAdapter = new JobsAdapter(new ArrayList<>(), this, userName);
        jobsRecyclerView.setAdapter(jobsAdapter);
    }

    private void fetchJobs() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference jobsRef = database.getReference("jobs");

        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Job> jobs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Job job = snapshot.getValue(Job.class);
                    jobs.add(job);
                }
                jobsAdapter.setJobs(jobs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainPageActivity.this, "Failed to load jobs.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}