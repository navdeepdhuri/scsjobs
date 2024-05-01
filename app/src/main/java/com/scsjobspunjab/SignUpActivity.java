package com.scsjobspunjab;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements PaymentResultListener {

    private EditText nameEditText, emailEditText, passwordEditText, mobileEditText;
    private Button signupButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        nameEditText = findViewById(R.id.signupnameEditText);
        emailEditText = findViewById(R.id.signupmailEditText);
        passwordEditText = findViewById(R.id.signupPasswordEditText);
        mobileEditText = findViewById(R.id.signupmobileEditText);
        signupButton = findViewById(R.id.signupButton);
        welcomeMessage = findViewById(R.id.welcomeMessage);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    startPayment();
                }
            }
        });
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(nameEditText.getText())) {
            nameEditText.setError("Please enter your name");
            return false;
        }
        if (TextUtils.isEmpty(emailEditText.getText())) {
            emailEditText.setError("Please enter email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches()) {
            emailEditText.setError("Please enter a valid email address");
            return false;
        }
        if (TextUtils.isEmpty(passwordEditText.getText()) || passwordEditText.getText().length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters long");
            return false;
        }
        if (TextUtils.isEmpty(mobileEditText.getText())) {
            mobileEditText.setError("Please enter mobile number");
            return false;
        }
        return true;
    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_qUigfCdJJEgYwg");
        checkout.setImage(R.drawable.logo);

        try {
            JSONObject options = new JSONObject();
            options.put("name", "SCS Jobs Punjab");
            options.put("description", "Registration Fee");
            options.put("currency", "INR");
            options.put("amount", "55000");
            options.put("prefill.email", emailEditText.getText().toString());
            options.put("prefill.contact", mobileEditText.getText().toString());

            checkout.open(SignUpActivity.this, options);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        createUserInFirebase();
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_LONG).show();
    }

    private void createUserInFirebase() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String mobile = mobileEditText.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                UserData userData = new UserData(name, email, mobile);
                mDatabase.child("users").child(user.getUid()).setValue(userData);

                Toast.makeText(SignUpActivity.this, "Account Registered successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, MainPageActivity.class));
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class UserData {
        public String name, email, mobile;

        public UserData(String name, String email, String mobile) {
            this.name = name;
            this.email = email;
            this.mobile = mobile;
        }
    }
    public void toggleLoginSignup(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
