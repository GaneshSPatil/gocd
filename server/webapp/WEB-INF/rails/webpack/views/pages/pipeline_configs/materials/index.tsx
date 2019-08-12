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
import {GitMaterialAttributes, Material, Materials} from "models/materials/types";
import {Secondary} from "views/components/buttons";
import {CollapsiblePanel} from "views/components/collapsible_panel";
import * as styles from "views/pages/pipeline_configs/index.scss";
import * as _ from "lodash";
import {MaterialModal} from "views/pages/pipeline_configs/materials/form";
import {ConceptDiagram} from "views/pages/pipelines/concept_diagram";

interface Attrs {
  materials?: Materials;
}

const materialImg = require("../../../../../app/assets/images/concept_diagrams/concept_material.svg");

export class MaterialsWidget extends MithrilViewComponent<Attrs> {
  view(vnode: m.Vnode<Attrs>) {
    let materials = [];
    let expanded  = false;
    if (_.isEmpty(vnode.attrs) || _.isEmpty(vnode.attrs.materials)) {
      expanded  = true;
      materials =
        [<ConceptDiagram image={materialImg}/>,
          <div style="align-self: center;width: 350px">
            A <strong>material</strong> triggers your pipeline to run.
            Typically this is a <strong>source repository</strong> or an <strong>upstream pipeline</strong>.
          </div>];
    } else {
      materials = ["test"];
    }

    return <CollapsiblePanel header={<div class={styles.headerText}>Materials</div>}
                             actions={<Secondary onclick={this.addMaterialModal}>Add material</Secondary>}
                             expanded={expanded}>
      <div class={styles.collapsibleContent}>
        {materials}
      </div>
    </CollapsiblePanel>;
  }

  addMaterialModal(e: MouseEvent) {
    new MaterialModal(new Material("git", new GitMaterialAttributes())).render();
    e.stopPropagation();
  }
}
