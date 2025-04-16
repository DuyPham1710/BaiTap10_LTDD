package vn.duy.baitap10;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import vn.duy.baitap10.model.User;
import vn.duy.baitap10.model.VideoModel;
import vn.duy.baitap10.utils.FileUtils;

public class UploadVideoActivity extends AppCompatActivity {
    private static final int PICK_VIDEO_REQUEST = 1;
    private ImageView ivVideoPreview;
    private ImageView btnSelectVideo;
    private TextInputEditText etTitle;
    private TextInputEditText etDesc;
    private Button btnUpload;
    private ProgressBar progressBar;
    private Uri videoUri;
    private User user;
    private Cloudinary cloudinary;
    private DatabaseReference videosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_video);

        user = (User) getIntent().getSerializableExtra("user");

        initCloudinary();
        initFirebase();
        initViews();

        setupClickListeners();
    }

    private void initCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "dfqf9wjji");
        config.put("api_key", "858621571969466");
        config.put("api_secret", "BPMW1rZAexoe15H4i8pmBTsm9Qo");
        config.put("secure", true);
        cloudinary = new Cloudinary(config);
    }

    private void initFirebase() {
        videosRef = FirebaseDatabase.getInstance().getReference("videos_bai10");
    }

    private void initViews() {
        ivVideoPreview = findViewById(R.id.ivVideoPreview);
        btnSelectVideo = findViewById(R.id.btnSelectVideo);
        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        btnUpload = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        View.OnClickListener selectVideoListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        };

        ivVideoPreview.setOnClickListener(selectVideoListener);
        btnSelectVideo.setOnClickListener(selectVideoListener);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVideo();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            if (videoUri != null) {
                try {
                    android.media.MediaMetadataRetriever retriever = new android.media.MediaMetadataRetriever();
                    retriever.setDataSource(this, videoUri);
                    android.graphics.Bitmap bitmap = retriever.getFrameAtTime();
                    ivVideoPreview.setImageBitmap(bitmap);
                    btnSelectVideo.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading video preview", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadVideo() {
        if (videoUri == null) {
            Toast.makeText(this, "Please select video", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            etTitle.setError("Please enter title");
            return;
        }

        String desc = etDesc.getText().toString().trim();
        if (desc.isEmpty()) {
            etDesc.setError("Please enter description");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);

        // Upload video to Cloudinary
        new Thread(() -> {
            try {
                File videoFile = FileUtils.getFileFromUri(this, videoUri);
                Map uploadResult = cloudinary.uploader().upload(videoFile, 
                    ObjectUtils.asMap("resource_type", "video"));
                String videoUrl = (String) uploadResult.get("secure_url");

                // Create video model
                VideoModel videoModel = new VideoModel(title, desc, videoUrl, null, null, user.getEmail(), user.getAvatarUrl());

                // Save to Firebase
                String videoId = videosRef.push().getKey();
                videosRef.child(videoId).setValue(videoModel)
                    .addOnCompleteListener(task -> {
                        runOnUiThread(() -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Upload failed: " + task.getException().getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                btnUpload.setEnabled(true);
                            }
                        });
                    });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                });
            }
        }).start();
    }
}