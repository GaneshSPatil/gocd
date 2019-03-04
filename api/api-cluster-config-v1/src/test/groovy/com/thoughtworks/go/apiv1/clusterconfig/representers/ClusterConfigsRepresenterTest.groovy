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

package com.thoughtworks.go.apiv1.clusterconfig.representers

import com.thoughtworks.go.config.elastic.ClusterConfig
import com.thoughtworks.go.config.elastic.ClusterConfigs
import com.thoughtworks.go.security.GoCipher
import org.junit.jupiter.api.Test

import static com.thoughtworks.go.CurrentGoCDVersion.apiDocsUrl
import static com.thoughtworks.go.api.base.JsonUtils.toObjectString
import static com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother.create
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson

class ClusterConfigsRepresenterTest {
  def expectedJson = [
    _links   : [
      self: [href: 'http://test.host/go/api/admin/elastic/clusters'],
      doc : [href: apiDocsUrl('#cluster-config')],
    ],
    _embedded: [
      clusters: [
        [
          _links      : [
            self: [href: 'http://test.host/go/api/admin/elastic/clusters/docker'],
            doc : [href: apiDocsUrl('#cluster-config')],
            find: [href: 'http://test.host/go/api/admin/elastic/clusters/:cluster_id'],
          ],
          id          : 'docker',
          plugin_id   : 'cd.go.docker',
          "properties": [
            [
              "key"  : "docker-uri",
              "value": "unix:///var/run/docker"
            ]
          ],
        ],
        [
          _links      : [
            self: [href: 'http://test.host/go/api/admin/elastic/clusters/ecs'],
            doc : [href: apiDocsUrl('#cluster-config')],
            find: [href: 'http://test.host/go/api/admin/elastic/clusters/:cluster_id'],
          ],
          id          : 'ecs',
          plugin_id   : 'cd.go.ecs',
          "properties": [
            [
              "key"            : "ACCESS_KEY",
              "encrypted_value": new GoCipher().encrypt('encrypted-key')
            ]
          ],
        ]
      ]
    ]
  ]

  @Test
  void 'should serialize cluster configurations to json'() {
    def clusterConfigs = new ClusterConfigs(
      new ClusterConfig("docker", "cd.go.docker", create("docker-uri", false, "unix:///var/run/docker")),
      new ClusterConfig("ecs", "cd.go.ecs", create("ACCESS_KEY", true, "encrypted-key"))
    )

    def actualJson = toObjectString({ ClusterConfigsRepresenter.toJSON(it, clusterConfigs) })

    assertThatJson(actualJson).isEqualTo(expectedJson)
  }
}
