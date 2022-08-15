package hieutm.dev.snakesneaker.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.multidex.MultiDex;

import com.google.android.gms.ads.MobileAds;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.SplashScreen;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class YouApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(YouApplication.this, initializationStatus -> {

        });

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        //id setup firebase: 98f8ea74-a327-46a2-a8c2-d4f428c180b1
        OneSignal.setAppId("98f8ea74-a327-46a2-a8c2-d4f428c180b1");
        OneSignal.setNotificationOpenedHandler(new NotificationExtenderExample());

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/poppins_medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }

    class NotificationExtenderExample implements OneSignal.OSNotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {

            try {

                String id = result.getNotification().getAdditionalData().getString("id");
                String subId = result.getNotification().getAdditionalData().getString("sub_id");
                String title = result.getNotification().getAdditionalData().getString("title");
                String type = result.getNotification().getAdditionalData().getString("type");
                String url = result.getNotification().getAdditionalData().getString("external_link");

                Intent intent;
                if (!url.equals("false") && !url.trim().isEmpty()) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                } else {
                    intent = new Intent(YouApplication.this, SplashScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", id);
                    intent.putExtra("sub_id", subId);
                    intent.putExtra("type", type);
                    intent.putExtra("title", title);
                }
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
