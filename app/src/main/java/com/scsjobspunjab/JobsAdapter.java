package com.scsjobspunjab;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.JobViewHolder> {

    private List<Job> jobsList;
    private Context context;
    private String userName;


    public static class JobViewHolder extends RecyclerView.ViewHolder {
        public TextView company, title, description, location, salary;

        public JobViewHolder(View v) {
            super(v);
            company = v.findViewById(R.id.job_company);
            title = v.findViewById(R.id.job_title);
            description = v.findViewById(R.id.job_description);
            location = v.findViewById(R.id.job_location);
            salary = v.findViewById(R.id.job_salary);
        }
    }

    public JobsAdapter(List<Job> jobsList, Context context, String userName) {
        this.jobsList = jobsList;
        this.context = context;
        this.userName = userName;
    }

    // Method to update the jobs list and notify the adapter of the change
    public void setJobs(List<Job> jobs) {
        this.jobsList = jobs;
        notifyDataSetChanged(); // Notify any registered observers that the data set has changed.
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(JobViewHolder holder, int position) {
        Job job = jobsList.get(position);
        holder.company.setText(job.getCompany());
        holder.title.setText(job.getTitle());
        holder.description.setText(job.getDescription());
        holder.location.setText(job.getLocation());
        holder.salary.setText(job.getSalary());

        // Set OnClickListener to handle click events on TextViews
        holder.company.setOnClickListener(v -> openWhatsAppWithDetails(job));
        holder.title.setOnClickListener(v -> openWhatsAppWithDetails(job));
        holder.description.setOnClickListener(v -> openWhatsAppWithDetails(job));
        holder.location.setOnClickListener(v -> openWhatsAppWithDetails(job));
        holder.salary.setOnClickListener(v -> openWhatsAppWithDetails(job));
    }

    // Method to open WhatsApp with job details
    private void openWhatsAppWithDetails(Job job) {
        String phoneNumber = "918054020198";
        String message = "Hi, my name is " + userName + ". I want to know about this job:\n" +
                "Company: " + job.getCompany() + "\n" +
                "Title: " + job.getTitle() + "\n" +
                "Description: " + job.getDescription() + "\n" +
                "Location: " + job.getLocation() + "\n" +
                "Salary: " + job.getSalary(); // Construct the message

        // Create a URI for WhatsApp using the phone number
        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(message));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        // Check if WhatsApp or WhatsApp Business is installed and direct the intent to the appropriate app
        boolean whatsappInstalled = isPackageInstalled("com.whatsapp");
        boolean whatsappBusinessInstalled = isPackageInstalled("com.whatsapp.w4b");

        if (whatsappInstalled || whatsappBusinessInstalled) {
            if (whatsappInstalled) {
                intent.setPackage("com.whatsapp");
            } else {
                intent.setPackage("com.whatsapp.w4b");
            }

            context.startActivity(intent);
        } else {
            // Notify the user if neither WhatsApp nor WhatsApp Business is installed
            Toast.makeText(context, "WhatsApp is not installed on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to check if WhatsApp or WhatsApp Business is installed
    private boolean isPackageInstalled(String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return jobsList != null ? jobsList.size() : 0;
    }
}
