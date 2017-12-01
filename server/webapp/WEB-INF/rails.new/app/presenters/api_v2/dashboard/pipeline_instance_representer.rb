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
    class PipelineInstanceRepresenter < ApiV2::MyBaseRepresenter
      def initialize(options)
        @pipeline_instance = options[:represented][:pipeline_instance]
        @url_builder = options[:represented][:url_builder]

        super(options)
      end

      link :self do |opts|
        @url_builder.pipeline_instance_by_counter_api_url(@pipeline_instance.getName(), @pipeline_instance.getCounter())
      end

      link :doc do
        'https://api.go.cd/current/#get-pipeline-instance'
      end

      link :history_url do |opts|
        @url_builder.pipeline_history_url(@pipeline_instance.getName())
      end

      link :vsm_url do |opts|
        @url_builder.vsm_show_url(@pipeline_instance.getName(), :pipeline_counter => @pipeline_instance.getCounter())

      end
      link :compare_url do |opts|
        @url_builder.compare_pipelines_url(:from_counter => @pipeline_instance.getCounter()-1, :to_counter => @pipeline_instance.getCounter(), :pipeline_name => @pipeline_instance.getName())
      end

      link :build_cause_url do |opts|
        @url_builder.build_cause_url(:pipeline_counter => @pipeline_instance.getCounter(), :pipeline_name => @pipeline_instance.getName())
      end

      property :label do
        @pipeline_instance.getLabel
      end

      property :scheduled_at do
        @pipeline_instance.getScheduledDate
      end

      property :triggered_by do
        @pipeline_instance.getApprovedBy
      end

      embed :stages do
        stages_params = @pipeline_instance.getStageHistory().collect do |stage|
          [stage, {
            pipeline_name:    @pipeline_instance.getName(),
            pipeline_counter: @pipeline_instance.getCounter(),
            render_previous:  true
          }
          ]
        end

        stages_params.map {|opt| StageRepresenter.new({stage_instance: opt, url_builder: @url_builder})}
      end
    end
  end
end
