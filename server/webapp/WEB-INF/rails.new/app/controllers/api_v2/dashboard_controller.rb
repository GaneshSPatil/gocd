##########################################################################
# Copyright 2017 ThoughtWorks, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
##########################################################################
module ApiV2
  class DashboardController < ApiV2::BaseController

    include ApplicationHelper

    def dashboard
      name_of_current_user = CaseInsensitiveString.str(current_user.getUsername())
      all_pipelines_for_dashboard = go_dashboard_service.allPipelinesForDashboard()
      if stale?(etag: all_pipelines_for_dashboard.lastUpdatedTimeStamp())
        pipelines_across_groups = all_pipelines_for_dashboard.orderedEntries()
        pipeline_selections = pipeline_selections_service.getSelectedPipelines(cookies[:selected_pipelines], current_user_entity_id)
        pipelines_viewable_by_user = pipelines_across_groups.select do |pipeline|
          does_user_have_view_permissions = pipeline.canBeViewedBy(name_of_current_user)
          has_user_selected_the_pipeline_for_display = pipeline_selections.includesPipeline(pipeline.name())
          does_user_have_view_permissions && has_user_selected_the_pipeline_for_display
        end
        presenters = Dashboard::PipelineGroupsRepresenter.new(pipelines_viewable_by_user)
        presenters_to_hash = presenters.to_hash(url_builder: self)

        render DEFAULT_FORMAT => presenters_to_hash, status: status
      end
    end
  end
end
