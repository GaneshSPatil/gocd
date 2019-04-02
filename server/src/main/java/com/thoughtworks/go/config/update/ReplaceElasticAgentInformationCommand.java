/*
 * Copyright 2019 ThoughtWorks, Inc.
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

package com.thoughtworks.go.config.update;

import com.thoughtworks.go.config.CruiseConfig;
import com.thoughtworks.go.config.commands.EntityConfigUpdateCommand;
import com.thoughtworks.go.config.elastic.ClusterProfile;
import com.thoughtworks.go.config.elastic.ElasticProfile;
import com.thoughtworks.go.plugin.access.elastic.ElasticAgentExtension;
import com.thoughtworks.go.plugin.access.elastic.models.ElasticAgentInformation;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import com.thoughtworks.go.server.service.ClusterProfilesService;
import com.thoughtworks.go.server.service.ElasticProfileService;
import com.thoughtworks.go.server.service.GoConfigService;

import java.util.HashMap;
import java.util.List;

public class ReplaceElasticAgentInformationCommand implements EntityConfigUpdateCommand<ElasticAgentInformation> {

    private final ClusterProfilesService clusterProfilesService;
    private final ElasticProfileService elasticProfileService;
    private final ElasticAgentExtension elasticAgentExtension;
    private final GoConfigService goConfigService;
    private final GoPluginDescriptor pluginDescriptor;
    private final HashMap<String, String> pluginSettings;
    private ElasticAgentInformation migratedElasticAgentInformation;

    public ReplaceElasticAgentInformationCommand(ClusterProfilesService clusterProfilesService, ElasticProfileService elasticProfileService, ElasticAgentExtension elasticAgentExtension, GoConfigService goConfigService, GoPluginDescriptor pluginDescriptor, HashMap<String, String> pluginSettings) {
        this.clusterProfilesService = clusterProfilesService;
        this.elasticProfileService = elasticProfileService;
        this.elasticAgentExtension = elasticAgentExtension;
        this.goConfigService = goConfigService;
        this.pluginDescriptor = pluginDescriptor;
        this.pluginSettings = pluginSettings;
    }


    @Override
    public void update(CruiseConfig preprocessedConfig) throws Exception {
        String pluginId = pluginDescriptor.id();

        List<ClusterProfile> clusterProfiles = clusterProfilesService.getPluginProfiles().findByPluginId(pluginId);
        List<ElasticProfile> elasticAgentProfiles = elasticProfileService.getPluginProfiles().findByPluginId(pluginId);

        ElasticAgentInformation elasticAgentInformation = new ElasticAgentInformation(pluginSettings, clusterProfiles, elasticAgentProfiles);
        migratedElasticAgentInformation = elasticAgentExtension.migrateConfig(pluginId, elasticAgentInformation);

        List<ClusterProfile> migratedClusterProfiles = migratedElasticAgentInformation.getClusterProfiles();
        List<ElasticProfile> migratedElasticAgentProfiles = migratedElasticAgentInformation.getElasticAgentProfiles();

        preprocessedConfig.getElasticConfig().getClusterProfiles().removeAll(clusterProfiles);
        preprocessedConfig.getElasticConfig().getClusterProfiles().addAll(migratedClusterProfiles);

        preprocessedConfig.getElasticConfig().getProfiles().removeAll(elasticAgentProfiles);
        preprocessedConfig.getElasticConfig().getProfiles().addAll(migratedElasticAgentProfiles);
    }

    @Override
    public boolean isValid(CruiseConfig preprocessedConfig) {
        //todo: Add validation to check if plugin returns valid config
        return true;
    }

    @Override
    public void clearErrors() {
    }

    @Override
    public ElasticAgentInformation getPreprocessedEntityConfig() {
        return migratedElasticAgentInformation;
    }

    @Override
    public boolean canContinue(CruiseConfig cruiseConfig) {
        return true;
    }
}
