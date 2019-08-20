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
import {Job} from "models/pipeline_configs/job";
import {AutocompleteField} from "views/components/forms/autocomplete";
import {IdentifierInputField} from "views/components/forms/common_validating_inputs";
import {Form, FormBody} from "views/components/forms/form";
import {RadioField, TextField} from "views/components/forms/input_fields";
import {ElasticProfileDynamicSuggestionsProvider} from "views/pages/pipeline_configs/jobs/elastic_profile_dynamic_suggestions_provider";
import {IDENTIFIER_FORMAT_HELP_MESSAGE} from "../messages";
import * as _ from "lodash";
import Stream from "mithril/stream";

interface Attrs {
  job: Job;
}

export class JobDetails extends MithrilViewComponent<Attrs> {
  private runOnElasticAgent: Stream<boolean> = Stream();

  oninit(vnode: m.Vnode<Attrs, this>): any {
    this.runOnElasticAgent = Stream(!_.isEmpty(vnode.attrs.job.elasticProfileId()));
  }

  view(vnode: m.Vnode<Attrs>) {
    let html;
    if (this.runOnElasticAgent()) {
      html = <AutocompleteField label="Dynamic" property={vnode.attrs.job.elasticProfileId} provider={new ElasticProfileDynamicSuggestionsProvider()}/>
        // <TextField property={vnode.attrs.job.elasticProfileId} label="Elastic profile"/>;
    } else {
      html = <TextField property={vnode.attrs.job.resources} label="Resources"/>;
    }
    return <FormBody>
      <Form last={true} compactForm={true}>
        <IdentifierInputField label="Job Name" helpText={IDENTIFIER_FORMAT_HELP_MESSAGE}
                              placeholder="e.g., run-unit-tests" property={vnode.attrs.job.name}
                              errorText={vnode.attrs.job.errors().errorsForDisplay("name")} required={true}/>
        <RadioField label="Where should this jon run?"
                    property={this.runOnElasticAgent}
                    possibleValues={new Map([
                                              ["Static agent", false],
                                              ["Elastic agent", true]
                                            ])}>
        </RadioField>
        {html}
      </Form>
    </FormBody>;
  }
}
