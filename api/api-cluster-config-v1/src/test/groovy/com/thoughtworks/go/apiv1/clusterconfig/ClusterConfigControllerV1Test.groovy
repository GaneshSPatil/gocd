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

package com.thoughtworks.go.apiv1.clusterconfig

import com.thoughtworks.go.api.SecurityTestTrait
import com.thoughtworks.go.api.spring.ApiAuthenticationHelper
import com.thoughtworks.go.apiv1.clusterconfig.representers.ClusterConfigRepresenter
import com.thoughtworks.go.apiv1.clusterconfig.representers.ClusterConfigsRepresenter
import com.thoughtworks.go.config.elastic.ClusterConfig
import com.thoughtworks.go.config.elastic.ClusterConfigs
import com.thoughtworks.go.server.service.ClusterConfigService
import com.thoughtworks.go.spark.AdminUserSecurity
import com.thoughtworks.go.spark.ControllerTrait
import com.thoughtworks.go.spark.SecurityServiceTrait
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock

import static org.mockito.Mockito.when
import static org.mockito.MockitoAnnotations.initMocks

class ClusterConfigControllerV1Test implements SecurityServiceTrait, ControllerTrait<ClusterConfigControllerV1> {
  @Mock
  ClusterConfigService clusterConfigService;

  @BeforeEach
  void setUp() {
    initMocks(this)
  }

  @Override
  ClusterConfigControllerV1 createControllerInstance() {
    new ClusterConfigControllerV1(new ApiAuthenticationHelper(securityService, goConfigService), clusterConfigService)
  }

  @Nested
  class Index {
    @Nested
    class Security implements SecurityTestTrait, AdminUserSecurity {

      @Override
      String getControllerMethodUnderTest() {
        return "index"
      }

      @Override
      void makeHttpCall() {
        getWithApiHeader(controller.controllerBasePath())
      }
    }

    @Nested
    class AsAdminUser {
      def clusterConfig

      @BeforeEach
      void setUp() {
        enableSecurity()
        loginAsAdmin()

        clusterConfig = new ClusterConfig("docker", "cd.go.docker")

        when(clusterConfigService.getPluginProfiles()).thenReturn(new ClusterConfigs(clusterConfig))
      }

      @Test
      void 'should render all clusters'() {
        getWithApiHeader(controller.controllerBasePath())

        assertThatResponse()
          .isOk()
          .hasContentType(controller.mimeType)
          .hasBodyWithJsonObject(new ClusterConfigs(clusterConfig), ClusterConfigsRepresenter.class)
      }
    }
  }

  @Nested
  class GetClusterConfig {
    @Nested
    class Security implements SecurityTestTrait, AdminUserSecurity {

      @Override
      String getControllerMethodUnderTest() {
        return "getClusterConfig"
      }

      @Override
      void makeHttpCall() {
        getWithApiHeader(controller.controllerPath("/docker"))
      }
    }

    @Nested
    class AsAdminUser {
      def clusterConfig

      @BeforeEach
      void setUp() {
        enableSecurity()
        loginAsAdmin()

        clusterConfig = new ClusterConfig("docker", "cd.go.docker")

        when(clusterConfigService.getPluginProfiles()).thenReturn(new ClusterConfigs(clusterConfig))
      }

      @Test
      void 'should render cluster config'() {
        getWithApiHeader(controller.controllerPath("/docker"))

        assertThatResponse()
          .isOk()
          .hasContentType(controller.mimeType)
          .hasBodyWithJsonObject(clusterConfig, ClusterConfigRepresenter.class)
      }

      @Test
      void 'should render not found exception for non existent cluster config'() {
        getWithApiHeader(controller.controllerPath("/test"))

        assertThatResponse()
          .isNotFound()
      }

    }
  }
}
