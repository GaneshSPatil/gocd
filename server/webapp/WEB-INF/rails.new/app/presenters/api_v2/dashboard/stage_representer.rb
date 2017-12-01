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
    class StageRepresenter < ApiV2::MyBaseRepresenter
      def initialize(options)
        @stage_instance = options[:stage_instance].first
        @pipeline_counter = options[:stage_instance].last.delete(:pipeline_counter)
        @pipeline_name = options[:stage_instance].last.delete(:pipeline_name)
        @render_previous = options[:stage_instance].last.delete(:render_previous)

        @url_builder = options[:url_builder]

        super(options)
      end

      link :self do
        @url_builder.apiv1_stage_instance_by_counter_api_url(pipeline_name: @pipeline_name, pipeline_counter: @pipeline_counter,
                                                             stage_name: @stage_instance.getName, stage_counter: @stage_instance.getCounter)
      end

      link :doc do
        'https://api.go.cd/current/#get-stage-instance'
      end

      property :name do
        @stage_instance.getName
      end

      property :status do
        @stage_instance.getState()
      end

      property :previous_stage do
        if @stage_instance.hasPreviousStage
          stage_presenter_opts = {
            pipeline_name: @pipeline_name,
            pipeline_counter: @stage_instance.getPreviousStage().getIdentifier().getPipelineCounter()
          }

          StageRepresenter.new({stage_instance: [@stage_instance.getPreviousStage(), stage_presenter_opts], url_builder: @url_builder})
        end
      end

      property :approved_by do
        @stage_instance.getApprovedBy
      end

      property :scheduled_at do
        @stage_instance.getScheduledDate()
      end
    end
  end
end
