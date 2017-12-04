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
import com.thoughtworks.go.presentation.pipelinehistory.EmptyPipelineInstanceModel;
import com.thoughtworks.go.presentation.pipelinehistory.PipelineInstanceModel;
import com.thoughtworks.go.presentation.pipelinehistory.PipelineInstanceModels;
import com.thoughtworks.go.server.dashboard.GoDashboardPipeline;
import com.thoughtworks.go.server.domain.Username;
import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;

import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Link.self;

public class PipelineRepresenter extends HalRepresentation {
    @JsonProperty("name")
    private String name;

    @JsonProperty("last_updated_timestamp")
    private long lastUpdatedTimeStamp;

    @JsonProperty("locked")
    private boolean currentlyLocked;

    @JsonProperty("pause_info")
    private PauseInfoRerepresenter pauseInfoRerepresenter;

    @JsonProperty("can_operate")
    private final boolean canOperate;

    @JsonProperty("can_administer")
    private final boolean canAdministere;

    @JsonProperty("can_unlock")
    private final boolean canUnlock;

    @JsonProperty("can_pause")
    private final boolean canPause;

    public PipelineRepresenter(Links links, Embedded embedded, String name, long lastUpdatedTimeStamp, boolean currentlyLocked, PauseInfoRerepresenter pauseInfoRerepresenter, boolean canOperate, boolean canAdministere, boolean canUnlock, boolean canPause) {
        super(links, embedded);

        this.name = name;
        this.lastUpdatedTimeStamp = lastUpdatedTimeStamp;
        this.currentlyLocked = currentlyLocked;
        this.pauseInfoRerepresenter = pauseInfoRerepresenter;
        this.canOperate = canOperate;
        this.canAdministere = canAdministere;
        this.canUnlock = canUnlock;
        this.canPause = canPause;
    }

    public static PipelineRepresenter create(GoDashboardPipeline pipeline, Username username, Embedded embedded) {
        PipelineInstanceModel latestPipelineInstance = pipeline.model().getLatestPipelineInstance();
        PipelineInstanceModel pipelineInstance = latestPipelineInstance;

        PipelineInstanceModels pipelineInstanceModels = PipelineInstanceModels.createPipelineInstanceModels();

        for (PipelineInstanceModel model : pipeline.model().getActivePipelineInstances()) {
            if (!(model instanceof EmptyPipelineInstanceModel)) {
                pipelineInstanceModels.add(model);
            }
        }

        Links links = Links.linkingTo(
                self("http://localhost:8153/go/api/pipelines/up42/history"),
                link("doc", "https://api.go.cd/current/#pipelines"),
                link("settings_path", "http://localhost:8153/go/admin/pipelines/up42/general"),
                link("trigger", "http://localhost:8153/go/api/pipelines/up42/schedule"),
                link("trigger_with_options", "http://localhost:8153/go/api/pipelines/up42/schedule"),
                link("pause", "http://localhost:8153/go/api/pipelines/up42/pause"),
                link("unpause", "http://localhost:8153/go/api/pipelines/up42/unpause")
        );

        return new PipelineRepresenter(links, embedded, pipeline.name().toString(),
                pipeline.getLastUpdatedTimeStamp(),
                pipelineInstance.isCurrentlyLocked(),
                PauseInfoRerepresenter.create(pipeline.model().getPausedInfo()),
                pipeline.isPipelineOperator(username.getUsername().toString()),
                pipeline.canBeAdministeredBy(username.getUsername().toString()),
                pipeline.canBeOperatedBy(username.getUsername().toString()),
                pipeline.canBeOperatedBy(username.getUsername().toString())
        );
    }
}
