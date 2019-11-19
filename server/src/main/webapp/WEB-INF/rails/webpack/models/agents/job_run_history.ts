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

import {ApiRequestBuilder, ApiResult} from "helpers/api_request_builder";
import {SparkRoutes} from "helpers/spark_routes";

export interface JobStateTransitionJSON {
  state_change_time: number,
  id: number,
  state: string
}

export class JobStateTransition {
  readonly stateChangeTime: number;
  readonly id: number;
  readonly state: string;

  constructor(stateChangeTime: number, id: number, state: string) {
    this.stateChangeTime = stateChangeTime;
    this.id              = id;
    this.state           = state;
  }

  static fromJSON(json: JobStateTransitionJSON) {
    return new JobStateTransition(json.state_change_time, json.id, json.state);
  }
}

export interface JobRunHistoryJSON {
  scheduled_date: number, //might not be required as this information can be read from job state transitions
  pipeline_name: string,
  pipeline_counter: number,
  stage_name: string,
  stage_counter: string,
  "name": string,
  rerun: boolean,
  result: string,
  state: string,
  id: number,
  "agent_uuid": string,
  "job_state_transitions": JobStateTransitionJSON[]
}

export class JobRunHistory {
  readonly pipelineName: string;
  readonly pipelineCounter: number;
  readonly stageName: string;
  readonly stageCounter: string;
  readonly jobName: string;
  readonly rerun: boolean;
  readonly result: string;
  readonly id: number;
  readonly jobStateTransitions: JobStateTransition[];

  constructor(pipelineName: string,
              pipelineCounter: number,
              stageName: string,
              stageCounter: string,
              jobName: string,
              rerun: boolean,
              id: number,
              result: string,
              jobStateTransitions: JobStateTransition[]) {
    this.pipelineName        = pipelineName;
    this.pipelineCounter     = pipelineCounter;
    this.stageName           = stageName;
    this.stageCounter        = stageCounter;
    this.jobName             = jobName;
    this.rerun               = rerun;
    this.id                  = id;
    this.result              = result;
    this.jobStateTransitions = jobStateTransitions;
  }

  static fromJSON(json: JobRunHistoryJSON) {
    const jobStateTransitions: JobStateTransition[] = json.job_state_transitions.map(JobStateTransition.fromJSON);
    return new JobRunHistory(json.pipeline_name,
                             json.pipeline_counter,
                             json.stage_name,
                             json.stage_counter,
                             json.name,
                             json.rerun,
                             json.id,
                             json.result,
                             jobStateTransitions);
  }
}

export class JobRunHistoryAPI {
  static all(uuid: string) {
    return ApiRequestBuilder.GET(SparkRoutes.agentJobRunHistoryAPIPath(uuid))
                            .then((result: ApiResult<string>) => result.map((body) => {
                              return JobRunHistory.fromJSON(JSON.parse(body));
                            }));
  }
}
