/*
 * Copyright 2019 ThoughtWorks, Inc.
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

import * as m from "mithril";
import {PipelineConfig} from "models/pipeline_configs/pipeline_config";
import {PipelineSettingsModal} from "views/pages/pipeline_configs/pipeline_settings_modal";
import {PipelineConfigsWidget} from "views/pages/pipeline_configs/pipeline_configs_widget";
import {Page} from "views/pages/page";

interface State {
  dummy: PipelineConfig;
}

export class PipelineConfigsPage extends Page<null, State> {
  componentToDisplay(vnode: m.Vnode<null, State>): m.Children {
    return <PipelineConfigsWidget pipelineConfig={vnode.state.dummy}
                                  onPipelineSettingsEdit={this.showPipelineSettings}/>;
  }

  pageName(): string {
    return "Pipelines";
  }

  fetchData(vnode: m.Vnode<null, State>): Promise<any> {
    // to be implemented
    const pipelineConfig = new PipelineConfig("", [], []);
    vnode.state.dummy    = pipelineConfig;
    return Promise.resolve(pipelineConfig);
  }

  showPipelineSettings(e: Event) {
    e.stopPropagation();
    new PipelineSettingsModal().render();
  }
}
