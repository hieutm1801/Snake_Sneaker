package hieutm.dev.snakesneaker.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OtpCheck implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("email_otp_status")
    private String email_otp_status;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail_otp_status() {
        return email_otp_status;
    }
}
