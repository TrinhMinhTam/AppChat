package com.example.chatphase1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

public class RegisterPhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_phone_number);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        progressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpBtn.setOnClickListener((v)->{
            String phoneNumber = phoneInput.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                phoneInput.setError("Please enter phone number");
                return;
            }
            if (!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Phone number not valid");
                return;
            }
            checkIfUserExists(countryCodePicker.getFullNumberWithPlus());
        });
    }
    void checkIfUserExists(String phoneNumber) {

        // Kiểm tra xem người dùng đã tồn tại trong cơ sở dữ liệu chưa
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("phone", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Người dùng mới, chuyển đến màn hình OTP
                            Intent intent = new Intent(RegisterPhoneNumberActivity.this, RegisterOtpActivity.class);
                            intent.putExtra("phone", phoneNumber);
                            startActivity(intent);

                        } else {
                            // Người dùng đã đăng ký, hiển thị thông báo
                            Toast.makeText(RegisterPhoneNumberActivity.this, "Phone number registered. Please sign in.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterPhoneNumberActivity.this, LoginPhoneNumberApp.class);
                            startActivity(intent);

                        }
                    } else {
                        // Xảy ra lỗi khi kiểm tra
                        Toast.makeText(RegisterPhoneNumberActivity.this, "Error checking user existence. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}