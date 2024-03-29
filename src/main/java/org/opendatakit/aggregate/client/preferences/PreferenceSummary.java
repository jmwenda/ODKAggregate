/*
 * Copyright (C) 2011 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.opendatakit.aggregate.client.preferences;

import java.io.Serializable;

public class PreferenceSummary implements Serializable {

  private static final long serialVersionUID = -5344882762820967968L;

  private String googleSimpleApiKey;

  private String googleApiClientId;

  private String enketoApiUrl;

  private String enketoApiToken;

  private Boolean odkTablesEnabled;

  private Boolean fasterBackgroundActionsDisabled;

  public PreferenceSummary() {

  }

  public PreferenceSummary(String googleSimpleApiKey, String googleApiClientId,
      String enketoApiUrl, String enketoApiToken, Boolean odkTablesEnabled,
      Boolean fasterBackgroundActionsDisabled) {
    this.googleSimpleApiKey = googleSimpleApiKey;
    this.googleApiClientId = googleApiClientId;
    this.enketoApiUrl = enketoApiUrl;
    this.enketoApiToken = enketoApiToken;
    this.odkTablesEnabled = odkTablesEnabled;
    this.fasterBackgroundActionsDisabled = fasterBackgroundActionsDisabled;
  }

  public String getGoogleSimpleApiKey() {
    return googleSimpleApiKey;
  }

  public String getGoogleApiClientId() {
    return googleApiClientId;
  }

  public String getEnketoApiUrl() {
    return enketoApiUrl;
  }

  public String getEnketoApiToken() {
    return enketoApiToken;
  }

  public Boolean getOdkTablesEnabled() {
    return odkTablesEnabled;
  }

  public Boolean getFasterBackgroundActionsDisabled() {
    return fasterBackgroundActionsDisabled;
  }

}
