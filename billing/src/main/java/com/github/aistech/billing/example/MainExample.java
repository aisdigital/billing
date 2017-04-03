package com.github.aistech.billing.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.aistech.billing.model.BillingPurchase;
import com.github.aistech.billing.utils.BillingHelper;

import org.json.JSONException;

import java.util.Arrays;

import com.github.aistech.billing.model.BillingInventory;
import com.github.aistech.billing.model.BillingProduct;
import com.github.aistech.billing.model.BillingResult;
import com.github.aistech.billing.utils.BillingSingleton;

/**
 * Created by jonathan on 20/10/16.
 */

public class MainExample extends Activity implements BillingHelper.QueryBillingInventoryFinishedListener {

    private static final String TAG = "Billing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */
        String base64EncodedPublicKey = "";

        // Below: Application#onCreate.
        BillingSingleton.getInstance().init(this, base64EncodedPublicKey);
        BillingSingleton.getInstance().enableDebugging(TAG);

        // Below: Code should be in some Activity
        BillingSingleton.getInstance().registerInventoryListener(this);
        BillingSingleton.getInstance().startSetup(new BillingHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(BillingResult result) {
                Log.d(BillingSingleton.TAG, "Setup finished.");

                // Oh noes, there was a problem.
                if (result.isFailure()) {
                    Log.e(BillingSingleton.TAG, "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (BillingSingleton.getInstance().isStarted() == false) {
                    return;
                }

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // BillingHelper is setup, but before first call to getPurchases().
                BillingSingleton.getInstance().registerBroadcast(MainExample.this);

                BillingSingleton.getInstance().queryInventory(Arrays.asList("diamante"));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        BillingSingleton.getInstance().handlePurchase(requestCode, resultCode, data);
    }

     /* BillingHelper.QueryBillingInventoryFinishedListener */

    @Override
    public void onQueryBillingInventoryFinished(BillingResult result, BillingInventory inv) {

        // Have we been disposed of in the meantime? If so, quit.
        if (BillingSingleton.getInstance().isStarted() == false) {
            return;
        }

        // Is it a failure?
        if (result.isFailure()) {
            Log.e(BillingSingleton.TAG, "Failed to query inventory: " + result);
            return;
        }

        Log.i(BillingSingleton.TAG, result.toString());
        Log.i(BillingSingleton.TAG, "PURCHASES: " + inv.getAllPurchases().toString() + "\n\nBILLING PRODUCTS: " + inv.getAllOwnedBillingProduct().toString() + "\n\n");

        // Check for diamante purchased to consume.
        BillingPurchase purchase = inv.getPurchase("diamante");
        if (purchase != null) {
            BillingSingleton.getInstance().consumePurchaseForProduct(purchase, new BillingHelper.OnConsumeFinishedListener() {
                @Override
                public void onConsumeFinished(BillingPurchase purchase, BillingResult result) {
                    Log.i(BillingSingleton.TAG, result.toString());
                    Log.i(BillingSingleton.TAG, "PURCHASE: " + purchase.toString());
                }

                @Override
                public void onError(BillingHelper.BillingAsyncInProgressException e) {
                    Log.e(BillingSingleton.TAG, e.getMessage());
                }
            });
        }
    }

    @Override
    public void onError(BillingHelper.BillingAsyncInProgressException e) {
        Log.e(BillingSingleton.TAG, e.getMessage());
    }

    /* Purchase */

    public void purchaseTest(Activity activity) {
        try {

            // This product MUST be retrieve from backend.
            BillingProduct product = new BillingProduct("{\"productId\":\"diamante\",\"type\":\"inapp\",\"price\":\"R$3.19\",\"price_amount_micros\":3190000,\"price_currency_code\":\"BRL\",\"title\":\"Diamante (Mapa da Saúde)\",\"description\":\"Diamante\"}");
            BillingSingleton.getInstance().requestPurchaseForProduct(activity, product, BillingSingleton.PURCHASE_REQUEST, "", new BillingHelper.OnPurchaseFinishedListener() {
                @Override
                public void onPurchaseFinished(BillingResult result, BillingPurchase info) {

                    if (result.isFailure()) {
                        Log.e(BillingSingleton.TAG, result.getMessage());
                    } else {
                        Log.i(BillingSingleton.TAG, "PURCHASE INFO: " + info.toString());
                        Log.i(BillingSingleton.TAG, "RESULT: " + result.toString());
                    }
                }

                @Override
                public void onError(BillingHelper.BillingAsyncInProgressException e) {
                    Log.e(BillingSingleton.TAG, e.getMessage());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
