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
import {Job} from "models/pipeline_configs/job";
import {Stage} from "models/pipeline_configs/stage";
import {IdentifierInputField} from "views/components/forms/common_validating_inputs";
import {Form, FormBody} from "views/components/forms/form";
import {Modal, Size} from "views/components/modal";
import m from "mithril";
import {SwitchBtn} from "views/components/switch";
import {Tabs} from "views/components/tab";
import * as Buttons from "views/components/buttons";
import {TooltipSize} from "views/components/tooltip";
import {JobDetails} from "views/pages/pipeline_configs/jobs/forms";
import {Tasks} from "views/pages/pipeline_configs/stages/on_create/tasks_widget";
import {AdvancedSettings} from "views/pages/pipelines/advanced_settings";
import {IDENTIFIER_FORMAT_HELP_MESSAGE} from "views/pages/pipelines/messages";
import * as Tooltip from "views/components/tooltip";


interface Attrs {
  stage: Stage
}

export class AddStageModal extends Modal {
  private readonly stage: Stage;
  private readonly job: Job;

  constructor() {
    super(Size.large);
    this.stage = new Stage("", []);
    this.job   = new Job("", []);
  }

  body(): m.Children {
    const stageDetails = <StagesDetails stage={this.stage}/>;
    const jobDetails   = <JobDetails job={this.job}/>;
    const tasks   = <Tasks />;


    return <Tabs tabs={["Stage details", "Job details", "Tasks"]} contents={[tasks, stageDetails, jobDetails]}/>;
  }

  title(): string {
    return "Add stage";
  }

  buttons(): m.ChildArray {
    return [
      <Buttons.Primary data-test-id="button-ok" onclick={() => {
        this.close();
      }}>Add</Buttons.Primary>
    ];
  }
}

export class StagesDetails extends MithrilViewComponent<Attrs> {
  view(vnode: m.Vnode<Attrs>) {
    const stage = vnode.attrs.stage;

    return <FormBody>
      <Form last={true} compactForm={true}>
        <IdentifierInputField label="Stage Name" helpText={IDENTIFIER_FORMAT_HELP_MESSAGE}
                              placeholder="e.g., Test-and-Report" property={stage.name}
                              errorText={stage.errors().errorsForDisplay("name")} required={true}/>

        <AdvancedSettings>
          <SwitchBtn label={<div>
            Automatically run this stage on upstream changes
            <Tooltip.Help size={TooltipSize.medium}
                          content="Enabling this means that this stage will automatically run when code is updated or its preceding or upstream stage passes. Disabling this means you must trigger this stage manually."/>
          </div>} field={stage.approval().state} small={true}/>

          <SwitchBtn label={<div>
            Fetch materials
            <Tooltip.Help size={TooltipSize.small} content="Perform material updates or checkouts"/>
          </div>} field={stage.fetchMaterials} small={true}/>

          <SwitchBtn label={<div>
            Never cleanup artifacts
            <Tooltip.Help size={TooltipSize.small}
                          content="Never cleanup artifacts for this stage, if purging artifacts is configured at the Server Level"/>
          </div>} field={stage.artifactCleanupProhibited} small={true}/>

          <SwitchBtn label={<div>
            Clean working directory
            <Tooltip.Help size={TooltipSize.small}
                          content="Remove all files/directories in the working directory on the agent"/>
          </div>} field={stage.cleanWorkingDir} small={true}/>
        </AdvancedSettings>
      </Form>
    </FormBody>;
  }
}
