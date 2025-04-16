package vn.duy.baitap10;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.duy.baitap10.model.User;

public class LoginActivity extends AppCompatActivity {
    TextView tvRegister;
    TextInputEditText emailEditText, passwordEditText;
    Button loginButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mapping();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(emailEditText.getText());
                String password = String.valueOf(passwordEditText.getText());

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Please enter your email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Please enter your password");
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user != null) {
                                        String uid = user.getUid();
                                        // Truy vấn thông tin user trong Firebase Database
                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    String fullName = snapshot.child("fullName").getValue(String.class);
                                                    String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                                                    String emailUser = snapshot.child("email").getValue(String.class);

                                                    User userObj = new User(fullName, emailUser, avatarUrl);
                                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(LoginActivity.this, VideoShortFirebaseActivity.class);
                                                    intent.putExtra("user", userObj);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(LoginActivity.this, "Lỗi khi truy vấn dữ liệu.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void mapping() {
        mAuth = FirebaseAuth.getInstance();
        tvRegister = findViewById(R.id.tvRegister);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
    }
} 