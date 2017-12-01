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
    class PipelineRepresenter < ApiV2::MyBaseRepresenter
      def initialize(options)
        @options = options

        @pipeline = options[:represented][:pipeline]
        @user = options[:represented][:user]
        @url_builder = options[:represented][:url_builder]

        super(options)
      end

      link :self do
        @url_builder.pipeline_history_url(@pipeline.name())
      end

      link :doc do
        'https://api.go.cd/current/#pipelines'
      end

      link :settings_path do
        @url_builder.pipeline_edit_url(@pipeline.name(), current_tab: :'general')
      end

      link :trigger do
        @url_builder.api_pipeline_action_url(@pipeline.name(), action: :'schedule')
      end

      link :trigger_with_options do
        @url_builder.api_pipeline_action_url(@pipeline.name(), action: :'schedule')
      end

      link :pause do
        @url_builder.pause_pipeline_url(@pipeline.name())
      end

      link :unpause do
        @url_builder.unpause_pipeline_url(@pipeline.name())
      end

      property :name do
        @pipeline.name().toString()
      end

      property :last_updated_timestamp do
        @pipeline.getLastUpdatedTimeStamp
      end

      property :locked do
        @pipeline.model().getLatestPipelineInstance().isCurrentlyLocked
      end

      property :pause_info do
        paused_info = @pipeline.model().getPausedInfo()
        {
          paused: paused_info.paused,
          paused_by: paused_info.pauseBy.blank? ? nil : paused_info.pauseBy,
          pause_reason: paused_info.pauseCause.blank? ? nil : paused_info.pauseCause
        }
      end

      property :can_operate do
        @pipeline.isPipelineOperator(@user.getUsername().toString())
      end

      property :can_administer do
        @pipeline.canBeAdministeredBy(@user.getUsername().toString())
      end

      property :can_unlock do
        @pipeline.canBeOperatedBy(@user.getUsername().toString())
      end

      property :can_pause do
        @pipeline.canBeOperatedBy(@user.getUsername().toString())
      end

      embed :instances do
        instance_parameters = @pipeline.model().getActivePipelineInstances().select do |pipeline_instance_model|
          !pipeline_instance_model.instance_of?(com.thoughtworks.go.presentation.pipelinehistory.EmptyPipelineInstanceModel)
        end
        instance_parameters.map {|opt| PipelineInstanceRepresenter.new(embed: {stages: true}, represented: {pipeline_instance: opt, url_builder: @url_builder})}
      end
    end
  end
end
