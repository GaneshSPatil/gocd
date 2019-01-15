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

package com.thoughtworks.go.config;

/*  @description
 * Keeps track all config repo parsing states
 */


import com.thoughtworks.go.config.remote.PartialConfig;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.server.service.ConfigRepoService;
import com.thoughtworks.go.serverhealth.HealthStateScope;
import com.thoughtworks.go.serverhealth.ServerHealthService;
import com.thoughtworks.go.serverhealth.ServerHealthState;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigReposMaterialParseResultManager {
    private Map<String, PartialConfigParseResult> fingerprintOfPartialToParseResultMap = new ConcurrentHashMap<>();
    private ServerHealthService serverHealthService;
    private ConfigRepoService configRepoService;

    public ConfigReposMaterialParseResultManager(ServerHealthService serverHealthService, ConfigRepoService configRepoService) {
        this.serverHealthService = serverHealthService;
        this.configRepoService = configRepoService;
    }

    public PartialConfigParseResult get(String fingerprint) {
        PartialConfigParseResult result = fingerprintOfPartialToParseResultMap.get(fingerprint);
        // config repository was never parsed, check if there are any material clone or update related errors
        if (result == null) {
            HealthStateScope healthStateScope = HealthStateScope.forMaterialConfig(configRepoService.findByFingerprint(fingerprint).getMaterialConfig());
            List<ServerHealthState> serverHealthStates = serverHealthService.filterByScope(healthStateScope);
            if (!serverHealthStates.isEmpty()) {
                result = PartialConfigParseResult.parseFailed(null, represent(serverHealthStates.get(0)));
            }
        }

        //config repository was parsed, but does not have merge or clone related errors.
        if (result != null && result.getLastFailure() == null) {
            HealthStateScope healthStateScope = HealthStateScope.forPartialConfigRepo(fingerprint);
            List<ServerHealthState> serverHealthStates = serverHealthService.filterByScope(healthStateScope);
            if (!serverHealthStates.isEmpty()) {
                result.setException(represent(serverHealthStates.get(0)));

                //clear out the good modification, in case good modification is same as of latest parsed modification
                if (result.getLatestParsedModification().equals(result.getGoodModification())) {
                    result.setGoodModification(null);
                    result.setPartialConfig(null);
                }
            }
        }

        return result;
    }

    private Exception represent(ServerHealthState serverHealthState) {
        String message = new StringBuilder()
                .append(serverHealthState.getMessage())
                .append("\n\t")
                .append(serverHealthState.getDescription())
                .toString();

        return new Exception(message);
    }

    public Set<String> allFingerprints() {
        return fingerprintOfPartialToParseResultMap.keySet();
    }

    public void remove(String fingerprint) {
        fingerprintOfPartialToParseResultMap.remove(fingerprint);
    }

    public PartialConfigParseResult parseFailed(String fingerprint, Modification modification, Exception exception) {
        PartialConfigParseResult existingResult = get(fingerprint);
        if (existingResult == null) { //if no result exists in the map, create a new one
            return fingerprintOfPartialToParseResultMap.put(fingerprint, PartialConfigParseResult.parseFailed(modification, exception));
        } else {
            PartialConfigParseResult newResult = PartialConfigParseResult.parseFailed(modification, exception);
            newResult.setGoodModification(existingResult.getGoodModification());
            newResult.setPartialConfig(existingResult.lastGoodPartialConfig());
            return fingerprintOfPartialToParseResultMap.put(fingerprint, newResult);
        }
    }

    public PartialConfigParseResult parseSuccess(String fingerprint, Modification modification, PartialConfig newPart) {
        //if no result exists in the map, create a new one
        //if already a result exists in the map, override the result, as the latest modification is successful. regardless of the result being successful or failed
        return fingerprintOfPartialToParseResultMap.put(fingerprint, PartialConfigParseResult.parseSuccess(modification, newPart));
    }
}
