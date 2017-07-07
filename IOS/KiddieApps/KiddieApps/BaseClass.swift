//
//  BaseClass.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/21/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import Foundation

public struct ClassProperties{
    var IsPrimaryKey:Bool
    var FieldName:String
    var FieldType:String
    var IsNullable:Bool
}

public class BaseClass : NSObject{
    func getTypeOfProperty(name:String)->String?
    {
        let type: Mirror = Mirror(reflecting:self)
        
        for child in type.children {
            if child.label! == name
            {
                return String(describing: type(of: child.value))
            }
        }
        return nil
    }
    
    
    func propertyNames() -> [ClassProperties] {
        let lstName = Mirror(reflecting: self).children.flatMap { $0.label }
        var result:Array<ClassProperties> = Array();
        let lstKey = getPrimaryKey();
        let lstNullable = getNullableColumn();
        
        for name in lstName {
            let type = getTypeOfProperty(name: name);
            var newElement:ClassProperties = ClassProperties(IsPrimaryKey: false, FieldName: "", FieldType: "", IsNullable: false);
            newElement.IsPrimaryKey = false;
            if lstKey.contains(name) {
                newElement.IsPrimaryKey = true;
            }
            newElement.IsNullable = false;
            if lstNullable.contains(name) {
                newElement.IsNullable = true;
            }
            
            newElement.FieldName = name;
            newElement.FieldType = type!;
            result.append(newElement);
        }
        return result;
        
    }
    
    func getPrimaryKey() -> [String]{ return []; }
    func getNullableColumn() -> [String]{ return []; }
}

