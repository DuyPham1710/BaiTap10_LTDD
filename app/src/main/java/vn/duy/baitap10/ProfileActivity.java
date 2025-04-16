package vn.duy.baitap10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.duy.baitap10.model.User;

public class ProfileActivity extends AppCompatActivity {
    private Button uploadVideo;
    private ImageView ivProfile;
    private TextView tvName, tvEmail, tvVideoCount;
    private User user;
    private DatabaseReference videosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        mapping();
        getVideoCount();

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
}