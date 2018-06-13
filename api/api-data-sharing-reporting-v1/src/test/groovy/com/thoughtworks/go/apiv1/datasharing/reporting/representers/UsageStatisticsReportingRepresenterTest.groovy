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

package com.thoughtworks.go.apiv1.datasharing.reporting.representers

import com.thoughtworks.go.api.util.GsonTransformer
import com.thoughtworks.go.domain.UsageStatisticsReporting
import org.junit.jupiter.api.Test

import static com.thoughtworks.go.api.base.JsonUtils.toObjectString
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import static org.junit.jupiter.api.Assertions.assertEquals

class UsageStatisticsReportingRepresenterTest {

    @Test
    void "should represent usage statistics reporting"() {
        def metricsReporting = new UsageStatisticsReporting("server-id", new java.util.Date())
        def statsSharedAt = new Date()
        metricsReporting.setLastReportedAt(statsSharedAt)

        def actualJson = toObjectString({ UsageStatisticsReportingRepresenter.toJSON(it, metricsReporting) })

        def expectedJson = [
                _links     : [
                    self: [href: 'http://test.host/go/api/internal/data_sharing/reporting']
                ],
                "_embedded": [
                        server_id      : metricsReporting.getServerId(),
                        last_reported_at: metricsReporting.lastReportedAt().getTime()
                ]
        ]

        assertThatJson(actualJson).isEqualTo(expectedJson)
    }
    @Test
    void "should map errors"() {
        def reporting = new UsageStatisticsReporting("server-id", new java.util.Date(0l))
        reporting.validate(null)

        def actualJson = toObjectString({ UsageStatisticsReportingRepresenter.toJSON(it, reporting) })

      def expectedJson = [
        _links     : [
          self: [href: 'http://test.host/go/api/internal/data_sharing/reporting']
        ],
        "_embedded": [
          server_id       : reporting.getServerId(),
          last_reported_at: reporting.lastReportedAt().getTime(),
          errors          : [last_reported_at: ["Invalid time"]]
        ]
      ]

      assertThatJson(actualJson).isEqualTo(expectedJson)
    }

    @Test
    void "should represent usage statistics reporting when last_reported_at is unset"() {
        def metricsReporting = new UsageStatisticsReporting("server-id", new java.util.Date(0l))

        def actualJson = toObjectString({ UsageStatisticsReportingRepresenter.toJSON(it, metricsReporting) })

        def expectedJson = [
                _links     : [
                  self: [href: 'http://test.host/go/api/internal/data_sharing/reporting']
                ],
                "_embedded": [
                        server_id      : metricsReporting.getServerId(),
                        last_reported_at: 0
                ]
        ]

        assertThatJson(actualJson).isEqualTo(expectedJson)
    }

    @Test
    void "should deserialize usage statistics reporting"() {
        def date = new Date()
        def json = [
          last_reported_at: date.getTime()
        ]
        def jsonReader = GsonTransformer.instance.jsonReaderFrom(json)
        UsageStatisticsReporting reporting = UsageStatisticsReportingRepresenter.fromJSON(jsonReader)
        assertEquals(reporting.lastReportedAt().toInstant(), date.toInstant())
    }
}
