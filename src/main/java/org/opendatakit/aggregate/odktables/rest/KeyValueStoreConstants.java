package org.opendatakit.aggregate.odktables.rest;

/**
 * Constants used to access well-known values within the KVS.
 *
 * @author mitchellsundt@gmail.com
 *
 */
public class KeyValueStoreConstants {


  // special well-known partitions
  public static final String TABLE_PARTITION = "Table";
  public static final String COLUMN_PARTITION = "Column";
  // others should use their class names as the partition name

  // default aspect
  public static final String DEFAULT_ASPECT = "default";

  // Keys for:
  // TABLE_PARTITION, DEFAULT_ASPECT...
  public static final String TABLE_TYPE = "tableType";
  public static final String TABLE_ACCESS_CONTROL_TABLE_ID = "accessControlTableId";

  public static final String TABLE_DISPLAY_NAME = "displayName";
  public static final String TABLE_PRIME_COLS = "primeCols";
  public static final String TABLE_SORT_COL = "sortCol";
  public static final String TABLE_CO_VIEW_SETTINGS = "coViewSettings";
  public static final String TABLE_DETAIL_VIEW_FILE = "detailViewFile";
  public static final String TABLE_SUMMARY_DISPLAY_FORMAT = "summaryDisplayFormat";
  public static final String TABLE_COL_ORDER = "colOrder";
  public static final String TABLE_OV_VIEW_SETTINGS = "ovViewSettings";

  // Keys for:
  // COLUMN_PARTITION, dbColName...
  public static final String COLUMN_DISPLAY_NAME = "displayName";
  public static final String COLUMN_DISPLAY_VISIBLE = "displayVisible";
  public static final String COLUMN_DISPLAY_CHOICES_LIST = "displayChoicesList";
  public static final String COLUMN_DISPLAY_FORMAT = "displayFormat";
  public static final String COLUMN_SMS_IN = "smsIn";
  public static final String COLUMN_SMS_OUT = "smsOut";
  public static final String COLUMN_SMS_LABEL = "smsLabel";
  public static final String COLUMN_FOOTER_MODE = "footerMode";
  public static final String COLUMN_JOINS = "joins";

}
