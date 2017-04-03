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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a block of information about in-app items.
 * An Inventory is returned by such methods as {@link BillingHelper#queryBillingInventory()}.
 */
public class BillingInventory {

    private Map<String, BillingProduct> productsMap;
    private Map<String, BillingPurchase> purchaseMap;

    public BillingInventory() {
        this.productsMap = new HashMap<String, BillingProduct>();
        this.purchaseMap = new HashMap<String, BillingPurchase>();
    }

    /**
     * Returns the listing details for an in-app product.
     */
    public BillingProduct getBillingProducts(String sku) {
        return productsMap.get(sku);
    }

    /**
     * Returns purchase information for a given product, or null if there is no purchase.
     */
    public BillingPurchase getPurchase(String sku) {
        return purchaseMap.get(sku);
    }

    /**
     * Returns whether or not there exists a purchase of the given product.
     */
    public boolean hasPurchase(String sku) {
        return purchaseMap.containsKey(sku);
    }

    /**
     * Return whether or not details about the given product are available.
     */
    public boolean hasDetails(String sku) {
        return productsMap.containsKey(sku);
    }

    /**
     * Erase a purchase (locally) from the inventory, given its product ID. This just
     * modifies the Inventory object locally and has no effect on the server! This is
     * useful when you have an existing Inventory object which you know to be up to date,
     * and you have just consumed an item successfully, which means that erasing its
     * purchase data from the Inventory you already have is quicker than querying for
     * a new Inventory.
     */
    public void erasePurchase(String sku) {
        if (purchaseMap.containsKey(sku)) purchaseMap.remove(sku);
    }

    /**
     * Returns a list of all owned product IDs.
     */
    public List<String> getAllOwnedBillingProduct() {
        return new ArrayList<String>(purchaseMap.keySet());
    }

    /**
     * Returns a list of all owned product IDs of a given type
     */
    public List<String> getAllOwnedBillingProduct(String itemType) {
        List<String> result = new ArrayList<String>();
        for (BillingPurchase p : purchaseMap.values()) {
            if (p.getItemType().equals(itemType)) result.add(p.getSku());
        }
        return result;
    }

    /**
     * Returns a list of all purchases.
     */
    public List<BillingPurchase> getAllPurchases() {
        return new ArrayList<BillingPurchase>(purchaseMap.values());
    }

    public void addBillingProduct(BillingProduct d) {
        productsMap.put(d.getSku(), d);
    }

    public void addPurchase(BillingPurchase p) {
        purchaseMap.put(p.getSku(), p);
    }
}
