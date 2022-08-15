package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

public class ProReviewAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private List<ProReviewList> proReviewLists;

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public ProReviewAdapter(Activity activity, List<ProReviewList> proReviewLists) {
        this.activity = activity;
        this.proReviewLists = proReviewLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.pro_review_adapter, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            if (position == proReviewLists.size() - 1) {
                viewHolder.view.setVisibility(View.GONE);
            }

            Glide.with(activity).load(proReviewLists.get(position).getUser_image())
                    .placeholder(R.drawable.user_profile).into(viewHolder.imageView);

            viewHolder.ratingView.setRating(Float.parseFloat(proReviewLists.get(position).getUser_rate()));
            viewHolder.textViewUserName.setText(proReviewLists.get(position).getUser_name());
            viewHolder.textViewTime.setText(proReviewLists.get(position).getRate_date());
            viewHolder.textViewReview.setText(proReviewLists.get(position).getRate_desc());

            viewHolder.recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
            viewHolder.recyclerView.setLayoutManager(layoutManager);
            viewHolder.recyclerView.setFocusable(false);
            viewHolder.recyclerView.setNestedScrollingEnabled(false);

            ProReviewImageAdapter proReviewImageAdapter = new ProReviewImageAdapter(activity, proReviewLists.get(position).getReviewProImageLists());
            viewHolder.recyclerView.setAdapter(proReviewImageAdapter);

        }

    }

    @Override
    public int getItemCount() {
        if (proReviewLists.size() != 0) {
            return proReviewLists.size() + 1;
        } else {
            return proReviewLists.size();
        }
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == proReviewLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private RatingView ratingView;
        private CircleImageView imageView;
        private RecyclerView recyclerView;
        private MaterialTextView textViewUserName, textViewTime;
        private ExpandableTextView textViewReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view_proReview_adapter);
            imageView = itemView.findViewById(R.id.imageView_proReview_adapter);
            ratingView = itemView.findViewById(R.id.ratingBar_proReview_adapter);
            textViewUserName = itemView.findViewById(R.id.textView_user_proReview_adapter);
            textViewTime = itemView.findViewById(R.id.textView_time_proReview_adapter);
            textViewReview = itemView.findViewById(R.id.textView_proReview_adapter);
            recyclerView = itemView.findViewById(R.id.recyclerView_proReview_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar_loading);
        }
    }

}
