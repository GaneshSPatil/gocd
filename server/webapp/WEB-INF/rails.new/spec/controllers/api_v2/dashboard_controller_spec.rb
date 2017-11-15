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

require 'rails_helper'

describe ApiV2::DashboardController do
  include GoDashboardPipelineMother

  before do
    @user = Username.new(CaseInsensitiveString.new("foo"))

    allow(controller).to receive(:current_user).and_return(@user)
    allow(controller).to receive(:populate_config_validity)

    @go_dashboard_service = stub_service(:go_dashboard_service)
    @pipeline_selections_service = stub_service(:pipeline_selections_service)
  end

  describe 'dashboard' do

    # Ignoring tests around fetching pipelines delta for now, should consider these scenarios when the delta functionality is implemented
    # before(:each) do
    #   clock = double('Clock')
    #   clock.stub(:currentTimeMillis).and_return(11111)
    #   @timestamp_provider = com.thoughtworks.go.server.dashboard.TimeStampBasedCounter.new(clock)
    # end

    it 'should get dashboard json' do
      pipeline_selections = PipelineSelections::ALL
      pipeline_group = GoDashboardPipelineGroup.new('group1', Permissions.new(Everyone.INSTANCE, Everyone.INSTANCE, Everyone.INSTANCE, Everyone.INSTANCE))
      pipeline_group.addPipeline(dashboard_pipeline('pipeline1'))
      pipeline_group.addPipeline(dashboard_pipeline('pipeline2'))

      allow(controller).to receive(:current_user).and_return(@user = Username.new(CaseInsensitiveString.new(SecureRandom.hex)))
      @pipeline_selections_service.should_receive(:getSelectedPipelines).with(anything, anything).and_return(pipeline_selections)
      @go_dashboard_service.should_receive(:allPipelineGroupsForDashboard).with(pipeline_selections, @user).and_return([pipeline_group])

      get_with_api_header :dashboard

      expect(response).to be_ok
      expect(actual_response).to eq(expected_response({pipeline_groups: [pipeline_group], user: @user}, ApiV2::Dashboard::PipelineGroupsRepresenter))
    end


    it 'should get empty json when dashboard is empty' do
      no_pipeline_groups = []
      pipeline_selections = PipelineSelections::ALL

      allow(controller).to receive(:current_user).and_return(@user = Username.new(CaseInsensitiveString.new(SecureRandom.hex)))
      @pipeline_selections_service.should_receive(:getSelectedPipelines).with(anything, anything).and_return(pipeline_selections)
      @go_dashboard_service.should_receive(:allPipelineGroupsForDashboard).with(pipeline_selections, @user).and_return(no_pipeline_groups)

      get_with_api_header :dashboard

      expect(response).to be_ok
      expect(actual_response).to eq(expected_response({pipeline_groups: no_pipeline_groups, user: @user}, ApiV2::Dashboard::PipelineGroupsRepresenter))
    end

    # Ignoring tests around fetching pipelines delta for now, should consider these scenarios when the delta functionality is implemented
    xit 'should accept only number for If-Go-Dashboard-Modified-Since header' do
      controller.request.env['HTTP_IF_GO_DASHBOARD_MODIFIED_SINCE'] = "Garbage"
      get_with_api_header :dashboard

      expect(response.status).to eq(412)
      expect(actual_response).to eq({message: "Please provide a numeric value for header 'If-Go-Dashboard-Modified-Since'"})
    end

    xit 'should return only modified pipelines based on If-Go-Dashboard-Modified-Since' do
      pipeline1 = dashboard_pipeline("pipeline1", "group1", Permissions.new(Everyone.INSTANCE, Everyone.INSTANCE, Everyone.INSTANCE, Everyone.INSTANCE), 3000)
      pipeline2 = dashboard_pipeline("pipeline2", "group1", Permissions.new(Everyone.INSTANCE, Everyone.INSTANCE, Everyone.INSTANCE, Everyone.INSTANCE), 1000)
      all_pipelines = [pipeline1, pipeline2]
      go_dashboard_pipelines = GoDashboardPipelines.new(all_pipelines, @timestamp_provider)
      @go_dashboard_service.should_receive(:allPipelinesForDashboard).and_return(go_dashboard_pipelines)
      @pipeline_selections_service.should_receive(:getSelectedPipelines).with(anything, anything).and_return(PipelineSelections::ALL)

      controller.request.env['HTTP_IF_GO_DASHBOARD_MODIFIED_SINCE'] = "2000"
      get_with_api_header :dashboard
      expect(response.status).to be(206)
      expect(actual_response).to eq(expected_response([pipeline1], ApiV2::Dashboard::PipelineGroupsRepresenter))
      expect(response.headers['Go-Dashboard-Last-Modified']).to eq(go_dashboard_pipelines.lastUpdatedTimeStamp().to_s)
    end

    xit "should not output any pipelines if no new changes have happened since Go-Dashboard-Last-Modified" do
      pipeline1 = dashboard_pipeline("pipeline1", "group1", Permissions.new(Everyone.INSTANCE, Everyone.INSTANCE, Everyone.INSTANCE, Everyone.INSTANCE), 3000)
      pipeline2 = dashboard_pipeline("pipeline2", "group1", Permissions.new(Everyone.INSTANCE, Everyone.INSTANCE, Everyone.INSTANCE, Everyone.INSTANCE), 1000)
      all_pipelines = [pipeline1, pipeline2]
      go_dashboard_pipelines = GoDashboardPipelines.new(all_pipelines, @timestamp_provider)
      @go_dashboard_service.should_receive(:allPipelinesForDashboard).and_return(go_dashboard_pipelines)

      controller.request.env['HTTP_IF_GO_DASHBOARD_MODIFIED_SINCE'] = go_dashboard_pipelines.lastUpdatedTimeStamp()
      get_with_api_header :dashboard
      expect(response.status).to eq(304)
      expect(actual_response).to eq({})
      expect(response.headers['Go-Dashboard-Last-Modified']).to eq(go_dashboard_pipelines.lastUpdatedTimeStamp().to_s)
    end
  end
end
