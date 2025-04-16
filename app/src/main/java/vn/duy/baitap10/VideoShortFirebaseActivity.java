package vn.duy.baitap10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import vn.duy.baitap10.Adapter.VideoFireBaseAdapter;
import vn.duy.baitap10.model.User;
import vn.duy.baitap10.model.VideoModel;

public class VideoShortFirebaseActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private VideoFireBaseAdapter videosAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_short_firebase);

        User user = (User) getIntent().getSerializableExtra("user");
        mapping();
        getVideos(user);
    }

    private void mapping() {
 //       imPerson = findViewById(R.id.imPerson);
        viewPager2 = findViewById(R.id.vpager);
    }

    private void getVideos(User user) {
        //**set database*/
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("videos_bai10");
        FirebaseRecyclerOptions<VideoModel> options =
                new FirebaseRecyclerOptions.Builder<VideoModel>()
                        .setQuery(mDatabaseReference, VideoModel.class)
                        .build();
        //**set adapter*/
        videosAdapter = new VideoFireBaseAdapter(options, VideoShortFirebaseActivity.this, user);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setAdapter(videosAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        videosAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videosAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videosAdapter.notifyDataSetChanged();
    }
}