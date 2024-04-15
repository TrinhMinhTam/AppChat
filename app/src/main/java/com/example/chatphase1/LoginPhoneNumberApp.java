package com.example.chatphase1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberApp extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button signinBtn;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_phone_number_app);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_mobile_number);
        signinBtn = findViewById(R.id.sign_in_btn);
        registerBtn = findViewById(R.id.register_btn);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);

        signinBtn.setOnClickListener((v) -> {
            String phoneNumber = phoneInput.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                phoneInput.setError("Please enter phone number");
                return;
            }

            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Phone number not valid");
                return;
            }

            checkIfUserExists(countryCodePicker.getFullNumberWithPlus());
        });

        registerBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(LoginPhoneNumberApp.this, RegisterPhoneNumberActivity.class);
            startActivity(intent);
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
                            // Người dùng mới, hiển thị thông báo
                            Toast.makeText(LoginPhoneNumberApp.this, "Phone number not registered. Please register.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Người dùng đã tồn tại, chuyển đến màn hình OTP
                            Intent intent = new Intent(LoginPhoneNumberApp.this, LoginOtpApp.class);
                            intent.putExtra("phone", phoneNumber);
                            startActivity(intent);
                        }
                    } else {
                        // Xảy ra lỗi khi kiểm tra
                        Toast.makeText(LoginPhoneNumberApp.this, "Error checking user existence. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
