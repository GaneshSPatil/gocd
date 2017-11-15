/*
 * Copyright 2017 ThoughtWorks, Inc.
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

const PipelineGroups = require('models/dashboard/pipeline_groups');
const Pipelines      = require('models/dashboard/pipelines');
const mrequest       = require('helpers/mrequest');
const Routes         = require('gen/js-routes');
const $              = require('jquery');
const m              = require('mithril');

const Dashboard = function (json) {
  const pipelineGroups = new PipelineGroups(json._embedded.pipeline_groups);
  const pipelines      = new Pipelines(json._embedded.pipelines);

  this.getPipelineGroups = () => {
    return pipelineGroups.groups;
  };

  this.getPipelines = () => {
    return pipelines.pipelines;
  };

  this.findPipeline = (pipelineName) => {
    return pipelines.find(pipelineName);
  };
};


Dashboard.get = () => {
  const config = (xhr) => {
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Accept", "application/vnd.go.cd.v2+json");
  };

  return $.Deferred(function () {
    const deferred = this;
    const jqXHR = $.ajax({
      method:     'GET',
      url:        Routes.apiv2ShowDashboardPath(),
      timeout:    mrequest.timeout,
      beforeSend: config
    });

    jqXHR.then((data, _textStatus, _jqXHR) => {
      deferred.resolve((new Dashboard(data)));
    });

    jqXHR.always(m.redraw);

  }).promise();
};

module.exports = Dashboard;
