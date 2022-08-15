package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.SubCategoryList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class SubCatAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private Method method;
    private String type;
    private List<SubCategoryList> subCategoryLists;

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public SubCatAdapter(Activity activity, List<SubCategoryList> subCategoryLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.subCategoryLists = subCategoryLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.category_adapter, parent, false);
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

            viewHolder.textView.setText(subCategoryLists.get(position).getSub_category_name());
            Glide.with(activity).load(subCategoryLists.get(position).getSub_category_image_thumb())
                    .placeholder(R.drawable.placeholder_cat)
                    .into(viewHolder.imageView);

            viewHolder.materialCardView.setOnClickListener(view -> method.click(position, type, subCategoryLists.get(position).getSub_category_name(), subCategoryLists.get(position).getCategory_id(), subCategoryLists.get(position).getId(), "", ""));

        }

    }

    @Override
    public int getItemCount() {
        return subCategoryLists.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == subCategoryLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textView;
        private ImageView imageView;
        private MaterialCardView materialCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView_name_cat_adapter);
            imageView = itemView.findViewById(R.id.imageView_cat_adapter);
            materialCardView = itemView.findViewById(R.id.cardView_cat_adapter);

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
