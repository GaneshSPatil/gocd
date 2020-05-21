/*
 * Copyright 2020 ThoughtWorks, Inc.
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

package com.thoughtworks.go.apiv1.webhook.request.mixins.github;

import com.thoughtworks.go.apiv1.webhook.request.mixins.HasAuth;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.security.MessageDigest;

import static org.apache.commons.lang3.StringUtils.isBlank;

public interface GitHubAuth extends HasAuth {
    default void validateAuth(String secret) {
        String signature = request().headers("X-Hub-Signature");

        if (isBlank(signature)) {
            throw die("No HMAC signature specified via 'X-Hub-Signature' header!");
        }

        String expectedSignature = "sha1=" + new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secret).hmacHex(request().body());

        if (!MessageDigest.isEqual(expectedSignature.getBytes(), signature.getBytes())) {
            throw die("HMAC signature specified via 'X-Hub-Signature' did not match!");
        }
    }
}
