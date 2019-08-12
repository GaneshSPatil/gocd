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

import * as m from "mithril";
import * as Buttons from "views/components/buttons";
import {Modal, Size} from "views/components/modal";

export class PipelineSettingsModal extends Modal {

  constructor() {
    super(Size.large);
    this.closeModalOnOverlayClick = false;
  }

  body(): m.Children {
    return <p>Pipeline Settings go here</p>;
  }

  buttons(): m.ChildArray {
    return [
      <Buttons.Primary data-test-id="button-ok" onclick={this.close.bind(this)}>Save</Buttons.Primary>,
      <Buttons.Cancel data-test-id="button-cancel" onclick={this.close.bind(this)}>Cancel</Buttons.Cancel>
    ];
  }

  title(): string {
    return "Pipeline Settings";
  }
}
