package com.github.aistech.billing.exceptions;

import android.annotation.TargetApi;
import android.os.Build;

import com.github.aistech.billing.model.BillingResult;

/**
 * Exception thrown when something went wrong with in-app billing.
 * An BillingException has an associated BillingResult (an error).
 * To get the BillingResult result that caused this exception to be thrown,
 * call {@link #getResult()}.
 * Created by jonathan on 19/10/16.
 */
public class BillingException extends Exception {

    private BillingResult result;

    public BillingException() {
    }

    public BillingException(String message) {
        super(message);
    }

    public BillingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BillingException(Throwable cause) {
        super(cause);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public BillingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BillingException(BillingResult result) {
        this(result, null);
    }

    public BillingException(int response, String message) {
        this(new BillingException(response, message));
    }

    public BillingException(BillingResult r, Exception cause) {
        super(r.getMessage(), cause);
        result = r;
    }

    public BillingException(int response, String message, Exception cause) {
        this(new BillingResult(response, message), cause);
    }

    /**
     * Returns the BillingResult result (error) that this exception signals.
     */
    public BillingResult getResult() {
        return result;
    }
}
