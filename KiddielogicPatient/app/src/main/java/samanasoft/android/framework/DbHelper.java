package samanasoft.android.framework;

import java.lang.reflect.Field;

import android.database.Cursor;
import samanasoft.android.framework.attribute.Column;
import samanasoft.android.framework.attribute.Table;
import samanasoft.android.framework.attribute.EnumAttribute.Bool;
import samanasoft.android.framework.attribute.EnumAttribute.DataType;

public class DbHelper{
	private Class<?> type;
	private String tableName;
	private static final String fieldPrefix = "@p_";
	public DbHelper(Class<?> type) {
		this.type = type;
		tableName = getTableName();
	}
	
	//region Public Method
	/** Get One Value From DataRow
	 * 
	 * @param row
	 * @return
	 */
	public Object dataRowToValue(Cursor row){
		String result = "";
		if(row.moveToFirst()){
			result = row.getString(0);
		}
		row.close();
		return result;
	}
	
	/**From Cursor To Single Object
	 * 
	 * @param row
	 * @param obj
	 * @return
	 */
	public Object dataRowToObject(Cursor row, Object obj){
        if(row.getCount() == 0)
            return null;
		if(row.moveToFirst()){
			Field[] fields = type.getDeclaredFields();
			int ctr = 0;
	        for (Field field : fields) {
	        	Column colAttribute = (Column) field.getAnnotation(Column.class); 
	        	if(field.isAnnotationPresent(Column.class)){		        			
	        		try {
	        			setFieldValue(colAttribute, field, row, obj, ctr);
		        		ctr++;
	        		} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} 
	        	}
	        }
		}
		row.close();
		return obj;
	}
	
	public Object dataReaderToObjectColumn(Cursor row){
		String result = "";
		result = row.getString(0);
		return result;
	}
	
	public Object dataReaderToObject(Cursor row, Object obj){
		Field[] fields = type.getDeclaredFields();
		int ctr = 0;
        for (Field field : fields) {
        	Column colAttribute = (Column) field.getAnnotation(Column.class); 
        	if(field.isAnnotationPresent(Column.class)){		        			
        		try {
        			setFieldValue(colAttribute, field, row, obj, ctr);
	        		ctr++;
        		} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} 
        	}
        }
		return obj;
	}
	//endregion
	
	//region SQL Builder
	public String getMaxColumnValue(String columnName){
		StringBuilder result = new StringBuilder();
		result.append("SELECT MAX(").append(columnName).append(") FROM ").append(tableName);
		return result.toString();
	}
	public String getRowCount(String filterExpression){
		StringBuilder result = new StringBuilder();
		result.append("SELECT COUNT(*) FROM ").append(tableName);
		if (filterExpression != null && filterExpression.trim().length() > 0){
            result.append(" WHERE ").append(filterExpression);
        }
		return result.toString();
	}
	
	public String select(String filterExpression){
		StringBuilder fieldName = new StringBuilder();
		Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
        	if(field.isAnnotationPresent(Column.class)){
        		Column colAttribute = (Column) field.getAnnotation(Column.class); 
        		if(!fieldName.toString().equals(""))
            		fieldName.append(", ");  
        		fieldName.append(colAttribute.Name()); 
        	}
        }
		
		StringBuilder result = new StringBuilder();
		result.append("SELECT ").append(fieldName.toString());
		result.append(" FROM ").append(tableName);
        if (filterExpression != null && filterExpression.trim().length() > 0){
            result.append(" WHERE ").append(filterExpression);
        }
        return result.toString();
    }
	
	public String selectListColumn(String filterExpression, String columnName){
		StringBuilder result = new StringBuilder();
		result.append("SELECT ").append(columnName);
		result.append(" FROM ").append(tableName);
        if (filterExpression != null && filterExpression.trim().length() > 0){
            result.append(" WHERE ").append(filterExpression);
        }
        return result.toString();
    }
	
	//region GetRecord
	//region Next Record
	public String getNextRecord(String filterExpressionList, Object currObject){
		String orderByField = getOrderByField(filterExpressionList);		
		StringBuilder newFilterExpression = new StringBuilder();
		try {
			Field field = type.getField(orderByField);
			if(field.isAnnotationPresent(Column.class)){
	    		Column colAttribute = (Column) field.getAnnotation(Column.class); 
				String colName = colAttribute.Name();
				Object currValue = field.get(currObject);
				
				if(filterExpressionList.toUpperCase().contains(" DESC"))
					newFilterExpression.append(colName).append(" < ");
				else
					newFilterExpression.append(colName).append(" > ");
				newFilterExpression.append(colName).append(" > ");
				if(colAttribute.DataType() == DataType.INT)
					newFilterExpression.append(currValue);
				else
					newFilterExpression.append("'").append(currValue).append("'");
				
				if(!filterExpressionList.trim().equals("")){
					newFilterExpression.append(" AND ").append(filterExpressionList);
				}
				newFilterExpression.append(" LIMIT 1");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}		
        return select(newFilterExpression.toString());
    }
	//endregion
	//region Prev Record
	public String getPrevRecord(String filterExpressionList, Object currObject){
		String orderByField = getOrderByField(filterExpressionList);	
		String whereExpression = getWhereExpression(filterExpressionList);
		String newOrderByStatement = reverseOrderByStatement(filterExpressionList);
		StringBuilder newFilterExpression = new StringBuilder();
		try {
			Field field = type.getField(orderByField);
			if(field.isAnnotationPresent(Column.class)){
	    		Column colAttribute = (Column) field.getAnnotation(Column.class); 
				String colName = colAttribute.Name();
				Object currValue = field.get(currObject);
				
				if(newOrderByStatement.toUpperCase().contains(" DESC"))
					newFilterExpression.append(colName).append(" > ");
				else
					newFilterExpression.append(colName).append(" < ");
				if(colAttribute.DataType() == DataType.INT)
					newFilterExpression.append(currValue);
				else
					newFilterExpression.append("'").append(currValue).append("'");
				
				if(!whereExpression.trim().equals("")){
					newFilterExpression.append(" AND ").append(whereExpression);
				}
				newFilterExpression.append(" ORDER BY ").append(newOrderByStatement);
				newFilterExpression.append(" LIMIT 1");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}		
        return select(newFilterExpression.toString());
    }
	//endregion
	//region First Record
	public String getFirstRecord(String filterExpressionList){		
		StringBuilder newFilterExpression = new StringBuilder();
		if(filterExpressionList == null || filterExpressionList.trim().equals(""))
			filterExpressionList = "1 = 1";
		newFilterExpression.append(filterExpressionList).append(" ");
		newFilterExpression.append("LIMIT 1");		
        return select(newFilterExpression.toString());
    }
	//endregion
	//region Last Record
	public String getLastRecord(String filterExpressionList){
		String whereExpression = getWhereExpression(filterExpressionList);
		String newOrderByStatement = reverseOrderByStatement(filterExpressionList);
		StringBuilder newFilterExpression = new StringBuilder();
		newFilterExpression.append(whereExpression).append(" ORDER BY ").append(newOrderByStatement.toString()).append(" ").append("LIMIT 1");	
        return select(newFilterExpression.toString());
    }
	//endregion
	
	public String getRecord(){
		StringBuilder fieldName = new StringBuilder();
		StringBuilder whereExpression = new StringBuilder();
		Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
        	if(field.isAnnotationPresent(Column.class)){
        		Column colAttribute = (Column) field.getAnnotation(Column.class); 
        		if(colAttribute.IsPrimaryKey() == Bool.TRUE){
					if(!whereExpression.toString().equals(""))
						whereExpression.append(" AND ");
					
					String colName = colAttribute.Name();
					whereExpression.append(colName).append(" = ");
					if(colAttribute.DataType() == DataType.INT)
            			whereExpression.append(fieldPrefix).append(colName);
            		else
            			whereExpression.append("'").append(fieldPrefix).append(colName).append("'");
				}
        		if(!fieldName.toString().equals(""))
            		fieldName.append(", ");  
        		fieldName.append(colAttribute.Name());
        	}
        }

		StringBuilder result = new StringBuilder();
		result.append("SELECT ").append(fieldName.toString());
		result.append(" FROM ").append(tableName);
		result.append(" WHERE ").append(whereExpression.toString());
		return result.toString();
	}
	//endregion
	
	public String insert(Object record){    	
    	StringBuilder fieldName = new StringBuilder();
    	StringBuilder fieldValue = new StringBuilder();
    	
    	Field[] fields = record.getClass().getDeclaredFields();
        for (Field field : fields) {
        	if(field.isAnnotationPresent(Column.class)){
        		Column colAttribute = (Column) field.getAnnotation(Column.class);    
        		if(colAttribute.IsIdentity() == Bool.FALSE){        			
		        	try {
		        		if(colAttribute.IsNullable() == Bool.FALSE || (colAttribute.IsNullable() == Bool.TRUE && field.get(record) != null)){
			        		if(!fieldName.toString().equals(""))
			            		fieldName.append(", ");  
			        		fieldName.append(colAttribute.Name());
			        		
			        		if(!fieldValue.toString().equals(""))
			        			fieldValue.append(", ");
			        		
			        		fieldValue.append(getSqlText(colAttribute, field, record));
		        		}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
        		}
        	}
        }        

    	StringBuilder sqlInsert = new StringBuilder();
    	sqlInsert.append("INSERT INTO ").append(tableName);
    	sqlInsert.append(" (").append(fieldName).append(")");
    	sqlInsert.append(" VALUES (").append(fieldValue).append(")");

    	return sqlInsert.toString();
    }
	
	public String update(Object record){
    	StringBuilder fieldStatement = new StringBuilder();
    	StringBuilder whereExpression = new StringBuilder();
    	
    	Field[] fields = record.getClass().getDeclaredFields();
        for (Field field : fields) {
        	if(field.isAnnotationPresent(Column.class)){
        		Column colAttribute = (Column) field.getAnnotation(Column.class);
        	
	        	try {
	        		if(colAttribute.IsNullable() == Bool.FALSE || (colAttribute.IsNullable() == Bool.TRUE && field.get(record) != null)){
		        		if(colAttribute.IsPrimaryKey() == Bool.TRUE){
		        			if(!whereExpression.toString().equals(""))
		        				whereExpression.append(" AND ");
		        			
		        			whereExpression.append(colAttribute.Name()).append(" = ");
		        			whereExpression.append(getSqlText(colAttribute, field, record));
		        		}
		        		else{
		        			if(!fieldStatement.toString().equals(""))
			        			fieldStatement.append(", ");  
			        		fieldStatement.append(colAttribute.Name()).append(" = ");
			        		fieldStatement.append(getSqlText(colAttribute, field, record));
		        		}
	        		}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
        	}
        }        

    	StringBuilder sqlUpdate = new StringBuilder();
    	sqlUpdate.append("UPDATE ").append(tableName);
    	sqlUpdate.append(" SET ").append(fieldStatement);
    	sqlUpdate.append(" WHERE ").append(whereExpression);
    	
    	return sqlUpdate.toString();  	
    }
	
	public String delete(Object record){
    	StringBuilder whereExpression = new StringBuilder();
    	
    	Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
        	if(field.isAnnotationPresent(Column.class)){
	        	Column colAttribute = (Column) field.getAnnotation(Column.class);
	        	try {
	        		if(colAttribute.IsPrimaryKey() == Bool.TRUE){
	        			if(!whereExpression.toString().equals(""))
	        				whereExpression.append(" AND ");
	        			
	        			whereExpression.append(colAttribute.Name()).append(" = ");
	        			whereExpression.append(getSqlText(colAttribute, field, record));
	        		}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
        	}
        }       
    	
    	StringBuilder sqlDelete = new StringBuilder();
    	sqlDelete.append("DELETE FROM ").append(tableName);
    	sqlDelete.append(" WHERE ").append(whereExpression);

    	return sqlDelete.toString(); 
    }
	//endregion
	
	//region Private Method
	private String getOrderByField(String filterExpressionList){
		String orderByField = "";
		if(filterExpressionList.contains("ORDER BY")){
			String[] param = filterExpressionList.split("ORDER BY");
			String orderByStatement = param[1].trim();
			String[] paramOrderBy = orderByStatement.split(" ");
			orderByField = paramOrderBy[0].trim();
		}
		else{
			Field[] fields = type.getDeclaredFields();
	        for (Field field : fields) {
	        	if(field.isAnnotationPresent(Column.class)){
	        		Column colAttribute = (Column) field.getAnnotation(Column.class); 
	        		if(colAttribute.IsPrimaryKey() == Bool.TRUE){
	        			orderByField = colAttribute.Name();
						break;
					}
	        	}
	        }
		}
		return orderByField;
	}
	
	private String reverseOrderByStatement(String filterExpressionList){
		StringBuilder newOrderByStatement = new StringBuilder();
		if(filterExpressionList != null && filterExpressionList.contains("ORDER BY")){
			String[] param = filterExpressionList.split("ORDER BY");
			String orderByStatement = param[1].substring(1);
			
			String[] paramOrderBy = orderByStatement.split(" ");
			String orderByType = "";
			if(paramOrderBy.length > 1){
				orderByType = paramOrderBy[1];
				if(orderByType.toUpperCase().equals("ASC"))
					orderByType = "DESC";
				else
					orderByType = "ASC";
			}
			else
				orderByType = "DESC";
			
			newOrderByStatement.append(paramOrderBy[0]).append(" ").append(orderByType);
		}
		else{
			Field[] fields = type.getDeclaredFields();
	        for (Field field : fields) {
	        	if(field.isAnnotationPresent(Column.class)){
	        		Column colAttribute = (Column) field.getAnnotation(Column.class); 
	        		if(colAttribute.IsPrimaryKey() == Bool.TRUE){
	        			newOrderByStatement.append(colAttribute.Name()).append(" DESC");
						break;
					}
	        	}
	        }
		}
		return newOrderByStatement.toString();
	}
	
	private String getWhereExpression(String filterExpressionList){
		String whereExpression = "";
		if(filterExpressionList != null && !filterExpressionList.trim().equals("")){
			if(filterExpressionList.contains("ORDER BY")){
				String[] param = filterExpressionList.split("ORDER BY");
				whereExpression = param[0];
			}
			else{
				whereExpression = filterExpressionList;
			}
		}
		else{
			whereExpression = "1 = 1";
		}
		return whereExpression;
	}
	
	private String getTableName(){
		return ((Table) type.getAnnotation(Table.class)).Name();	
	}
	
	private void setFieldValue(Column colAttribute, Field field, Cursor row, Object obj, int ctr) throws IllegalArgumentException, IllegalAccessException{
		Object value = null;
		if(colAttribute.DataType() == DataType.BOOLEAN){
			value = row.getString(ctr);
			boolean val = (value.equals("1") ? true : false);
			field.set(obj, val);
		}
		else if(colAttribute.DataType() == DataType.DATETIME){
			value = row.getString(ctr);
			if(value != null)
				field.set(obj, new DateTime((String)value));
			else
				field.set(obj, new DateTime());
		}
		else{
			if(colAttribute.DataType() == DataType.INT)
    			value = row.getInt(ctr);
			else if(colAttribute.DataType() == DataType.LONG)
				value = row.getLong(ctr);
    		else
    			value = row.getString(ctr);
			field.set(obj, value);
		}
	}
	
	private String getSqlText(Column colAttribute, Field field, Object record) throws IllegalArgumentException, IllegalAccessException{
		StringBuilder result = new StringBuilder();
		if(colAttribute.DataType() == DataType.INT)
			result.append(field.get(record));
		else if(colAttribute.DataType() == DataType.DATETIME){
			DateTime value = (DateTime) field.get(record);
			result.append("'").append(value.toString(Constant.FormatString.DATE_TIME_FORMAT_DB)).append("'");
		}
		else if(colAttribute.DataType() == DataType.BOOLEAN){
			boolean value = (Boolean) field.get(record);
			String sValue = value == true ? "1" : "0";
			result.append("'").append(sValue).append("'");
		}
		else
			result.append("'").append(field.get(record)).append("'");
		return result.toString();
	}
	//endregion
}
