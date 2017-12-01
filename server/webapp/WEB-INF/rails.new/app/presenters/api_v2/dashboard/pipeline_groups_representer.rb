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
  module Dashboard
    class PipelineGroupsRepresenter < ApiV2::MyBaseRepresenter

      def initialize(options)
        @groups = options[:represented][:pipeline_groups]
        @user = options[:represented][:user]

        @url_builder = options[:represented][:url_builder]
        super(options)
      end

      link :self do
        @url_builder.apiv2_show_dashboard_url
      end

      link :doc do
        'https://api.go.cd/current/#dashboard'
      end

      embed :pipelines do
        pipeline_instance_paramerers = @groups.inject([]) {|r, e| r + e.allPipelines()}.inject([]) {|r, e| r << {pipeline: e, user: @user, url_builder: @url_builder}}
        pipeline_instance_paramerers.map {|opt| PipelineRepresenter.new(embed: {instances: true}, represented: opt)}
      end

      embed :pipeline_groups do
        pipeline_group_paramerers = @groups.inject([]) {
          |r, e| r << {pipeline_group: e, user: @user, url_builder: @url_builder}}
        pipeline_group_paramerers.map {|opt| PipelineGroupRepresenter.new(opt)}
      end

    end
  end
end
