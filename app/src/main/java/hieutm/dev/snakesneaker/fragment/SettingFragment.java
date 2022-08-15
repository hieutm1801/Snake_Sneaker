package hieutm.dev.snakesneaker.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.AboutUs;
import hieutm.dev.snakesneaker.activity.CancellationPolicy;
import hieutm.dev.snakesneaker.activity.ContactUs;
import hieutm.dev.snakesneaker.activity.Faq;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.activity.PrivacyPolicy;
import hieutm.dev.snakesneaker.activity.RRPolicy;
import hieutm.dev.snakesneaker.activity.SplashScreen;
import hieutm.dev.snakesneaker.activity.TermsOfUse;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textview.MaterialTextView;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;


public class SettingFragment extends Fragment {

    private Method method;
    private String themMode;

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.setting_fragment, container, false);
        method = new Method(getActivity());

        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.setting) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.setting) + "</font>"));
            }
        }

        SwitchCompat switchCompat = view.findViewById(R.id.switch_setting);
        MaterialTextView textViewContactUs = view.findViewById(R.id.textView_contactUs_setting);
        MaterialTextView textViewTermsOfUse = view.findViewById(R.id.textView_terms_Of_use_setting);
        MaterialTextView textViewFaq = view.findViewById(R.id.textView_faq_setting);
        MaterialTextView textViewPayment = view.findViewById(R.id.textView_payment_setting);
        MaterialTextView textViewRrPolicy = view.findViewById(R.id.textView_refund_return_policy_setting);
        MaterialTextView textViewCancellationPolicy = view.findViewById(R.id.textView_cancellation_policy_setting);
        MaterialTextView textViewShareApp = view.findViewById(R.id.textView_shareApp_setting);
        MaterialTextView textViewRateApp = view.findViewById(R.id.textView_rateApp_setting);
        MaterialTextView textViewMoreApp = view.findViewById(R.id.textView_moreApp_setting);
        MaterialTextView textViewPrivacyPolicy = view.findViewById(R.id.textView_privacy_policy_setting);
        MaterialTextView textViewAboutUs = view.findViewById(R.id.textView_aboutUs_setting);
        MaterialTextView textViewThemType = view.findViewById(R.id.textView_themType_setting);
        ImageView imageView = view.findViewById(R.id.imageView_them_setting);
        RelativeLayout relThem = view.findViewById(R.id.rel_them_setting);

        switchCompat.setChecked(method.pref.getBoolean(method.notification, true));

        if (method.isDarkMode()) {
            Glide.with(getActivity().getApplicationContext()).load(R.drawable.mode_dark)
                    .placeholder(R.drawable.placeholder_square)
                    .into(imageView);
        } else {
            Glide.with(getActivity().getApplicationContext()).load(R.drawable.mode_icon)
                    .placeholder(R.drawable.placeholder_square)
                    .into(imageView);
        }

        switch (method.themMode()) {
            case "system":
                textViewThemType.setText(getResources().getString(R.string.system_default));
                break;
            case "light":
                textViewThemType.setText(getResources().getString(R.string.light));
                break;
            case "dark":
                textViewThemType.setText(getResources().getString(R.string.dark));
                break;
            default:
                break;
        }

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            OneSignal.unsubscribeWhenNotificationsAreDisabled(isChecked);
            method.editor.putBoolean(method.notification, isChecked);
            method.editor.commit();
        });

        textViewContactUs.setOnClickListener(v -> startActivity(new Intent(getActivity(), ContactUs.class)));

        textViewTermsOfUse.setOnClickListener(v -> startActivity(new Intent(getActivity(), TermsOfUse.class)));

        textViewFaq.setOnClickListener(v -> startActivity(new Intent(getActivity(), Faq.class)
                .putExtra("type", "faq")));

        textViewPayment.setOnClickListener(v -> startActivity(new Intent(getActivity(), Faq.class)
                .putExtra("type", "payment")));

        textViewRrPolicy.setOnClickListener(v -> startActivity(new Intent(getActivity(), RRPolicy.class)));

        textViewCancellationPolicy.setOnClickListener(v -> startActivity(new Intent(getActivity(), CancellationPolicy.class)));

        textViewShareApp.setOnClickListener(v -> shareApp());

        textViewRateApp.setOnClickListener(v -> rateApp());

        textViewMoreApp.setOnClickListener(v -> moreApp());

        textViewAboutUs.setOnClickListener(v -> startActivity(new Intent(getActivity(), AboutUs.class)));

        textViewPrivacyPolicy.setOnClickListener(v -> startActivity(new Intent(getActivity(), PrivacyPolicy.class)));

        relThem.setOnClickListener(v -> {

            Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialogbox_them);
            if (method.isRtl()) {
                dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup_them);
            MaterialTextView textViewOk = dialog.findViewById(R.id.textView_ok_them);
            MaterialTextView textViewCancel = dialog.findViewById(R.id.textView_cancel_them);

            switch (method.themMode()) {
                case "system":
                    radioGroup.check(radioGroup.getChildAt(0).getId());
                    break;
                case "light":
                    radioGroup.check(radioGroup.getChildAt(1).getId());
                    break;
                case "dark":
                    radioGroup.check(radioGroup.getChildAt(2).getId());
                    break;
                default:
                    break;
            }

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                MaterialRadioButton rb = group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    switch (checkedId) {
                        case R.id.radioButton_system_them:
                            themMode = "system";
                            break;
                        case R.id.radioButton_light_them:
                            themMode = "light";
                            break;
                        case R.id.radioButton_dark_them:
                            themMode = "dark";
                            break;
                        default:
                            break;
                    }
                }
            });

            textViewOk.setOnClickListener(v12 -> {
                method.editor.putString(method.themSetting, themMode);
                method.editor.commit();
                dialog.dismiss();

                startActivity(new Intent(getActivity(), SplashScreen.class));
                getActivity().finishAffinity();

            });

            textViewCancel.setOnClickListener(v1 -> dialog.dismiss());

            dialog.show();

        });

        return view;

    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getApplication().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getApplication().getPackageName())));
        }
    }

    private void moreApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.play_more_app))));
    }

    private void shareApp() {

        try {

            String string = getResources().getString(R.string.Let_me_recommend_you_this_application) + "\n\n"
                    + "https://play.google.com/store/apps/details?id=" + getActivity().getApplication().getPackageName();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, string);
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_one)));

        } catch (Exception e) {
            e.toString();
            Log.d("error_data", e.toString());
        }

    }

}
