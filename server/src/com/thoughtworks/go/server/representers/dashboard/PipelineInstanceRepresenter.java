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
import com.thoughtworks.go.presentation.pipelinehistory.PipelineInstanceModel;
import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;

import java.util.Date;

import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Link.self;

public class PipelineInstanceRepresenter extends HalRepresentation {
    @JsonProperty("labelfoo")
    private String label;

    @JsonProperty("scheduled_at")
    private final Date scheduledDate;

    @JsonProperty("triggered_by")
    private final String approvedBy;

    public PipelineInstanceRepresenter(Links links, Embedded embedded, String label, Date scheduledDate, String approvedBy) {
        super(links, embedded);
        this.label = label;
        this.scheduledDate = scheduledDate;
        this.approvedBy = approvedBy;
    }

    public static PipelineInstanceRepresenter create(Embedded embedded, PipelineInstanceModel instance) {
        Links links = Links.linkingTo(
                self("http://localhost:8153/go/api/pipelines/up42/history"),
                link("doc", "https://api.go.cd/current/#pipelines"),
                link("history_url", "http://localhost:8153/go/api/pipelines/up42/history"),
                link("vsm_url", "http://localhost:8153/go/pipelines/value_stream_map/up42/1"),
                link("compare_url", "http://localhost:8153/go/compare/up42/0/with/1"),
                link("build_cause_url", "http://localhost:8153/go/pipelines/up42/1/build_cause")
        );

        return new PipelineInstanceRepresenter(links, embedded, instance.getLabel(), instance.getScheduledDate(), instance.getApprovedBy());
    }
}
