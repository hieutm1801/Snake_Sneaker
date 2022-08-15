package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.ProReviewImageView;
import hieutm.dev.snakesneaker.item.ReviewProImageList;
import hieutm.dev.snakesneaker.util.Constant;

import java.util.List;

public class ProReviewImageAdapter extends RecyclerView.Adapter<ProReviewImageAdapter.ViewHolder> {

    private Activity activity;
    private List<ReviewProImageList> reviewProImageLists;

    public ProReviewImageAdapter(Activity activity, List<ReviewProImageList> reviewProImageLists) {
        this.activity = activity;
        this.reviewProImageLists = reviewProImageLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.pro_review_image_adapter, parent, false);

        return new ProReviewImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(activity).load(reviewProImageLists.get(position).getImage_path_thumb())
                .placeholder(R.drawable.placeholder_square).into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            Constant.reviewProImageLists.clear();
            Constant.reviewProImageLists.addAll(reviewProImageLists);
            activity.startActivity(new Intent(activity, ProReviewImageView.class)
                    .putExtra("position", position));
        });

    }

    @Override
    public int getItemCount() {
        return reviewProImageLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_pro_review_image);

        }
    }
}
