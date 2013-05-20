package org.opendatakit.aggregate.client.odktables;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is the client-side version of 
 * org.opendatakit.aggregate.odktables.entity.api.
 * <br>
 * The idea is that it will do the same thing, but on the client side.
 * Usual caveat that it is not yet clear if this is needed or if another
 * thing needs be created for the server to do non-phone things.
 * @author sudar.sam@gmail.com
 *
 */
public class TableDefinitionClient implements IsSerializable {

  /**
	 * 
	 */
	private static final long serialVersionUID = -1413634509888543150L;

  private String tableId;
  private String tableKey;
  private String dbTableName;
  private TableTypeClient type;
  private String tableIdAccessControls;
  private List<ColumnClient> columns;
  
  @SuppressWarnings("unused")
  private TableDefinitionClient() {
    // necessary for gwt serialization
  }

  public TableDefinitionClient(final String tableId, 
      final List<ColumnClient> columns, final String tableKey, 
      final String dbTableName, final TableTypeClient type, 
      final String tableIdAccessControls) {
    this.tableId = tableId;
    this.columns = columns;
    this.tableKey = tableKey;
    this.dbTableName = dbTableName;
    this.type = type;
    this.tableIdAccessControls = tableIdAccessControls;
  }

  public String getTableId() {
    return this.tableId;
  }

  public List<ColumnClient> getColumns() {
    return this.columns;
  }
  
  public String getTableKey() {
    return this.tableKey;
  }
  
  public String getDbTableName() {
    return this.dbTableName;
  }
  
  public TableTypeClient getType() {
    return this.type;
  }
  
  public String getTableIdAccessControls() {
    return this.tableIdAccessControls;
  }

  public void setColumns(final List<ColumnClient> columns) {
    this.columns = columns;
  }

  @Override
  public String toString() {
    return "TableDefinitionClient[tableId=" + getTableId() 
        + ", columns=" + getColumns().toString() 
        + ", tableKey=" + getTableKey()
        + ", dbTableName=" + getDbTableName()
        + ", type=" + getType()
        + ", tableIdAccessControls=" + getTableIdAccessControls()
        + "]";
  }
}
