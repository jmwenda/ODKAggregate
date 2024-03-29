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

package org.opendatakit.aggregate.client.widgets;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class AggregateListBox extends ListBox implements ChangeHandler {

  private final AggregateBaseHandlers handlers;

  public AggregateListBox(String tooltipText, boolean multipleValueSelection) {
    this(tooltipText, multipleValueSelection, null);
  }

  public AggregateListBox(String tooltipText, boolean multipleValueSelection, String helpBalloonText) {
    super(multipleValueSelection);

    addChangeHandler(this);

    // setup help system
    handlers = new AggregateBaseHandlers(this, tooltipText, helpBalloonText);
    addMouseOverHandler(handlers);
    addMouseOutHandler(handlers);
  }

  @Override
  public void onChange(ChangeEvent event) {
    handlers.userAction();
  }

}
