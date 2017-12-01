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

import com.thoughtworks.go.server.dashboard.GoDashboardPipeline;
import com.thoughtworks.go.server.dashboard.GoDashboardPipelineGroup;
import com.thoughtworks.go.server.domain.Username;
import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.otto.edison.hal.Embedded.embeddedBuilder;
import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Link.self;
import static de.otto.edison.hal.Links.linkingTo;

public class GoDashboard extends HalRepresentation {

    public GoDashboard(Links links, Embedded embedded) {
        super(links, embedded);
    }

    public static GoDashboard create(List<GoDashboardPipelineGroup> groups, Username username) {
        System.out.println(getGroups(groups, username).toString());
        return new GoDashboard(linkingTo(
                self("http://localhost:8153/go/api/dashboard"),
                link("doc", "https://api.go.cd/current/#dashboard")
        ), embeddedBuilder()
                .with("pipeline_groups", getGroups(groups, username))
                .with("pipelines", getPipelines(groups, username))
                .build());
    }

    private static List<? extends HalRepresentation> getPipelines(List<GoDashboardPipelineGroup> groups, Username username) {
        ArrayList<PipelineRepresenter> pipelines = new ArrayList<>();

        for (GoDashboardPipelineGroup group : groups) {
            for (GoDashboardPipeline pipeline : group.allPipelines()) {
                String name = pipeline.name().toString();
                pipelines.add(PipelineRepresenter.create(name));
            }
        }

        return pipelines;
    }

    private static List<PipelineGroup> getGroups(List<GoDashboardPipelineGroup> groups, Username username) {
        ArrayList<PipelineGroup> pipelineGroups = new ArrayList<>();

        for (GoDashboardPipelineGroup group : groups) {
            String name = group.getName();
            Set<String> pipelines = group.allPipelineNames();
            boolean canBeAdministeredBy = group.canBeAdministeredBy(username.getUsername().toString());

            pipelineGroups.add(PipelineGroup.create(name, pipelines, canBeAdministeredBy));
        }

        return pipelineGroups;
    }
}