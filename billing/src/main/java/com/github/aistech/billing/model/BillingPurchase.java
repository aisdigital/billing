/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.aistech.billing.model;

import com.github.aistech.billing.utils.BillingHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Represents an in-app billing purchase.
 */
public class BillingPurchase implements Serializable {

    private static final long serialVersionUID = 2792749608498709442L;

    /**
     * Is either {@link BillingHelper#ITEM_TYPE_INAPP}
     * or {@link BillingHelper#ITEM_TYPE_SUBS}
     */
    private String itemType;

    private String sku;
    private String token;
    private String orderId;
    private String packageName;
    private Long purchaseTime;
    private String developerPayload;
    private Integer purchaseState;
    private String originalJson;
    private String signature;
    private Boolean isAutoRenewing;

    public BillingPurchase(String itemType, String jsonPurchaseInfo, String signature) throws JSONException {
        this.itemType = itemType;
        this.originalJson = jsonPurchaseInfo;

        JSONObject jsonObject = new JSONObject(originalJson);
        this.orderId = jsonObject.optString("orderId");
        this.packageName = jsonObject.optString("packageName");
        this.sku = jsonObject.optString("productId");
        this.purchaseTime = jsonObject.optLong("purchaseTime");
        this.purchaseState = jsonObject.optInt("purchaseState");
        this.developerPayload = jsonObject.optString("developerPayload");
        this.token = jsonObject.optString("token", jsonObject.optString("purchaseToken"));
        this.isAutoRenewing = jsonObject.optBoolean("autoRenewing");
        this.signature = signature;
    }

    public String getItemType() {
        return itemType;
    }

    public String getSku() {
        return sku;
    }

    public String getToken() {
        return token;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public Long getPurchaseTime() {
        return purchaseTime;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public Integer getPurchaseState() {
        return purchaseState;
    }

    public String getOriginalJson() {
        return originalJson;
    }

    public String getSignature() {
        return signature;
    }

    public Boolean getAutoRenewing() {
        return isAutoRenewing;
    }

    @Override
    public String toString() {
        return "PurchaseInfo(type:" + itemType + "):" + originalJson;
    }
}
