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
import {MithrilComponent} from "jsx/mithril-component";
import {ExecTask, Task} from "views/pages/pipeline_configs/stages/on_create/models/task";
import {TasksListWidget} from "views/pages/pipeline_configs/stages/on_create/tasks_list_widget";
import styles from "./tasks.scss";
import {TaskDescriptionWidget} from "./task_description_widget";

export interface Attrs {
}

export interface State {
  tasks: Task[]
  selectedTask: Stream<Task>
}

export class Tasks extends MithrilComponent<Attrs, State> {
  oninit(vnode: m.Vnode<Attrs, State>) {
    vnode.state.tasks        = [];
    vnode.state.selectedTask = Stream(new ExecTask("ls -alh") as Task);
  }

  view(vnode: m.Vnode<Attrs, State>) {
    return <div class={styles.tasksContainer} data-test-id="tasks-tab">
      <TasksListWidget/>
      <TaskDescriptionWidget task={vnode.state.selectedTask}/>
    </div>;
  }
}
