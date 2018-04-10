/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.elasticsearch.cloud.kubernetes;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Endpoints;
import io.kubernetes.client.models.V1EndpointsList;
import io.kubernetes.client.util.Config;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.settings.Settings;

import java.io.Closeable;
import java.io.IOException;

public class KubernetesAPIServiceImpl extends AbstractComponent implements KubernetesAPIService, Closeable {

  private final String namespace;
  private final String serviceName;

  @Override
  public V1Endpoints endpoints() {
    logger.debug("get endpoints for service {}, namespace {}", serviceName, namespace);

    try {
      return client().readNamespacedEndpoints(serviceName,namespace, null, null, null);
    } catch (ApiException e) {
      e.printStackTrace();
      return null;
    }
  }

  private CoreV1Api api;

  public KubernetesAPIServiceImpl(Settings settings) {
    super(settings);
    this.namespace = NAME_SPACE_SETTING.get(settings);
    this.serviceName = SERVICE_NAME_SETTING.get(settings);
  }

  public synchronized CoreV1Api client() {
    if (api == null) {
      try {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        api = new CoreV1Api();


      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return api;
  }

  @Override
  public void close() {
  }

}
