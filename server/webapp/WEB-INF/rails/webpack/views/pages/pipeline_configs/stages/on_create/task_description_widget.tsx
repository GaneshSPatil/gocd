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
import $ from "jquery";
import {MithrilComponent, MithrilViewComponent} from "jsx/mithril-component";
import {Dropdown, DropdownElement} from "views/components/dropdown";
import {ExecTask, Task, TaskType} from "views/pages/pipeline_configs/stages/on_create/models/task";
import Stream from "mithril/stream";
import css from "views/pages/pipelines/task_terminal.scss";
import styles from "./tasks.scss";


export interface TasksDescriptionState {
  showCaveats: Stream<boolean>;
  refreshOnce: Stream<boolean>;
  toggleCaveats: () => void
}

export interface TasksDescriptionAttrs {
  task: Stream<Task>;
}

class ExecTaskDescriptionWidget extends MithrilComponent<TasksDescriptionAttrs, TasksDescriptionState> {
  oninit(vnode: m.Vnode<TasksDescriptionAttrs, TasksDescriptionState>) {
    //todo: Remove this. It was done to render the task command text only once. Talk to @Viraj for more information
    vnode.state.refreshOnce   = Stream();
    vnode.state.showCaveats   = Stream();
    vnode.state.toggleCaveats = function (): void {
      vnode.state.showCaveats(!vnode.state.showCaveats());
    };
  }

  oncreate(vnode: m.Vnode<TasksDescriptionAttrs, TasksDescriptionState>) {
    $("#command-editor").bind("DOMSubtreeModified", function () {
      (vnode.attrs.task() as ExecTask).command(this.textContent!);
    });
  }

  view(vnode: m.Vnode<TasksDescriptionAttrs, TasksDescriptionState>) {
    const dropdownItems = [
      {id: "0", value: "Custom Command"} as DropdownElement,
      {id: "1", value: "Fetch Artifact"} as DropdownElement
    ];

    //@ts-ignore
    const task: ExecTask = vnode.attrs.task();

    if (!vnode.state.refreshOnce()) {
      m.redraw();
      vnode.state.refreshOnce(true);
    }

    return <div class={styles.execTaskDescriptionContainer} data-test-class="exec-task-description">
      <Dropdown label={"Task Type"}
                property={Stream(`${task.getType()}`)}
                possibleValues={dropdownItems}/>
      <div class={styles.commandSection}>
        <span>Command</span>
        <code class={`${css.execEditor} ${styles.execEditor}`}>
          <div class={css.caveats + " " + (vnode.state.showCaveats() ? css.open : "")}>
            <span onclick={vnode.state.toggleCaveats.bind(vnode.state)}>Caveats</span>
            <p>This is not a real shell:</p>
            <p>- Pipes, loops, and conditionals will NOT work</p>
            <p>- Commands are not stateful; e.g., `cd foo` will NOT change cwd for subsequent commands</p>
          </div>
          <pre id="command-editor" contenteditable={true}
               class={css.currentEditor}>{m.trust(task.command() ? task.command()! : "")}</pre>
        </code>
      </div>
    </div>;
  }
}

class FetchArtifactTaskDescriptionWidget extends MithrilViewComponent<TasksDescriptionAttrs> {
  view(vnode: m.Vnode<TasksDescriptionAttrs>) {
    return <div>
      i am representing a fetch artifact task..
    </div>;
  }
}

export class TaskDescriptionWidget extends MithrilViewComponent<TasksDescriptionAttrs> {
  view(vnode: m.Vnode<TasksDescriptionAttrs>) {
    const taskToDisplay: Task = vnode.attrs.task();

    if (!taskToDisplay) {
      return <div>i have nothing to display</div>;
    }

    let widget: any;

    switch (taskToDisplay.getType()) {
      case TaskType.Exec:
        widget = <ExecTaskDescriptionWidget {...vnode.attrs}/>;
        break;
      case TaskType.Fetch:
        widget = <FetchArtifactTaskDescriptionWidget {...vnode.attrs}/>;
        break;
      default:
        throw new Error(`Tasks of type '${taskToDisplay.getType()}' can not be rendered!`);
    }

    return <div class={styles.taskDescriptionContainer}>
      {widget}
    </div>;
  }
}

