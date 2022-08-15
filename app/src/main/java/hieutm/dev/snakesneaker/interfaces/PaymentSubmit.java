package hieutm.dev.snakesneaker.interfaces;

import hieutm.dev.snakesneaker.response.PaymentSubmitRP;

public interface PaymentSubmit {

    void paymentStart();

    void paymentSuccess(PaymentSubmitRP paymentSubmitRP);

    void paymentFail(String message);

    void suspendUser(String message);

}
