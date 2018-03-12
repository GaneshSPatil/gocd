const m           = require('mithril');
const Stream      = require('mithril/stream');
const sparkRoutes = require('helpers/spark_routes');
const AjaxHelper  = require('helpers/ajax_helper');

class Material {
  constructor({type, name, fingerprint, folder, revision, pipelineName}) {
    this.type             = type;
    this.name             = name;
    this.fingerprint      = fingerprint;
    this.destination      = folder;
    this.revision         = revision;
    this.pipelineName     = pipelineName;
    this.selection        = Stream();
    this.searchText       = Stream('');
    this.searchInProgress = Stream(false);
    this.searchResults    = Stream([]);
  }

  selectRevision = (revision) => {
    this.updateSearchText(revision);
    this.selection(revision);
  };

  updateSearchText = (newText) => {
    this.selection(undefined);
    this.searchText(newText);
    this.debouncedSearch();
  };

  isRevisionSelected = () => {
    return !_.isEmpty(this.selection());
  };

  debouncedSearch = _.debounce(() => {
    this.performSearch();
  }, 200);

  performSearch = () => {
    this.searchInProgress(true);
    AjaxHelper.GET({
      url:        sparkRoutes.pipelineMaterialSearchPath(this.pipelineName, this.fingerprint, this.searchText),
      apiVersion: 'v1',
    }).then((result) => {
      this.searchResults(result);
      if ((result.length === 1) && (this.searchText() === result[0].revision)) {
        this.selection(this.searchText());
      }
    }).always(() => {
      this.searchInProgress(false);
      m.redraw();
    });
  }

}

module.exports = Material;
