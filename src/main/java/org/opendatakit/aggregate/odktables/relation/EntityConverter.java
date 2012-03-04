package org.opendatakit.aggregate.odktables.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.val;

import org.apache.commons.lang.Validate;
import org.opendatakit.aggregate.odktables.entity.Column;
import org.opendatakit.aggregate.odktables.entity.Column.ColumnType;
import org.opendatakit.aggregate.odktables.entity.Row;
import org.opendatakit.aggregate.odktables.entity.TableEntry;
import org.opendatakit.aggregate.odktables.exception.RowVersionMismatchException;
import org.opendatakit.common.ermodel.simple.Entity;
import org.opendatakit.common.ermodel.simple.Relation;
import org.opendatakit.common.persistence.CommonFieldsBase;
import org.opendatakit.common.persistence.DataField;
import org.opendatakit.common.persistence.DataField.DataType;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.persistence.exception.ODKEntityNotFoundException;
import org.opendatakit.common.web.CallingContext;

/**
 * Converts between datastore {@link Entity} objects and domain objects in
 * org.opendatakit.aggregate.odktables.entity.
 * 
 * @author the.dylan.price@gmail.com
 * 
 */
public class EntityConverter {

  /**
   * Convert a {@link DbTableEntry} entity to a {@link TableEntry}
   */
  public TableEntry toTableEntry(Entity entity) {
    TableEntry entry = new TableEntry();
    entry.setTableId(entity.getId());
    entry.setDataEtag(entity.getAsString(DbTableEntry.MODIFICATION_NUMBER));
    return entry;
  }

  /**
   * Convert a {@link DbColumn} entity to a {@link Column}
   */
  public Column toColumn(Entity entity) {
    Column column = new Column(entity.getString(DbColumn.COLUMN_NAME), ColumnType.valueOf(entity
        .getString(DbColumn.COLUMN_TYPE)));
    return column;
  }

  /**
   * Convert a list of {@link DbColumn} entities to a list of {@link Column}
   */
  public List<Column> toColumns(List<Entity> entities) {
    List<Column> columns = new ArrayList<Column>();
    for (Entity entity : entities) {
      columns.add(toColumn(entity));
    }
    return columns;
  }

  /**
   * Convert a {@link Column} to a {@link DataField}
   */
  public DataField toField(Column column) {
    DataField field = new DataField(RUtil.convertIdentifier(column.getName()),
        DataType.valueOf(column.getType().name()), true);
    return field;
  }

  /**
   * Convert a {@link DbColumn} entity to a {@link DataField}
   */
  public DataField toField(Entity entity) {
    DataField field = new DataField(RUtil.convertIdentifier(entity.getId()),
        DataType.valueOf(entity.getString(DbColumn.COLUMN_TYPE)), true);
    return field;
  }

  /**
   * Convert a list of {@link DbColumn} entities to a list of {@link DataField}
   */
  public List<DataField> toFields(List<Entity> entities) {
    List<DataField> fields = new ArrayList<DataField>();
    for (Entity entity : entities)
      fields.add(toField(entity));
    return fields;
  }

  /**
   * Convert a {@link DbTable} entity into a {@link Row}
   * 
   * @param entity
   *          the {@link DbTable} entity.
   * @param columns
   *          the {@link DbColumn} entities of the table
   * @return the row
   */
  public Row toRow(Entity entity, List<Entity> columns) {
    val row = new Row();
    row.setRowId(entity.getId());
    row.setRowEtag(entity.getString(DbTable.ROW_VERSION));
    row.setGroupOrUserId(entity.getString(DbTable.GROUP_OR_USER_ID));
    row.setDeleted(entity.getBoolean(DbTable.DELETED));

    val values = new HashMap<String, String>();
    for (Entity column : columns) {
      String name = column.getString(DbColumn.COLUMN_NAME);
      String value = entity.getAsString(RUtil.convertIdentifier(column.getId()));
      values.put(name, value);
    }
    row.setValues(values);
    return row;
  }

  /**
   * Convert a list of {@link DbTable} entities into a list of {@link Row}
   * 
   * @param entities
   *          the {@link DbTable} entities
   * @param columns
   *          the {@link DbColumn} of the table
   * @return the converted rows
   */
  public List<Row> toRows(List<Entity> entities, List<Entity> columns) {
    val rows = new ArrayList<Row>();
    for (Entity entity : entities) {
      rows.add(toRow(entity, columns));
    }
    return rows;
  }

  /**
   * Create a new {@link DbTableEntry} entity.
   * 
   * @param tableId
   *          the table id. May be null to auto generate.
   * @param cc
   * @return the created entity, not yet persisted
   * @throws ODKDatastoreException
   */
  public Entity newTableEntryEntity(String tableId, CallingContext cc) throws ODKDatastoreException {
    Validate.notNull(cc);

    if (tableId == null)
      tableId = CommonFieldsBase.newUri();

    Entity entity = DbTableEntry.getRelation(cc).newEntity(tableId, cc);
    entity.set(DbTableEntry.MODIFICATION_NUMBER, 0);
    return entity;
  }

  /**
   * Create a new {@link DbColumn} entity.
   * 
   * @param tableId
   *          the id of the table for the new column.
   * @param column
   *          the new column.
   * @param cc
   * @return the created entity, not yet persisted
   * @throws ODKDatastoreException
   */
  public Entity newColumnEntity(String tableId, Column column, CallingContext cc)
      throws ODKDatastoreException {
    Validate.notEmpty(tableId);
    Validate.notNull(column);
    Validate.notNull(cc);

    Entity entity = DbColumn.getRelation(cc).newEntity(cc);
    entity.set(DbColumn.TABLE_ID, tableId);
    entity.set(DbColumn.COLUMN_NAME, column.getName());
    entity.set(DbColumn.COLUMN_TYPE, column.getType().name());
    return entity;
  }

  /**
   * Create a new {@link DbTable} row entity.
   * 
   * @param table
   *          the {@link DbTable} relation.
   * @param rowId
   *          the id of the new row. May be null to auto generate.
   * @param modificationNumber
   *          the modification number for this row.
   * @param groupOrUserId
   *          the group or user id for this row. May be null.
   * @param values
   *          the values to set on the row.
   * @param columns
   *          the {@link DbColumn} entities for the table
   * @param cc
   * @return the created entity, not yet persisted
   * @throws ODKDatastoreException
   */
  public Entity newRowEntity(Relation table, String rowId, int modificationNumber,
      String groupOrUserId, Map<String, String> values, List<Entity> columns, CallingContext cc)
      throws ODKDatastoreException {
    Validate.notNull(table);
    Validate.isTrue(modificationNumber >= 0);
    Validate.noNullElements(values.keySet());
    Validate.noNullElements(columns);
    Validate.notNull(cc);

    if (rowId == null)
      rowId = CommonFieldsBase.newUri();
    String rowVersion = CommonFieldsBase.newUri();

    Entity entity = table.newEntity(rowId, cc);
    entity.set(DbTable.ROW_VERSION, rowVersion);
    entity.set(DbTable.MODIFICATION_NUMBER, modificationNumber);
    entity.set(DbTable.GROUP_OR_USER_ID, groupOrUserId);
    entity.set(DbTable.DELETED, false);

    for (Entity column : columns) {
      String id = column.getId();
      String name = column.getString(DbColumn.COLUMN_NAME);
      String value = values.get(name);
      entity.setAsString(RUtil.convertIdentifier(id), value);
    }
    return entity;
  }

  /**
   * Create a collection of new {@link DbTable} entities
   * 
   * @param table
   *          the {@link DbTable} relation
   * @param rows
   *          the rows, see {@link Row#forInsert(String, String, Map)}
   * @param modificationNumber
   *          the modification number for the rows.
   * @param columns
   *          the {@link DbColumn} entities for the table
   * @param cc
   * @return the created entities, not yet persisted
   * @throws ODKDatastoreException
   */
  public List<Entity> newRowEntities(Relation table, List<Row> rows, int modificationNumber,
      List<Entity> columns, CallingContext cc) throws ODKDatastoreException {
    Validate.notNull(table);
    Validate.noNullElements(rows);
    Validate.isTrue(modificationNumber >= 0);
    Validate.noNullElements(columns);
    Validate.notNull(cc);

    List<Entity> entities = new ArrayList<Entity>();
    for (Row row : rows) {
      Entity entity = newRowEntity(table, row.getRowId(), modificationNumber,
          row.getGroupOrUserId(), row.getValues(), columns, cc);
      entities.add(entity);
    }
    return entities;
  }

  /**
   * Update an existing {@link DbTable} entity.
   * 
   * @param table
   *          the {@link DbTable} relation
   * @param modificationNumber
   *          the modification number for this row
   * @param rowId
   *          the id of the row
   * @param currentEtag
   *          the current etag value
   * @param groupOrUserId
   *          the group or user id for the row
   * @param values
   *          the values to set
   * @param deleted
   *          true if the row is deleted
   * @param columns
   *          the {@link DbColumn} entities for the table
   * @param cc
   * @return the updated entity, not yet persisted
   * @throws ODKEntityNotFoundException
   *           if there is no entity with the given rowId
   * @throws RowVersionMismatchException
   *           if currentEtag does not match the etag of the row
   * @throws ODKDatastoreException
   */
  public Entity updateRowEntity(Relation table, int modificationNumber, String rowId,
      String currentEtag, String groupOrUserId, Map<String, String> values, boolean deleted,
      List<Entity> columns, CallingContext cc) throws ODKEntityNotFoundException,
      ODKDatastoreException, RowVersionMismatchException {
    Validate.notNull(table);
    Validate.isTrue(modificationNumber >= 0);
    Validate.notEmpty(rowId);
    Validate.notEmpty(currentEtag);
    Validate.noNullElements(values.keySet());
    Validate.noNullElements(columns);
    Validate.notNull(cc);

    Entity row = table.getEntity(rowId, cc);
    String rowEtag = row.getString(DbTable.ROW_VERSION);
    if (!currentEtag.equals(rowEtag)) {
      throw new RowVersionMismatchException(String.format("%s does not match %s for rowId %s",
          currentEtag, rowEtag, row.getId()));
    }
    row.set(DbTable.ROW_VERSION, CommonFieldsBase.newUri());
    row.set(DbTable.MODIFICATION_NUMBER, modificationNumber);
    row.set(DbTable.GROUP_OR_USER_ID, groupOrUserId);
    row.set(DbTable.DELETED, deleted);

    for (val entry : values.entrySet()) {
      String value = entry.getValue();
      String name = entry.getKey();
      Entity column = findColumn(name, columns);
      if (column == null)
        throw new IllegalArgumentException("Bad column name " + name);
      row.setAsString(RUtil.convertIdentifier(column.getId()), value);
    }
    return row;
  }

  private Entity findColumn(String name, List<Entity> columns) {
    for (val entity : columns) {
      String colName = entity.getString(DbColumn.COLUMN_NAME);
      if (name.equals(colName))
        return entity;
    }
    return null;
  }

  /**
   * Updates a collection of {@link DbTable} entities.
   * 
   * @param table
   *          the {@link DbTable} relation.
   * @param modificationNumber
   *          the modification number for the rows.
   * @param rows
   *          the rows to update, see {@link Row#forUpdate(String, String, Map)}
   * @param columns
   *          the {@link DbColumn} entities for the table
   * @param cc
   * @return the updated entities, not yet persisted
   * @throws ODKEntityNotFoundException
   *           if one of the rows does not exist in the datastore
   * @throws RowVersionMismatchException
   *           if one of the row's etags does not match the etag for the row in
   *           the datastore
   * @throws ODKDatastoreException
   */
  public List<Entity> updateRowEntities(Relation table, int modificationNumber, List<Row> rows,
      List<Entity> columns, CallingContext cc) throws ODKEntityNotFoundException,
      ODKDatastoreException, RowVersionMismatchException {
    Validate.notNull(table);
    Validate.isTrue(modificationNumber >= 0);
    Validate.noNullElements(rows);
    Validate.noNullElements(columns);
    Validate.notNull(cc);

    val entities = new ArrayList<Entity>();
    for (Row row : rows) {
      entities.add(updateRowEntity(table, modificationNumber, row.getRowId(), row.getRowEtag(),
          row.getGroupOrUserId(), row.getValues(), row.isDeleted(), columns, cc));
    }
    return entities;
  }

  /**
   * Retrieve a list of {@link DbTable} row entities.
   * 
   * @param table
   *          the {@link DbTable} relation.
   * @param rowIds
   *          the ids of the rows to get.
   * @param cc
   * @return
   * @throws ODKEntityNotFoundException
   *           if one of the rows does not exist
   * @throws ODKDatastoreException
   */
  public List<Entity> getRowEntities(Relation table, List<String> rowIds, CallingContext cc)
      throws ODKEntityNotFoundException, ODKDatastoreException {
    Validate.notNull(table);
    Validate.noNullElements(rowIds);
    Validate.notNull(cc);

    val entities = new ArrayList<Entity>();
    for (val rowId : rowIds) {
      entities.add(table.getEntity(rowId, cc));
    }
    return entities;
  }

}
