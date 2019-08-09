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

import Page from "helpers/spa_base";
import {PipelineConfigsPage} from "views/pages/pipeline_configs";
import * as m from 'mithril';

export class PipelineConfigsSPA extends Page {
  constructor() {
    super(PipelineConfigsPage);
  }
}

//tslint:disable-next-line
new PipelineConfigsSPA();

document.addEventListener("DOMContentLoaded", function(event) {
  m.route(document.body, 'abcd', {
    abcd: {
      view: function () {
        return "hi"
      }
    },
    '/:pipeline_name':        {
      view: function () {
        return "kjxfndksjf"
      }
    },
    '':                         {
      view: function () {
        return "sdfsdf"
      }
    },
  });
});

