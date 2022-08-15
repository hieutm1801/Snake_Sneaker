package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.OfferList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter {

    private Method method;
    private Activity activity;
    private String type;
    private int columnWidth;
    private List<OfferList> offerLists;

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public OfferAdapter(Activity activity, List<OfferList> offerLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.offerLists = offerLists;
        method = new Method(activity, onClick);
        Resources r = activity.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
        columnWidth = (int) (method.getScreenWidth() - ((4 + 2) * padding));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.coupons_adapter, parent, false);
            return new OfferAdapter.ViewHolder(view);
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

            viewHolder.imageView.setLayoutParams(new MaterialCardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, columnWidth / 4));

            Glide.with(activity).load(offerLists.get(position).getOffer_image_thumb())
                    .placeholder(R.drawable.placeholder_rectangle)
                    .into(viewHolder.imageView);

            viewHolder.imageView.setOnClickListener(v -> method.click(position, type, offerLists.get(position).getOffer_title(), offerLists.get(position).getId(), "", "", ""));

        }

    }

    @Override
    public int getItemCount() {
        return offerLists.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == offerLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_home_coupons_adapter);

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
