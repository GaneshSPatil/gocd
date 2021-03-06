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

package com.thoughtworks.go.apiv1.webhook.request.push;

import com.thoughtworks.go.apiv1.webhook.request.payload.push.GitHubPushPayload;
import com.thoughtworks.go.config.exceptions.BadRequestException;
import com.thoughtworks.go.junit5.FileSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.MimeType;
import spark.QueryParamsMap;
import spark.Request;

import static com.thoughtworks.go.apiv1.webhook.request.WebhookRequest.KEY_SCM_NAME;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.*;

class GitHubPushRequestTest {
    @Test
    void shouldSupportJsonAndUrlEncodedPayload() {
        Request request = newRequest("push", "", "{}", APPLICATION_JSON);

        GitHubPushRequest gitHubPushRequest = new GitHubPushRequest(request);

        assertThat(gitHubPushRequest.supportedContentTypes())
                .hasSize(3)
                .contains(APPLICATION_FORM_URLENCODED, APPLICATION_JSON, APPLICATION_JSON_UTF8);
    }

    @ParameterizedTest
    @FileSource(files = "/github-payload.json")
    void shouldParseWebhookRequestToGitHubRequest(String body) {
        Request request = newRequest("push", "", body, APPLICATION_JSON);

        GitHubPushRequest gitHubPushRequest = new GitHubPushRequest(request);

        assertThat(gitHubPushRequest.event()).isEqualTo("push");
        assertPayload(gitHubPushRequest.getPayload());
    }

    @ParameterizedTest
    @FileSource(files = "/github-url-encoded-payload.json")
    void shouldParseRequestWithUrlEncodedPayload(String urlEncodedPayload) {
        Request request = newRequest("push", "", urlEncodedPayload, APPLICATION_FORM_URLENCODED);

        GitHubPushRequest gitHubPushRequest = new GitHubPushRequest(request);

        assertThat(gitHubPushRequest.event()).isEqualTo("push");
        assertPayload(gitHubPushRequest.getPayload());
    }

    @ParameterizedTest
    @FileSource(files = "/github-payload.json")
    void shouldGuessTheGitHubUrls(String body) {
        Request request = newRequest("push", "", body, APPLICATION_JSON);

        GitHubPushRequest gitHubPushRequest = new GitHubPushRequest(request);

        assertThat(gitHubPushRequest.webhookUrls())
                .hasSize(24)
                .contains(
                        "https://github.com/gocd/spaceship",
                        "https://github.com/gocd/spaceship/",
                        "https://github.com/gocd/spaceship.git",
                        "https://github.com/gocd/spaceship.git/",
                        "http://github.com/gocd/spaceship",
                        "http://github.com/gocd/spaceship/",
                        "http://github.com/gocd/spaceship.git",
                        "http://github.com/gocd/spaceship.git/",
                        "git://github.com/gocd/spaceship",
                        "git://github.com/gocd/spaceship/",
                        "git://github.com/gocd/spaceship.git",
                        "git://github.com/gocd/spaceship.git/",
                        "git@github.com:gocd/spaceship",
                        "git@github.com:gocd/spaceship/",
                        "git@github.com:gocd/spaceship.git",
                        "git@github.com:gocd/spaceship.git/",
                        "ssh://git@github.com/gocd/spaceship",
                        "ssh://git@github.com/gocd/spaceship/",
                        "ssh://git@github.com/gocd/spaceship.git",
                        "ssh://git@github.com/gocd/spaceship.git/",
                        "ssh://github.com/gocd/spaceship",
                        "ssh://github.com/gocd/spaceship/",
                        "ssh://github.com/gocd/spaceship.git",
                        "ssh://github.com/gocd/spaceship.git/"
                );
    }

    @ParameterizedTest
    @FileSource(files = "/github-payload.json")
    void shouldReturnScmNamesIfAny(String body) {
        Request request = newRequest("push", "", body, APPLICATION_JSON);

        QueryParamsMap map = mock(QueryParamsMap.class);
        when(request.queryMap()).thenReturn(map);
        when(map.hasKey(KEY_SCM_NAME)).thenReturn(false);
        GitHubPushRequest request1 = new GitHubPushRequest(request);
        assertThat(request1.getScmNames()).isEmpty();

        when(request.queryMap()).thenReturn(map);
        when(map.hasKey(KEY_SCM_NAME)).thenReturn(true);

        QueryParamsMap value = mock(QueryParamsMap.class);
        when(map.get(KEY_SCM_NAME)).thenReturn(value);
        when(value.values()).thenReturn(new String[]{"scm1"});
        assertThat(request1.getScmNames()).containsExactly("scm1");
    }

    @Nested
    class Validate {
        @ParameterizedTest
        @ValueSource(strings = {"merge", "issue_created", "foo"})
        void shouldErrorOutIfEventTypeIsNotPingOrPush(String event) {
            Request request = newRequest(event, "", "{}", APPLICATION_JSON);

            assertThatCode(() -> new GitHubPushRequest(request).validate("webhook-secret"))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(format("Invalid event type `%s`. Allowed events are [push, ping].", event));
        }

        @Test
        void shouldErrorOutIfSignatureHeaderIsNull() {
            Request request = newRequest("push", null, "{}", APPLICATION_JSON);

            assertThatCode(() -> new GitHubPushRequest(request).validate("webhook-secret"))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("No HMAC signature specified via 'X-Hub-Signature' header!");

        }

        @Test
        void shouldErrorOutIfSignatureHeaderDoesNotMatch() {
            Request request = newRequest("push", "random-signature", "{}", APPLICATION_JSON);

            assertThatCode(() -> new GitHubPushRequest(request).validate("webhook-secret"))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("HMAC signature specified via 'X-Hub-Signature' did not match!");

        }

        @ParameterizedTest
        @ValueSource(strings = {"push", "ping"})
        void shouldBeValidWhenEventTypeAndSignatureMatches(String event) {
            Request request = newRequest(event, "sha1=021757dde54540ff083dcf680688c4c9676e5c44", "{}", APPLICATION_JSON);

            assertThatCode(() -> new GitHubPushRequest(request).validate("webhook-secret"))
                    .doesNotThrowAnyException();

        }
    }

    private Request newRequest(String event, String signature, String body, MimeType contentType) {
        Request request = mock(Request.class);
        when(request.headers("X-Hub-Signature")).thenReturn(signature);
        when(request.headers("X-GitHub-Event")).thenReturn(event);
        when(request.contentType()).thenReturn(contentType.toString());
        when(request.body()).thenReturn(body);

        return request;
    }

    private void assertPayload(GitHubPushPayload payload) {
        assertThat(payload.getBranch()).isEqualTo("release");
        assertThat(payload.getFullName()).isEqualTo("gocd/spaceship");
        assertThat(payload.getHostname()).isEqualTo("github.com");
    }
}
