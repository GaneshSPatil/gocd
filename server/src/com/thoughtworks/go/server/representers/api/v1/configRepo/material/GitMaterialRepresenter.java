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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.go.config.materials.git.GitMaterialConfig;

import java.util.HashMap;
import java.util.Map;

public class GitMaterialRepresenter extends SCMMaterialRepresenter {
    @JsonProperty
    private final String url;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private final String name;

    @JsonProperty
    private final String branch;

    @JsonProperty("auto_update")
    private final boolean autoUpdate;

    @JsonCreator
    public GitMaterialRepresenter(@JsonProperty("url") String url, @JsonProperty("name") String name, @JsonProperty("branch") String branch, @JsonProperty("auto_update") boolean autoUpdate) {
        this.url = url;
        this.name = name;
        this.branch = branch;
        this.autoUpdate = autoUpdate;
    }

    public GitMaterialRepresenter(GitMaterialConfig materialConfig) {
        this.url = materialConfig.getUrl();
        this.name = materialConfig.getMaterialName() != null ? materialConfig.getMaterialName().toString() : null;
        this.branch = materialConfig.getBranch();
        this.autoUpdate = materialConfig.getAutoUpdate();
    }

    @JsonIgnore
    public Map<String, String> getAllAttributes() {
        HashMap<String, String> attributes = new HashMap<>();

        attributes.put("url", url);
        attributes.put("name", name);
        attributes.put("autoUpdate", String.valueOf(autoUpdate));
        attributes.put("branch", branch);

        return attributes;
    }
}
