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
import {PipelineConfig} from "models/pipeline_configs/pipeline_config";
import {IdentifierInputField} from "views/components/forms/common_validating_inputs";
import * as Icons from "views/components/icons";
import {CollapsiblePanel} from "views/components/collapsible_panel";
import {MaterialOperations} from "views/pages/pipeline_configs";
import * as styles from "views/pages/pipeline_configs/index.scss";
import {MaterialsWidget} from "views/pages/pipeline_configs/materials";
import {StagesWidget} from "views/pages/pipeline_configs/stages";
import {ConceptDiagram} from "views/pages/pipelines/concept_diagram";
import * as _ from "lodash";

interface Attrs {
  pipelineConfig: PipelineConfig;
  onPipelineSettingsEdit: (e: Event) => void;
  materialOperations: MaterialOperations;
}

const pipelineImg = require("../../../../app/assets/images/concept_diagrams/concept_pipeline.svg");

export class PipelineConfigsWidget extends MithrilViewComponent<Attrs> {
  view(vnode: m.Vnode<Attrs>) {
    const expandedPipelinePanel = _.isEmpty(vnode.attrs.pipelineConfig.name());
    const pipelineLevelSettings = <Icons.Settings onclick={vnode.attrs.onPipelineSettingsEdit.bind(vnode.attrs)}/>;
    return <div>
      <CollapsiblePanel header={<div className={styles.headerText}>Pipelines</div>}
                        actions={pipelineLevelSettings}
                        expanded={expandedPipelinePanel}>
        <div style="display: flex">
          <IdentifierInputField required={true}
                     helpText="No spaces. Only letters, numbers, hyphens, underscores and period. Max 255 chars"
                     label="Pipeline name"
                     placeholder="e.g. build-and-deploy"
                     property={vnode.attrs.pipelineConfig.name}/>
          <div style="display:flex">
            <ConceptDiagram image={pipelineImg}/>
            <div style="align-self: center;width: 350px">
              In GoCD, a <strong>pipeline</strong> is a representation of a <strong>workflow</strong>.
              Pipelines consists of one or more <strong>stages</strong>.
            </div>
          </div>
        </div>
      </CollapsiblePanel>
      <MaterialsWidget materialOperations={vnode.attrs.materialOperations} materials={vnode.attrs.pipelineConfig.materials()}/>
      <StagesWidget stages={vnode.attrs.pipelineConfig.stages()}/>
    </div>;
  }
}
