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

package org.opendatakit.aggregate.client;

import org.opendatakit.aggregate.client.preferences.Preferences;
import org.opendatakit.aggregate.client.widgets.UpdateGMapsKeyButton;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PreferencesSubTab extends VerticalPanel implements SubTabInterface{

  // Preferences tab
  private static final String GOOGLE_MAPS_API_KEY_LABEL = "<h2>Google Maps API Key</h2> To obtain a key signup at <a href=\"http://code.google.com/apis/maps/signup.html\"> Google Maps </a>";
  private TextBox mapsApiKey = new TextBox();
  
  public PreferencesSubTab() {
    HTML labelMapsKey = new HTML(GOOGLE_MAPS_API_KEY_LABEL);
    mapsApiKey.setText(Preferences.getGoogleMapsApiKey());

    add(labelMapsKey);
    add(mapsApiKey);
    add(new UpdateGMapsKeyButton(mapsApiKey));
  }


  @Override
  public boolean canLeave() {
	  return true;
  }
  
  @Override
  public void update() {
    mapsApiKey.setText(Preferences.getGoogleMapsApiKey());
  }
  
}
