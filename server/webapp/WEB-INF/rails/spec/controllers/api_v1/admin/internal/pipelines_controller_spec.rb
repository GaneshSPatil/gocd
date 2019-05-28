#
# Copyright 2019 ThoughtWorks, Inc.
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
#

require 'rails_helper'

describe ApiV1::Admin::Internal::PipelinesController do
  include ApiHeaderSetupForRouting
  include ApiV1::ApiVersionHelper

  before(:each) do
    @pipeline_config_service = double('pipeline_config_service')
    @entity_hashing_service = double('entity_hashing_service')
    allow(controller).to receive('pipeline_config_service').and_return(@pipeline_config_service)
    allow(controller).to receive('entity_hashing_service').and_return(@entity_hashing_service)
  end

  describe "security" do
    describe "index" do
      it 'should allow anyone, with security disabled' do
        disable_security

        expect(controller).to allow_action(:get, :index)
      end

      it 'should disallow non-admin user, with security enabled' do
        enable_security
        login_as_user

        expect(controller).to disallow_action(:get, :index).with(403, 'You are not authorized to perform this action.')
      end

      it 'should allow admin users, with security enabled' do
        login_as_admin

        expect(controller).to allow_action(:get, :index)
      end

      it 'should allow group admin users, with security enabled' do
        login_as_group_admin

        expect(controller).to allow_action(:get, :index)
      end
    end
  end

  describe "action" do
    before :each do
      enable_security
    end

    describe "index" do
      it 'should fetch all the pipelines for the user' do
        login_as_admin
        pipeline_configs = BasicPipelineConfigs.new(PipelineConfigMother.createPipelineConfigWithStages('regression', 'fetch', 'run'))
        pipeline_configs_list = Arrays.asList(pipeline_configs)

        expect(@pipeline_config_service).to receive(:viewableOrOperatableGroupsFor).with(controller.current_user).and_return(pipeline_configs_list)
        expect(@entity_hashing_service).to receive(:md5ForEntity).and_return("md5")

        get_with_api_header :index

        expect(response).to be_ok
        expect(response.headers["ETag"]).not_to include('W/')
        expected_response = expected_response(pipeline_configs_list, ApiV1::Config::PipelineConfigsWithMinimalAttributesRepresenter)
        expect(actual_response).to eq(expected_response)
      end
    end
  end
end
