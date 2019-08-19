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

import {MithrilViewComponent} from "jsx/mithril-component";
import m from "mithril";
import {NameableSet} from "models/pipeline_configs/nameable_set";
import {Stage} from "models/pipeline_configs/stage";
import {Secondary} from "views/components/buttons";
import * as styles from "views/pages/pipeline_configs/index.scss";
import {AddStageModal} from "views/pages/pipeline_configs/stages/forms";
import {ConceptDiagram} from "views/pages/pipelines/concept_diagram";
import * as collapseStyles from "views/components/collapsible_panel/index.scss";
import * as _ from "lodash";

interface Attrs {
  stages: NameableSet<Stage>;
}

const stageImg = require("../../../../../app/assets/images/concept_diagrams/concept_stage.svg");
const jobImg   = require("../../../../../app/assets/images/concept_diagrams/concept_job.svg");

export class StagesWidget extends MithrilViewComponent<Attrs> {
  view(vnode: m.Vnode<Attrs>) {
    let stages = [];
    if (_.isEmpty(vnode.attrs) || vnode.attrs.stages.size == 0) {
      stages =
        [
          <div style="display: flex; justify-content:space-around">
            <ConceptDiagram image={stageImg}>
              A <strong>stage</strong> is a group of jobs, and a <strong>job</strong> is a piece of work to execute.
            </ConceptDiagram>
            <ConceptDiagram image={jobImg}>
              A <strong>job</strong> is like a script, where each sequential step is called a <strong>task</strong>.
              Typically, a task is a single command.
            </ConceptDiagram>
          </div>];
    } else {
      stages = ["sdfsd"];
    }
    return <div>
      <div class={styles.header}>
        <div class={collapseStyles.headerDetails}>
          <div class={styles.headerText}>Stages</div>
        </div>
        <div class={collapseStyles.actions}>
          <Secondary onclick={() => new AddStageModal().render()}>Add stage</Secondary>
        </div>
      </div>
      <div class={styles.panelContent}>
        {stages}
      </div>
    </div>;
  }
}
