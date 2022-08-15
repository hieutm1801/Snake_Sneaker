package hieutm.dev.snakesneaker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.ProductList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.DecimalConverter;
import hieutm.dev.snakesneaker.util.Method;

import com.github.ornolfr.ratingview.RatingView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter {

    private Method method;
    private Activity activity;
    private String type, sign;
    private int columnWidth;
    private List<ProductList> productLists;

    private boolean isGird;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final String DISTANCE = " ";


    public ProductAdapter(Activity activity, List<ProductList> productLists, String type, OnClick onClick, boolean isGird) {
        this.activity = activity;
        this.type = type;
        this.isGird = isGird;
        sign = DISTANCE + Constant.currency;
        this.productLists = productLists;
        method = new Method(activity, onClick);
        Resources r = activity.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
        columnWidth = (int) (method.getScreenWidth() - ((4 + 2) * padding));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            if (isGird) {
                View view = LayoutInflater.from(activity).inflate(R.layout.product_grid_adapter, parent, false);
                return new ViewHolderGird(view);
            } else {
                View view = LayoutInflater.from(activity).inflate(R.layout.product_list_adapter, parent, false);
                return new ViewHolder(view);
            }
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }
        return null;
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor", "Range"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            if (isGird) {

                final ViewHolderGird viewHolder = (ViewHolderGird) holder;

                viewHolder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (columnWidth / 2.2)));

                if (productLists.get(position).getProduct_status().equals("0")) {
                    viewHolder.textViewMessage.setVisibility(View.VISIBLE);
                    viewHolder.view.setVisibility(View.VISIBLE);
                    viewHolder.textViewMessage.setText(productLists.get(position).getProduct_status_lbl());
                } else {
                    viewHolder.textViewMessage.setVisibility(View.GONE);
                    viewHolder.view.setVisibility(View.GONE);
                }

                Glide.with(activity).load(productLists.get(position).getProduct_image_portrait())
                        .placeholder(R.drawable.placeholder_square)
                        .into(viewHolder.imageView);

                viewHolder.textView.setText(productLists.get(position).getProduct_title());
                viewHolder.textViewSellPrice.setText(DecimalConverter.currencyFormat(productLists.get(position).getProduct_sell_price()) + sign);
                viewHolder.textViewSellPrice.setTypeface(viewHolder.textViewSellPrice.getTypeface(), Typeface.BOLD);
//                viewHolder.textViewSellPrice.setTextColor(Color.argb(1000, 0, 0, 0));
                if (productLists.get(position).getYou_save().equals("0.00")) {
                    viewHolder.cardViewSave.setVisibility(View.GONE);
                    viewHolder.textViewPrice.setVisibility(View.GONE);
                } else {
                    viewHolder.cardViewSave.setVisibility(View.VISIBLE);
                    viewHolder.textViewPrice.setVisibility(View.VISIBLE);
                    viewHolder.textViewSave.setText(productLists.get(position).getYou_save_per());
                    viewHolder.textViewPrice.setText(DecimalConverter.currencyFormat(productLists.get(position).getProduct_mrp()) + sign);
                    viewHolder.textViewPrice.setPaintFlags(viewHolder.textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

                viewHolder.cardView.setOnClickListener(view -> method.click(position, type, productLists.get(position).getProduct_title(), productLists.get(position).getId(), productLists.get(position).getId(), "", ""));


            } else {

                final ViewHolder viewHolder = (ViewHolder) holder;

                if (productLists.get(position).getProduct_status().equals("0")) {
                    viewHolder.textViewMessage.setVisibility(View.VISIBLE);
                    viewHolder.view.setVisibility(View.VISIBLE);
                    viewHolder.textViewMessage.setText(productLists.get(position).getProduct_status_lbl());
                } else {
                    viewHolder.textViewMessage.setVisibility(View.GONE);
                    viewHolder.view.setVisibility(View.GONE);
                }

                Glide.with(activity).load(productLists.get(position).getProduct_image_square())
                        .placeholder(R.drawable.placeholder_square)
                        .into(viewHolder.imageView);

                viewHolder.textView.setText(productLists.get(position).getProduct_title());
                viewHolder.textViewDes.setText(Html.fromHtml(productLists.get(position).getProduct_desc()));
                viewHolder.textView.setText(productLists.get(position).getProduct_title());
                viewHolder.textViewSellPrice.setText(DecimalConverter.currencyFormat(productLists.get(position).getProduct_sell_price()) + sign);
                viewHolder.textViewSellPrice.setTypeface(viewHolder.textViewSellPrice.getTypeface(), Typeface.BOLD);
//                viewHolder.textViewSellPrice.setTextColor(Color.argb(1000, 0, 0, 0));
                if (productLists.get(position).getYou_save().equals("0.00")) {
                    viewHolder.textViewSave.setVisibility(View.GONE);
                    viewHolder.textViewPrice.setVisibility(View.GONE);
                } else {
                    viewHolder.textViewSave.setVisibility(View.VISIBLE);
                    viewHolder.textViewPrice.setVisibility(View.VISIBLE);
                    viewHolder.textViewSave.setText(productLists.get(position).getYou_save_per());
                    viewHolder.textViewPrice.setText(DecimalConverter.currencyFormat(productLists.get(position).getProduct_mrp()) + sign);
                    viewHolder.textViewPrice.setPaintFlags(viewHolder.textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                viewHolder.textViewRating.setText(productLists.get(position).getTotal_rate());
                viewHolder.ratingView.setRating(Float.parseFloat(productLists.get(position).getRate_avg()));

                viewHolder.cardView.setOnClickListener(view -> method.click(position, type, productLists.get(position).getProduct_title(), productLists.get(position).getId(), productLists.get(position).getId(), "", ""));

            }

        }

    }

    @Override
    public int getItemCount() {
        if (productLists.size() != 0) {
            return productLists.size() + 1;
        } else {
            return productLists.size();
        }
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == productLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView imageView;
        private RatingView ratingView;
        private MaterialCardView cardView;
        private MaterialTextView textView, textViewDes, textViewSellPrice, textViewPrice, textViewSave, textViewRating, textViewMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view_proList_adapter);
            cardView = itemView.findViewById(R.id.cartView_proList_adapter);
            textView = itemView.findViewById(R.id.textView_name_proList_adapter);
            imageView = itemView.findViewById(R.id.imageView_proList_adapter);
            textViewDes = itemView.findViewById(R.id.textView_des_proList_adapter);
            ratingView = itemView.findViewById(R.id.ratingBar_proList_adapter);
            textViewRating = itemView.findViewById(R.id.textView_rate_proList_adapter);
            textViewSellPrice = itemView.findViewById(R.id.textView_sellPrice_proList_adapter);
            textViewPrice = itemView.findViewById(R.id.textView_price_proList_adapter);
            textViewSave = itemView.findViewById(R.id.textView_save_proList_adapter);
            textViewMessage = itemView.findViewById(R.id.textView_message_proList_adapter);

        }
    }

    public class ViewHolderGird extends RecyclerView.ViewHolder {

        private View view;
        private ImageView imageView;
        private MaterialCardView cardView, cardViewSave;
        private MaterialTextView textView, textViewSellPrice, textViewPrice, textViewSave, textViewMessage;

        public ViewHolderGird(@NonNull View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view_proGrid_adapter);
            textView = itemView.findViewById(R.id.textView_name_proGrid_adapter);
            imageView = itemView.findViewById(R.id.imageView_proGrid_adapter);
            textViewSellPrice = itemView.findViewById(R.id.textView_sellPrice_proGrid_adapter);
            textViewPrice = itemView.findViewById(R.id.textView_price_proGrid_adapter);
            textViewSave = itemView.findViewById(R.id.textView_save_proGrid_adapter);
            textViewMessage = itemView.findViewById(R.id.textView_message_proGrid_adapter);
            cardViewSave = itemView.findViewById(R.id.cardView_save_proGrid_adapter);
            cardView = itemView.findViewById(R.id.cardView_proGrid_adapter);

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
