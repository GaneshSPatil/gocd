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
    class PipelineGroupRepresenter < ApiV2::MyBaseRepresenter
      def initialize(options)
        @pipeline_group = options[:pipeline_group]
        @user = options[:user]
        @url_builder = options[:url_builder]

        super(options)
      end

      link :self do
        @url_builder.pipeline_group_config_list_api_url
      end

      link :doc do
        'https://api.go.cd/current/#pipeline-groups'
      end

      property :name do
        @pipeline_group.getName()
      end

      property :pipelines do
        @pipeline_group.allPipelineNames()
      end

      property :can_administer do
        @pipeline_group.canBeAdministeredBy(@user.getUsername().toString())
      end
    end
  end
end
