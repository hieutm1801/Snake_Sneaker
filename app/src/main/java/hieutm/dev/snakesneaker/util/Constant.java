package hieutm.dev.snakesneaker.util;

import android.os.Environment;

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.PayPalClient;
import hieutm.dev.snakesneaker.item.ProImageList;
import hieutm.dev.snakesneaker.item.ReviewProImageList;
import hieutm.dev.snakesneaker.response.AppRP;

import java.util.ArrayList;
import java.util.List;

public class Constant {

    private Constant() {
        throw new UnsupportedOperationException();
    }

    //app currency code
    public static String currency = "";

    //storage folder path
    public static String appStorage = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS;

    public static String stringSameColor = "#FF5152";

    public static String colorCompareBg = stringSameColor;
    public static String colorCompareIcon = "#FFFFFF";

    //Change WebView text color light and dark mode
    public static String webTextLight = "#8b8b8b;";
    public static String webTextDark = "#FFFFFF;";

    //Change WebView link color light and dark mode
    public static String webLinkLight = "#0782C1;";
    public static String webLinkDark = "#0782C1;";

    public static int ADCOUNT = 0;
    public static int ADCOUNTSHOW = 0;

    public static String pre_min = "";
    public static String pre_max = "";
    public static String pre_min_temp = "";//temporary price min
    public static String pre_max_temp = "";//temporary price max

    public static String brand_ids = "";
    public static String brand_ids_temp = "";//temporary brand ids

    public static String sizes = "";
    public static String sizes_temp = "";//temporary size list

    public static AppRP appRP;

    public static List<ReviewProImageList> reviewProImageLists = new ArrayList<>();
    public static List<ProImageList> proImageLists = new ArrayList<>();

    //log tag
    public static String exceptionError = "exception_error";
    public static String failApi = "fail_api";

    public static BraintreeClient braintreeClient;
    public static PayPalClient payPalClient;

    public static String braintreeType="";
    public static String braintreeCouponId="";
    public static String braintreeAddressId="";
    public static String braintreeCartIds="";

}
