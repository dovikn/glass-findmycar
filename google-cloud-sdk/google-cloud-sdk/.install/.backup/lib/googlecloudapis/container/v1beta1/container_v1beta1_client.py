"""Generated client library for container version v1beta1."""

from googlecloudapis.apitools.base.py import base_api
from googlecloudapis.container.v1beta1 import container_v1beta1_messages as messages


class ContainerV1beta1(base_api.BaseApiClient):
  """Generated client library for service container version v1beta1."""

  MESSAGES_MODULE = messages

  _PACKAGE = u'container'
  _SCOPES = [u'https://www.googleapis.com/auth/cloud-platform']
  _VERSION = u'v1beta1'
  _CLIENT_ID = ''
  _CLIENT_SECRET = ''
  _USER_AGENT = ''
  _CLIENT_CLASS_NAME = u'ContainerV1beta1'
  _URL_VERSION = u'v1beta1'

  def __init__(self, url='', credentials=None,
               get_credentials=True, http=None, model=None,
               log_request=False, log_response=False,
               credentials_args=None, default_global_params=None,
               additional_http_headers=None):
    """Create a new container handle."""
    url = url or u'https://www.googleapis.com/container/v1beta1/'
    super(ContainerV1beta1, self).__init__(
        url, credentials=credentials,
        get_credentials=get_credentials, http=http, model=model,
        log_request=log_request, log_response=log_response,
        credentials_args=credentials_args,
        default_global_params=default_global_params,
        additional_http_headers=additional_http_headers)
    self.projects_clusters = self.ProjectsClustersService(self)
    self.projects_operations = self.ProjectsOperationsService(self)
    self.projects_zones_clusters = self.ProjectsZonesClustersService(self)
    self.projects_zones_operations = self.ProjectsZonesOperationsService(self)
    self.projects_zones = self.ProjectsZonesService(self)
    self.projects = self.ProjectsService(self)

  class ProjectsClustersService(base_api.BaseApiService):
    """Service class for the projects_clusters resource."""

    _NAME = u'projects_clusters'

    def __init__(self, client):
      super(ContainerV1beta1.ProjectsClustersService, self).__init__(client)
      self._method_configs = {
          'List': base_api.ApiMethodInfo(
              http_method=u'GET',
              method_id=u'container.projects.clusters.list',
              ordered_params=[u'projectId'],
              path_params=[u'projectId'],
              query_params=[],
              relative_path=u'projects/{projectId}/clusters',
              request_field='',
              request_type_name=u'ContainerProjectsClustersListRequest',
              response_type_name=u'ListAggregatedClustersResponse',
              supports_download=False,
          ),
          }

      self._upload_configs = {
          }

    def List(self, request, global_params=None):
      """Lists all clusters owned by a project, aggregating across the different compute zones.

      Args:
        request: (ContainerProjectsClustersListRequest) input message
        global_params: (StandardQueryParameters, default: None) global arguments
      Returns:
        (ListAggregatedClustersResponse) The response message.
      """
      config = self.GetMethodConfig('List')
      return self._RunMethod(
          config, request, global_params=global_params)

  class ProjectsOperationsService(base_api.BaseApiService):
    """Service class for the projects_operations resource."""

    _NAME = u'projects_operations'

    def __init__(self, client):
      super(ContainerV1beta1.ProjectsOperationsService, self).__init__(client)
      self._method_configs = {
          'List': base_api.ApiMethodInfo(
              http_method=u'GET',
              method_id=u'container.projects.operations.list',
              ordered_params=[u'projectId'],
              path_params=[u'projectId'],
              query_params=[],
              relative_path=u'projects/{projectId}/operations',
              request_field='',
              request_type_name=u'ContainerProjectsOperationsListRequest',
              response_type_name=u'ListAggregatedOperationsResponse',
              supports_download=False,
          ),
          }

      self._upload_configs = {
          }

    def List(self, request, global_params=None):
      """Lists all operations owned by a project, aggregating across the different compute zones.

      Args:
        request: (ContainerProjectsOperationsListRequest) input message
        global_params: (StandardQueryParameters, default: None) global arguments
      Returns:
        (ListAggregatedOperationsResponse) The response message.
      """
      config = self.GetMethodConfig('List')
      return self._RunMethod(
          config, request, global_params=global_params)

  class ProjectsZonesClustersService(base_api.BaseApiService):
    """Service class for the projects_zones_clusters resource."""

    _NAME = u'projects_zones_clusters'

    def __init__(self, client):
      super(ContainerV1beta1.ProjectsZonesClustersService, self).__init__(client)
      self._method_configs = {
          'Create': base_api.ApiMethodInfo(
              http_method=u'POST',
              method_id=u'container.projects.zones.clusters.create',
              ordered_params=[u'projectId', u'zoneId'],
              path_params=[u'projectId', u'zoneId'],
              query_params=[],
              relative_path=u'projects/{projectId}/zones/{zoneId}/clusters',
              request_field=u'createClusterRequest',
              request_type_name=u'ContainerProjectsZonesClustersCreateRequest',
              response_type_name=u'Operation',
              supports_download=False,
          ),
          'Delete': base_api.ApiMethodInfo(
              http_method=u'DELETE',
              method_id=u'container.projects.zones.clusters.delete',
              ordered_params=[u'projectId', u'zoneId', u'clusterId'],
              path_params=[u'clusterId', u'projectId', u'zoneId'],
              query_params=[],
              relative_path=u'projects/{projectId}/zones/{zoneId}/clusters/{clusterId}',
              request_field='',
              request_type_name=u'ContainerProjectsZonesClustersDeleteRequest',
              response_type_name=u'Operation',
              supports_download=False,
          ),
          'Get': base_api.ApiMethodInfo(
              http_method=u'GET',
              method_id=u'container.projects.zones.clusters.get',
              ordered_params=[u'projectId', u'zoneId', u'clusterId'],
              path_params=[u'clusterId', u'projectId', u'zoneId'],
              query_params=[],
              relative_path=u'projects/{projectId}/zones/{zoneId}/clusters/{clusterId}',
              request_field='',
              request_type_name=u'ContainerProjectsZonesClustersGetRequest',
              response_type_name=u'Cluster',
              supports_download=False,
          ),
          'List': base_api.ApiMethodInfo(
              http_method=u'GET',
              method_id=u'container.projects.zones.clusters.list',
              ordered_params=[u'projectId', u'zoneId'],
              path_params=[u'projectId', u'zoneId'],
              query_params=[],
              relative_path=u'projects/{projectId}/zones/{zoneId}/clusters',
              request_field='',
              request_type_name=u'ContainerProjectsZonesClustersListRequest',
              response_type_name=u'ListClustersResponse',
              supports_download=False,
          ),
          }

      self._upload_configs = {
          }

    def Create(self, request, global_params=None):
      """Create method for the projects_zones_clusters service.

      Args:
        request: (ContainerProjectsZonesClustersCreateRequest) input message
        global_params: (StandardQueryParameters, default: None) global arguments
      Returns:
        (Operation) The response message.
      """
      config = self.GetMethodConfig('Create')
      return self._RunMethod(
          config, request, global_params=global_params)

    def Delete(self, request, global_params=None):
      """Tear down the complete cluster, api main and worker nodes.

      Args:
        request: (ContainerProjectsZonesClustersDeleteRequest) input message
        global_params: (StandardQueryParameters, default: None) global arguments
      Returns:
        (Operation) The response message.
      """
      config = self.GetMethodConfig('Delete')
      return self._RunMethod(
          config, request, global_params=global_params)

    def Get(self, request, global_params=None):
      """Get method for the projects_zones_clusters service.

      Args:
        request: (ContainerProjectsZonesClustersGetRequest) input message
        global_params: (StandardQueryParameters, default: None) global arguments
      Returns:
        (Cluster) The response message.
      """
      config = self.GetMethodConfig('Get')
      return self._RunMethod(
          config, request, global_params=global_params)

    def List(self, request, global_params=None):
      """List method for the projects_zones_clusters service.

      Args:
        request: (ContainerProjectsZonesClustersListRequest) input message
        global_params: (StandardQueryParameters, default: None) global arguments
      Returns:
        (ListClustersResponse) The response message.
      """
      config = self.GetMethodConfig('List')
      return self._RunMethod(
          config, request, global_params=global_params)

  class ProjectsZonesOperationsService(base_api.BaseApiService):
    """Service class for the projects_zones_operations resource."""

    _NAME = u'projects_zones_operations'

    def __init__(self, client):
      super(ContainerV1beta1.ProjectsZonesOperationsService, self).__init__(client)
      self._method_configs = {
          'Get': base_api.ApiMethodInfo(
              http_method=u'GET',
              method_id=u'container.projects.zones.operations.get',
              ordered_params=[u'projectId', u'zoneId', u'operationId'],
              path_params=[u'operationId', u'projectId', u'zoneId'],
              query_params=[],
              relative_path=u'projects/{projectId}/zones/{zoneId}/operations/{operationId}',
              request_field='',
              request_type_name=u'ContainerProjectsZonesOperationsGetRequest',
              response_type_name=u'Operation',
              supports_download=False,
          ),
          'List': base_api.ApiMethodInfo(
              http_method=u'GET',
              method_id=u'container.projects.zones.operations.list',
              ordered_params=[u'projectId', u'zoneId'],
              path_params=[u'projectId', u'zoneId'],
              query_params=[],
              relative_path=u'projects/{projectId}/zones/{zoneId}/operations',
              request_field='',
              request_type_name=u'ContainerProjectsZonesOperationsListRequest',
              response_type_name=u'ListOperationsResponse',
              supports_download=False,
          ),
          }

      self._upload_configs = {
          }

    def Get(self, request, global_params=None):
      """Get method for the projects_zones_operations service.

      Args:
        request: (ContainerProjectsZonesOperationsGetRequest) input message
        global_params: (StandardQueryParameters, default: None) global arguments
      Returns:
        (Operation) The response message.
      """
      config = self.GetMethodConfig('Get')
      return self._RunMethod(
          config, request, global_params=global_params)

    def List(self, request, global_params=None):
      """List method for the projects_zones_operations service.

      Args:
        request: (ContainerProjectsZonesOperationsListRequest) input message
        global_params: (StandardQueryParameters, default: None) global arguments
      Returns:
        (ListOperationsResponse) The response message.
      """
      config = self.GetMethodConfig('List')
      return self._RunMethod(
          config, request, global_params=global_params)

  class ProjectsZonesService(base_api.BaseApiService):
    """Service class for the projects_zones resource."""

    _NAME = u'projects_zones'

    def __init__(self, client):
      super(ContainerV1beta1.ProjectsZonesService, self).__init__(client)
      self._method_configs = {
          }

      self._upload_configs = {
          }

  class ProjectsService(base_api.BaseApiService):
    """Service class for the projects resource."""

    _NAME = u'projects'

    def __init__(self, client):
      super(ContainerV1beta1.ProjectsService, self).__init__(client)
      self._method_configs = {
          }

      self._upload_configs = {
          }
