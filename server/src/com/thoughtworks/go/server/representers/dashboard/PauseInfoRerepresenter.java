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
import com.thoughtworks.go.domain.PipelinePauseInfo;
import de.otto.edison.hal.HalRepresentation;

public class PauseInfoRerepresenter extends HalRepresentation{
    @JsonProperty("paused")
    private final boolean paused;

    @JsonProperty("paused_by")
    private String pauseBy;

    @JsonProperty("pause_reason")
    private final String pauseCause;

    public PauseInfoRerepresenter(boolean paused, String pauseBy, String pauseCause) {
        this.paused = paused;
        this.pauseBy = pauseBy;
        this.pauseCause = pauseCause;
    }

    public static PauseInfoRerepresenter create(PipelinePauseInfo pausedInfo) {
        return new PauseInfoRerepresenter(pausedInfo.isPaused(), pausedInfo.getPauseBy(), pausedInfo.getPauseCause());
    }
}
