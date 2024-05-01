package com.scsjobspunjab;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JobListActivity extends AppCompatActivity {

    private RecyclerView jobsRecyclerView;
    private JobsAdapter jobsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page); // Replace with your layout that contains the RecyclerView

        jobsRecyclerView = findViewById(R.id.jobs_recycler_view);
        jobsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobsAdapter = new JobsAdapter(new ArrayList<>(), this, "Welcome to JobListActivity"); // Initialize with an empty list and welcome message
        jobsRecyclerView.setAdapter(jobsAdapter);

        fetchJobsFromFirebase();
    }

    private void fetchJobsFromFirebase() {
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
                // Update the adapter with the new job list
                jobsAdapter.setJobs(jobs);
                jobsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(JobListActivity.this, "Failed to load jobs.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
