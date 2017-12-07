/*
 * Copyright 2017 ThoughtWorks, Inc.
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

package com.thoughtworks.go.server.representers.api.v1.configRepo.material;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;

import java.util.HashMap;
import java.util.Map;

public class HgMaterialRepresenter extends SCMMaterialRepresenter {
    @JsonProperty
    private String url;

    @JsonCreator
    public HgMaterialRepresenter(@JsonProperty("url") String url) {
        this.url = url;
    }

    @Override
    public Map<String, String> getAllAttributes() {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("url", url);
        return attributes;
    }

    public HgMaterialRepresenter(HgMaterialConfig materialConfig) {
        this.url = materialConfig.getUrl();
    }
}
