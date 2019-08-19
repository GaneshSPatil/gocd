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
import m from "mithril";
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
    let materialAttributes = this.material.attributes();
    if (_.isUndefined(materialAttributes) || _.isEmpty(materialAttributes.name())) {
      return "Add material";
    }

    let materialName = materialAttributes.name();
    return _.isUndefined(materialName) ? "" : materialName;
  }

  buttons(): m.ChildArray {
    return [
      <Buttons.Primary data-test-id="button-ok" onclick={() => {
        this.close();
        let materialAttributes = this.material.attributes();
        if (!_.isUndefined(materialAttributes)) {
          if (_.isEmpty(materialAttributes.materialName())) {
            materialAttributes.name(this.material.materialUrl());
          } else {
            materialAttributes.name(materialAttributes.materialName());
          }
        }
        this.onSuccessfulAdd(this.material);
      }}>Add</Buttons.Primary>
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
        let materialAttributes = this.material.attributes();
        this.close();
        if (!_.isUndefined(materialAttributes)) {
          if (_.isEmpty(materialAttributes.materialName())) {
            materialAttributes.name(this.material.materialUrl());
          } else {
            materialAttributes.name(materialAttributes.materialName());
          }
        }
        this.onSuccessfulUpdate(originalMaterialName, this.material);
      }}>Update</Buttons.Primary>
    ];
  }
}