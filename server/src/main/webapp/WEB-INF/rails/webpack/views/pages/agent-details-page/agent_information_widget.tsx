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
import {Table} from "views/components/table";
import styles from "views/pages/agent-details-page/index.scss";

interface Attrs {
  agent: Stream<Agent>;
}

export class AgentInformationWidget extends MithrilViewComponent<Attrs> {
  view(vnode: m.Vnode<Attrs>) {
    const agent        = vnode.attrs.agent();
    const resources    = agent.resources.length == 0
      ? <em>Not Specified</em>
      : agent.resources.join("| ");
    const environments = agent.environments.length == 0
      ? <em>Not Specified</em>
      : agent.environments.map(agentEnv => agentEnv.name).join("| ");

    const data = [
      [<div class={styles.key}>Free Space:</div>, agent.readableFreeSpace()],
      [<div class={styles.key}>Sandbox:</div>, agent.sandbox],
      [<div class={styles.key}>IP Address:</div>, agent.ipAddress],
      [<div class={styles.key}>Operating System:</div>, agent.operatingSystem],
      [<div class={styles.key}>Resources: </div>, resources],
      [<div class={styles.key}>Environments:</div>, environments],
    ];

    return <div className={styles.agentInformation}>
      <Table headers={[] as any} data={data}/>
    </div>;
  }
}
