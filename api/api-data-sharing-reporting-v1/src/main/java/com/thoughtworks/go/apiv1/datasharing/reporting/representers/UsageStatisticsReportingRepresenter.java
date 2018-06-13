/*
 * Copyright 2018 ThoughtWorks, Inc.
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

package com.thoughtworks.go.apiv1.datasharing.reporting.representers;

import com.thoughtworks.go.api.base.OutputWriter;
import com.thoughtworks.go.api.representers.ErrorGetter;
import com.thoughtworks.go.api.representers.JsonReader;
import com.thoughtworks.go.domain.UsageStatisticsReporting;
import com.thoughtworks.go.spark.Routes.DataSharing;

import java.util.Date;
import java.util.HashMap;

public class UsageStatisticsReportingRepresenter {
    public static void toJSON(OutputWriter outputWriter, UsageStatisticsReporting usageStatisticsReporting) {
        outputWriter
                .addLinks(linksWriter -> linksWriter.addLink("self", DataSharing.REPORTING_PATH))
                .addChild("_embedded", childWriter -> {
                    childWriter
                            .add("server_id", usageStatisticsReporting.getServerId())
                            .add("last_reported_at", usageStatisticsReporting.lastReportedAt().getTime());
                    if (!usageStatisticsReporting.errors().isEmpty()) {
                        outputWriter.addChild("errors", errorWriter -> {
                            HashMap<String, String> errorMapping = new HashMap<>();
                            errorMapping.put("lastReportedAt", "last_reported_at");
                            new ErrorGetter(errorMapping).toJSON(errorWriter, usageStatisticsReporting);
                        });
                    }
                });
    }

    public static UsageStatisticsReporting fromJSON(JsonReader jsonReader) {
        UsageStatisticsReporting usageStatisticsReporting = new UsageStatisticsReporting();
        Long lastReportedAt = jsonReader.optLong("last_reported_at").orElse(0l);
        usageStatisticsReporting.setLastReportedAt(new Date(lastReportedAt));
        return usageStatisticsReporting;
    }
}
