package hieutm.dev.snakesneaker.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.activity.RatingReview;
import hieutm.dev.snakesneaker.adapter.MyOrderDetailAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.response.MyOrderDetailRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.DecimalConverter;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrderDetailFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private String sign;
    private ProgressBar progressBar;
    private RatingView ratingView;
    private RecyclerView recyclerView;
    private MyOrderDetailRP myOrderDetailRP;
    private int REQUEST_CODE_PERMISSION = 101;
    private MyOrderDetailAdapter myOrderDetailAdapter;
    private ImageView imageView, imageViewDownload;
    private View viewRating, viewDeliver, viewCancelOrder;
    private MaterialCardView cardViewCancel, cardViewOtherOrder;
    private RelativeLayout relNoData, relMyOrder, relRating, relCancelOrderPrice;
    private LinearLayout llMain, llPaymentId, llOrderStatus, llRefundStatus, llInvoice;
    private MaterialTextView textViewOrderId, textViewTitle, textViewSizeColor, textViewUserName,
            textViewEmail, textViewAddress, textViewPhoneNo, textViewPaymentMode, textViewPaymentId, textViewOrderStatus, textViewOrderDate, textViewDeliveryStatus,
            textViewDeliveryDate, textViewCancelThisProduct, textViewCancelOrder, textViewProRefundClaim, textViewOrderRefundClaim, textViewReason, textViewRefundStatus,
            textViewPriceDetail, textViewSellingPrice, textViewDiscount, textViewDelivery, textViewQty, textViewTotalPrice,
            textViewOpdPrice, textViewOpdCancelOrderPrice, textViewOpdDiscount, textViewOpdDelivery, textViewOpdAmountPayable;
    private String distance = " ";

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_order_detail_fragment, container, false);

        GlobalBus.getBus().register(this);
        method = new Method(getActivity());

        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.my_order_detail) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.my_order_detail) + "</font>"));
            }
        }

        assert getArguments() != null;
        String orderUniqueId = getArguments().getString("order_unique_id");
        String productId = getArguments().getString("product_id");

        onClick = (position, type, title, id, sub_id, sub_sub_id, tag) -> {
            if (type.equals("my_order_detail")) {
                ProDetailFragment proDetailFragment = new ProDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("title", title);
                proDetailFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.frameLayout_main, proDetailFragment, title).addToBackStack(title)
                        .commitAllowingStateLoss();
            } else {
                getActivity().getSupportFragmentManager().popBackStack();
                MyOrderDetailFragment myOrderDetailFragment = new MyOrderDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("order_unique_id", id);
                bundle.putString("product_id", sub_id);
                myOrderDetailFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                        myOrderDetailFragment, getResources().getString(R.string.order_detail))
                        .addToBackStack(getResources().getString(R.string.order_detail)).commitAllowingStateLoss();
            }
        };
        method.forceRTLIfSupported();

        sign = distance + Constant.currency;

        progressBar = view.findViewById(R.id.progressBar_myOr_detail);
        imageView = view.findViewById(R.id.imageView_myOr_detail);
        imageViewDownload = view.findViewById(R.id.imageView_download_myOr_detail);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        textViewOrderId = view.findViewById(R.id.textView_orderId_myOr_detail);
        textViewTitle = view.findViewById(R.id.textView_title_myOr_detail);
        textViewSizeColor = view.findViewById(R.id.textView_sizeColor_myOr_detail);
        textViewUserName = view.findViewById(R.id.textView_userName_myOr_detail);
        textViewEmail = view.findViewById(R.id.textView_email_myOr_detail);
        textViewAddress = view.findViewById(R.id.textView_add_myOr_detail);
        textViewPhoneNo = view.findViewById(R.id.textView_phoneNO_myOr_detail);
        textViewPaymentMode = view.findViewById(R.id.textView_paymentMode_myOr_detail);
        textViewPaymentId = view.findViewById(R.id.textView_paymentId_myOr_detail);
        textViewOrderStatus = view.findViewById(R.id.textView_orderStatus_myOr_detail);
        textViewOrderDate = view.findViewById(R.id.textView_orderDate_myOr_detail);
        textViewDeliveryStatus = view.findViewById(R.id.textView_deliveryStatus_myOr_detail);
        textViewDeliveryDate = view.findViewById(R.id.textView_deliveryDate_myOr_detail);
        textViewCancelThisProduct = view.findViewById(R.id.textView_cancelThis_myOr_detail);
        textViewCancelOrder = view.findViewById(R.id.textView_cancelOrder_myOr_detail);
        textViewProRefundClaim = view.findViewById(R.id.textView_proRefundClaim_myOr_detail);
        textViewOrderRefundClaim = view.findViewById(R.id.textView_orderRefundClaim_myOr_detail);
        textViewReason = view.findViewById(R.id.textView_reason_myOr_detail);
        textViewRefundStatus = view.findViewById(R.id.textView_refundStatus_myOr_detail);
        textViewPriceDetail = view.findViewById(R.id.textView_IpdPrice_myOr_detail);
        textViewSellingPrice = view.findViewById(R.id.textView_IpdSellingPrice_myOr_detail);
        textViewDiscount = view.findViewById(R.id.textView_IpdDiscount_myOr_detail);
        textViewDelivery = view.findViewById(R.id.textView_IpdDelivery_myOr_detail);
        textViewQty = view.findViewById(R.id.textView_IpdQty_myOr_detail);
        textViewTotalPrice = view.findViewById(R.id.textView_IpdTotalPrice_myOr_detail);
        textViewOpdPrice = view.findViewById(R.id.textView_OpdPrice_myOr_detail);
        textViewOpdCancelOrderPrice = view.findViewById(R.id.textView_OpdCancelOrderPrice_myOr_detail);
        textViewOpdDiscount = view.findViewById(R.id.textView_OpdDiscount_myOr_detail);
        textViewOpdDelivery = view.findViewById(R.id.textView_OpdDelivery_myOr_detail);
        textViewOpdAmountPayable = view.findViewById(R.id.textView_OpdAmountPayable_myOr_detail);
        ratingView = view.findViewById(R.id.ratingBar_myOr_detail);
        relMyOrder = view.findViewById(R.id.rel_myOr_detail);
        relRating = view.findViewById(R.id.rel_rating_myOr_detail);
        relCancelOrderPrice = view.findViewById(R.id.rel_OpdCancelOrderPrice_myOr_detail);
        viewRating = view.findViewById(R.id.view_rating_myOr_detail);
        viewDeliver = view.findViewById(R.id.view_deliver_myOr_detail);
        viewCancelOrder = view.findViewById(R.id.view_cancelOrder_myOr_detail);
        llMain = view.findViewById(R.id.ll_main_myOr_detail);
        llPaymentId = view.findViewById(R.id.ll_paymentId_myOr_detail);
        llOrderStatus = view.findViewById(R.id.ll_orderStatus_myOr_detail);
        llRefundStatus = view.findViewById(R.id.ll_refundStatus_myOrder_detail);
        llInvoice = view.findViewById(R.id.ll_invoice_myOr_detail);
        cardViewCancel = view.findViewById(R.id.cardView_cancel_myOr_detail);
        cardViewOtherOrder = view.findViewById(R.id.cardView_otherOrder_myOr_detail);
        recyclerView = view.findViewById(R.id.recyclerView_myOr_detail);

        if (method.isRtl()) {
            imageViewDownload.setImageDrawable(getResources().getDrawable(R.drawable.ic_invoice_white));
        } else {
            imageViewDownload.setImageDrawable(getResources().getDrawable(R.drawable.ic_invoice));
        }

        llMain.setVisibility(View.GONE);
        relNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                orderDetail(method.userId(), productId, orderUniqueId);
            } else {
                method.alertBox(getResources().getString(R.string.you_have_not_login));
                relNoData.setVisibility(View.VISIBLE);
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;
    }

    @Subscribe
    public void geString(Events.RatingUpdate ratingUpdate) {
        if (ratingView != null) {
            ratingView.setRating(Float.parseFloat(ratingUpdate.getRate()));
        }
    }

    @Subscribe
    public void geString(Events.CancelOrder cancelOrder) {
        llMain.setVisibility(View.GONE);
        relNoData.setVisibility(View.GONE);
        orderDetail(method.userId(), cancelOrder.getProductId(), cancelOrder.getOrderUniqueId());
    }

    @Subscribe
    public void geString(Events.ClaimOrder claimOrder) {
        llMain.setVisibility(View.GONE);
        relNoData.setVisibility(View.GONE);
        orderDetail(method.userId(), claimOrder.getProductId(), claimOrder.getOrderUniqueId());
    }


    public void orderDetail(String userId, String productId, String orderUniqueId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("product_id", productId);
            jsObj.addProperty("order_unique_id", orderUniqueId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<MyOrderDetailRP> call = apiService.getMyOrderDetail(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<MyOrderDetailRP>() {
                @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
                @Override
                public void onResponse(@NotNull Call<MyOrderDetailRP> call, @NotNull Response<MyOrderDetailRP> response) {

                    if (getActivity() != null) {

                        try {

                            myOrderDetailRP = response.body();

                            assert myOrderDetailRP != null;
                            if (myOrderDetailRP.getStatus().equals("1")) {

                                if (myOrderDetailRP.getSuccess().equals("1")) {

                                    Glide.with(getActivity().getApplicationContext()).load(myOrderDetailRP.getProduct_image())
                                            .placeholder(R.drawable.placeholder_square).into(imageView);

                                    String order = getResources().getString(R.string.order_id) + " " + myOrderDetailRP.getOrder_unique_id();
                                    textViewOrderId.setText(order);
                                    textViewTitle.setText(myOrderDetailRP.getProduct_title());
                                    if (!myOrderDetailRP.getProduct_size().equals("") && !myOrderDetailRP.getProduct_color().equals("")) {
                                        String sizeColor = getResources().getString(R.string.size) + " "
                                                + myOrderDetailRP.getProduct_size() + ", " +
                                                getResources().getString(R.string.color) + " " + myOrderDetailRP.getProduct_color();
                                        textViewSizeColor.setText(sizeColor);
                                    } else {
                                        if (!myOrderDetailRP.getProduct_size().equals("")) {
                                            textViewSizeColor.setText(getResources().getString(R.string.size) + " " + myOrderDetailRP.getProduct_size());
                                        } else if (!myOrderDetailRP.getProduct_color().equals("")) {
                                            textViewSizeColor.setText(getResources().getString(R.string.color) + " " + myOrderDetailRP.getProduct_color());
                                        } else {
                                            textViewSizeColor.setVisibility(View.GONE);
                                        }
                                    }

                                    textViewUserName.setText(myOrderDetailRP.getName());
                                    textViewEmail.setText(myOrderDetailRP.getEmail());
                                    textViewAddress.setText(myOrderDetailRP.getAddress());
                                    textViewPhoneNo.setText(myOrderDetailRP.getMobile_no());
                                    textViewPaymentMode.setText(myOrderDetailRP.getPayment_mode());

                                    if (myOrderDetailRP.getPayment_id().equals("0")) {
                                        llPaymentId.setVisibility(View.GONE);
                                    } else {
                                        llPaymentId.setVisibility(View.VISIBLE);
                                        textViewPaymentId.setText(myOrderDetailRP.getPayment_id());
                                    }

                                    textViewOrderStatus.setText(myOrderDetailRP.getOrderTrackingLists().get(0).getStatus_title());
                                    textViewOrderDate.setText(myOrderDetailRP.getOrderTrackingLists().get(0).getDatetime());
                                    textViewDeliveryStatus.setText(myOrderDetailRP.getOrderTrackingLists().get(1).getStatus_title());
                                    textViewDeliveryDate.setText(myOrderDetailRP.getOrderTrackingLists().get(1).getDatetime());

                                    if (myOrderDetailRP.getOrderTrackingLists().get(1).getIs_status().equals("true")) {
                                        viewDeliver.setBackground(getResources().getDrawable(R.drawable.view_circle_red));
                                    } else {
                                        viewDeliver.setBackground(getResources().getDrawable(R.drawable.view_circle_green));
                                    }

                                    if (myOrderDetailRP.getReason().equals("") && myOrderDetailRP.getRefund_status().equals("")) {
                                        cardViewCancel.setVisibility(View.GONE);
                                    } else {
                                        cardViewCancel.setVisibility(View.VISIBLE);
                                        textViewReason.setText(myOrderDetailRP.getReason());
                                        if (myOrderDetailRP.getRefund_status().equals("")) {
                                            llRefundStatus.setVisibility(View.GONE);
                                        } else {
                                            llRefundStatus.setVisibility(View.VISIBLE);
                                            textViewRefundStatus.setText(myOrderDetailRP.getRefund_status());
                                        }
                                    }

                                    if (myOrderDetailRP.getCurrent_order_status().equals("true")) {
                                        llInvoice.setVisibility(View.VISIBLE);
                                        viewRating.setVisibility(View.VISIBLE);
                                        relRating.setVisibility(View.VISIBLE);
                                        ratingView.setRating(Float.parseFloat(myOrderDetailRP.getMy_rating()));
                                    } else {
                                        llInvoice.setVisibility(View.GONE);
                                        viewRating.setVisibility(View.GONE);
                                        relRating.setVisibility(View.GONE);
                                    }

                                    if (myOrderDetailRP.getCancel_product().equals("true") &&
                                            myOrderDetailRP.getOrder_other_items_status().equals("true")) {
                                        viewCancelOrder.setVisibility(View.VISIBLE);
                                    } else {
                                        viewCancelOrder.setVisibility(View.GONE);
                                    }
                                    if (myOrderDetailRP.getCancel_product().equals("true")) {
                                        textViewCancelThisProduct.setVisibility(View.VISIBLE);
                                    } else {
                                        textViewCancelThisProduct.setVisibility(View.GONE);
                                    }
                                    if (myOrderDetailRP.getOrder_other_items_status().equals("true")) {
                                        textViewCancelOrder.setVisibility(View.VISIBLE);
                                    } else {
                                        textViewCancelOrder.setVisibility(View.GONE);
                                    }
                                    if (myOrderDetailRP.getIs_claim().equals("true")) {
                                        textViewProRefundClaim.setVisibility(View.VISIBLE);
                                    } else {
                                        textViewProRefundClaim.setVisibility(View.GONE);
                                    }
                                    if (myOrderDetailRP.getIs_order_claim().equals("true")) {
                                        textViewOrderRefundClaim.setVisibility(View.VISIBLE);
                                    } else {
                                        textViewOrderRefundClaim.setVisibility(View.GONE);
                                    }

                                    if (myOrderDetailRP.getMyOrderLists().size() != 0) {
                                        cardViewOtherOrder.setVisibility(View.VISIBLE);
                                        myOrderDetailAdapter = new MyOrderDetailAdapter(getActivity(), "my_order", myOrderDetailRP.getMyOrderLists(), onClick);
                                        recyclerView.setAdapter(myOrderDetailAdapter);
                                    } else {
                                        cardViewOtherOrder.setVisibility(View.GONE);
                                    }

                                    textViewPriceDetail.setText(DecimalConverter.currencyFormat(myOrderDetailRP.getProduct_price()) + sign);
                                    if (!myOrderDetailRP.getDiscount_amt().equals("0")) {
                                        textViewPriceDetail.setPaintFlags(textViewPriceDetail.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    }
                                    textViewSellingPrice.setText(DecimalConverter.currencyFormat(myOrderDetailRP.getSelling_price()) + sign);
                                    textViewDiscount.setText(DecimalConverter.currencyFormat(myOrderDetailRP.getDiscount_amt()) + sign);
                                    if (method.isNumeric(myOrderDetailRP.getDelivery_charge())) {
                                        textViewDelivery.setText(DecimalConverter.currencyFormat(myOrderDetailRP.getDelivery_charge()) + sign);
                                    } else {
                                        textViewDelivery.setText(myOrderDetailRP.getDelivery_charge());
                                    }
                                    textViewQty.setText(myOrderDetailRP.getProduct_qty());
                                    textViewTotalPrice.setText(DecimalConverter.currencyFormat(myOrderDetailRP.getPayable_amt()) + sign);

                                    textViewOpdPrice.setText(DecimalConverter.currencyFormat(myOrderDetailRP.getOpd_price()) + sign);
                                    if (myOrderDetailRP.getCancel_order_amt().equals("")) {
                                        relCancelOrderPrice.setVisibility(View.GONE);
                                    } else {
                                        relCancelOrderPrice.setVisibility(View.VISIBLE);
                                        textViewOpdCancelOrderPrice.setText(DecimalConverter.currencyFormat(myOrderDetailRP.getCancel_order_amt()) + sign);
                                    }
                                    textViewOpdDiscount.setText(DecimalConverter.currencyFormat(myOrderDetailRP.getOpd_discount()) + sign);
                                    if (method.isNumeric(myOrderDetailRP.getDelivery_charge())) {
                                        textViewOpdDelivery.setText(DecimalConverter.currencyFormat(myOrderDetailRP.getOpd_delivery()) + sign);
                                    } else {
                                        textViewOpdDelivery.setText(myOrderDetailRP.getOpd_delivery());
                                    }
                                    textViewOpdAmountPayable.setText(DecimalConverter.currencyFormat(myOrderDetailRP.getOpd_amountPayable()) + sign);

                                    llMain.setVisibility(View.VISIBLE);

                                    relMyOrder.setOnClickListener(v -> onClick.click(0, "my_order_detail", myOrderDetailRP.getProduct_title(), myOrderDetailRP.getProduct_id(), "", "", ""));

                                    relRating.setOnClickListener(v -> startActivity(new Intent(getActivity(), RatingReview.class)
                                            .putExtra("product_id", myOrderDetailRP.getProduct_id())));

                                    llOrderStatus.setOnClickListener(v -> {
                                        OrderTrackingFragment orderTrackingFragment = new OrderTrackingFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("order_id", myOrderDetailRP.getOrder_id());
                                        bundle.putString("product_id", myOrderDetailRP.getProduct_id());
                                        orderTrackingFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                                                orderTrackingFragment, "")
                                                .addToBackStack("").commitAllowingStateLoss();
                                    });

                                    llInvoice.setOnClickListener(v -> {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                                        } else {
                                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                                        }
                                    });

                                    textViewCancelThisProduct.setOnClickListener(v -> {
                                        CancelOrderFragment cancelOrderFragment = new CancelOrderFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("type", "cancel_product");
                                        bundle.putString("order_id", myOrderDetailRP.getOrder_id());
                                        bundle.putString("order_unique_id", myOrderDetailRP.getOrder_unique_id());
                                        bundle.putString("product_id", myOrderDetailRP.getProduct_id());
                                        bundle.putString("payment_type", myOrderDetailRP.getPayment_mode());
                                        cancelOrderFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, cancelOrderFragment,
                                                getResources().getString(R.string.cancel_order)).addToBackStack(getResources().getString(R.string.cancel_order))
                                                .commitAllowingStateLoss();
                                    });

                                    textViewCancelOrder.setOnClickListener(v -> {
                                        CancelOrderFragment cancelOrderFragment = new CancelOrderFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("type", "cancel_order");
                                        bundle.putString("order_id", myOrderDetailRP.getOrder_id());
                                        bundle.putString("order_unique_id", myOrderDetailRP.getOrder_unique_id());
                                        bundle.putString("product_id", myOrderDetailRP.getProduct_id());
                                        bundle.putString("payment_type", myOrderDetailRP.getPayment_mode());
                                        cancelOrderFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, cancelOrderFragment,
                                                getResources().getString(R.string.cancel_order)).addToBackStack(getResources().getString(R.string.cancel_order))
                                                .commitAllowingStateLoss();
                                    });

                                    textViewProRefundClaim.setOnClickListener(v -> {
                                        CancelOrderFragment cancelOrderFragment = new CancelOrderFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("type", "claim_product");
                                        bundle.putString("order_id", myOrderDetailRP.getOrder_id());
                                        bundle.putString("order_unique_id", myOrderDetailRP.getOrder_unique_id());
                                        bundle.putString("product_id", myOrderDetailRP.getProduct_id());
                                        bundle.putString("payment_type", "isCancel");
                                        cancelOrderFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, cancelOrderFragment,
                                                getResources().getString(R.string.cancel_order)).addToBackStack(getResources().getString(R.string.cancel_order))
                                                .commitAllowingStateLoss();
                                    });

                                    textViewOrderRefundClaim.setOnClickListener(v -> {
                                        CancelOrderFragment cancelOrderFragment = new CancelOrderFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("type", "claim_order");
                                        bundle.putString("order_id", myOrderDetailRP.getOrder_id());
                                        bundle.putString("order_unique_id", myOrderDetailRP.getOrder_unique_id());
                                        bundle.putString("product_id", myOrderDetailRP.getProduct_id());
                                        bundle.putString("payment_type", "isCancel");
                                        cancelOrderFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, cancelOrderFragment,
                                                getResources().getString(R.string.cancel_order)).addToBackStack(getResources().getString(R.string.cancel_order))
                                                .commitAllowingStateLoss();
                                    });

                                } else {
                                    relNoData.setVisibility(View.VISIBLE);
                                    method.alertBox(myOrderDetailRP.getMsg());
                                }

                            } else if (myOrderDetailRP.getStatus().equals("2")) {
                                method.suspend(myOrderDetailRP.getMessage());
                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(myOrderDetailRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<MyOrderDetailRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail_data", t.toString());
                    relNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (myOrderDetailRP != null) {
                    downloadInvoice(myOrderDetailRP.getOrder_unique_id(), myOrderDetailRP.getDownload_invoice()); // perform action when allow permission success
                } else {
                    method.alertBox(getResources().getString(R.string.wrong));
                }
            } else {
                method.alertBox(getResources().getString(R.string.storage_permission));
            }
        }
    }

    public void downloadInvoice(String id, String fileUrl) {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {

            String file_name = id + ".pdf";
            final String CANCEL_TAG = "c_dig" + id;

            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
            okhttp3.Request.Builder builder = new okhttp3.Request.Builder()
                    .url(fileUrl)
                    .addHeader("Accept-Encoding", "identity")
                    .get()
                    .tag(CANCEL_TAG);

            okhttp3.Call call = client.newCall(builder.build());
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                    assert response.body() != null;
                    final okhttp3.ResponseBody responseBody = ProgressHelper.withProgress(response.body(), new ProgressUIListener() {

                        //if you don't need this method, don't override this method. It isn't an abstract method, just an empty method.
                        @Override
                        public void onUIProgressStart(long totalBytes) {
                            super.onUIProgressStart(totalBytes);
                            Log.d("progress_data", " totalBytes:- " + totalBytes);
                        }

                        @Override
                        public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                            Log.d("progress_data", "percent:- " + percent + " totalBytes:- " + totalBytes);
                        }

                        //if you don't need this method, don't override this method. It isn't an abstract method, just an empty method.
                        @Override
                        public void onUIProgressFinish() {
                            super.onUIProgressFinish();
                            progressDialog.dismiss();
                            method.alertBox(getResources().getString(R.string.download));
                        }
                    });

                    try {

                        BufferedSource source = responseBody.source();
                        File outFile = new File(Constant.appStorage + "/" + file_name);
                        BufferedSink sink = Okio.buffer(Okio.sink(outFile));
                        source.readAll(sink);
                        sink.flush();
                        source.close();

                    } catch (Exception e) {
                        Log.d("data_error", e.toString());
                        progressDialog.dismiss();
                        method.alertBox(getResources().getString(R.string.failed_try_again));
                    }

                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.d(Constant.exceptionError, e.toString());
            method.alertBox(getResources().getString(R.string.failed_try_again));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
