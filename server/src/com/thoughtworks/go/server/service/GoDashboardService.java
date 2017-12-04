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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.CruiseConfig;
import com.thoughtworks.go.config.PipelineConfig;
import com.thoughtworks.go.config.PipelineConfigs;
import com.thoughtworks.go.config.security.Permissions;
import com.thoughtworks.go.domain.PipelineGroupVisitor;
import com.thoughtworks.go.domain.PiplineConfigVisitor;
import com.thoughtworks.go.server.dashboard.GoDashboardCache;
import com.thoughtworks.go.server.dashboard.GoDashboardCurrentStateLoader;
import com.thoughtworks.go.server.dashboard.GoDashboardPipeline;
import com.thoughtworks.go.server.dashboard.GoDashboardPipelineGroup;
import com.thoughtworks.go.server.domain.Username;
import com.thoughtworks.go.server.domain.user.PipelineSelections;
import com.thoughtworks.go.server.representers.dashboard.GoDashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/* Understands how to interact with the GoDashboardCache cache. */
@Service
public class GoDashboardService {
    private final GoDashboardCache cache;
    private final GoDashboardCurrentStateLoader dashboardCurrentStateLoader;
    private final GoConfigService goConfigService;

    @Autowired
    public GoDashboardService(GoDashboardCache cache, GoDashboardCurrentStateLoader dashboardCurrentStateLoader, GoConfigService goConfigService) {
        this.cache = cache;
        this.dashboardCurrentStateLoader = dashboardCurrentStateLoader;
        this.goConfigService = goConfigService;
    }

    public List<GoDashboardPipelineGroup> allPipelineGroupsForDashboard(PipelineSelections pipelineSelections, Username user) {
        List<GoDashboardPipelineGroup> pipelineGroups = new ArrayList<>();

        goConfigService.groups().accept(new PipelineGroupVisitor() {
            @Override
            public void visit(PipelineConfigs group) {
                GoDashboardPipelineGroup dashboardPipelineGroup = dashboardPipelineGroupFor(group, pipelineSelections, user);
                if (dashboardPipelineGroup.hasPipelines()) {
                    pipelineGroups.add(dashboardPipelineGroup);
                }
            }
        });

        return pipelineGroups;
    }

    public String asJson(PipelineSelections pipelineSelections, Username user) {
        List<GoDashboardPipelineGroup> groups = allPipelineGroupsForDashboard(pipelineSelections, user);

        final ObjectMapper mapper = new ObjectMapper();

        try {
            long startTime = System.currentTimeMillis();

            String json = mapper.writeValueAsString(GoDashboard.create(groups, user));
            long endTime = System.currentTimeMillis();

            System.out.println("Took " + (endTime - startTime) + " ms");

            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "{'error': 'Something went wrong!!'}";
    }

    public void updateCacheForPipeline(CaseInsensitiveString pipelineName) {
        PipelineConfigs group = goConfigService.findGroupByPipeline(pipelineName);
        PipelineConfig pipelineConfig = group.findBy(pipelineName);

        updateCache(group, pipelineConfig);
    }

    public void updateCacheForPipeline(PipelineConfig pipelineConfig) {
        updateCache(goConfigService.findGroupByPipeline(pipelineConfig.name()), pipelineConfig);
    }

    public void updateCacheForAllPipelinesIn(CruiseConfig config) {
        cache.replaceAllEntriesInCacheWith(dashboardCurrentStateLoader.allPipelines(config));
    }

    private GoDashboardPipelineGroup dashboardPipelineGroupFor(PipelineConfigs pipelineGroup, PipelineSelections pipelineSelections, Username user) {
        GoDashboardPipelineGroup goDashboardPipelineGroup = new GoDashboardPipelineGroup(pipelineGroup.getGroup(), resolvePermissionsForPipelineGroup(pipelineGroup));

        if (goDashboardPipelineGroup.hasPermissions() && goDashboardPipelineGroup.canBeViewedBy(user.getUsername().toString())) {
            pipelineGroup.accept(new PiplineConfigVisitor() {
                @Override
                public void visit(PipelineConfig pipelineConfig) {
                    if (pipelineSelections.includesPipeline(pipelineConfig)) {
                        goDashboardPipelineGroup.addPipeline(cache.get(pipelineConfig.getName()));
                    }
                }
            });
        }
        return goDashboardPipelineGroup;
    }

    private Permissions resolvePermissionsForPipelineGroup(PipelineConfigs pipelineGroup) {
        for (PipelineConfig pipelineConfig : pipelineGroup) {
            GoDashboardPipeline goDashboardPipeline = cache.get(pipelineConfig.getName());
            if (goDashboardPipeline != null) {
                return goDashboardPipeline.permissions();
            }
        }

        return null;
    }

    private void updateCache(PipelineConfigs group, PipelineConfig pipelineConfig) {
        cache.put(dashboardCurrentStateLoader.pipelineFor(pipelineConfig, group));
    }
}
