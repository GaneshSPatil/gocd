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

import {MithrilViewComponent} from "jsx/mithril-component";
import * as m from "mithril";
import {GitMaterialAttributes, Material} from "models/materials/types";
import {NameableSet} from "models/pipeline_configs/nameable_set";
import {Secondary} from "views/components/buttons";
import {CollapsiblePanel} from "views/components/collapsible_panel";
import {IconGroup} from "views/components/icons/index";
import * as Icons from "views/components/icons/index";
import * as styles from "views/pages/pipeline_configs/index.scss";
import * as _ from "lodash";
import {AddMaterialModal, UpdateMaterialModal} from "views/pages/pipeline_configs/materials/form";
import {ConceptDiagram} from "views/pages/pipelines/concept_diagram";
import * as tableStyles from "views/components/table/index.scss";

interface Attrs {
  materials: NameableSet<Material>;
  onMaterialAdd: any;
}

const materialImg = require("../../../../../app/assets/images/concept_diagrams/concept_material.svg");

export class MaterialsWidget extends MithrilViewComponent<Attrs> {
  view(vnode: m.Vnode<Attrs>) {
    let materials = [];
    let expanded  = true;
    if (_.isEmpty(vnode.attrs) || _.isEmpty(vnode.attrs.materials) || vnode.attrs.materials.length == 0) {
      expanded  = true;
      materials =
        [<ConceptDiagram image={materialImg}/>,
          <div style="align-self: center;width: 350px">
            A <strong>material</strong> triggers your pipeline to run.
            Typically this is a <strong>source repository</strong> or an <strong>upstream pipeline</strong>.
          </div>];
    } else {
      materials = [<table className={tableStyles.table} data-test-id={"materials-index-table"}>
        <thead data-test-id="table-header">
        <tr data-test-id="table-header-row">
          <th>Material Name</th>
          <th>Type</th>
          <th>Url</th>
          <th></th>
        </tr>
        </thead>
        {this.materialsData(vnode)}
      </table>];
    }

    return <CollapsiblePanel header={<div class={styles.headerText}>Materials</div>}
                             actions={<Secondary onclick={(e: MouseEvent) => this.addMaterialModal(e, vnode)}>Add
                               material</Secondary>}
                             expanded={expanded}>
      <div class={styles.collapsibleContent}>
        {materials}
      </div>
    </CollapsiblePanel>;
  }

  materialsData(vnode: m.Vnode<Attrs>): any {
    let tbodyContent = Array<any>();
    vnode.attrs.materials.forEach(material => {
      let materialContent = Array<any>();
      materialContent.push(<td>{material.name()}</td>);
      materialContent.push(<td>{this.getTypeForDisplay(material.type())}</td>);
      materialContent.push(<td>{material.materialUrl()}</td>);
      materialContent.push(<td class={styles.textAlignRight}>
        <IconGroup>
          <Icons.Edit onclick={() => this.editMaterialModal(material, vnode)}/>
          <Icons.Delete/>
        </IconGroup>
      </td>);
      tbodyContent.push(<tr>{materialContent}</tr>)
    });
    return <tbody>{tbodyContent}</tbody>;
  }

  getTypeForDisplay(type: string) {
    switch (type) {
      case "git":
        return "Git";
      case "hg":
        return "Mercurial";
      case "svn":
        return "Subversion";
      case "p4":
        return "Perforce";
      case "tfs":
        return "Team Foundation Server";
      case "dependency":
        return "Another Pipeline";
    }
  }

  addMaterialModal(e: MouseEvent, vnode: m.Vnode<Attrs>) {
    new AddMaterialModal(new Material("git", new GitMaterialAttributes()), vnode.attrs.onMaterialAdd).render();
    e.stopPropagation();
  }

  editMaterialModal(material: Material, vnode: m.Vnode<Attrs>) {
    new UpdateMaterialModal(material, vnode.attrs.onMaterialAdd).render();
  }
}
