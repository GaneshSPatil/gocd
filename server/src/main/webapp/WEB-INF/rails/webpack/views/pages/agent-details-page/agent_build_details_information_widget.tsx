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
import Stream from "mithril/stream";
import {Agent} from "models/agents/agents";
import {JobRunHistoryAPI} from "models/agents/job_run_history";

interface Attrs {
  agent: Stream<Agent>;
}

export class AgentBuildDetailsInformationWidget extends MithrilViewComponent<Attrs> {
  constructor() {
    super();
    JobRunHistoryAPI.all("b23c4be9-a278-4746-bab0-7c6c13c02f0b");
  }

  view(vnode: m.Vnode<Attrs>) {
    return <div>This is blah</div>;
  }
}

