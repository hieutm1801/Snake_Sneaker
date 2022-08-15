package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.item.ProReviewList;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.chensir.expandabletextview.ExpandableTextView;

public class ProDetailProReviewAdapter extends RecyclerView.Adapter<ProDetailProReviewAdapter.ViewHolder> {

    private Activity activity;
    private List<ProReviewList> proReviewLists;

    public ProDetailProReviewAdapter(Activity activity, List<ProReviewList> proReviewLists) {
        this.activity = activity;
        this.proReviewLists = proReviewLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.pro_review_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position == proReviewLists.size() - 1) {
            holder.view.setVisibility(View.GONE);
        }

        Glide.with(activity).load(proReviewLists.get(position).getUser_image())
                .placeholder(R.drawable.user_profile).into(holder.imageView);

        holder.ratingView.setRating(Float.parseFloat(proReviewLists.get(position).getUser_rate()));
        holder.textViewUserName.setText(proReviewLists.get(position).getUser_name());
        holder.textViewTime.setText(proReviewLists.get(position).getRate_date());
        holder.textViewReview.setText(proReviewLists.get(position).getRate_desc());

        holder.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setFocusable(false);
        holder.recyclerView.setNestedScrollingEnabled(false);

        ProReviewImageAdapter proReviewImageAdapter = new ProReviewImageAdapter(activity, proReviewLists.get(position).getReviewProImageLists());
        holder.recyclerView.setAdapter(proReviewImageAdapter);

    }

    @Override
    public int getItemCount() {
        return proReviewLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private RatingView ratingView;
        private CircleImageView imageView;
        private RecyclerView recyclerView;
        private ExpandableTextView textViewReview;
        private MaterialTextView textViewUserName, textViewTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view_proReview_adapter);
            ratingView = itemView.findViewById(R.id.ratingBar_proReview_adapter);
            imageView = itemView.findViewById(R.id.imageView_proReview_adapter);
            textViewUserName = itemView.findViewById(R.id.textView_user_proReview_adapter);
            textViewTime = itemView.findViewById(R.id.textView_time_proReview_adapter);
            textViewReview = itemView.findViewById(R.id.textView_proReview_adapter);
            recyclerView = itemView.findViewById(R.id.recyclerView_proReview_adapter);

        }
    }

}
