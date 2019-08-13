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

import {Material} from "models/materials/types";
import * as Buttons from "views/components/buttons";
import {Modal, Size} from "views/components/modal";
import * as m from "mithril";
import * as _ from "lodash";
import {MaterialEditor} from "views/pages/pipeline_configs/materials/material_editor";

export const SUPPORTED_MATERIALS = [
  {id: "git", text: "Git"},
  {id: "hg", text: "Mercurial"},
  {id: "svn", text: "Subversion"},
  {id: "p4", text: "Perforce"},
  {id: "tfs", text: "Team Foundation Server"},
  {id: "dependency", text: "Another Pipeline"},
];

export class AddMaterialModal extends Modal {
  private readonly material: Material;
  private readonly onSuccessfulAdd: Function;

  constructor(material: Material, onSuccessfulAdd: Function) {
    super(Size.large);
    this.material        = material;
    this.onSuccessfulAdd = onSuccessfulAdd;
  }

  body(): m.Children {
    return <MaterialEditor material={this.material}/>;
  }

  title(): string {
    if (_.isEmpty(this.material.attributes()) || _.isEmpty(this.material.attributes().name())) {
      return "Add material";
    }
    return this.material.attributes().name();
  }

  buttons(): m.ChildArray {
    return [
      <Buttons.Primary data-test-id="button-ok" onclick={() => {
        this.close();
        if (_.isEmpty(this.material.attributes().materialName())) {
          this.material.attributes().name(this.material.materialUrl());
        } else {
          this.material.attributes().name(this.material.attributes().materialName());
        }
        this.onSuccessfulAdd(this.material);
      }}>Add</Buttons.Primary>,
      <Buttons.Secondary>Check connection</Buttons.Secondary>
    ];
  }
}


export class UpdateMaterialModal extends Modal {
  private readonly material: Material;
  private readonly onSuccessfulUpdate: Function;

  constructor(material: Material, onSuccessfulUpdate: Function) {
    super(Size.large);
    this.material           = material;
    this.onSuccessfulUpdate = onSuccessfulUpdate;
  }

  body(): m.Children {
    return <MaterialEditor material={this.material}/>;
  }

  title(): string {
    return this.material.name();
  }

  buttons(): m.ChildArray {
    let originalMaterialName = this.material.name();
    return [
      <Buttons.Primary data-test-id="button-ok" onclick={() => {
        this.close();
        if (_.isEmpty(this.material.attributes().materialName())) {
          this.material.attributes().name(this.material.materialUrl());
        } else {
          this.material.attributes().name(this.material.attributes().materialName());
        }
        this.onSuccessfulUpdate(originalMaterialName, this.material);
      }}>Update</Buttons.Primary>
    ];
  }
}