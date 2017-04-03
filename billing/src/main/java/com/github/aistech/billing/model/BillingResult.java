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

/**
 * Represents the result of an in-app billing operation.
 * A result is composed of a response code (an integer) and possibly a
 * message (String). You can get those by calling
 * {@link #getResponse} and {@link #getMessage()}, respectively. You
 * can also inquire whether a result is a success or a failure by
 * calling {@link #isSuccess()} and {@link #isFailure()}.
 */
public class BillingResult {

    private Integer response;
    private String message;

    public BillingResult(Integer response, String message) {
        this.response = response;

        if (message == null || message.trim().length() == 0) {
            this.message = BillingHelper.getResponseDesc(response);
        } else {
            this.message = message + " (response: " + BillingHelper.getResponseDesc(response) + ")";
        }
    }

    public Boolean isSuccess() {
        return response == BillingHelper.BILLING_RESPONSE_RESULT_OK;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    /* Getters and Setters */

    public Integer getResponse() {
        return response;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return "BillingResult: " + getMessage();
    }
}

