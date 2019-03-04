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

package com.thoughtworks.go.apiv1.clusterconfig;

import com.thoughtworks.go.api.ApiController;
import com.thoughtworks.go.api.ApiVersion;
import com.thoughtworks.go.api.CrudController;
import com.thoughtworks.go.api.base.OutputWriter;
import com.thoughtworks.go.api.representers.JsonReader;
import com.thoughtworks.go.api.spring.ApiAuthenticationHelper;
import com.thoughtworks.go.api.util.GsonTransformer;
import com.thoughtworks.go.apiv1.clusterconfig.representers.ClusterConfigRepresenter;
import com.thoughtworks.go.apiv1.clusterconfig.representers.ClusterConfigsRepresenter;
import com.thoughtworks.go.config.PluginProfiles;
import com.thoughtworks.go.config.elastic.ClusterConfig;
import com.thoughtworks.go.config.exceptions.RecordNotFoundException;
import com.thoughtworks.go.server.service.ClusterConfigService;
import com.thoughtworks.go.server.service.EntityHashingService;
import com.thoughtworks.go.spark.Routes;
import com.thoughtworks.go.spark.spring.SparkSpringController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.function.Consumer;

import static spark.Spark.*;

@Component
public class ClusterConfigControllerV1 extends ApiController implements SparkSpringController, CrudController<ClusterConfig> {

    private final ApiAuthenticationHelper apiAuthenticationHelper;
    private final ClusterConfigService clusterConfigService;
    private EntityHashingService entityHashingService;

    @Autowired
    public ClusterConfigControllerV1(ApiAuthenticationHelper apiAuthenticationHelper, ClusterConfigService clusterConfigService, EntityHashingService entityHashingService) {
        super(ApiVersion.v1);
        this.apiAuthenticationHelper = apiAuthenticationHelper;
        this.clusterConfigService = clusterConfigService;
        this.entityHashingService = entityHashingService;
    }

    @Override
    public String controllerBasePath() {
        return Routes.ClusterConfigAPI.BASE;
    }

    @Override
    public void setupRoutes() {
        path(controllerBasePath(), () -> {
            before("", mimeType, this::setContentType);
            before("/*", mimeType, this::setContentType);

            before("", this.mimeType, this.apiAuthenticationHelper::checkAdminUserAnd403);
            before("/*", this.mimeType, this.apiAuthenticationHelper::checkAdminUserAnd403);

            get("", mimeType, this::index);
            get(Routes.ClusterConfigAPI.ID, mimeType, this::getClusterConfig);

            exception(RecordNotFoundException.class, this::notFound);
        });
    }

    public String index(Request request, Response response) throws IOException {
        final PluginProfiles<ClusterConfig> clusters = clusterConfigService.getPluginProfiles();
        return writerForTopLevelObject(request, response, outputWriter -> ClusterConfigsRepresenter.toJSON(outputWriter, clusters));
    }

    public String getClusterConfig(Request request, Response response) throws IOException {
        final ClusterConfig clusterConfig = fetchEntityFromConfig(request.params("cluster_id"));
        String etag = etagFor(clusterConfig);

        if (fresh(request, etag)) {
            return notModified(response);
        }

        setEtagHeader(response, etag);
        return writerForTopLevelObject(request, response, jsonWriter(clusterConfig));
    }

    @Override
    public String etagFor(ClusterConfig entityFromServer) {
        return entityHashingService.md5ForEntity(entityFromServer);
    }

    @Override
    public ClusterConfig doFetchEntityFromConfig(String name) {
        return clusterConfigService.getPluginProfiles().find(name);
    }

    @Override
    public ClusterConfig buildEntityFromRequestBody(Request req) {
        JsonReader jsonReader = GsonTransformer.getInstance().jsonReaderFrom(req.body());
        return ClusterConfigRepresenter.fromJSON(jsonReader);
    }

    @Override
    public Consumer<OutputWriter> jsonWriter(ClusterConfig clusterConfig) {
        return outputWriter -> ClusterConfigRepresenter.toJSON(outputWriter, clusterConfig);
    }
}
