//
//  DBHelper.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/21/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import Foundation
import UIKit

public class Util{
    class func getPath(fileName: String) -> String {
        let documentsURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let fileURL = documentsURL.appendingPathComponent(fileName)
        return fileURL.path;
    }
    class func copyFile(fileName: NSString) {
        let dbPath: String = getPath(fileName: fileName as String)
        let fileManager = FileManager.default;
        if fileManager.fileExists(atPath: dbPath) {
            do {
                try fileManager.removeItem(atPath: dbPath)
            } catch let error1 as NSError {
                var error2 = error1
            }
        }
        if !fileManager.fileExists(atPath: dbPath) {
            let documentsURL = Bundle.main.resourceURL
            let fromPath = documentsURL!.appendingPathComponent(fileName as String)
            var error : NSError?
            do {
                try fileManager.copyItem(atPath: fromPath.path, toPath: dbPath)
            } catch let error1 as NSError {
                error = error1
            }
            let alert: UIAlertView = UIAlertView()
            if (error != nil) {
                alert.title = "Error Occured"
                alert.message = error?.localizedDescription
            } else {
                alert.title = "Successfully Copy"
                alert.message = "Your database copy successfully"
            }
            alert.addButton(withTitle: "Ok")
            alert.show()
        }
    }
    class func invokeAlertMethod(strTitle: NSString, strBody: NSString, delegate: AnyObject?)
    {
        let alert: UIAlertView = UIAlertView()
        alert.message = strBody as String
        alert.title = strTitle as String
        alert.delegate = delegate
        alert.addButton(withTitle: "Ok")
        alert.show()
    }
}

public class DBHelper{
    public func getRecord(tableName:String, lstPrimaryKey:[String]) -> String{
        var whereExpression = "";
        
        for key in lstPrimaryKey {
            if(!whereExpression.isEmpty){
                whereExpression += " AND ";
            }
            whereExpression += key + " = ";
            whereExpression += "'@p_" + key + "'";
        }
        
        var result = "";
        result += "SELECT * FROM " + tableName;
        result += " WHERE " + whereExpression;
        
        return result;
    }
    public func select(tableName:String, filterExpression:String) -> String{
        var result = "";
        result += "SELECT * FROM " + tableName;
        if(!filterExpression.isEmpty){
            result += " WHERE " + filterExpression;
        }
        return result;
    }

    
    public func delete(tableName:String, lstPrimaryKey:[String]) -> String{
        var whereExpression = "";
        
        for key in lstPrimaryKey {
            if(!whereExpression.isEmpty){
                whereExpression += " AND ";
            }
            whereExpression += key + " = ";
            whereExpression += "'@p_" + key + "'";
        }
        
        var result = "";
        result += "DELETE FROM " + tableName;
        result += " WHERE " + whereExpression;
        
        return result;
    }

    
    public func insert(tableName:String, record:BaseClass) -> String?{
        var fieldName = "";
        var fieldValue = "";
        
        let lstProp = record.propertyNames();
        for colAttribute in lstProp {
            if(!colAttribute.IsNullable || (colAttribute.IsNullable && record.value(forKey: colAttribute.FieldName) != nil)){
                if(!fieldName.isEmpty){
                    fieldName += ",";
                    fieldValue += ",";
                }
                fieldName += colAttribute.FieldName;
                fieldValue += getSqlText(colAttribute: colAttribute, record: record);
            }
        }
        var sqlInsert = "";
        sqlInsert += "INSERT INTO " + tableName;
        sqlInsert += " (" + fieldName + ")";
        sqlInsert += " VALUES (" + fieldValue + ")";
        
        return sqlInsert;
    }
    public func update(tableName:String, record:BaseClass) -> String?{
        var fieldStatement = "";
        var whereExpression = "";
        
        let lstProp = record.propertyNames();
        for colAttribute in lstProp {
            if(!colAttribute.IsNullable || (colAttribute.IsNullable && record.value(forKey: colAttribute.FieldName) != nil)){
                if(colAttribute.IsPrimaryKey){
                    if(!whereExpression.isEmpty){
                        whereExpression += " AND ";
                    }
                    whereExpression += colAttribute.FieldName + " = ";
                    whereExpression.append(getSqlText(colAttribute: colAttribute, record: record));
                }
                else{
                    if(!fieldStatement.isEmpty){
                        fieldStatement += ", ";
                    }
                    fieldStatement += colAttribute.FieldName + " = ";
                    fieldStatement.append(getSqlText(colAttribute: colAttribute, record: record));
                }
            }
        }
        var sqlUpdate = "";
        sqlUpdate += "UPDATE " + tableName;
        sqlUpdate += " SET " + fieldStatement;
        sqlUpdate += " WHERE " + whereExpression;
        
        return sqlUpdate;
    }
    private func getSqlText(colAttribute:ClassProperties, record:BaseClass) -> String{
        var result = "";
        if(colAttribute.FieldType == "Optional<NSNumber>"){
            result += String(describing: record.value(forKey: colAttribute.FieldName) as! NSNumber);
        }
        else if(colAttribute.FieldType == "Optional<DateTime>"){
            if(record.value(forKey: colAttribute.FieldName) != nil){
                let value = record.value(forKey: colAttribute.FieldName) as! DateTime;
                result += "'" + value.toString(format: Constant.FormatString.DATE_TIME_FORMAT_DB) + "'";
            }
            else{
                result += "'1900-01-01 00:00:00'";
            }
        }
        else if(colAttribute.FieldType == "Optional<Bool>"){
            let value = record.value(forKey: colAttribute.FieldName) as! Bool;
            let sValue = value == true ? "1" : "0";
            result += "'" + sValue + "'";
        }
        else{
            if(record.value(forKey: colAttribute.FieldName) != nil){
                result += "'" + (record.value(forKey: colAttribute.FieldName) as! String) + "'";
            }
            else{
                result += "''";
            }
        }
        return result;
    }
    public func dataListRowToObject(row:FMResultSet?, obj:BaseClass) -> BaseClass?{
        if (row != nil) {
            var obj2:BaseClass = BaseClass();
            while (row?.next())! {
                obj2 = dataRowToObject(row: row, obj: obj)!;
            }
            return obj2;
        }
        return nil;
    }
    public func dataRowToObject(row:FMResultSet?, obj:BaseClass) -> BaseClass?{
        let lstProp = obj.propertyNames();
        for colAttribute in lstProp {
            if(colAttribute.FieldType == "Optional<DateTime>"){
                let temp:String = (row?.string(forColumn: colAttribute.FieldName))!;
                let dt:DateTime = DateTime(sValue: temp);
                obj.setValue(dt, forKey: colAttribute.FieldName)
            }
            else{
                obj.setValue(row?.string(forColumn: colAttribute.FieldName), forKey: colAttribute.FieldName)
            }
        }
        return obj;
    }
}
