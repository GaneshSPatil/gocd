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
import com.thoughtworks.go.domain.StageState;
import com.thoughtworks.go.presentation.pipelinehistory.StageInstanceModel;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;

import java.util.Date;

import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Link.self;

public class StageInstanceRepresenter extends HalRepresentation {
    @JsonProperty("name")
    private final String name;

    @JsonProperty("status")
    private final StageState state;

    @JsonProperty("approved_by")
    private final String approvedBy;

    @JsonProperty("scheduled_at")
    private final Date scheduledDate;

    public StageInstanceRepresenter(Links links, String name, StageState state, String approvedBy, Date scheduledDate) {
        super(links);
        this.name = name;
        this.state = state;
        this.approvedBy = approvedBy;
        this.scheduledDate = scheduledDate;
    }

    public static StageInstanceRepresenter create(StageInstanceModel model) {
        Links links = Links.linkingTo(
                self("http://localhost:8153/go/api/stages/up42/1/up42_stage/1"),
                link("doc", "https://api.go.cd/current/#get-stage-instance")
        );

        return new StageInstanceRepresenter(links, model.getName(), model.getState(), model.getApprovedBy(), model.getScheduledDate());
    }
}
