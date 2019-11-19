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

import m from "mithril";
import Stream from "mithril/stream";
import {Agent} from "models/agents/agents";
import {AgentsCRUD} from "models/agents/agents_crud";
import {AgentDetailsPageWidget} from "views/pages/agent-details-page/agent_details_page_widget";
import {Page, PageState} from "views/pages/page";

interface State {
  agent: Stream<Agent>;
}

export class AgentDetailsPagePage extends Page<null, State> {

  componentToDisplay(vnode: m.Vnode<null, State>): m.Children {
    return <AgentDetailsPageWidget agent={vnode.state.agent}/>;
  }

  pageName(): string {
    return "Agent Details Page";
  }

  fetchData(vnode: m.Vnode<null, State>): Promise<any> {

    return Promise.all([AgentsCRUD.get("b23c4be9-a278-4746-bab0-7c6c13c02f0b")])
                  .then((results) => {
                    results[0].do((successResponse) => {
                      vnode.state.agent = Stream(successResponse.body);
                      this.pageState    = PageState.OK;
                    }, () => this.setErrorState());
                  });
  }
}
