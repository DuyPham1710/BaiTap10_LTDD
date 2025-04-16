package vn.duy.baitap10;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import vn.duy.baitap10.model.User;
import vn.duy.baitap10.utils.FileUtils;

public class RegisterActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView imageView;
    TextView tvLogin;
    TextInputEditText nameEditText, emailEditText, passwordEditText;
    Button registerButton;
    FirebaseAuth mAuth;
    Cloudinary cloudinary;
    Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mapping();
        initCloudinary();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = String.valueOf(nameEditText.getText());
                String email = String.valueOf(emailEditText.getText());
                String password = String.valueOf(passwordEditText.getText());

                if (TextUtils.isEmpty(fullName)) {
                    emailEditText.setError("Please enter your full name");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Please enter your email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Please enter your password");
                    return;
                }
                else if (password.length() < 6) {
                    passwordEditText.setError("Password must be at least 6 characters long");
                    return;
                }

                // Đăng ký tài khoản
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            String uid = user.getUid();

                                            if (selectedImageUri != null) {
                                                uploadImageToCloudinary(selectedImageUri, url -> {
                                                    User userModel = new User(uid, fullName, email, password, url);
                                                    saveUserToDatabase(userModel);
                                                });
                                            }
                                            else {
                                                User userModel = new User(uid, fullName, email, password, "");
                                                saveUserToDatabase(userModel);
                                            }
                                        }
                                    }
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
    }

    private void uploadImageToCloudinary(Uri imageUri, OnImageUploadListener listener) {
        new Thread(() -> {
            try {
                File file = FileUtils.getFileFromUri(this, imageUri);
                Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                runOnUiThread(() -> listener.onUploadSuccess(imageUrl));
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Fail to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    listener.onUploadSuccess(""); // fallback nếu lỗi
                });
            }
        }).start();
    }

    private void saveUserToDatabase(User userModel) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userModel.getId());
        userRef.setValue(userModel)
                .addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to save user: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mapping() {
        mAuth = FirebaseAuth.getInstance();
        tvLogin = findViewById(R.id.tvLogin);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        imageView = findViewById(R.id.imageView);
    }
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop() // tự động bo tròn ảnh
                    .into(imageView);
        }
    }

    private void initCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "dfqf9wjji");
        config.put("api_key", "858621571969466");
        config.put("api_secret", "BPMW1rZAexoe15H4i8pmBTsm9Qo");
        config.put("secure", true);
        cloudinary = new Cloudinary(config);
    }

    interface OnImageUploadListener {
        void onUploadSuccess(String imageUrl);
    }

}
