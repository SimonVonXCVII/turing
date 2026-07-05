/*
 * Copyright 2020-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.Map;

/**
 * @author Joe Grandja
 * @since 1.4
 */
@Controller
public class DefaultController {

    @Value("${app.base-uri}")
    private String appBaseUri;

    @GetMapping("/")
    public String root() {
        return "redirect:" + this.appBaseUri;
    }

    // '/authorized' is the registered 'redirect_uri' for authorization_code
    @GetMapping("/authorized")
    public String authorized() {
        return "redirect:" + this.appBaseUri;
    }

    @GetMapping("/token")
    public Map<String, String> getToken(@RegisteredOAuth2AuthorizedClient("messaging-client-oidc") OAuth2AuthorizedClient authorizedClient) {
        return Collections.singletonMap("token", authorizedClient.getAccessToken().getTokenValue());
    }

}
