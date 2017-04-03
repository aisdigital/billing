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
 * Represents an in-app product's listing details.
 */
public class BillingProduct implements Serializable {

    private static final long serialVersionUID = 2826221906063716276L;

    private String sku;
    private String itemType;
    private String type;
    private String price;
    private Long priceAmountMicros;
    private String priceCurrencyCode;
    private String title;
    private String description;
    private String json;

    public BillingProduct(String json) throws JSONException {
        this(BillingHelper.ITEM_TYPE_INAPP, json);
    }

    public BillingProduct(String itemType, String json) throws JSONException {
        this.itemType = itemType;
        this.json = json;

        JSONObject jsonObject = new JSONObject(this.json);

        this.sku = jsonObject.optString("productId");
        this.type = jsonObject.optString("type");
        this.price = jsonObject.optString("price");
        this.priceAmountMicros = jsonObject.optLong("price_amount_micros");
        this.priceCurrencyCode = jsonObject.optString("price_currency_code");
        this.title = jsonObject.optString("title");
        this.description = jsonObject.optString("description");
    }

    public String getSku() {
        return sku;
    }

    public String getItemType() {
        return itemType;
    }

    public String getType() {
        return type;
    }

    public String getPrice() {
        return price;
    }

    public Long getPriceAmountMicros() {
        return priceAmountMicros;
    }

    public String getPriceCurrencyCode() {
        return priceCurrencyCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getJson() {
        return json;
    }

    @Override
    public String toString() {
        return "BillingProduct:" + this.json;
    }
}
