/*
 * Copyright 2020 ThoughtWorks, Inc.
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
import {MithrilViewComponent} from "../../../jsx/mithril-component";
import {ButtonGroup, Primary} from "../../components/buttons";
import * as styles from "./index.scss";
import Stream from "./jobs_list_widget";
import {JobsViewModel} from "./models/jobs_view_model";

export interface Attrs {
  jobsVM: Stream<JobsViewModel>;
}

export class JobCountAndRerunWidget extends MithrilViewComponent<Attrs> {
  view(vnode: m.Vnode<Attrs, this>): m.Children | void | null {
    return <div class={styles.jobCountAndRerunContainer} data-test-id="job-count-and-rerun-container">
      <div class={styles.jobCountContainer} data-test-id="job-cont-container">
        <div class={styles.countContainer} data-test-id="in-progress-jobs-container">
          <div class={`${styles.unknown} ${styles.jobResult}`}/>
          <div class={styles.countText}>In Progress : {vnode.attrs.jobsVM().buildingJobNames().length}</div>
        </div>
        <div class={styles.countContainer} data-test-id="passed-jobs-container">
          <div className={`${styles.passed} ${styles.jobResult}`}/>
          <div class={styles.countText}>Passed : {vnode.attrs.jobsVM().passedJobNames().length}</div>
        </div>
        <div class={styles.countContainer} data-test-id="failed-jobs-container">
          <div className={`${styles.failed} ${styles.jobResult}`}/>
          <div class={styles.countText}>Failed : {vnode.attrs.jobsVM().failedJobNames().length}</div>
        </div>
      </div>
      <div data-test-id="job-rerun-container">
        <ButtonGroup>
          <Primary small={true}>Rerun Failed</Primary>
          <Primary small={true}>Rerun Selected</Primary>
        </ButtonGroup>
      </div>
    </div>;
  }

}