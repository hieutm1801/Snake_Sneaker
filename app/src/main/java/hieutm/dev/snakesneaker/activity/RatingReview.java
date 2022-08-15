package hieutm.dev.snakesneaker.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.ReviewImageAdapter;
import hieutm.dev.snakesneaker.interfaces.ImageDelete;
import hieutm.dev.snakesneaker.item.ReviewProImageList;
import hieutm.dev.snakesneaker.response.DataRP;
import hieutm.dev.snakesneaker.response.RRSubmitRP;
import hieutm.dev.snakesneaker.response.RatingReviewRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GetPath;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Build.VERSION.SDK_INT;

public class RatingReview extends AppCompatActivity {

    private Method method;
    private ImageDelete imageDelete;
    private ProgressDialog progressDialog;
    private MaterialToolbar toolbar;
    private String productId;
    private int rate;
    private InputMethodManager imm;
    private List<ReviewProImageList> reviewProImageLists;
    private RecyclerView recyclerView;
    private ReviewImageAdapter reviewImageAdapter;
    private ImageView imageView;
    private TextInputEditText editText;
    private RatingView ratingView;
    private LinearLayout linearLayout;
    private RelativeLayout relMain;
    private ProgressBar progressBar;
    private MaterialTextView textView, textViewAddImage, textViewSubmit;

    private int REQUEST_CODE_PERMISSION = 101;
    private int REQUEST_CODE_CHOOSE = 100;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_review);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        productId = getIntent().getStringExtra("product_id");

        method = new Method(RatingReview.this);
        method.forceRTLIfSupported();

        reviewProImageLists = new ArrayList<>();

        progressDialog = new ProgressDialog(RatingReview.this);

        toolbar = findViewById(R.id.toolbar_rating_review);
        toolbar.setTitle(getResources().getString(R.string.rating_review));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        relMain = findViewById(R.id.rel_main_rating_review);
        progressBar = findViewById(R.id.progressBar_rating_review);
        imageView = findViewById(R.id.imageView_rating_review);
        textView = findViewById(R.id.textView_title_rating_review);
        ratingView = findViewById(R.id.ratingBar_rating_review);
        editText = findViewById(R.id.editText_rating_review);
        recyclerView = findViewById(R.id.recyclerView_rating_review);
        textViewAddImage = findViewById(R.id.textView_addImage_rating_review);
        textViewSubmit = findViewById(R.id.textView_submit_rating_review);

        linearLayout = findViewById(R.id.linearLayout_rating_review);
        method.bannerAd(linearLayout);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RatingReview.this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        relMain.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            myRatingReview(method.userId(), productId);
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void myRatingReview(String user_id, String product_id) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RatingReview.this));
        jsObj.addProperty("user_id", user_id);
        jsObj.addProperty("product_id", product_id);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RatingReviewRP> call = apiService.getRatingReview(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RatingReviewRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<RatingReviewRP> call, @NotNull Response<RatingReviewRP> response) {

                try {

                    RatingReviewRP ratingReviewRP = response.body();

                    assert ratingReviewRP != null;
                    if (ratingReviewRP.getStatus().equals("1")) {

                        textView.setText(ratingReviewRP.getProduct_title());

                        Glide.with(RatingReview.this).load(ratingReviewRP.getProduct_image())
                                .placeholder(R.drawable.placeholder_square).into(imageView);

                        rate = Integer.parseInt(ratingReviewRP.getRate());

                        ratingView.setRating(Float.parseFloat(ratingReviewRP.getRate()));
                        editText.setText(ratingReviewRP.getRating_desc());

                        reviewProImageLists.addAll(ratingReviewRP.getReviewProImageLists());

                        imageDelete = (id, type, position) -> {
                            if (id.equals("")) {
                                reviewProImageLists.remove(position);
                                reviewImageAdapter.notifyDataSetChanged();
                            } else {
                                deleteImage(reviewProImageLists.get(position).getId(), position);
                            }
                        };


                        if (reviewProImageLists.size() != 0) {
                            reviewImageAdapter = new ReviewImageAdapter(RatingReview.this, "review_image", reviewProImageLists, imageDelete);
                            recyclerView.setAdapter(reviewImageAdapter);
                        }

                        relMain.setVisibility(View.VISIBLE);

                        ratingView.setOnRatingChangedListener((oldRating, newRating) -> rate = (int) newRating);

                        textViewAddImage.setOnClickListener(v -> {
                            if (SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                            } else {
                                chooseGalleryImage();
                            }
                        });

                        textViewSubmit.setOnClickListener(v -> {

                            String review_description = editText.getText().toString();

                            editText.clearFocus();
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                            if (review_description.equals("") || review_description.isEmpty()) {
                                editText.requestFocus();
                                editText.setError(getResources().getString(R.string.please_enter_review));
                            } else if (rate == 0) {
                                method.alertBox(getResources().getString(R.string.please_rating));
                            } else {
                                submitRatingReview(method.userId(), product_id, review_description, String.valueOf(rate));
                            }

                        });

                    } else if (ratingReviewRP.getStatus().equals("2")) {
                        method.suspend(ratingReviewRP.getMessage());
                    } else {
                        method.alertBox(ratingReviewRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<RatingReviewRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void submitRatingReview(String user_id, String product_id, String review_desc, String rate) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        List<MultipartBody.Part> list = new ArrayList<>();

        MultipartBody.Part body = null;
        for (int i = 0; i < reviewProImageLists.size(); i++) {
            if (reviewProImageLists.get(i).getId().equals("")) {
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(reviewProImageLists.get(i).getImage()));
                // MultipartBody.Part is used to send also the actual file name
                body = MultipartBody.Part.createFormData("product_images[]", new File(reviewProImageLists.get(i).getImage()).getName(), requestFile);
                list.add(body);
            }
        }
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RatingReview.this));
        jsObj.addProperty("user_id", user_id);
        jsObj.addProperty("product_id", product_id);
        jsObj.addProperty("review_desc", review_desc);
        jsObj.addProperty("rate", rate);
        // add another part within the multipart request
        RequestBody requestBody_data =
                RequestBody.create(MediaType.parse("multipart/form-data"), API.toBase64(jsObj.toString()));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RRSubmitRP> call = apiService.submitRatingReview(requestBody_data, list);
        call.enqueue(new Callback<RRSubmitRP>() {
            @Override
            public void onResponse(@NotNull Call<RRSubmitRP> call, @NotNull Response<RRSubmitRP> response) {

                try {
                    RRSubmitRP rrSubmitRP = response.body();
                    assert rrSubmitRP != null;

                    if (rrSubmitRP.getStatus().equals("1")) {

                        if (rrSubmitRP.getSuccess().equals("1")) {

                            Events.RatingUpdate ratingUpdate = new Events.RatingUpdate(rrSubmitRP.getRate());
                            GlobalBus.getBus().post(ratingUpdate);

                            onBackPressed();

                        }

                        Toast.makeText(RatingReview.this, rrSubmitRP.getMsg(), Toast.LENGTH_SHORT).show();

                    } else if (rrSubmitRP.getStatus().equals("2")) {
                        method.suspend(rrSubmitRP.getMessage());
                    } else {
                        method.alertBox(rrSubmitRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }


                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<RRSubmitRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void deleteImage(String id, int position) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RatingReview.this));
        jsObj.addProperty("image_id", id);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DataRP> call = apiService.removeReviewImage(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DataRP>() {
            @Override
            public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                try {

                    DataRP dataRP = response.body();

                    assert dataRP != null;
                    if (dataRP.getStatus().equals("1")) {
                        if (dataRP.getSuccess().equals("1")) {
                            reviewProImageLists.remove(position);
                            reviewImageAdapter.notifyDataSetChanged();
                        }
                    } else {
                        method.alertBox(dataRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }

        });

    }

    private void chooseGalleryImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_CHOOSE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            try {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri imageUri = item.getUri();
                        String string = GetPath.getPath(RatingReview.this, imageUri);
                        reviewProImageLists.add(i, new ReviewProImageList("", string, ""));
                    }
                    if (reviewImageAdapter != null) {
                        reviewImageAdapter.notifyDataSetChanged();
                    } else {
                        reviewImageAdapter = new ReviewImageAdapter(RatingReview.this, "review_image", reviewProImageLists, imageDelete);
                        recyclerView.setAdapter(reviewImageAdapter);
                    }
                } else if (data.getData() != null) {
                    String string = GetPath.getPath(RatingReview.this, data.getData());
                    reviewProImageLists.add(0, new ReviewProImageList("", string, ""));
                    if (reviewImageAdapter != null) {
                        reviewImageAdapter.notifyDataSetChanged();
                    } else {
                        reviewImageAdapter = new ReviewImageAdapter(RatingReview.this, "review_image", reviewProImageLists, imageDelete);
                        recyclerView.setAdapter(reviewImageAdapter);
                    }
                }
            } catch (Exception e) {
                method.alertBox(getResources().getString(R.string.upload_folder_error));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseGalleryImage(); // perform action when allow permission success
            } else {
                method.alertBox(getResources().getString(R.string.storage_permission));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
