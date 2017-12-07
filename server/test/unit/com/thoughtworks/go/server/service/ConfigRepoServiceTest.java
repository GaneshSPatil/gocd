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

package com.thoughtworks.go.server.service;

import com.thoughtworks.go.config.BasicCruiseConfig;
import com.thoughtworks.go.config.materials.git.GitMaterialConfig;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.config.remote.ConfigRepoConfig;
import com.thoughtworks.go.config.remote.ConfigReposConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigRepoServiceTest {
    @Mock
    private SecurityService securityService;
    @Mock
    private EntityHashingService entityHashingService;
    @Mock
    private GoConfigService goConfigService;

    private ConfigRepoService configRepoService;
    private String repoId;
    private ConfigRepoConfig configRepoConfig;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        configRepoService = new ConfigRepoService(goConfigService, securityService, entityHashingService);
        repoId = "repoId";
        configRepoConfig = new ConfigRepoConfig(new GitMaterialConfig("https://foo.git"), "json-config-plugin", repoId);

        BasicCruiseConfig config = new BasicCruiseConfig();
        config.setConfigRepos(new ConfigReposConfig(configRepoConfig));

        when(goConfigService.getConfigForEditing()).thenReturn(config);
    }

    @Test
    public void shouldReturnJsonRepresentationOfConfigRepo() throws Exception {
        String json = configRepoService.getConfigRepoAsJson(repoId);
        System.out.println("json = " + json);

        String expectedJson = "{\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"https://ci.example.com/go/api/admin/config_repos/repo1\"\n" +
                "    },\n" +
                "    \"doc\": {\n" +
                "      \"href\": \"https://api.gocd.org/#config-repos\"\n" +
                "    },\n" +
                "    \"find\": {\n" +
                "      \"href\": \"https://ci.example.com/go/api/admin/config_repos/:id\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"id\": \"repoId\",\n" +
                "  \"plugin_id\": \"json-config-plugin\",\n" +
                "  \"material\": {\n" +
                "    \"type\": \"GitMaterial\",\n" +
                "    \"attributes\": {\n" +
                "      \"url\": \"https://foo.git\",\n" +
                "      \"name\": null,\n" +
                "      \"branch\": \"master\",\n" +
                "      \"auto_update\": true\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JSONAssert.assertEquals(expectedJson, json, true);
    }


    @Test
    public void shouldConvertFromJsonToConfigRepoConfigEntity() throws Exception {
        String json = "{\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"https://ci.example.com/go/api/admin/config_repos/repo1\"\n" +
                "    },\n" +
                "    \"doc\": {\n" +
                "      \"href\": \"https://api.gocd.org/#config-repos\"\n" +
                "    },\n" +
                "    \"find\": {\n" +
                "      \"href\": \"https://ci.example.com/go/api/admin/config_repos/:id\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"id\": \"repoId\",\n" +
                "  \"plugin_id\": \"json-config-plugin\",\n" +
                "  \"material\": {\n" +
                "    \"type\": \"GitMaterial\",\n" +
                "    \"attributes\": {\n" +
                "      \"url\": \"https://foo.git\",\n" +
                "      \"name\": null,\n" +
                "      \"branch\": \"master\",\n" +
                "      \"auto_update\": true\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ConfigRepoConfig config =  configRepoService.parseFromJson(json);

        assertThat(config, is(configRepoConfig));
    }

    @Test
    public void shouldConvertFromJsonToConfigRepoConfigEntityWithHgMaterial() throws Exception {
        String json = "{\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"https://ci.example.com/go/api/admin/config_repos/repo1\"\n" +
                "    },\n" +
                "    \"doc\": {\n" +
                "      \"href\": \"https://api.gocd.org/#config-repos\"\n" +
                "    },\n" +
                "    \"find\": {\n" +
                "      \"href\": \"https://ci.example.com/go/api/admin/config_repos/:id\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"id\": \"repoId\",\n" +
                "  \"plugin_id\": \"json-config-plugin\",\n" +
                "  \"material\": {\n" +
                "    \"type\": \"HgMaterial\",\n" +
                "    \"attributes\": {\n" +
                "      \"url\": \"https://foo.git\",\n" +
                "      \"name\": null,\n" +
                "      \"auto_update\": true\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ConfigRepoConfig config =  configRepoService.parseFromJson(json);

        HgMaterialConfig hgMaterialConfig = new HgMaterialConfig();
        hgMaterialConfig.setUrl("https://foo.git");
        hgMaterialConfig.setAutoUpdate(true);

        ConfigRepoConfig expectedConfigRepoConfig = new ConfigRepoConfig(hgMaterialConfig, "json-config-plugin", repoId);

        assertThat(config, is(expectedConfigRepoConfig));
    }

    @Test
    public void shouldGetAllConfigReposAsJson() throws Exception {
        String expectedJson = "{\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"https://ci.example.com/go/api/admin/config_repos\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"_embedded\": {\n" +
                "    \"config_repos\": [\n" +
                "      {\n" +
                "        \"id\": \"repoId\",\n" +
                "        \"plugin_id\": \"json-config-plugin\",\n" +
                "        \"material\": {\n" +
                "          \"type\": \"GitMaterial\",\n" +
                "          \"attributes\": {\n" +
                "            \"autoUpdate\": \"true\",\n" +
                "            \"name\": null,\n" +
                "            \"branch\": \"master\",\n" +
                "            \"url\": \"https://foo.git\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"_links\": {\n" +
                "          \"self\": {\n" +
                "            \"href\": \"https://ci.example.com/go/api/admin/config_repos/repo1\"\n" +
                "          },\n" +
                "          \"doc\": {\n" +
                "            \"href\": \"https://api.gocd.org/#config-repos\"\n" +
                "          },\n" +
                "          \"find\": {\n" +
                "            \"href\": \"https://ci.example.com/go/api/admin/config_repos/:id\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        String actualJson = configRepoService.getAllConfigReposAsJson();

        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}