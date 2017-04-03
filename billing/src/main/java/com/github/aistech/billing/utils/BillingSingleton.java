package com.github.aistech.billing.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.github.aistech.billing.model.BillingProduct;
import com.github.aistech.billing.model.BillingPurchase;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.aistech.billing.broadcasts.BillingBroadcastReceiver;
import com.github.aistech.billing.model.BillingInventory;
import com.github.aistech.billing.model.BillingResult;

/**
 * Created by jonathan on 19/10/16.
 */

public class BillingSingleton implements BillingBroadcastReceiver.BillingBroadcastListener {

    public static final String TAG = "BillingSingleton";
    public static final int PURCHASE_REQUEST = 2323;

    private static BillingSingleton instance = null;

    private BillingHelper billingHelper;
    private BillingBroadcastReceiver billingBroadcastReceiver;
    private Set<BillingHelper.QueryBillingInventoryFinishedListener> billingInventoryFinishedListeners;

    public static BillingSingleton getInstance() {
        if (instance == null) {
            instance = new BillingSingleton();
        }
        return instance;
    }

    private BillingSingleton() {
        this.billingInventoryFinishedListeners = new LinkedHashSet<>();
    }

    /**
     * Should be called on {@link Application#onCreate()}
     */
    public void init(Context context, String base64PublicKey) {
        this.billingHelper = new BillingHelper(context, base64PublicKey);
    }

    /**
     * Well, the name say it all.
     */
    public Boolean isStarted() {
        return this.billingHelper != null;
    }

    /**
     * Well, the name say it all.
     */
    public void destroy() {
        if (isStarted()) {
            try {
                this.billingHelper.dispose();
                this.billingHelper = null;
            } catch (BillingHelper.BillingAsyncInProgressException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * Enable debug mode usaing the TAG
     *
     * @param tag
     */
    public void enableDebugging(String tag) {
        this.billingHelper.enableDebugLogging(true, tag);
    }

    /**
     * Starts the setup process. This will start up the setup process asynchronously.
     * You will be notified through the listener when the setup process is complete.
     * This method is safe to call from a UI thread.
     *
     * @param finishedListener The listener to notify when the setup process is complete.
     */
    public void startSetup(BillingHelper.OnIabSetupFinishedListener finishedListener) {
        this.billingHelper.startSetup(finishedListener);
    }

    /**
     * Register the {@link BillingSingleton} for receive the Broadcast for PURCHASES_UPDATED
     *
     * @param context
     */
    public void registerBroadcast(Context context) {
        this.billingBroadcastReceiver = new BillingBroadcastReceiver(this);
        context.registerReceiver(this.billingBroadcastReceiver, new IntentFilter(BillingBroadcastReceiver.ACTION));
    }

    /**
     * Register for {@link BillingHelper.QueryBillingInventoryFinishedListener}
     *
     * @param finishedListener
     */
    public void registerInventoryListener(BillingHelper.QueryBillingInventoryFinishedListener finishedListener) {
        if (finishedListener == null) return;
        this.billingInventoryFinishedListeners.add(finishedListener);
    }

    /**
     * Unregistered for {@link BillingHelper.QueryBillingInventoryFinishedListener}
     *
     * @param finishedListener
     */
    public void unregisterInventoryListener(BillingHelper.QueryBillingInventoryFinishedListener finishedListener) {
        if (finishedListener == null) return;
        this.billingInventoryFinishedListeners.remove(finishedListener);
    }

    /**
     * Query inventory
     */
    public void queryInventory() {
        BillingHelper.QueryBillingInventoryFinishedListener finishedListener = createInventoryNotifier();

        try {
            this.billingHelper.queryBillingInventoryAsync(finishedListener);
        } catch (BillingHelper.BillingAsyncInProgressException e) {
            notifyInventoryOnError(e);
        }
    }

    /**
     * Query and return all available products with full details.
     *
     * @param skus
     */
    public void queryInventory(List<String> skus) {
        BillingHelper.QueryBillingInventoryFinishedListener finishedListener = createInventoryNotifier();

        try {
            this.billingHelper.queryBillingInventoryAsync(true, skus, null, finishedListener);
        } catch (BillingHelper.BillingAsyncInProgressException e) {
            notifyInventoryOnError(e);
        }
    }

    /**
     * Util method that create an Listener that will notify all registered {@link BillingHelper.QueryBillingInventoryFinishedListener}
     *
     * @return
     */
    private BillingHelper.QueryBillingInventoryFinishedListener createInventoryNotifier() {
        return new BillingHelper.QueryBillingInventoryFinishedListener() {
            @Override
            public void onQueryBillingInventoryFinished(BillingResult result, BillingInventory inv) {
                for (BillingHelper.QueryBillingInventoryFinishedListener listener : billingInventoryFinishedListeners) {
                    listener.onQueryBillingInventoryFinished(result, inv);
                }
            }

            @Override
            public void onError(BillingHelper.BillingAsyncInProgressException e) {
                notifyInventoryOnError(e);
            }
        };
    }

    /**
     * Util method that notify all Listener for {@link BillingHelper.BillingAsyncInProgressException}
     * in case of {@link Exception}.
     *
     * @param e
     */
    private void notifyInventoryOnError(BillingHelper.BillingAsyncInProgressException e) {
        for (BillingHelper.QueryBillingInventoryFinishedListener listener : billingInventoryFinishedListeners) {
            listener.onError(e);
        }
    }

    public void requestPurchaseForProduct(Activity activity, BillingProduct product, int requestCode, String developerPayload, BillingHelper.OnPurchaseFinishedListener listener) {
        try {
            this.billingHelper.launchPurchaseFlow(activity, product.getSku(), requestCode, listener, developerPayload);
        } catch (BillingHelper.BillingAsyncInProgressException e) {
            listener.onError(e);
        }
    }

    public void handlePurchase(int requestCode, int resultCode, Intent data) {
        this.billingHelper.handleActivityResult(requestCode, resultCode, data);
    }

    public void consumePurchaseForProduct(BillingPurchase billingPurchase, BillingHelper.OnConsumeFinishedListener consumeFinishedListener) {
        try {
            this.billingHelper.consumeAsync(billingPurchase, consumeFinishedListener);
        } catch (BillingHelper.BillingAsyncInProgressException e) {
            consumeFinishedListener.onError(e);
        }
    }

    /* Callback for BillingBroadcastReceiver*/

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        queryInventory();
    }
}
