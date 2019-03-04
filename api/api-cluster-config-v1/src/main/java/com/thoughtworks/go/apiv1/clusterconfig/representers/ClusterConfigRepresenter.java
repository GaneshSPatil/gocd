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

package com.thoughtworks.go.apiv1.clusterconfig.representers;

import com.thoughtworks.go.api.base.OutputWriter;
import com.thoughtworks.go.api.representers.ConfigurationPropertyRepresenter;
import com.thoughtworks.go.api.representers.ErrorGetter;
import com.thoughtworks.go.api.representers.JsonReader;
import com.thoughtworks.go.config.elastic.ClusterConfig;
import com.thoughtworks.go.spark.Routes;

import java.util.Collections;
import java.util.Map;

public class ClusterConfigRepresenter {
    public static void toJSON(OutputWriter outputWriter, ClusterConfig clusterConfig) {
        outputWriter
                .addLinks(linksWriter -> linksWriter
                        .addLink("self", Routes.ClusterConfigAPI.id(clusterConfig.getId()))
                        .addAbsoluteLink("doc", Routes.ClusterConfigAPI.DOC)
                        .addLink("find", Routes.ClusterConfigAPI.find()))
                .add("id", clusterConfig.getId())
                .add("plugin_id", clusterConfig.getPluginId())
                .addChildList("properties", listWriter ->
                        clusterConfig.forEach(property -> listWriter.addChild(propertyWriter -> ConfigurationPropertyRepresenter.toJSON(propertyWriter, property))));

        if (clusterConfig.hasErrors()) {
            Map<String, String> fieldMapping = Collections.singletonMap("pluginId", "plugin_id");
            outputWriter.addChild("errors", errorWriter -> new ErrorGetter(fieldMapping).toJSON(errorWriter, clusterConfig));
        }
    }

    public static ClusterConfig fromJSON(JsonReader jsonReader) {
        ClusterConfig clusterConfig = new ClusterConfig(
                jsonReader.getString("id"),
                jsonReader.getString("plugin_id"));
        clusterConfig.addConfigurations(ConfigurationPropertyRepresenter.fromJSONArray(jsonReader, "properties"));

        return clusterConfig;
    }
}
