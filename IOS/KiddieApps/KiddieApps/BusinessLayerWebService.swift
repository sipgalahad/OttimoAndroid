//
//  BusinessLayerWebService.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/28/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import Foundation

public class BusinessLayerWebService{
    // MARK: Appointment    
    public static func getAppointmentList(filterExpression:String, completionHandler: @escaping (_ result:WebServiceResponse) -> Void){
        WebServiceHelper().getListObject(methodName: "GetvMobileAppointmentPerPatientList", filterExpression: filterExpression, completionHandler: { (result) -> Void in
            //self.txtMedicalNo.text = result;
            let retval:WebServiceResponse = WebServiceResponse();
            
            let dict = WebServiceHelper.convertToDictionary(text: result)
            retval.timeStamp = WebServiceHelper.JSONDateToDateTime(jsonDate: dict?["Timestamp"] as! String);
            let obj = dict?["ReturnObj"] as! NSArray
            
            for tmp in obj{
                let entity:Appointment = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: Appointment()) as! Appointment
                retval.returnObj.append(entity);
            }
            completionHandler(retval);
        });
    }
}
