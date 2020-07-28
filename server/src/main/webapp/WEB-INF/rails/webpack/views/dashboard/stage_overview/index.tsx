/*
 * Copyright 2020 ThoughtWorks, Inc.
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

import m from "mithril";
import Stream from "mithril/stream";
import {MithrilComponent} from "../../../jsx/mithril-component";
import {Spinner} from "../../components/spinner";
import * as styles from "./index.scss";
import {JobsListWidget} from "./jobs_list_widget";
import {JobCountAndRerunWidget} from "./job_count_and_rerun_widget";
import {StageOverviewViewModel} from "./models/stage_overview_view_model";
import {StageState} from "./models/types";
import {StageHeaderWidget} from "./stage_overview_header";

export interface Attrs {
  pipelineName: string;
  pipelineCounter: string | number;
  stageName: string;
  stageCounter: string | number;
  stageStatus: StageState;
  stageOverviewVM: Stream<StageOverviewViewModel | undefined>;
}

export class StageOverview extends MithrilComponent<Attrs, {}> {
  onupdate(vnode: m.Vnode<Attrs, {}>) {
    vnode.dom.onclick = (e) => {
      e.stopPropagation();
    };
  }

  view(vnode: m.Vnode<Attrs, {}>): m.Children | void | null {
    if (!vnode.attrs.stageOverviewVM()) {
      return <div data-test-id="stage-overview-container" className={styles.stageOverviewContainer}>
        <Spinner/>
      </div>;
    }

    return <div data-test-id="stage-overview-container" class={styles.stageOverviewContainer}>
      <StageHeaderWidget stageName={vnode.attrs.stageName}
                         stageCounter={vnode.attrs.stageCounter}
                         pipelineName={vnode.attrs.pipelineName}
                         pipelineCounter={vnode.attrs.pipelineCounter}
                         stageInstance={vnode.attrs.stageOverviewVM().stageInstance}/>
      <JobCountAndRerunWidget jobsVM={vnode.attrs.stageOverviewVM().jobsVM}/>
      <JobsListWidget stageName={vnode.attrs.stageName}
                      stageCounter={vnode.attrs.stageCounter}
                      pipelineName={vnode.attrs.pipelineName}
                      pipelineCounter={vnode.attrs.pipelineCounter}
                      jobsVM={vnode.attrs.stageOverviewVM().jobsVM}
                      agents={vnode.attrs.stageOverviewVM().agents}
                      lastPassedStageInstance={vnode.attrs.stageOverviewVM().lastPassedStageInstance}/>
    </div>;

  }
}
