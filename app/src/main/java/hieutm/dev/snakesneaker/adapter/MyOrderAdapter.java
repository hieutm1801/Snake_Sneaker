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
import hieutm.dev.snakesneaker.item.MyOrderList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private Method method;
    private String type;
    private List<MyOrderList> myOrderLists;

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public MyOrderAdapter(Activity activity, String type, List<MyOrderList> myOrderLists, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.myOrderLists = myOrderLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.my_order_adapter, parent, false);
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

            Glide.with(activity).load(myOrderLists.get(position).getProduct_image())
                    .placeholder(R.drawable.placeholder_square)
                    .into(viewHolder.imageView);

            if (myOrderLists.get(position).getCurrent_order_status().equals("true")) {
                viewHolder.view.setBackgroundColor(activity.getResources().getColor(R.color.green));
            } else {
                viewHolder.view.setBackgroundColor(activity.getResources().getColor(R.color.red));
            }

            viewHolder.textViewStatus.setText(myOrderLists.get(position).getOrder_status());
            viewHolder.textViewTitle.setText(myOrderLists.get(position).getProduct_title());

            viewHolder.cardView.setOnClickListener(v -> method.click(position, type, myOrderLists.get(position).getProduct_title(), myOrderLists.get(position).getOrder_unique_id(),
                    myOrderLists.get(position).getProduct_id(), myOrderLists.get(position).getOrder_id(), ""));
            viewHolder.textViewUniqueId.setText(myOrderLists.get(position).getOrder_unique_id());
        }


    }

    @Override
    public int getItemCount() {
        return myOrderLists.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == myOrderLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView imageView;
        private MaterialCardView cardView;
        private MaterialTextView textViewTitle, textViewStatus, textViewUniqueId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView_my_order_adapter);
            imageView = itemView.findViewById(R.id.imageView_my_order_adapter);
            view = itemView.findViewById(R.id.view_my_order_adapter);
            textViewTitle = itemView.findViewById(R.id.textView_title_my_order_adapter);
            textViewStatus = itemView.findViewById(R.id.textView_status_my_order_adapter);
            textViewUniqueId = itemView.findViewById(R.id.textView_my_order_unique_id_adapter);

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
