# -*- encoding: utf-8 -*-
# stub: halogen 0.0.5 ruby lib

Gem::Specification.new do |s|
  s.name = "halogen"
  s.version = "0.0.5"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib"]
  s.authors = ["Heather Rivers"]
  s.date = "2017-04-27"
  s.description = "Provides a framework-agnostic interface for generating HAL+JSON representations of resources"
  s.email = ["heather@modeanalytics.com"]
  s.homepage = "https://github.com/mode/halogen"
  s.licenses = ["MIT"]
  s.required_ruby_version = Gem::Requirement.new("~> 2.0")
  s.rubygems_version = "2.4.8"
  s.summary = "HAL+JSON generator"

  s.installed_by_version = "2.4.8" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<json>, ["~> 1.8"])
      s.add_development_dependency(%q<bundler>, ["~> 1.7"])
      s.add_development_dependency(%q<rake>, ["~> 10.0"])
      s.add_development_dependency(%q<rspec>, ["~> 3.2"])
      s.add_development_dependency(%q<simplecov>, ["~> 0.9"])
      s.add_development_dependency(%q<yard>, ["~> 0.8"])
    else
      s.add_dependency(%q<json>, ["~> 1.8"])
      s.add_dependency(%q<bundler>, ["~> 1.7"])
      s.add_dependency(%q<rake>, ["~> 10.0"])
      s.add_dependency(%q<rspec>, ["~> 3.2"])
      s.add_dependency(%q<simplecov>, ["~> 0.9"])
      s.add_dependency(%q<yard>, ["~> 0.8"])
    end
  else
    s.add_dependency(%q<json>, ["~> 1.8"])
    s.add_dependency(%q<bundler>, ["~> 1.7"])
    s.add_dependency(%q<rake>, ["~> 10.0"])
    s.add_dependency(%q<rspec>, ["~> 3.2"])
    s.add_dependency(%q<simplecov>, ["~> 0.9"])
    s.add_dependency(%q<yard>, ["~> 0.8"])
  end
end
