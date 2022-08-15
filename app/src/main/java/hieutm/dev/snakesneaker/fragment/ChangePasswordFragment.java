package hieutm.dev.snakesneaker.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.response.DataRP;
import hieutm.dev.snakesneaker.response.UserProfileRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {

    private Method method;
    private InputMethodManager imm;
    private MaterialButton button;
    private ProgressBar progressBar;
    private CircleImageView imageView;
    private LinearLayout llMain;
    private RelativeLayout relNoData;
    private MaterialTextView textViewName, textViewEmail;
    private TextInputEditText editTextOldPassword, editTextPassword, editTextConfirmPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.change_password_fragment, container, false);
        method = new Method(getActivity());

        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.change_password) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.change_password) + "</font>"));
            }
        }


        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        llMain = view.findViewById(R.id.ll_main_cp_fragment);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        progressBar = view.findViewById(R.id.progressbar_cp_fragment);
        imageView = view.findViewById(R.id.imageView_cp_fragment);
        textViewName = view.findViewById(R.id.textView_name_cp_fragment);
        textViewEmail = view.findViewById(R.id.textView_email_cp_fragment);
        editTextOldPassword = view.findViewById(R.id.editText_old_password_cp_fragment);
        editTextPassword = view.findViewById(R.id.editText_password_cp_fragment);
        editTextConfirmPassword = view.findViewById(R.id.editText_confirm_password_cp_fragment);
        button = view.findViewById(R.id.button_edit_cp_fragment);

        llMain.setVisibility(View.GONE);
        relNoData.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            getUserDetail(method.userId());
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    private void getUserDetail(String id) {

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

                                    Glide.with(getActivity().getApplicationContext()).load(userProfileRP.getUser_image())
                                            .placeholder(R.drawable.user_profile).into(imageView);

                                    textViewName.setText(userProfileRP.getUser_name());
                                    if (userProfileRP.getUser_email().equals("")) {
                                        textViewEmail.setVisibility(View.GONE);
                                    } else {
                                        textViewEmail.setVisibility(View.VISIBLE);
                                        textViewEmail.setText(userProfileRP.getUser_email());
                                    }

                                    llMain.setVisibility(View.VISIBLE);

                                    button.setOnClickListener(v -> save());

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

    private void save() {

        String oldPassword = editTextOldPassword.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        editTextOldPassword.setError(null);
        editTextPassword.setError(null);
        editTextConfirmPassword.setError(null);

        if (oldPassword.equals("") || oldPassword.isEmpty()) {
            editTextOldPassword.requestFocus();
            editTextOldPassword.setError(getResources().getString(R.string.please_enter_old_password));
        } else if (password.equals("") || password.isEmpty()) {
            editTextPassword.requestFocus();
            editTextPassword.setError(getResources().getString(R.string.please_enter_new_password));
        } else if (confirmPassword.equals("") || confirmPassword.isEmpty()) {
            editTextConfirmPassword.requestFocus();
            editTextConfirmPassword.setError(getResources().getString(R.string.please_enter_new_confirm_password));
        } else if (!password.equals(confirmPassword)) {
            method.alertBox(getResources().getString(R.string.new_password_not_match));
        } else {
            if (method.isNetworkAvailable()) {

                editTextOldPassword.clearFocus();
                editTextPassword.clearFocus();
                editTextConfirmPassword.clearFocus();
                imm.hideSoftInputFromWindow(editTextOldPassword.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextConfirmPassword.getWindowToken(), 0);

                passwordUpdate(method.userId(), oldPassword, password);

            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    private void passwordUpdate(String userId, String oldPassword, String password) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("old_password", oldPassword);
            jsObj.addProperty("new_password", password);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DataRP> call = apiService.updatePassword(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DataRP>() {
                @Override
                public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                    if (getActivity() != null) {

                        try {
                            DataRP dataRP = response.body();
                            assert dataRP != null;

                            if (dataRP.getStatus().equals("1")) {
                                if (dataRP.getSuccess().equals("1")) {
                                    editTextOldPassword.setText("");
                                    editTextPassword.setText("");
                                    editTextConfirmPassword.setText("");
                                }
                                method.alertBox(dataRP.getMsg());
                            } else if (dataRP.getStatus().equals("2")) {
                                method.suspend(dataRP.getMessage());
                            } else {
                                method.alertBox(dataRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
