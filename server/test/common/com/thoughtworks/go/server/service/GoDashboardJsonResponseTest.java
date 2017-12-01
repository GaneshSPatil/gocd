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

import com.thoughtworks.go.config.CruiseConfig;
import com.thoughtworks.go.config.PipelineConfig;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.server.dashboard.GoDashboardCache;
import com.thoughtworks.go.server.dashboard.GoDashboardCurrentStateLoader;
import com.thoughtworks.go.server.dashboard.GoDashboardPipeline;
import com.thoughtworks.go.server.domain.Username;
import com.thoughtworks.go.server.domain.user.PipelineSelections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;

import static com.thoughtworks.go.server.dashboard.GoDashboardPipelineMother.pipeline;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GoDashboardJsonResponseTest {
    @Mock
    private GoDashboardCache cache;
    @Mock
    private GoDashboardCurrentStateLoader dashboardCurrentStateLoader;
    @Mock
    private GoConfigService goConfigService;

    private GoDashboardService service;

    private GoConfigMother configMother;
    private CruiseConfig config;
    private String json;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        configMother = new GoConfigMother();
        config = configMother.defaultCruiseConfig();

        service = new GoDashboardService(cache, dashboardCurrentStateLoader, goConfigService);

        PipelineSelections pipelineSelections = mock(PipelineSelections.class);

        configMother.addPipelineWithGroup(config, "group1", "pipeline2", "stage1A", "job1A1");
        GoDashboardPipeline pipeline2 = pipeline("pipeline2", "group1");

        configMother.addPipelineWithGroup(config, "group1", "pipeline1", "stage1A", "job1A1");
        GoDashboardPipeline pipeline1 = pipeline("pipeline1", "group1");

        addPipelinesToCache(pipeline1, pipeline2);
        when(pipelineSelections.includesPipeline(any(PipelineConfig.class))).thenReturn(true);

        when(goConfigService.groups()).thenReturn(config.getGroups());

        json = service.asJson(pipelineSelections, new Username("user1"));
    }

    @Test
    public void shouldContainLinks() throws Exception {
        String links = "{" +
                "\"_links\": {\n" +
                "  \"self\": {\n" +
                "    \"href\": \"http://localhost:8153/go/api/dashboard\"\n" +
                "  },\n" +
                "  \"doc\": {\n" +
                "    \"href\": \"https://api.go.cd/current/#dashboard\"\n" +
                "  }\n" +
                "  }" +
                "}";

        JSONAssert.assertEquals(links, json, false);
    }

    @Test
    public void shouldContainPipelineGroupsInformation() throws Exception {
        String links = "{" +
                "\"_embedded\": {\n" +
                "    \"pipeline_groups\": [\n" +
                "      {\n" +
                "        \"_links\": {\n" +
                "          \"self\": {\n" +
                "            \"href\": \"http://localhost:8153/go/api/config/pipeline_groups\"\n" +
                "          },\n" +
                "          \"doc\": {\n" +
                "            \"href\": \"https://api.go.cd/current/#pipeline-groups\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"name\": \"group1\",\n" +
                "        \"pipelines\": [\n" +
                "          \"pipeline1\",\n" +
                "          \"pipeline2\"\n" +
                "        ],\n" +
                "        \"can_administer\": true\n" +
                "      }\n" +
                "    ]" +
                "  }" +
                "}";

        System.out.println("json = " + json);

        JSONAssert.assertEquals(links, json, false);
    }


    private void addPipelinesToCache(GoDashboardPipeline... pipelines) {
        for (GoDashboardPipeline pipeline : pipelines) {
            when(cache.get(pipeline.name())).thenReturn(pipeline);
        }
    }
}