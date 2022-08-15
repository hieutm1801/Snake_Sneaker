package hieutm.dev.snakesneaker.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.response.UserProfileRP;
import hieutm.dev.snakesneaker.response.UserProfileUpdateRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private Method method;
    private String profileId;
    private ProgressBar progressBar;
    private ImageView imageViewAdd;
    private String imageProfile;
    private InputMethodManager imm;
    private LinearLayout llMain;
    private MaterialButton buttonSubmit;
    private RelativeLayout relNoData;
    private CircleImageView circleImageView;
    private boolean isProfile = false, isRemove = false;
    private TextInputEditText editTextName, editTextEmail, editTextPhoneNo;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_fragment, container, false);

        GlobalBus.getBus().register(this);

        method = new Method(getActivity());
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.edit_profile) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.edit_profile) + "</font>"));
            }
        }

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        assert getArguments() != null;
        profileId = getArguments().getString("profileId");

        llMain = view.findViewById(R.id.ll_main_editPro);
        progressBar = view.findViewById(R.id.progressbar_edit_profile);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        progressBar = view.findViewById(R.id.progressbar_edit_profile);
        circleImageView = view.findViewById(R.id.imageView_user_editPro);
        imageViewAdd = view.findViewById(R.id.imageView_editPro);
        editTextName = view.findViewById(R.id.editText_name_edit_profile);
        editTextEmail = view.findViewById(R.id.editText_email_edit_profile);
        editTextPhoneNo = view.findViewById(R.id.editText_phone_edit_profile);
        TextInputLayout textInputEmail = view.findViewById(R.id.textInput_email_edit_profile);
        buttonSubmit = view.findViewById(R.id.button_edit_profile);

        relNoData.setVisibility(View.GONE);
        llMain.setVisibility(View.GONE);

        if (method.isDarkMode()) {
            imageViewAdd.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_profile));
        } else {
            imageViewAdd.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_profile_white));
        }

        if (method.getLoginType().equals("google") || method.getLoginType().equals("facebook")) {
            editTextName.setFocusable(false);
            editTextName.setCursorVisible(false);
            textInputEmail.setVisibility(View.GONE);
        } else {
            textInputEmail.setVisibility(View.VISIBLE);
        }

        circleImageView.setOnClickListener(v -> {
            BottomSheetDialogFragment fragment = new ProImage();
            fragment.show(getActivity().getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
        });

        buttonSubmit.setOnClickListener(v -> save());

        if (method.isLogin()) {
            if (method.isNetworkAvailable()) {
                profile(method.userId());
            } else {
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.you_have_not_login));
        }

        return view;

    }

    @Subscribe
    public void getData(Events.ProImage proImage) {
        isProfile = proImage.isIs_profile();
        isRemove = proImage.isIs_remove();
        if (proImage.isIs_profile()) {
            imageProfile = proImage.getImage_path();
            Uri uri = Uri.fromFile(new File(imageProfile));
            Glide.with(getActivity().getApplicationContext()).load(uri)
                    .placeholder(R.drawable.user_profile)
                    .into(circleImageView);
        }
        if (proImage.isIs_remove()) {
            Glide.with(getActivity().getApplicationContext()).load(R.drawable.user_profile)
                    .placeholder(R.drawable.user_profile)
                    .into(circleImageView);
        }
    }


    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void save() {

        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String phoneNo = editTextPhoneNo.getText().toString();

        editTextName.setError(null);
        editTextEmail.setError(null);
        editTextPhoneNo.setError(null);

        if (name.equals("") || name.isEmpty()) {
            editTextName.requestFocus();
            editTextName.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (phoneNo.equals("") || phoneNo.isEmpty()) {
            editTextPhoneNo.requestFocus();
            editTextPhoneNo.setError(getResources().getString(R.string.please_enter_phone));
        } else {
            if (method.isNetworkAvailable()) {

                editTextName.clearFocus();
                editTextEmail.clearFocus();
                editTextPhoneNo.clearFocus();
                imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextPhoneNo.getWindowToken(), 0);

                profileUpdate(profileId, name, phoneNo, imageProfile);

            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    private void profile(String id) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", id);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserProfileRP> call = apiService.getProfile(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<UserProfileRP>() {
                @Override
                public void onResponse(@NotNull Call<UserProfileRP> call, @NotNull Response<UserProfileRP> response) {

                    if (getActivity() != null) {

                        try {

                            UserProfileRP userProfileRP = response.body();
                            assert userProfileRP != null;

                            if (userProfileRP.getStatus().equals("1")) {

                                if (userProfileRP.getSuccess().equals("1")) {

                                    imageProfile = userProfileRP.getUser_image();

                                    Glide.with(getActivity().getApplicationContext()).load(userProfileRP.getUser_image())
                                            .placeholder(R.drawable.user_profile).into(circleImageView);

                                    editTextName.setText(userProfileRP.getUser_name());
                                    editTextEmail.setText(userProfileRP.getUser_email());
                                    editTextPhoneNo.setText(userProfileRP.getUser_phone());

                                    llMain.setVisibility(View.VISIBLE);

                                } else {
                                    relNoData.setVisibility(View.VISIBLE);
                                    method.alertBox(userProfileRP.getMsg());
                                }

                            } else if (userProfileRP.getStatus().equals("2")) {
                                method.suspend(userProfileRP.getMessage());
                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(userProfileRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<UserProfileRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    relNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

    private void profileUpdate(String id, String sendName, String sendPhone, String profileImage) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);
            MultipartBody.Part body = null;

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("id", id);
            jsObj.addProperty("user_name", sendName);
            jsObj.addProperty("user_phone", sendPhone);
            jsObj.addProperty("is_remove", isRemove);
            if (isProfile) {
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(profileImage));
                // MultipartBody.Part is used to send also the actual file name
                body = MultipartBody.Part.createFormData("user_image", new File(profileImage).getName(), requestFile);
            }
            // add another part within the multipart request
            RequestBody requestBody_data =
                    RequestBody.create(MediaType.parse("multipart/form-data"), API.toBase64(jsObj.toString()));
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserProfileUpdateRP> call = apiService.editProfile(requestBody_data, body);
            call.enqueue(new Callback<UserProfileUpdateRP>() {
                @Override
                public void onResponse(@NotNull Call<UserProfileUpdateRP> call, @NotNull Response<UserProfileUpdateRP> response) {

                    if (getActivity() != null) {
                        try {
                            UserProfileUpdateRP userProfileUpdateRP = response.body();
                            assert userProfileUpdateRP != null;

                            if (userProfileUpdateRP.getStatus().equals("1")) {

                                if (userProfileUpdateRP.getSuccess().equals("1")) {
                                    Toast.makeText(getActivity(), userProfileUpdateRP.getMsg(), Toast.LENGTH_SHORT).show();
                                    Events.Login loginNotify = new Events.Login(userProfileUpdateRP.getUser_name(), userProfileUpdateRP.getUser_email(), userProfileUpdateRP.getUser_image());
                                    GlobalBus.getBus().post(loginNotify);
                                    Events.ProfileUpdate profileUpdate = new Events.ProfileUpdate("");
                                    GlobalBus.getBus().post(profileUpdate);
                                    getActivity().getSupportFragmentManager().popBackStack();
                                } else {
                                    method.alertBox(userProfileUpdateRP.getMsg());
                                }

                            } else if (userProfileUpdateRP.getStatus().equals("2")) {
                                method.suspend(userProfileUpdateRP.getMessage());
                            } else {
                                method.alertBox(userProfileUpdateRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NotNull Call<UserProfileUpdateRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
