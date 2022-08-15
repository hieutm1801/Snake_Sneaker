package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.interfaces.ImageDelete;
import hieutm.dev.snakesneaker.item.ReviewProImageList;
import hieutm.dev.snakesneaker.R;

import java.util.List;

public class ReviewImageAdapter extends RecyclerView.Adapter<ReviewImageAdapter.ViewHolder> {

    private Activity activity;
    private String type;
    private ImageDelete imageDelete;
    private List<ReviewProImageList> reviewProImageLists;

    public ReviewImageAdapter(Activity activity, String type, List<ReviewProImageList> reviewProImageLists, ImageDelete imageDelete) {
        this.activity = activity;
        this.type = type;
        this.imageDelete = imageDelete;
        this.reviewProImageLists = reviewProImageLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.review_image_adapter, parent, false);

        return new ReviewImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (reviewProImageLists.get(position).getId().equals("")) {
            Glide.with(activity).load("file://" + reviewProImageLists.get(position).getImage())
                    .placeholder(R.drawable.placeholder_square)
                    .into(holder.imageView);
        } else {
            Glide.with(activity).load(reviewProImageLists.get(position).getImage())
                    .placeholder(R.drawable.placeholder_square)
                    .into(holder.imageView);
        }

        holder.imageViewClose.setOnClickListener(v -> imageDelete.delete(reviewProImageLists.get(position).getId(), type, position));

    }

    @Override
    public int getItemCount() {
        return reviewProImageLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView, imageViewClose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_review_image_adapter);
            imageViewClose = itemView.findViewById(R.id.imageView_close_review_image_adapter);

        }
    }
}
