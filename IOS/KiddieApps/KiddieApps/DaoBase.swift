//
//  DaoBase.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/21/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import Foundation

let sharedInstance = DaoBase()

class DaoBase: NSObject {
    
    var database: FMDatabase? = nil
    
    class func getInstance() -> DaoBase
    {
        if(sharedInstance.database == nil)
        {
            sharedInstance.database = FMDatabase(path: Util.getPath(fileName: "OttimoPatient.db"))
        }
        return sharedInstance
    }
    public func executeNonQuery(query:String) -> Bool {
        sharedInstance.database!.open()
        let isInserted = sharedInstance.database!.executeUpdate(query, withArgumentsIn: []);
        sharedInstance.database!.close()
        return isInserted
    }
    public func getDataRow(query:String) -> FMResultSet {
        let resultSet: FMResultSet! = sharedInstance.database!.executeQuery(query, withArgumentsIn: [])
        return resultSet;
    }

}
