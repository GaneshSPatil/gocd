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

package com.thoughtworks.go.server.representers.api.v1.configRepo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.go.config.remote.ConfigRepoConfig;
import com.thoughtworks.go.server.representers.api.v1.configRepo.material.MaterialRepresenter;
import de.otto.edison.hal.Links;

import java.io.IOException;

import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Link.self;

public class ConfigRepoRepresenter extends BaseRepresenter {
    @JsonProperty
    private final String id;

    @JsonProperty("plugin_id")
    private final String pluginId;

    @JsonProperty
    private final MaterialRepresenter material;

    public ConfigRepoRepresenter(ConfigRepoConfig configRepo) {
        super(Links.linkingTo(
                self("https://ci.example.com/go/api/admin/config_repos/repo1"),
                link("doc", "https://api.gocd.org/#config-repos"),
                link("find", "https://ci.example.com/go/api/admin/config_repos/:id")
        ));

        this.id = configRepo.getId();
        this.pluginId = configRepo.getConfigProviderPluginName();
        this.material = new MaterialRepresenter(configRepo.getMaterialConfig());
    }

    @JsonCreator
    public ConfigRepoRepresenter(@JsonProperty("id") String id, @JsonProperty("plugin_id") String pluginId, @JsonProperty("material") MaterialRepresenter material) {
        this.id = id;
        this.pluginId = pluginId;
        this.material = material;
    }

    public static ConfigRepoConfig fromJson(String json) throws IOException {
        ConfigRepoRepresenter configRepoRepresenter = new ObjectMapper().readValue(json, ConfigRepoRepresenter.class);

        ConfigRepoConfig configRepoConfig = new ConfigRepoConfig();

        configRepoConfig.setId(configRepoRepresenter.id);
        configRepoConfig.setConfigProviderPluginName(configRepoRepresenter.pluginId);
        configRepoConfig.setMaterialConfig(configRepoRepresenter.material.getMaterialConfig());

        return configRepoConfig;
    }
}
