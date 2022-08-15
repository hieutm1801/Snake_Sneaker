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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.item.CartList;
import hieutm.dev.snakesneaker.response.RemoveCartRP;
import hieutm.dev.snakesneaker.response.UpdateCartRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.DecimalConverter;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private String sign;
    private List<CartList> cartLists;
    private ProgressDialog progressDialog;
    private ArrayList<CartList> checkedCartList = new ArrayList<>();
    private final String DISTANCE = " ";

    public CartAdapter(Activity activity, List<CartList> cartLists) {
        this.activity = activity;
        this.cartLists = cartLists;
        sign =DISTANCE + Constant.currency;
        method = new Method(activity);
        progressDialog = new ProgressDialog(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.cart_adapter, parent, false);

        return new CartAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (cartLists.get(position).getProduct_status().equals("0")) {
            holder.llPrice.setVisibility(View.GONE);
            holder.textViewSize.setVisibility(View.GONE);
            holder.cardViewControl.setVisibility(View.GONE);
            holder.textViewMessage.setText(cartLists.get(position).getProduct_status_lbl());
        } else {
            holder.textViewMessage.setVisibility(View.GONE);
        }

        holder.item = Integer.parseInt(cartLists.get(position).getProduct_qty());

        Glide.with(activity).load(cartLists.get(position).getProduct_image())
                .placeholder(R.drawable.placeholder_square)
                .into(holder.imageView);

        holder.textViewTitle.setText(cartLists.get(position).getProduct_title());
        holder.textViewSellPrice.setText(DecimalConverter.currencyFormat(cartLists.get(position).getProduct_sell_price()) + sign);

        if (cartLists.get(position).getYou_save().equals("0.00")) {
            holder.textViewSave.setVisibility(View.GONE);
            holder.textViewPrice.setVisibility(View.GONE);
        } else {
            holder.textViewSave.setText(cartLists.get(position).getYou_save_per());
            holder.textViewPrice.setText(DecimalConverter.currencyFormat(cartLists.get(position).getProduct_mrp()) + sign);
            holder.textViewPrice.setPaintFlags(holder.textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (cartLists.get(position).getProduct_size().equals("")) {
            holder.textViewSize.setVisibility(View.GONE);
        } else {
            String size = activity.getResources().getString(R.string.size) + " " + cartLists.get(position).getProduct_size();
            holder.textViewSize.setText(size);
        }

        holder.layoutDelete.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
            builder.setMessage(activity.getResources().getString(R.string.delete_msg));
            builder.setCancelable(false);
            builder.setPositiveButton(activity.getResources().getString(R.string.yes),
                    (arg0, arg1) -> removeItem(cartLists.get(position).getId(), method.userId(), position));
            builder.setNegativeButton(activity.getResources().getString(R.string.no),
                    (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

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
        private MaterialCardView cardViewControl;
        private FlexboxLayout llPrice;
        private ImageView imageView, imageViewUp, imageViewDown;
        private MaterialTextView textViewTitle, textViewSellPrice, textViewPrice, textViewMessage, textViewSave, textViewItem, textViewSize;
        private LinearLayout layoutDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_cart_adapter);
            textViewTitle = itemView.findViewById(R.id.textView_title_cart_adapter);
            textViewSellPrice = itemView.findViewById(R.id.textView_sellPrice_cart_adapter);
            textViewPrice = itemView.findViewById(R.id.textView_price_cart_adapter);
            textViewSave = itemView.findViewById(R.id.textView_save_cart_adapter);
            textViewMessage = itemView.findViewById(R.id.textView_message_cart_adapter);
            textViewSize = itemView.findViewById(R.id.textView_size_cart_adapter);
            textViewItem = itemView.findViewById(R.id.textView_item_cart_adapter);
            imageViewUp = itemView.findViewById(R.id.imageView_up_cart_adapter);
            imageViewDown = itemView.findViewById(R.id.imageView_down_cart_adapter);
            cardViewControl = itemView.findViewById(R.id.cardView_proControl_cart_adapter);
            llPrice = itemView.findViewById(R.id.ll_price_cart_adapter);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
        }
    }

    private void removeItem(String cartId, String userId, int position) {

        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("cart_id", cartId);
        jsObj.addProperty("user_id", userId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RemoveCartRP> call = apiService.removeCartItem(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RemoveCartRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<RemoveCartRP> call, @NotNull Response<RemoveCartRP> response) {

                try {

                    RemoveCartRP removeCartRP = response.body();

                    assert removeCartRP != null;
                    if (removeCartRP.getStatus().equals("1")) {

                        if (removeCartRP.getSuccess().equals("1")) {

                            cartLists.remove(position);
                            notifyDataSetChanged();

                            Toast.makeText(activity, removeCartRP.getMsg(), Toast.LENGTH_SHORT).show();

                            Events.UpdateCart updateCart = new Events.UpdateCart(removeCartRP.getTotal_item(), removeCartRP.getPrice(), removeCartRP.getDelivery_charge(),
                                    removeCartRP.getPayable_amt(), removeCartRP.getYou_save_msg(), removeCartRP.getCart_empty_msg());
                            GlobalBus.getBus().post(updateCart);

                            Events.CartItem cartItem = new Events.CartItem(updateCart.getTotal_item());
                            GlobalBus.getBus().post(cartItem);

                        }

                    } else if (removeCartRP.getStatus().equals("2")) {
                        method.suspend(removeCartRP.getMessage());
                    } else {
                        method.alertBox(removeCartRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(activity.getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<RemoveCartRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void cartItemUpdate(int item, String productId, String userId, String productSize, int position) {

        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("product_id", productId);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("product_qty", item);
        jsObj.addProperty("buy_now", "false");
        jsObj.addProperty("product_size", productSize);
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
                            cartLists.get(position).setProduct_size(productSize);

                            notifyDataSetChanged();

                            Toast.makeText(activity, updateCartRP.getMsg(), Toast.LENGTH_SHORT).show();

                            Events.UpdateCart updateCart = new Events.UpdateCart(updateCartRP.getTotal_item(), updateCartRP.getPrice(), updateCartRP.getDelivery_charge(),
                                    updateCartRP.getPayable_amt(), updateCartRP.getYou_save_msg(), updateCartRP.getMsg());
                            GlobalBus.getBus().post(updateCart);

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
