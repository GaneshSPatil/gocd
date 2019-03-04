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

package com.thoughtworks.go.server.service;

import com.thoughtworks.go.config.PluginProfiles;
import com.thoughtworks.go.config.elastic.ClusterConfig;
import com.thoughtworks.go.config.elastic.ClusterConfigs;
import com.thoughtworks.go.config.elastic.ElasticConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ClusterConfigServiceTest {
    @Mock
    private GoConfigService goConfigService;
    @Mock
    private EntityHashingService hashingService;

    private ClusterConfigService clusterConfigService;
    private ClusterConfig clusterConfig;

    @BeforeEach
    void setUp() {
        initMocks(this);

        clusterConfig = new ClusterConfig("prod_cluster", "k8s.ea.plugin");

        clusterConfigService = new ClusterConfigService(goConfigService, hashingService);
    }

    @Test
    void shouldFetchClustersDefinedAsPartOfElasticTag() {
        ElasticConfig elasticConfig = new ElasticConfig();
        elasticConfig.setClusters(new ClusterConfigs(clusterConfig));
        when(goConfigService.getElasticConfig()).thenReturn(elasticConfig);

        PluginProfiles<ClusterConfig> actualClusterConfigs = clusterConfigService.getPluginProfiles();

        assertThat(actualClusterConfigs).isEqualTo(elasticConfig.getClusters());
    }
}
