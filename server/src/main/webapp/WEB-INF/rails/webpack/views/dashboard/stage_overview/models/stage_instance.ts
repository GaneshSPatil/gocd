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

import Stream from "mithril/stream";
import moment from "moment";
import {JobsViewModel} from "./jobs_view_model";
import {JobJSON, Result, StageInstanceJSON} from "./types";

export class StageInstance {
  readonly jobsVM: Stream<JobsViewModel>;
  private json: StageInstanceJSON;

  constructor(json: StageInstanceJSON) {
    this.json = json;
    this.jobsVM = Stream(new JobsViewModel(json.jobs));
  }

  static fromJSON(json: StageInstanceJSON) {
    return new StageInstance(json);
  }

  triggeredBy(): string {
    return `Triggered by ${this.json.approved_by}`;
  }

  triggeredOn(): string {
    const LOCAL_TIME_FORMAT = "DD MMM, YYYY [at] HH:mm:ss [Local Time]";
    return `on ${moment.unix(this.stageScheduledTime()).format(LOCAL_TIME_FORMAT)}`;
  }

  stageDuration(): string {
    if (this.isStageInProgress()) {
      return `in progress`;
    }

    const highestJobTime = this.json.jobs.reduce((first: number, next: JobJSON) => {
      const completed = next.job_state_transitions.find(t => t.state === "Completed");
      return first < completed.state_change_time ? completed.state_change_time : first;
    }, 0);

    const end = moment.unix(highestJobTime / 1000);
    const start = moment.unix(this.stageScheduledTime());

    return moment.utc(end.diff(start)).format("HH:mm:ss");
  }

  private stageScheduledTime(): number {
    return this.json.jobs[0].scheduled_date / 1000;
  }

  private isStageInProgress(): boolean {
    return this.json.result === Result[Result.Unknown];
  }
}