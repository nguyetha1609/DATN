package org.o7planning.project_04.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.project_04.R;
import org.o7planning.project_04.utils.EmailSender;

import java.util.Random;

public class VerificationCodeActivity extends AppCompatActivity {
    private EditText etCode;
    private Button btnVerify;
    private TextView tvResend;
    private String sentCode, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        etCode    = findViewById(R.id.etVerificationCode);
        btnVerify = findViewById(R.id.btnVerify);

        // Nếu bạn đã thêm sẵn TextView "Gửi lại mã" trong XML thì không cần tạo programmatic
        tvResend  = findViewById(R.id.tvResendCode);

        // Lấy dữ liệu từ Intent
        Intent it = getIntent();
        email    = it.getStringExtra("email");
        sentCode = it.getStringExtra("code");

        btnVerify.setOnClickListener(v -> {
            String input = etCode.getText().toString().trim();
            if (input.equals(sentCode)) {
                Intent i2 = new Intent(this, ChangePasswordActivity.class);
                i2.putExtra("email", email);
                startActivity(i2);
                finish();
            } else {
                etCode.setError("Mã không đúng");
            }
        });

        tvResend.setOnClickListener(v -> {
            // tạo & gửi lại mã mới
            sentCode = String.format("%05d", new Random().nextInt(100000));
            EmailSender.sendCode(email, sentCode);
            Toast.makeText(this, "Mã mới đã được gửi", Toast.LENGTH_SHORT).show();
        });
    }
}
