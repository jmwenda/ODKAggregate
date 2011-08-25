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

package org.opendatakit.aggregate.client.table;

import java.util.ArrayList;

import org.opendatakit.aggregate.client.AggregateUI;
import org.opendatakit.aggregate.client.popups.BinaryPopup;
import org.opendatakit.aggregate.client.submission.Column;
import org.opendatakit.aggregate.client.submission.SubmissionUI;
import org.opendatakit.aggregate.client.submission.SubmissionUISummary;
import org.opendatakit.aggregate.client.widgets.DeleteSubmissionButton;
import org.opendatakit.aggregate.client.widgets.RepeatViewButton;
import org.opendatakit.aggregate.constants.common.UIConsts;
import org.opendatakit.common.security.common.GrantedAuthorityName;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class SubmissionTable extends FlexTable {

  private static final String BLANK_VALUE = " ";
  private ArrayList<Column> tableHeaders;
  private ArrayList<SubmissionUI> tableSubmissions;

  public SubmissionTable(SubmissionUISummary summary) {
    tableHeaders = summary.getHeaders();
    tableSubmissions = summary.getSubmissions();

    addStyleName("dataTable");
    getElement().setId("submission_table");

    boolean addDeleteButton = AggregateUI.getUI().getUserInfo().getGrantedAuthorities()
        .contains(GrantedAuthorityName.ROLE_DATA_OWNER);

    // setup header
    int headerIndex = 0;
    if (addDeleteButton) {
      setHTML(0, headerIndex++, BLANK_VALUE);
    }
    for (Column column : tableHeaders) {
      setText(0, headerIndex++, column.getDisplayHeader());
    }
    setHTML(0, headerIndex, BLANK_VALUE);
    setColumnFormatter(new HTMLTable.ColumnFormatter());
    getColumnFormatter().addStyleName(headerIndex, "blank-submission-column");

    getRowFormatter().addStyleName(0, "titleBar");

    // create rows
    int rowPosition = 1;
    for (SubmissionUI row : tableSubmissions) {
      int valueIndex = 0; // index matches to column headers
      int columnPosition = 0; // position matches to position in table
   
      // if authorized add delete button
      if (addDeleteButton) {
        DeleteSubmissionButton delete = new DeleteSubmissionButton(row.getSubmissionKeyAsString());
        setWidget(rowPosition, columnPosition, delete);
        columnPosition++;
      }
   
      // generate row
      for (final String value : row.getValues()) {
        switch (tableHeaders.get(valueIndex++).getUiDisplayType()) {
        case BINARY:
          if (value == null) {
            setText(rowPosition, columnPosition, UIConsts.EMPTY_STRING);
          } else {
            Image image = new Image(value + UIConsts.PREVIEW_SET);
            image.addClickHandler(new ClickHandler() {
              @Override
              public void onClick(ClickEvent event) {
                final PopupPanel popup = new BinaryPopup(value);
                popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                  @Override
                  public void setPosition(int offsetWidth, int offsetHeight) {
                    int left = ((Window.getScrollLeft() + Window.getClientWidth() - offsetWidth) / 2);
                    int top = ((Window.getScrollTop() + Window.getClientHeight() - offsetHeight) / 2);
                    popup.setPopupPosition(left, top);
                  }
                });
                AggregateUI.getUI().getTimer().restartTimer();
              }
            });
            setWidget(rowPosition, columnPosition, image);
          }
          break;
        case REPEAT:
          if (value == null) {
            setText(rowPosition, columnPosition, UIConsts.EMPTY_STRING);
          } else {
            RepeatViewButton repeat = new RepeatViewButton(value);
            setWidget(rowPosition, columnPosition, repeat);
          }
          break;
        default:
          setText(rowPosition, columnPosition, value);
        }
        columnPosition++;
      }
      setHTML(rowPosition, columnPosition, BLANK_VALUE);
      if (rowPosition % 2 == 0) {
        getRowFormatter().setStyleName(rowPosition, "evenTableRow");
      }
      rowPosition++;
    }
  }

  public ArrayList<Column> getHeaders() {
    return tableHeaders;
  }

  public ArrayList<SubmissionUI> getSubmissions() {
    return tableSubmissions;
  }
}
