package hieutm.dev.snakesneaker.rest;


import hieutm.dev.snakesneaker.response.AboutUsRP;
import hieutm.dev.snakesneaker.response.AddEditRemoveAddressRP;
import hieutm.dev.snakesneaker.response.AddEditRemoveBankRP;
import hieutm.dev.snakesneaker.response.AddToCartRP;
import hieutm.dev.snakesneaker.response.AddressListRP;
import hieutm.dev.snakesneaker.response.AppRP;
import hieutm.dev.snakesneaker.response.ApplyCouponRP;
import hieutm.dev.snakesneaker.response.BankDetailRP;
import hieutm.dev.snakesneaker.response.BraintreePayIdRP;
import hieutm.dev.snakesneaker.response.BrandFilterRP;
import hieutm.dev.snakesneaker.response.BrandRP;
import hieutm.dev.snakesneaker.response.CancelOrderProRP;
import hieutm.dev.snakesneaker.response.CancellationPolicyRP;
import hieutm.dev.snakesneaker.response.CartRP;
import hieutm.dev.snakesneaker.response.CategoryRP;
import hieutm.dev.snakesneaker.response.ChangeAddressRP;
import hieutm.dev.snakesneaker.response.ContactRP;
import hieutm.dev.snakesneaker.response.CountryRP;
import hieutm.dev.snakesneaker.response.CouponsDetailRP;
import hieutm.dev.snakesneaker.response.CouponsRP;
import hieutm.dev.snakesneaker.response.DataRP;
import hieutm.dev.snakesneaker.response.DeepLinkRP;
import hieutm.dev.snakesneaker.response.FaqRp;
import hieutm.dev.snakesneaker.response.FavouriteRP;
import hieutm.dev.snakesneaker.response.FilterRP;
import hieutm.dev.snakesneaker.response.GetAddressRP;
import hieutm.dev.snakesneaker.response.GetBankDetailRP;
import hieutm.dev.snakesneaker.response.HomeRP;
import hieutm.dev.snakesneaker.response.ISAddRP;
import hieutm.dev.snakesneaker.response.LoginRP;
import hieutm.dev.snakesneaker.response.MoMoPayRP;
import hieutm.dev.snakesneaker.response.MyOrderDetailRP;
import hieutm.dev.snakesneaker.response.MyOrderRP;
import hieutm.dev.snakesneaker.response.OfferRP;
import hieutm.dev.snakesneaker.response.OrderSummaryRP;
import hieutm.dev.snakesneaker.response.OrderTrackingRP;
import hieutm.dev.snakesneaker.response.OtpCheck;
import hieutm.dev.snakesneaker.response.BraintreeRP;
import hieutm.dev.snakesneaker.response.PayStackKeyRP;
import hieutm.dev.snakesneaker.response.PayableAmountRP;
import hieutm.dev.snakesneaker.response.PaymentMethodRP;
import hieutm.dev.snakesneaker.response.PaymentSubmitRP;
import hieutm.dev.snakesneaker.response.PriceSelectionRP;
import hieutm.dev.snakesneaker.response.PrivacyPolicyRP;
import hieutm.dev.snakesneaker.response.ProDetailRP;
import hieutm.dev.snakesneaker.response.ProRP;
import hieutm.dev.snakesneaker.response.ProReviewRP;
import hieutm.dev.snakesneaker.response.ProReviewTypeRP;
import hieutm.dev.snakesneaker.response.RRPolicyRP;
import hieutm.dev.snakesneaker.response.RRSubmitRP;
import hieutm.dev.snakesneaker.response.RatingReviewRP;
import hieutm.dev.snakesneaker.response.RazorPayRP;
import hieutm.dev.snakesneaker.response.RegisterRP;
import hieutm.dev.snakesneaker.response.RemoveCartRP;
import hieutm.dev.snakesneaker.response.RemoveCouponRP;
import hieutm.dev.snakesneaker.response.SizeFilterRP;
import hieutm.dev.snakesneaker.response.StripPaymentCheckRp;
import hieutm.dev.snakesneaker.response.StripRp;
import hieutm.dev.snakesneaker.response.SubCatRP;
import hieutm.dev.snakesneaker.response.TermsOfUseRP;
import hieutm.dev.snakesneaker.response.UpdateCartRP;
import hieutm.dev.snakesneaker.response.UserProfileRP;
import hieutm.dev.snakesneaker.response.UserProfileUpdateRP;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    //get app data
    @POST("apis/app_details")
    @FormUrlEncoded
    Call<AppRP> getAppData(@Field("data") String data);

    //get about us
    @POST("apis/about_us")
    @FormUrlEncoded
    Call<AboutUsRP> getAboutUs(@Field("data") String data);

    //get privacy policy
    @POST("apis/privacy_policy")
    @FormUrlEncoded
    Call<PrivacyPolicyRP> getPrivacyPolicy(@Field("data") String data);

    //get terms of use
    @POST("apis/get_data")
    @FormUrlEncoded
    Call<TermsOfUseRP> getTermsOfUse(@Field("data") String data);

    //get refund return policy
    @POST("apis/get_data")
    @FormUrlEncoded
    Call<RRPolicyRP> getRRPolicy(@Field("data") String data);

    //get cancellation policy
    @POST("apis/get_data")
    @FormUrlEncoded
    Call<CancellationPolicyRP> getCancellationPolicy(@Field("data") String data);

    //get faq and payment
    @POST("apis/faq")
    @FormUrlEncoded
    Call<FaqRp> getFaq(@Field("data") String data);

    //login
    @POST("apis/login")
    @FormUrlEncoded
    Call<LoginRP> getLogin(@Field("data") String data);

    //check otp verification
    @POST("apis/check_otp_status")
    @FormUrlEncoded
    Call<OtpCheck> checkOtp(@Field("data") String data);

    //otp verification
    @POST("apis/email_verify")
    @FormUrlEncoded
    Call<DataRP> getOtp(@Field("data") String data);

    //register
    @POST("apis/register")
    @FormUrlEncoded
    Call<RegisterRP> getRegister(@Field("data") String data);

    //forgot password
    @POST("apis/forgot_password")
    @FormUrlEncoded
    Call<DataRP> getForgotPass(@Field("data") String data);

    //get profile detail
    @POST("apis/profile")
    @FormUrlEncoded
    Call<UserProfileRP> getProfile(@Field("data") String data);

    //edit profile
    @POST("apis/edit_profile")
    @Multipart
    Call<UserProfileUpdateRP> editProfile(@Part("data") RequestBody data, @Part MultipartBody.Part part);

    //update password
    @POST("apis/change_password")
    @FormUrlEncoded
    Call<DataRP> updatePassword(@Field("data") String data);

    //get contact subject
    @POST("apis/contact_subjects")
    @FormUrlEncoded
    Call<ContactRP> getContactSub(@Field("data") String data);

    //submit contact
    @POST("apis/contact_form")
    @FormUrlEncoded
    Call<DataRP> submitContact(@Field("data") String data);

    //Get address country
    @POST("apis/get_countries")
    @FormUrlEncoded
    Call<CountryRP> getCountry(@Field("data") String data);

    //Address Check
    @POST("apis/is_address_avail")
    @FormUrlEncoded
    Call<ISAddRP> isAddress(@Field("data") String data);

    //Get address list
    @POST("apis/get_addresses")
    @FormUrlEncoded
    Call<AddressListRP> getAddressList(@Field("data") String data);

    //get address
    @POST("apis/single_address")
    @FormUrlEncoded
    Call<GetAddressRP> getAddress(@Field("data") String data);

    //add address
    @POST("apis/addedit_address")
    @FormUrlEncoded
    Call<AddEditRemoveAddressRP> addEditAddress(@Field("data") String data);

    //Delete address
    @POST("apis/delete_address")
    @FormUrlEncoded
    Call<AddEditRemoveAddressRP> deleteAddress(@Field("data") String data);

    //Change address
    @POST("apis/change_address")
    @FormUrlEncoded
    Call<ChangeAddressRP> getChangeAddress(@Field("data") String data);

    //Home
    @POST("apis/home")
    @FormUrlEncoded
    Call<HomeRP> getHome(@Field("data") String data);

    //category list
    @POST("apis/categories")
    @FormUrlEncoded
    Call<CategoryRP> getCategory(@Field("data") String data);

    //Sub category
    @POST("apis/sub_categories")
    @FormUrlEncoded
    Call<SubCatRP> getSubCat(@Field("data") String data);

    //brand list
    @POST("apis/brands")
    @FormUrlEncoded
    Call<BrandRP> getBrand(@Field("data") String data);

    //Offer list
    @POST("apis/offers")
    @FormUrlEncoded
    Call<OfferRP> getOffer(@Field("data") String data);

    //Favourite list
    @POST("apis/my_wishlist")
    @FormUrlEncoded
    Call<ProRP> getFavourite(@Field("data") String data);

    //Add to favourite
    @POST("apis/wishlist")
    @FormUrlEncoded
    Call<FavouriteRP> addToFavourite(@Field("data") String data);

    //brands product
    @POST("apis/products_by_brand")
    @FormUrlEncoded
    Call<ProRP> getBrandPro(@Field("data") String data);

    //products by banner
    @POST("apis/products_by_banner")
    @FormUrlEncoded
    Call<ProRP> getSliderPro(@Field("data") String data);

    //products by today deal
    @POST("apis/today_deal")
    @FormUrlEncoded
    Call<ProRP> getTodayDealPro(@Field("data") String data);

    //products by today deal
    @POST("apis/get_latest_products")
    @FormUrlEncoded
    Call<ProRP> getLatestPro(@Field("data") String data);

    //products by top rated
    @POST("apis/get_top_rated_products")
    @FormUrlEncoded
    Call<ProRP> getTopRatedPro(@Field("data") String data);

    //products by recent
    @POST("apis/get_recent_viewed_products")
    @FormUrlEncoded
    Call<ProRP> getRecentPro(@Field("data") String data);

    //products by offer
    @POST("apis/products_by_offer")
    @FormUrlEncoded
    Call<ProRP> getOfferPro(@Field("data") String data);

    //products by search
    @POST("apis/search")
    @FormUrlEncoded
    Call<ProRP> getSearchPro(@Field("data") String data);

    //products by category and sub category
    @POST("apis/productList_cat_sub")
    @FormUrlEncoded
    Call<ProRP> getCatSubPro(@Field("data") String data);

    //Filter list
    @POST("apis/filter_list")
    @FormUrlEncoded
    Call<FilterRP> getFilterType(@Field("data") String data);

    //Price selection filter
    @POST("apis/price_filter")
    @FormUrlEncoded
    Call<PriceSelectionRP> getPriceSelection(@Field("data") String data);

    //Brand filter
    @POST("apis/brand_filter")
    @FormUrlEncoded
    Call<BrandFilterRP> getBrandFilter(@Field("data") String data);

    //Size filter
    @POST("apis/size_filter")
    @FormUrlEncoded
    Call<SizeFilterRP> getSizeFilter(@Field("data") String data);

    //Filter product
    @POST("apis/apply_filter")
    @FormUrlEncoded
    Call<ProRP> getFilterPro(@Field("data") String data);

    //Product detail
    @POST("apis/single_product")
    @FormUrlEncoded
    Call<ProDetailRP> getProDetail(@Field("data") String data);

    //Product detail by deep link
    @POST("apis/get_id_by_slug")
    @FormUrlEncoded
    Call<DeepLinkRP> getProId(@Field("data") String data);

    //Add to cart
    @POST("apis/cart_add_update")
    @FormUrlEncoded
    Call<AddToCartRP> addToCart(@Field("data") String data);

    //remove cart item
    @POST("apis/cart_item_delete")
    @FormUrlEncoded
    Call<RemoveCartRP> removeCartItem(@Field("data") String data);

    //cart item update
    @POST("apis/cart_add_update")
    @FormUrlEncoded
    Call<UpdateCartRP> getCartItemUpdate(@Field("data") String data);

    //Get my cart list
    @POST("apis/my_cart")
    @FormUrlEncoded
    Call<CartRP> getCartList(@Field("data") String data);

    //Order summary
    @POST("apis/order_summary")
    @FormUrlEncoded
    Call<OrderSummaryRP> getOrderDetail(@Field("data") String data);

    //Coupon list
    @POST("apis/coupons")
    @FormUrlEncoded
    Call<CouponsRP> getCoupons(@Field("data") String data);

    //Coupon detail
    @POST("apis/single_coupon")
    @FormUrlEncoded
    Call<CouponsDetailRP> getCouponsDetail(@Field("data") String data);

    //Apply coupon
    @POST("apis/apply_coupon")
    @FormUrlEncoded
    Call<ApplyCouponRP> getApplyCouponsDetail(@Field("data") String data);

    //Remove coupon
    @POST("apis/remove_coupon")
    @FormUrlEncoded
    Call<RemoveCouponRP> getRemoveCouponDetail(@Field("data") String data);

    //Remove temporary card data
    @POST("apis/remove_temp_cart")
    @FormUrlEncoded
    Call<DataRP> removeTempCart(@Field("data") String data);

    //Get payment detail
    @POST("apis/payment_details")
    @FormUrlEncoded
    Call<PaymentMethodRP> getPaymentMethod(@Field("data") String data);

    //Get payment detail
    @POST("apis/get_payable_amount")
    @FormUrlEncoded
    Call<PayableAmountRP> getPayableAmount(@Field("data") String data);

    //Get braintree amount
    @POST("apis/generate_braintree_amount")
    @FormUrlEncoded
    Call<BraintreeRP> getBraintreeAmount(@Field("data") String data);

    //Get braintree payment id
    @POST("apis/generate_braintree_payment")
    @FormUrlEncoded
    Call<BraintreePayIdRP> getBraintreePayId(@Field("data") String data);

    //RazorPay response
    @POST("apis/razorpay_order_id")
    @FormUrlEncoded
    Call<RazorPayRP> getRazorPayData(@Field("data") String data);

    //MoMoPay response
    @POST("apis/momo_order_id")
    @FormUrlEncoded
    Call<MoMoPayRP> getMoMoPayData(@Field("data") String data);

    //Check strip payment
    @POST("apis/stripe_validate_checkout_amt")
    @FormUrlEncoded
    Call<StripPaymentCheckRp> checkStripPayment(@Field("data") String data);

    //get strip token
    @POST("apis/stripe_token")
    @FormUrlEncoded
    Call<StripRp> getStripToken(@Field("data") String data);

    //get paystack public key
    @POST("apis/generate_paystack_amount")
    @FormUrlEncoded
    Call<PayStackKeyRP> getPayStackKey(@Field("data") String data);

    //submit payment
    @POST("apis/payment")
    @FormUrlEncoded
    Call<PaymentSubmitRP> submitPayment(@Field("data") String data);

    //My order list
    @POST("apis/my_order")
    @FormUrlEncoded
    Call<MyOrderRP> getMyOderList(@Field("data") String data);

    //My order detail
    @POST("apis/order_detail")
    @FormUrlEncoded
    Call<MyOrderDetailRP> getMyOrderDetail(@Field("data") String data);

    //Cancel order and product
    @POST("apis/order_or_product_cancel")
    @FormUrlEncoded
    Call<CancelOrderProRP> cancelOrderORProduct(@Field("data") String data);

    //Claim order and product
    @POST("apis/claim_refund")
    @FormUrlEncoded
    Call<DataRP> claimOrderORProduct(@Field("data") String data);

    //My order tracking
    @POST("apis/order_status")
    @FormUrlEncoded
    Call<OrderTrackingRP> getOrderTrackingList(@Field("data") String data);

    //Bank list
    @POST("apis/get_bank_list")
    @FormUrlEncoded
    Call<BankDetailRP> getBankList(@Field("data") String data);

    //Delete bank
    @POST("apis/delete_bank_account")
    @FormUrlEncoded
    Call<AddEditRemoveBankRP> deleteBank(@Field("data") String data);

    //Get bank detail
    @POST("apis/get_bank_details")
    @FormUrlEncoded
    Call<GetBankDetailRP> getBankDetail(@Field("data") String data);

    //Submit bank detail
    @POST("apis/addedit_bank_account")
    @FormUrlEncoded
    Call<AddEditRemoveBankRP> submitBankDetail(@Field("data") String data);

    //Get rating review
    @POST("apis/my_review")
    @FormUrlEncoded
    Call<RatingReviewRP> getRatingReview(@Field("data") String data);

    //Submit rating review
    @POST("apis/product_review")
    @Multipart
    Call<RRSubmitRP> submitRatingReview(@Part("data") RequestBody data, @Part List<MultipartBody.Part> part);

    //Remove review image
    @POST("apis/remove_review_image")
    @FormUrlEncoded
    Call<DataRP> removeReviewImage(@Field("data") String data);

    //review type and detail
    @POST("apis/review_filter_list")
    @FormUrlEncoded
    Call<ProReviewTypeRP> getReviewType(@Field("data") String data);

    //all review
    @POST("apis/users_review")
    @FormUrlEncoded
    Call<ProReviewRP> getProReviewDetail(@Field("data") String data);

}
