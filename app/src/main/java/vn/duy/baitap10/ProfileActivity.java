package vn.duy.baitap10;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import vn.duy.baitap10.model.User;
import vn.duy.baitap10.utils.FileUtils;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button uploadVideo;
    private ImageView ivProfile;
    private TextView tvName, tvEmail, tvVideoCount;
    private User user;
    private DatabaseReference videosRef;
    Cloudinary cloudinary;
    Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        mapping();
        initCloudinary();
        getVideoCount();

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });

        uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, UploadVideoActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    private void mapping() {
        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvVideoCount = findViewById(R.id.tvVideoCount);
        uploadVideo = findViewById(R.id.btnUploadVideo);

        user = (User) getIntent().getSerializableExtra("user");
        videosRef = FirebaseDatabase.getInstance().getReference("videos_bai10");

        tvName.setText(user.getFullName());
        tvEmail.setText(user.getEmail());

        Glide.with(this)
                .load(user.getAvatarUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_account)
                .into(ivProfile);
    }

    private void getVideoCount() {
        videosRef.orderByChild("email").equalTo(user.getEmail())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount();
                        tvVideoCount.setText(String.valueOf(count));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvVideoCount.setText("-1");
                    }
                });
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
            if (selectedImageUri != null) {
                uploadImageToCloudinary(selectedImageUri, newUrl -> {
                    user.setAvatarUrl(newUrl);
                    saveUserToDatabase(user);
                });
            }
            Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop() // tự động bo tròn ảnh
                    .placeholder(R.drawable.ic_account)
                    .into(ivProfile);
        }
    }

    private void uploadImageToCloudinary(Uri imageUri, ProfileActivity.OnImageUploadListener listener) {
        new Thread(() -> {
            try {
                File file = FileUtils.getFileFromUri(this, imageUri);
                Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                runOnUiThread(() -> listener.onUploadSuccess(imageUrl));
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Fail to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    listener.onUploadSuccess(""); // fallback nếu lỗi
                });
            }
        }).start();
    }

    private void updateAvatarInVideos(User userModel) {
        videosRef.orderByChild("email").equalTo(userModel.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot videoSnap : snapshot.getChildren()) {
                            videoSnap.getRef().child("avatarOwner").setValue(userModel.getAvatarUrl());
                        }
                        Toast.makeText(ProfileActivity.this, "Avatar updated in your videos", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileActivity.this, "Failed to update avatar in videos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(User userModel) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userModel.getId());
        userRef.setValue(userModel)
                .addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        updateAvatarInVideos(userModel);
                        Toast.makeText(ProfileActivity.this, "Update avatar successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to save user: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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