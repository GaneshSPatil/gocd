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

package com.thoughtworks.go.server.representers.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;

import java.util.Set;

import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Link.self;
import static de.otto.edison.hal.Links.linkingTo;

public class PipelineGroup extends HalRepresentation {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("pipelines")
    private final Set<String> pipelines;
    @JsonProperty("can_administer")
    private final boolean canBeAdministeredBy;

    public PipelineGroup(String name, Set<String> pipelines, boolean canBeAdministeredBy, Links links) {
        super(links);

        this.name = name;
        this.pipelines = pipelines;
        this.canBeAdministeredBy = canBeAdministeredBy;
    }

    public static PipelineGroup create(String name, Set<String> pipelines, boolean canBeAdministeredBy) {
        Links links = linkingTo(
                self("http://localhost:8153/go/api/config/pipeline_groups"),
                link("doc", "https://api.go.cd/current/#pipeline-groups"));
        return new PipelineGroup(name, pipelines, canBeAdministeredBy, links);
    }
}