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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.go.config.materials.git.GitMaterialConfig;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import com.thoughtworks.go.server.representers.api.v1.configRepo.BaseRepresenter;

import java.util.Map;

public class MaterialRepresenter extends BaseRepresenter {
    @JsonProperty
    private final String type;

    @JsonProperty
    private final Map<String, String> attributes;

    public MaterialRepresenter(MaterialConfig materialConfig) {
        this.type = materialConfig.getType();
        if (materialConfig.getType().equals("GitMaterial")) {
            this.attributes = new GitMaterialRepresenter((GitMaterialConfig) materialConfig).getAllAttributes();
        } else {
            this.attributes = new HgMaterialRepresenter((HgMaterialConfig) materialConfig).getAllAttributes();
        }
    }

    @JsonCreator
    public MaterialRepresenter(@JsonProperty("type") String type, @JsonProperty("attributes") Map<String, String> attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    @JsonIgnore
    public MaterialConfig getMaterialConfig() {
        if("GitMaterial".equals(type)) {
            GitMaterialConfig gitMaterialConfig = new GitMaterialConfig();
            gitMaterialConfig.setConfigAttributes(attributes);
            return gitMaterialConfig;
        }

        HgMaterialConfig hgMaterialConfig = new HgMaterialConfig();
        hgMaterialConfig.setConfigAttributes(attributes);
        return hgMaterialConfig;
    }
}
