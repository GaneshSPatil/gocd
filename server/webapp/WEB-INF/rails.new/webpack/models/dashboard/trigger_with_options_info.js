/*
 * Copyright 2018 ThoughtWorks, Inc.
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

const _                    = require('lodash');
const s                    = require('helpers/string-plus');
const sparkRoutes          = require('helpers/spark_routes');
const AjaxHelper           = require('helpers/ajax_helper');
const Material             = require('models/dashboard/material');
const EnvironmentVariables = require('models/dashboard/environment_variables');

const TriggerWithOptionsInfo = function (materials, plainTextVariables, secureVariables) {
  const self = this;

  this.materials          = materials;
  this.plainTextVariables = plainTextVariables;
  this.secureVariables    = secureVariables;

  this.validateTriggerRevisions = (searchVM, vm) => {
    _.each(self.materials, (material) => {
      if (material.selection() !== searchVM[material.name]) {
        const msg = `Invalid revision ${searchVM[material.name]} found for material ${material.name}`;
        vm.error(msg);
      }
    });
  };

  this.getTriggerOptionsJSON = () => {
    const json = {};

    json['update_materials_before_scheduling'] = false;

    json['materials'] = _.reduce(self.materials, (selections, material) => {
      if (material.selection()) {
        selections.push({
          fingerprint: material.fingerprint,
          revision:    material.selection()
        });
      }

      return selections;
    }, []);

    json['environment_variables'] = _.reduce(self.plainTextVariables.concat(self.secureVariables), (allEnvs, envVar) => {
      if (envVar.isDirtyValue()) {
        allEnvs.push({
          name:   envVar.name,
          value:  envVar.value(),
          secure: envVar.isSecureValue()
        });
      }

      return allEnvs;
    }, []);

    return json;
  };
};

class Revision {
  constructor({date, user, comment, revision}) {
    this.date     = date;
    this.user     = user;
    this.comment  = comment;
    this.revision = revision;
  }
}

const isSecure    = (v) => v.secure;
const isPlainText = (v) => !v.secure;

TriggerWithOptionsInfo.fromJSON = (json, pipelineName) => {
  const materials = _.map(JSON.parse(JSON.stringify(json.materials, s.camelCaser)), function (materialMap) {
    materialMap.revision = new Revision({revision: _.get(materialMap, 'revision.lastRunRevision'), ...materialMap.revision});
    return new Material({pipelineName, ...materialMap});
  });

  const plainTextVariables = EnvironmentVariables.fromJSON(_.filter(json.variables, isPlainText));
  const secureVariables    = EnvironmentVariables.fromJSON(_.filter(json.variables, isSecure));

  return new TriggerWithOptionsInfo(materials, plainTextVariables, secureVariables);
};

TriggerWithOptionsInfo.all = function (pipelineName) {
  return AjaxHelper.GET({
    url:        sparkRoutes.pipelineTriggerWithOptionsViewPath(pipelineName),
    apiVersion: 'v1',
  }).then(function (data) {
    return TriggerWithOptionsInfo.fromJSON(data, pipelineName);
  });
};

module.exports = TriggerWithOptionsInfo;
