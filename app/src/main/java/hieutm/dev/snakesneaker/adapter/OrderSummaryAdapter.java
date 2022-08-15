package hieutm.dev.snakesneaker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.item.CartList;
import hieutm.dev.snakesneaker.response.UpdateCartRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.DecimalConverter;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;

import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private String sign, type;
    private List<CartList> cartLists;
    private ProgressDialog progressDialog;

    public OrderSummaryAdapter(Activity activity, String type, List<CartList> cartLists) {
        this.activity = activity;
        this.cartLists = cartLists;
        this.type = type;
        sign = Constant.currency + " ";
        method = new Method(activity);
        progressDialog = new ProgressDialog(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.order_summary_adapter, parent, false);

        return new OrderSummaryAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.item = Integer.parseInt(cartLists.get(position).getProduct_qty());

        Glide.with(activity).load(cartLists.get(position).getProduct_image())
                .placeholder(R.drawable.placeholder_square)
                .into(holder.imageView);

        holder.textViewTitle.setText(cartLists.get(position).getProduct_title());
        holder.textViewSellPrice.setText(DecimalConverter.currencyFormat(cartLists.get(position).getProduct_sell_price()) + " " + sign);
        if (cartLists.get(position).getYou_save().equals("0.00")) {
            holder.textViewSave.setVisibility(View.GONE);
            holder.textViewPrice.setVisibility(View.GONE);
        } else {
            holder.textViewPrice.setText(DecimalConverter.currencyFormat(cartLists.get(position).getProduct_mrp()) + " " + sign);
            holder.textViewSave.setText(cartLists.get(position).getYou_save_per());
            holder.textViewPrice.setPaintFlags(holder.textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (cartLists.get(position).getProduct_size().equals("")) {
            holder.textViewSize.setVisibility(View.GONE);
        } else {
            String size = activity.getResources().getString(R.string.size) + " " + cartLists.get(position).getProduct_size();
            holder.textViewSize.setText(size);
        }


        holder.textViewItem.setText(String.valueOf(holder.item));

        holder.imageViewUp.setOnClickListener(v -> {
            if (holder.item < Integer.parseInt(cartLists.get(position).getMax_unit_buy())) {
                cartItemUpdate(holder.item + 1, cartLists.get(position).getProduct_id(), method.userId(), cartLists.get(position).getProduct_size(), position);
            } else {
                String msg = activity.getResources().getString(R.string.max_value) + " " +
                        cartLists.get(position).getMax_unit_buy() + " " +
                        activity.getResources().getString(R.string.purchase_item);
                method.alertBox(msg);
            }
        });

        holder.imageViewDown.setOnClickListener(v -> {
            if (holder.item > 1) {
                cartItemUpdate(holder.item - 1, cartLists.get(position).getProduct_id(), method.userId(), cartLists.get(position).getProduct_size(), position);
            } else {
                method.alertBox(activity.getResources().getString(R.string.minimum_order_value_one));
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private int item = 0;
        private ImageView imageView, imageViewUp, imageViewDown;
        private MaterialTextView textViewTitle, textViewSellPrice, textViewPrice, textViewSave, textViewItem, textViewSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_orderSum_adapter);
            textViewTitle = itemView.findViewById(R.id.textView_title_orderSum_adapter);
            textViewSellPrice = itemView.findViewById(R.id.textView_sellPrice_orderSum_adapter);
            textViewPrice = itemView.findViewById(R.id.textView_price_orderSum_adapter);
            textViewSave = itemView.findViewById(R.id.textView_save_orderSum_adapter);
            textViewSize = itemView.findViewById(R.id.textView_size_orderSum_adapter);
            textViewItem = itemView.findViewById(R.id.textView_item_orderSum_adapter);
            imageViewUp = itemView.findViewById(R.id.imageView_up_orderSum_adapter);
            imageViewDown = itemView.findViewById(R.id.imageView_down_orderSum_adapter);

        }
    }

    private void cartItemUpdate(int item, String productId, String userId, String productSize, int position) {

        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("product_id", productId);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("product_qty", item);
        if (type.equals("by_now")) {
            jsObj.addProperty("buy_now", "true");
        } else {
            jsObj.addProperty("buy_now", "false");
        }
        jsObj.addProperty("product_size", productSize);
        jsObj.addProperty("device_id", method.getDeviceId());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UpdateCartRP> call = apiService.getCartItemUpdate(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<UpdateCartRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<UpdateCartRP> call, @NotNull Response<UpdateCartRP> response) {

                try {

                    UpdateCartRP updateCartRP = response.body();

                    assert updateCartRP != null;
                    if (updateCartRP.getStatus().equals("1")) {

                        Toast.makeText(activity, updateCartRP.getMsg(), Toast.LENGTH_SHORT).show();

                        if (updateCartRP.getSuccess().equals("1")) {

                            cartLists.get(position).setProduct_qty(String.valueOf(item));
                            cartLists.get(position).setProduct_sell_price(updateCartRP.getProduct_sell_price());
                            cartLists.get(position).setProduct_mrp(updateCartRP.getProduct_mrp());
                            cartLists.get(position).setYou_save_per(updateCartRP.getYou_save_per());

                            notifyDataSetChanged();

                            Events.UpdateCartOrderSum updateCartOrderSum = new Events.UpdateCartOrderSum(updateCartRP.getTotal_item(), updateCartRP.getPrice(), updateCartRP.getDelivery_charge(),
                                    updateCartRP.getPayable_amt(), updateCartRP.getYou_save_msg());
                            GlobalBus.getBus().post(updateCartOrderSum);

                        }

                    } else if (updateCartRP.getStatus().equals("2")) {
                        method.suspend(updateCartRP.getMessage());
                    } else {
                        method.alertBox(updateCartRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(activity.getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<UpdateCartRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });

    }

}
