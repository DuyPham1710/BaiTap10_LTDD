package vn.duy.baitap10.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import vn.duy.baitap10.ProfileActivity;
import vn.duy.baitap10.R;
import vn.duy.baitap10.model.User;
import vn.duy.baitap10.model.VideoModel;

public class VideoFireBaseAdapter extends FirebaseRecyclerAdapter<VideoModel, VideoFireBaseAdapter.MyHolder> {
    private Boolean isLiked = false;
    private Boolean isDisliked = false;
    private Context context;
    private User user;

    public VideoFireBaseAdapter(@NonNull FirebaseRecyclerOptions<VideoModel> options, Context context, User user) {
        super(options);
        this.context = context;
        this.user = user;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Implementation for creating the ViewHolder will go here
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_row, parent, false);
        return new MyHolder(view);
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private VideoView videoView;
        private ProgressBar videoProgressBar;
        private TextView textVideoTitle;
        private TextView textVideoDescription;
        private TextView textEmail;
        private TextView tvlike, tvdislike;
        private ImageView imPerson, imPersonOwner, imShare, imMore, like, dislike;
     //   private ImageView favorites;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            videoProgressBar = itemView.findViewById(R.id.videoProgressBar);
            textVideoTitle = itemView.findViewById(R.id.textVideoTitle);
            textVideoDescription = itemView.findViewById(R.id.textVideoDescription);
            textEmail = itemView.findViewById(R.id.emailEditText);
            imPerson = itemView.findViewById(R.id.imPerson);
            imPersonOwner = itemView.findViewById(R.id.imPersonOwner);
         //   favorites = itemView.findViewById(R.id.favorites);
            imShare = itemView.findViewById(R.id.imShare);
            imMore = itemView.findViewById(R.id.imMore);
            like = itemView.findViewById(R.id.like);
            dislike = itemView.findViewById(R.id.dislike);
            tvlike = itemView.findViewById(R.id.tvlike);
            tvdislike = itemView.findViewById(R.id.tvdislike);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull VideoModel model) {
        holder.textVideoTitle.setText(model.getTitle());
        holder.textVideoDescription.setText(model.getDesc());
        holder.textEmail.setText(model.getEmail());
        holder.tvlike.setText(String.valueOf(
                model.getLikes() != null ? model.getLikes().size() : 0
        ));

        holder.tvdislike.setText(String.valueOf(
                model.getDislikes() != null ? model.getDislikes().size() : 0
        ));
        holder.videoView.setVideoURI(Uri.parse(model.getUrl()));

        Glide.with(holder.itemView.getContext())
                .load(model.getAvatarOwner())
                .circleCrop()
                .placeholder(R.drawable.ic_person_pin) // Ảnh mặc định nếu chưa có avatar
                .into(holder.imPersonOwner);

        Glide.with(holder.itemView.getContext())
                .load(user.getAvatarUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_account) // Ảnh mặc định nếu chưa có avatar
                .into(holder.imPerson);

        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                holder.videoProgressBar.setVisibility(View.GONE);
                mp.start();

                float videoRatio = (float) mp.getVideoWidth() / (float) mp.getVideoHeight();
                float screenRatio = (float) holder.videoView.getWidth() / (float) holder.videoView.getHeight();
                float scale = videoRatio / screenRatio;

                if (scale >= 1f) {
                    holder.videoView.setScaleX(scale);
                } else {
                    holder.videoView.setScaleY(1f / scale);
                }
            }
        });
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLiked) {
                    holder.like.setImageResource(R.drawable.ic_like_on);
                    holder.dislike.setImageResource(R.drawable.ic_dislike_off);
                    isLiked = true;
                    isDisliked = false;
                } else {
                    holder.like.setImageResource(R.drawable.ic_like_off);
                    isLiked = false;
                }
            }
        });

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDisliked) {
                    holder.dislike.setImageResource(R.drawable.ic_dislike_on);
                    holder.like.setImageResource(R.drawable.ic_like_off);
                    isDisliked = true;
                    isLiked = false;
                } else {
                    holder.dislike.setImageResource(R.drawable.ic_dislike_off);
                    isDisliked = false;
                }
            }
        });

        holder.imPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });
    }
}
