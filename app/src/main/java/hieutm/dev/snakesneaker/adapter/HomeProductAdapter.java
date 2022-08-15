package hieutm.dev.snakesneaker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.ProductList;
import hieutm.dev.snakesneaker.util.DecimalConverter;
import hieutm.dev.snakesneaker.util.Method;
import hieutm.dev.snakesneaker.util.Constant;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.DecimalFormat;
import java.util.List;

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ViewHolder> {

    private Method method;
    private Activity activity;
    private String type, sign;
    private List<ProductList> productLists;
    private final String DISTANCE = " ";

    public HomeProductAdapter(Activity activity, List<ProductList> productLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        sign =DISTANCE + Constant.currency;
        this.productLists = productLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_product_adapter, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (productLists.get(position).getProduct_status().equals("0")) {
            holder.textViewMessage.setVisibility(View.VISIBLE);
            holder.view.setVisibility(View.VISIBLE);
            holder.textViewMessage.setText(productLists.get(position).getProduct_status_lbl());
        } else {
            holder.textViewMessage.setVisibility(View.GONE);
            holder.view.setVisibility(View.GONE);
        }

        Glide.with(activity).load(productLists.get(position).getProduct_image())
                .placeholder(R.drawable.placeholder_square)
                .into(holder.imageView);

        holder.textView.setText(productLists.get(position).getProduct_title());
        holder.textViewSellPrice.setTypeface(holder.textViewSellPrice.getTypeface(), Typeface.BOLD);
        holder.textViewSellPrice.setText(DecimalConverter.currencyFormat(productLists.get(position).getProduct_sell_price()) + sign);
        if (productLists.get(position).getYou_save().equals("0.00")) {
            holder.cardViewSave.setVisibility(View.GONE);
            holder.textViewPrice.setVisibility(View.GONE);
            holder.view_adapter.setVisibility(View.VISIBLE);
        } else {
            holder.cardViewSave.setVisibility(View.VISIBLE);
            holder.textViewPrice.setVisibility(View.VISIBLE);
            holder.textViewSave.setText(productLists.get(position).getYou_save_per());
            holder.textViewPrice.setText(DecimalConverter.currencyFormat(productLists.get(position).getProduct_mrp())+ sign);
            holder.textViewPrice.setPaintFlags(holder.textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.cardView.setOnClickListener(view -> method.click(position, type, productLists.get(position).getProduct_title(), productLists.get(position).getId(), "", "", ""));

    }

    @Override
    public int getItemCount() {
        return productLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view, view_adapter;
        private ImageView imageView;
        private MaterialCardView cardView, cardViewSave;
        private MaterialTextView textView, textViewSellPrice, textViewPrice, textViewSave, textViewMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view_homePro_adapter);
            view_adapter = itemView.findViewById(R.id.view_adapter);
            imageView = itemView.findViewById(R.id.imageView_homePro_adapter);
            textView = itemView.findViewById(R.id.textView_name_homePro_adapter);
            textViewSellPrice = itemView.findViewById(R.id.textView_sellPrice_homePro_adapter);
            textViewPrice = itemView.findViewById(R.id.textView_price_homePro_adapter);
            textViewSave = itemView.findViewById(R.id.textView_save_homePro_adapter);
            textViewMessage = itemView.findViewById(R.id.textView_message_homePro_adapter);
            cardViewSave = itemView.findViewById(R.id.cardView_save_homePro_adapter);
            cardView = itemView.findViewById(R.id.cardView_homePro_adapter);

        }
    }

}
