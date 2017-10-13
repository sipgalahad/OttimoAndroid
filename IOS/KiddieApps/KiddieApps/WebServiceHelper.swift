//
//  WebServiceHelper.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/27/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import Foundation

class WebServiceHelper : NSObject,XMLParserDelegate{
    var currentElement:String = "";
    var jsonResult:String = "";
    
    public func generateSOAPXMLFile(functionName:String, lstParameter:[Variable]) -> String{
        var result = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<soap:Body>" +
            "<\(functionName) xmlns=\"http://tempuri.org/\">";
        
        for param in lstParameter{
            result += "<\(String(describing: param.Code))>\(String(describing: param.Value))</\(String(describing: param.Code))>";
        }
        result += "</\(functionName)>" +
            "</soap:Body>" +
        "</soap:Envelope>";
        return result;
    }
    
    public func getListObject(methodName:String, filterExpression:String, completionHandler: @escaping (_ result:String) -> Void){
        let appToken:String = Constant.APP_TOKEN;
        
        var lstParameter:Array<Variable> = Array();
        lstParameter.append(Variable(Code : "appToken", Value : appToken));
        lstParameter.append(Variable(Code : "methodName", Value : methodName));
        lstParameter.append(Variable(Code : "filterExpression", Value : "<![CDATA[\(filterExpression)]]>"));
        
        /*let soapMessage = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<soap:Body>" +
            "<GetMobileListObject xmlns=\"http://tempuri.org/\">" +
            "<appToken>\(appToken)</appToken>" +
            "<methodName>\(methodName)</methodName>" +
            "<filterExpression><![CDATA[\(filterExpression)]]></filterExpression>" +
            "</GetMobileListObject>" +
            "</soap:Body>" +
        "</soap:Envelope>";*/
        let soapMessage = generateSOAPXMLFile(functionName: "GetMobileListObject", lstParameter: lstParameter);
        
        let urlString:String = Constant.Url.BRIDGING_SERVER;
        if let url = NSURL(string: urlString) {
            let theRequest = NSMutableURLRequest(url: url as URL)
            theRequest.addValue("text/xml; charset=utf-8", forHTTPHeaderField: "Content-Type")
            theRequest.addValue((soapMessage), forHTTPHeaderField: "Content-Length")
            theRequest.httpMethod = "POST"
            theRequest.httpBody = soapMessage.data(using: String.Encoding.utf8, allowLossyConversion: false)
            URLSession.shared.dataTask(with: theRequest as URLRequest) { (data, response, error) in
                if error == nil {
                    if let data = data, let _ = String(data: data, encoding: String.Encoding.utf8) {
                        let xmlParser = XMLParser(data: data)
                        xmlParser.delegate = self as XMLParserDelegate;
                        xmlParser.shouldResolveExternalEntities = true
                        
                        xmlParser.parse()
                        completionHandler(self.jsonResult);
                    }
                } else {
                    //self.txtMedicalNo.text = error.debugDescription;
                }
                }.resume()
        }
        
    }
    
    public func Login(medicalNo:String, password:String, deviceID:String, deviceName:String, OSVersion:String, appVersion:String, FCMToken:String, completionHandler: @escaping (_ result:String) -> Void){
        let appToken:String = Constant.APP_TOKEN;
        
        var data:String = "<REQUEST><DATA>";
        data += addXMLElement(elementName: "MEDICAL_NO", value: medicalNo);
        data += addXMLElement(elementName: "PASSWORD", value: password);
        data += addXMLElement(elementName: "DEVICE_ID", value: deviceID);
        data += addXMLElement(elementName: "DEVICE_NAME", value: deviceName);
        data += addXMLElement(elementName: "MANUFACTURER_NAME", value: "IPhone");
        data += addXMLElement(elementName: "OS_TYPE", value: Constant.OS_TYPE);
        data += addXMLElement(elementName: "OS_VERSION", value: OSVersion);
        data += addXMLElement(elementName: "SDK_VERSION", value: "1");
        data += addXMLElement(elementName: "APP_VERSION", value: appVersion);
        data += addXMLElement(elementName: "FCM_TOKEN", value: FCMToken);
        data += "</DATA></REQUEST>";

        var lstParameter:Array<Variable> = Array();
        lstParameter.append(Variable(Code : "appToken", Value : appToken));
        lstParameter.append(Variable(Code : "data", Value : "<![CDATA[\(data)]]>"));
        
        let soapMessage = generateSOAPXMLFile(functionName: "Login2", lstParameter: lstParameter);
        
        let urlString:String = Constant.Url.BRIDGING_SERVER;
        if let url = NSURL(string: urlString) {
            let theRequest = NSMutableURLRequest(url: url as URL)
            theRequest.addValue("text/xml; charset=utf-8", forHTTPHeaderField: "Content-Type")
            theRequest.addValue((soapMessage), forHTTPHeaderField: "Content-Length")
            theRequest.httpMethod = "POST"
            theRequest.httpBody = soapMessage.data(using: String.Encoding.utf8, allowLossyConversion: false)
            URLSession.shared.dataTask(with: theRequest as URLRequest) { (data, response, error) in
                if error == nil {
                    if let data = data, let _ = String(data: data, encoding: String.Encoding.utf8) {
                        let xmlParser = XMLParser(data: data)
                        xmlParser.delegate = self as XMLParserDelegate;
                        xmlParser.shouldResolveExternalEntities = true
                        
                        xmlParser.parse()
                        completionHandler(self.jsonResult);
                    }
                } else {
                    //self.txtMedicalNo.text = error.debugDescription;
                }
                }.resume()
        }
    }
    
    
    
    public func SyncPatient(MRN:Int, deviceID:String, patientLastUpdatedDate:String, photoLastUpdatedDate:String, appointmentLastUpdatedDate:String, vaccinationLastUpdatedDate:String, labResultLastUpdatedDate:String, completionHandler: @escaping (_ result:String) -> Void){
        let appToken:String = Constant.APP_TOKEN;
        
        var data:String = "<REQUEST><DATA>";
        data += addXMLElement(elementName: "MRN", value: String(MRN));
        data += addXMLElement(elementName: "DEVICE_ID", value: deviceID);
        data += addXMLElement(elementName: "PATIENT_LASTUPDATEDDATE", value: patientLastUpdatedDate);
        data += addXMLElement(elementName: "PHOTO_LASTUPDATEDDATE", value: photoLastUpdatedDate);
        data += addXMLElement(elementName: "APPOINTMENT_LASTUPDATEDDATE", value: appointmentLastUpdatedDate);
        data += addXMLElement(elementName: "VACCINATION_LASTUPDATEDDATE", value: vaccinationLastUpdatedDate);
        data += addXMLElement(elementName: "LAB_RESULT_LASTUPDATEDDATE", value: labResultLastUpdatedDate);
        data += "</DATA></REQUEST>";
        
        var lstParameter:Array<Variable> = Array();
        lstParameter.append(Variable(Code : "appToken", Value : appToken));
        lstParameter.append(Variable(Code : "data", Value : "<![CDATA[\(data)]]>"));
        
        let soapMessage = generateSOAPXMLFile(functionName: "SyncPatient2", lstParameter: lstParameter);
        
        let urlString:String = Constant.Url.BRIDGING_SERVER;
        if let url = NSURL(string: urlString) {
            let theRequest = NSMutableURLRequest(url: url as URL)
            theRequest.addValue("text/xml; charset=utf-8", forHTTPHeaderField: "Content-Type")
            theRequest.addValue((soapMessage), forHTTPHeaderField: "Content-Length")
            theRequest.httpMethod = "POST"
            theRequest.httpBody = soapMessage.data(using: String.Encoding.utf8, allowLossyConversion: false)
            URLSession.shared.dataTask(with: theRequest as URLRequest) { (data, response, error) in
                if error == nil {
                    if let data = data, let _ = String(data: data, encoding: String.Encoding.utf8) {
                        let xmlParser = XMLParser(data: data)
                        xmlParser.delegate = self as XMLParserDelegate;
                        xmlParser.shouldResolveExternalEntities = true
                        
                        xmlParser.parse()
                        completionHandler(self.jsonResult);
                    }
                } else {
                    //self.txtMedicalNo.text = error.debugDescription;
                }
                }.resume()
        }
    }
    
    public func SyncLabResult(MRN:Int, labResultLastUpdatedDate:String, completionHandler: @escaping (_ result:String) -> Void){
        let appToken:String = Constant.APP_TOKEN;
        
        var data:String = "<REQUEST><DATA>";
        data += addXMLElement(elementName: "MRN", value: String(MRN));
        data += addXMLElement(elementName: "LAB_RESULT_LASTUPDATEDDATE", value: labResultLastUpdatedDate);
        data += "</DATA></REQUEST>";
        
        var lstParameter:Array<Variable> = Array();
        lstParameter.append(Variable(Code : "appToken", Value : appToken));
        lstParameter.append(Variable(Code : "data", Value : "<![CDATA[\(data)]]>"));
        
        let soapMessage = generateSOAPXMLFile(functionName: "SyncLabResult", lstParameter: lstParameter);
        
        let urlString:String = Constant.Url.BRIDGING_SERVER;
        if let url = NSURL(string: urlString) {
            let theRequest = NSMutableURLRequest(url: url as URL)
            theRequest.addValue("text/xml; charset=utf-8", forHTTPHeaderField: "Content-Type")
            theRequest.addValue((soapMessage), forHTTPHeaderField: "Content-Length")
            theRequest.httpMethod = "POST"
            theRequest.httpBody = soapMessage.data(using: String.Encoding.utf8, allowLossyConversion: false)
            URLSession.shared.dataTask(with: theRequest as URLRequest) { (data, response, error) in
                if error == nil {
                    if let data = data, let _ = String(data: data, encoding: String.Encoding.utf8) {
                        let xmlParser = XMLParser(data: data)
                        xmlParser.delegate = self as XMLParserDelegate;
                        xmlParser.shouldResolveExternalEntities = true
                        
                        xmlParser.parse()
                        completionHandler(self.jsonResult);
                    }
                } else {
                    //self.txtMedicalNo.text = error.debugDescription;
                }
                }.resume()
        }
    }
    
    public func SyncLabResultPerID(ID:Int, completionHandler: @escaping (_ result:String) -> Void){
        let appToken:String = Constant.APP_TOKEN;
        
        var data:String = "<REQUEST><DATA>";
        data += addXMLElement(elementName: "LAB_RESULT_ID", value: String(ID));
        data += "</DATA></REQUEST>";
        
        var lstParameter:Array<Variable> = Array();
        lstParameter.append(Variable(Code : "appToken", Value : appToken));
        lstParameter.append(Variable(Code : "data", Value : "<![CDATA[\(data)]]>"));
        
        let soapMessage = generateSOAPXMLFile(functionName: "SyncLabResultPerID", lstParameter: lstParameter);
        
        let urlString:String = Constant.Url.BRIDGING_SERVER;
        if let url = NSURL(string: urlString) {
            let theRequest = NSMutableURLRequest(url: url as URL)
            theRequest.addValue("text/xml; charset=utf-8", forHTTPHeaderField: "Content-Type")
            theRequest.addValue((soapMessage), forHTTPHeaderField: "Content-Length")
            theRequest.httpMethod = "POST"
            theRequest.httpBody = soapMessage.data(using: String.Encoding.utf8, allowLossyConversion: false)
            URLSession.shared.dataTask(with: theRequest as URLRequest) { (data, response, error) in
                if error == nil {
                    if let data = data, let _ = String(data: data, encoding: String.Encoding.utf8) {
                        let xmlParser = XMLParser(data: data)
                        xmlParser.delegate = self as XMLParserDelegate;
                        xmlParser.shouldResolveExternalEntities = true
                        
                        xmlParser.parse()
                        completionHandler(self.jsonResult);
                    }
                } else {
                    //self.txtMedicalNo.text = error.debugDescription;
                }
                }.resume()
        }
    }


    
    public func ReloadDataAfterUpdateApps(listMRN:String, deviceID:String, completionHandler: @escaping (_ result:String) -> Void){
        let appToken:String = Constant.APP_TOKEN;
        
        var data:String = "<REQUEST><DATA>";
        data += addXMLElement(elementName: "LIST_MRN", value: listMRN);
        data += addXMLElement(elementName: "DEVICE_ID", value: deviceID);
        data += "</DATA></REQUEST>";
        
        var lstParameter:Array<Variable> = Array();
        lstParameter.append(Variable(Code : "appToken", Value : appToken));
        lstParameter.append(Variable(Code : "data", Value : "<![CDATA[\(data)]]>"));
        
        let soapMessage = generateSOAPXMLFile(functionName: "ReloadDataAfterUpdateApps", lstParameter: lstParameter);
        
        let urlString:String = Constant.Url.BRIDGING_SERVER;
        if let url = NSURL(string: urlString) {
            let theRequest = NSMutableURLRequest(url: url as URL)
            theRequest.addValue("text/xml; charset=utf-8", forHTTPHeaderField: "Content-Type")
            theRequest.addValue((soapMessage), forHTTPHeaderField: "Content-Length")
            theRequest.httpMethod = "POST"
            theRequest.httpBody = soapMessage.data(using: String.Encoding.utf8, allowLossyConversion: false)
            URLSession.shared.dataTask(with: theRequest as URLRequest) { (data, response, error) in
                if error == nil {
                    if let data = data, let _ = String(data: data, encoding: String.Encoding.utf8) {
                        let xmlParser = XMLParser(data: data)
                        xmlParser.delegate = self as XMLParserDelegate;
                        xmlParser.shouldResolveExternalEntities = true
                        
                        xmlParser.parse()
                        completionHandler(self.jsonResult);
                    }
                } else {
                    //self.txtMedicalNo.text = error.debugDescription;
                }
                }.resume()
        }
    }


    public func ChangePassword(MRN:Int, oldPassword:String, newPassword:String, completionHandler: @escaping (_ result:String) -> Void){
        let appToken:String = Constant.APP_TOKEN;
        
        var data:String = "<REQUEST><DATA>";
        data += addXMLElement(elementName: "MRN", value: String(MRN));
        data += addXMLElement(elementName: "OLD_PASSWORD", value: oldPassword);
        data += addXMLElement(elementName: "NEW_PASSWORD", value: newPassword);
        data += "</DATA></REQUEST>";
        
        var lstParameter:Array<Variable> = Array();
        lstParameter.append(Variable(Code : "appToken", Value : appToken));
        lstParameter.append(Variable(Code : "data", Value : "<![CDATA[\(data)]]>"));
        
        let soapMessage = generateSOAPXMLFile(functionName: "ChangePassword2", lstParameter: lstParameter);
        
        let urlString:String = Constant.Url.BRIDGING_SERVER;
        if let url = NSURL(string: urlString) {
            let theRequest = NSMutableURLRequest(url: url as URL)
            theRequest.addValue("text/xml; charset=utf-8", forHTTPHeaderField: "Content-Type")
            theRequest.addValue((soapMessage), forHTTPHeaderField: "Content-Length")
            theRequest.httpMethod = "POST"
            theRequest.httpBody = soapMessage.data(using: String.Encoding.utf8, allowLossyConversion: false)
            URLSession.shared.dataTask(with: theRequest as URLRequest) { (data, response, error) in
                if error == nil {
                    if let data = data, let _ = String(data: data, encoding: String.Encoding.utf8) {
                        let xmlParser = XMLParser(data: data)
                        xmlParser.delegate = self as XMLParserDelegate;
                        xmlParser.shouldResolveExternalEntities = true
                        
                        xmlParser.parse()
                        completionHandler(self.jsonResult);
                    }
                } else {
                    //self.txtMedicalNo.text = error.debugDescription;
                }
                }.resume()
        }
    }
    
    public func InsertErrorFeedback(deviceID:String, errorMessage:String, completionHandler: @escaping (_ result:String) -> Void){
        let appToken:String = Constant.APP_TOKEN;
        
        var data:String = "<REQUEST><DATA>";
        data += addXMLElement(elementName: "DEVICE_ID", value: deviceID);
        data += addXMLElement(elementName: "ERROR_MESSAGE", value: errorMessage);
        
        data += "</DATA></REQUEST>";
        
        var lstParameter:Array<Variable> = Array();
        lstParameter.append(Variable(Code : "appToken", Value : appToken));
        lstParameter.append(Variable(Code : "data", Value : "<![CDATA[\(data)]]>"));
        
        let soapMessage = generateSOAPXMLFile(functionName: "InsertErrorFeedback2", lstParameter: lstParameter);
        
        let urlString:String = Constant.Url.BRIDGING_SERVER;
        if let url = NSURL(string: urlString) {
            let theRequest = NSMutableURLRequest(url: url as URL)
            theRequest.addValue("text/xml; charset=utf-8", forHTTPHeaderField: "Content-Type")
            theRequest.addValue((soapMessage), forHTTPHeaderField: "Content-Length")
            theRequest.httpMethod = "POST"
            theRequest.httpBody = soapMessage.data(using: String.Encoding.utf8, allowLossyConversion: false)
            URLSession.shared.dataTask(with: theRequest as URLRequest) { (data, response, error) in
                if error == nil {
                    if let data = data, let _ = String(data: data, encoding: String.Encoding.utf8) {
                        let xmlParser = XMLParser(data: data)
                        xmlParser.delegate = self as XMLParserDelegate;
                        xmlParser.shouldResolveExternalEntities = true
                        
                        xmlParser.parse()
                        completionHandler(self.jsonResult);
                    }
                } else {
                    //self.txtMedicalNo.text = error.debugDescription;
                }
                }.resume()
        }
    }
    
    public func RequestPassword(medicalNo:String, emailAddress:String, completionHandler: @escaping (_ result:String) -> Void){
        let appToken:String = Constant.APP_TOKEN;
        
        var data:String = "<REQUEST><DATA>";
        data += addXMLElement(elementName: "MEDICAL_NO", value: medicalNo);
        data += addXMLElement(elementName: "EMAIL_ADDRESS", value: emailAddress);
        data += "</DATA></REQUEST>";
        
        var lstParameter:Array<Variable> = Array();
        lstParameter.append(Variable(Code : "appToken", Value : appToken));
        lstParameter.append(Variable(Code : "data", Value : "<![CDATA[\(data)]]>"));
        
        let soapMessage = generateSOAPXMLFile(functionName: "RequestPassword2", lstParameter: lstParameter);
        
        let urlString:String = Constant.Url.BRIDGING_SERVER;
        if let url = NSURL(string: urlString) {
            let theRequest = NSMutableURLRequest(url: url as URL)
            theRequest.addValue("text/xml; charset=utf-8", forHTTPHeaderField: "Content-Type")
            theRequest.addValue((soapMessage), forHTTPHeaderField: "Content-Length")
            theRequest.httpMethod = "POST"
            theRequest.httpBody = soapMessage.data(using: String.Encoding.utf8, allowLossyConversion: false)
            URLSession.shared.dataTask(with: theRequest as URLRequest) { (data, response, error) in
                if error == nil {
                    if let data = data, let _ = String(data: data, encoding: String.Encoding.utf8) {
                        let xmlParser = XMLParser(data: data)
                        xmlParser.delegate = self as XMLParserDelegate;
                        xmlParser.shouldResolveExternalEntities = true
                        
                        xmlParser.parse()
                        completionHandler(self.jsonResult);
                    }
                } else {
                    //self.txtMedicalNo.text = error.debugDescription;
                }
                }.resume()
        }
    }
    
    public func PostAppointmentAnswer(appointmentID:Int, deviceID:String, GCAppointmentStatus:String, completionHandler: @escaping (_ result:String) -> Void){
        let appToken:String = Constant.APP_TOKEN;
        
        var data:String = "<REQUEST><DATA>";
        data += addXMLElement(elementName: "APPOINTMENT_ID", value: String(appointmentID));
        data += addXMLElement(elementName: "DEVICE_ID", value: deviceID);
        data += addXMLElement(elementName: "GC_APPOINTMENT_STATUS", value: GCAppointmentStatus);

        data += "</DATA></REQUEST>";
        
        var lstParameter:Array<Variable> = Array();
        lstParameter.append(Variable(Code : "appToken", Value : appToken));
        lstParameter.append(Variable(Code : "data", Value : "<![CDATA[\(data)]]>"));
        
        let soapMessage = generateSOAPXMLFile(functionName: "PostAppointmentAnswer2", lstParameter: lstParameter);
        
        let urlString:String = Constant.Url.BRIDGING_SERVER;
        if let url = NSURL(string: urlString) {
            let theRequest = NSMutableURLRequest(url: url as URL)
            theRequest.addValue("text/xml; charset=utf-8", forHTTPHeaderField: "Content-Type")
            theRequest.addValue((soapMessage), forHTTPHeaderField: "Content-Length")
            theRequest.httpMethod = "POST"
            theRequest.httpBody = soapMessage.data(using: String.Encoding.utf8, allowLossyConversion: false)
            URLSession.shared.dataTask(with: theRequest as URLRequest) { (data, response, error) in
                if error == nil {
                    if let data = data, let _ = String(data: data, encoding: String.Encoding.utf8) {
                        let xmlParser = XMLParser(data: data)
                        xmlParser.delegate = self as XMLParserDelegate;
                        xmlParser.shouldResolveExternalEntities = true
                        
                        xmlParser.parse()
                        completionHandler(self.jsonResult);
                    }
                } else {
                    //self.txtMedicalNo.text = error.debugDescription;
                }
                }.resume()
        }
    }
    
    private func addXMLElement (elementName:String, value:String) -> String{
        return "<\(elementName)>\(value)</\(elementName)>";
    }
    
    func parser(_ parser: XMLParser, didStartElement elementName: String, namespaceURI: String?, qualifiedName qName: String?, attributes attributeDict: [String : String]) {
        currentElement=elementName;    }
    
    func parser(_ parser: XMLParser, didEndElement elementName: String, namespaceURI: String?, qualifiedName qName: String?) {
        currentElement="";    }
    
    func parser(_ parser: XMLParser, foundCharacters string: String) {
        if(currentElement == "GetMobileListObjectResult" || currentElement == "GetAndroidAppVersion2Result" || currentElement == "Login2Result" || currentElement == "ChangePassword2Result" || currentElement == "RequestPassword2Result" || currentElement == "PostAppointmentAnswer2Result" || currentElement == "InsertErrorFeedback2Result" || currentElement == "ReloadDataAfterUpdateAppsResult" || currentElement == "SyncPatient2Result" || currentElement == "SyncLabResultResult" || currentElement == "SyncLabResultPerIDResult"){
            jsonResult += string;
        }
    }
    
    func parser(_ parser: XMLParser, parseErrorOccurred parseError: Error) {
        print("failure error: ", parseError)
    }
    
    public static func convertToDictionary(text: String) -> [String: Any]? {
        if let data = text.data(using: .utf8) {
            do {
                return try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            } catch {
                print(error.localizedDescription)
            }
        }
        return nil
    }
    
    public static func JSONObjectToObject(row:[String:AnyObject], obj:BaseClass) -> BaseClass {
        /*let AppointmentID:NSNumber! = row["AppointmentID"] as! NSNumber!;
        let ServiceUnitName:NSString! = row["ServiceUnitName"] as! NSString!;
        //obj.AppointmentID = AppointmentID;
        obj.setValue("Test", forKey: "ServiceUnitName")
        obj.setValue("Test2", forKey: "ParamediName")
        obj.setValue(ServiceUnitName, forKey: "ServiceUnitName")
        obj.setValue(AppointmentID, forKey: "AppointmentID")
        obj.setValue(AppointmentID, forKey: "MRN")

*/
        let lstProp = obj.propertyNames();
        for colAttribute in lstProp {
            if(row[colAttribute.FieldName] != nil){
                if(colAttribute.FieldType == "Optional<NSNumber>"){
                    obj.setValue(row[colAttribute.FieldName] as! NSNumber!, forKey: colAttribute.FieldName)
                }
                else if(colAttribute.FieldType == "Optional<DateTime>"){
                    obj.setValue(JSONDateToDateTime(jsonDate: (row[colAttribute.FieldName] as! NSString!) as String), forKey: colAttribute.FieldName)
                }
                else if(colAttribute.FieldType == "Optional<Bool>"){
                    obj.setValue(row[colAttribute.FieldName] as! Bool!, forKey: colAttribute.FieldName)
                }
                else{
                    obj.setValue(row[colAttribute.FieldName] as! NSString!, forKey: colAttribute.FieldName)
                }
            }
        }
        return obj;

    }
    
    public static func JSONDateToDateTime(jsonDate:String) -> DateTime{
        if(jsonDate != ""){
            let theDate = Date(jsonDate: jsonDate)
            let dt:DateTime = DateTime();
            dt.Year = Int((theDate?.toString(dateFormat: "yyyy"))!)!;
            dt.Month = Int((theDate?.toString(dateFormat: "MM"))!)!;
            dt.Day = Int((theDate?.toString(dateFormat: "dd"))!)!;
            dt.Hour = Int((theDate?.toString(dateFormat: "HH"))!)!;
            dt.Minute = Int((theDate?.toString(dateFormat: "mm"))!)!;
            dt.Second = Int((theDate?.toString(dateFormat: "ss"))!)!;
            return dt;
        }
        return DateTime();
    }
}

extension Date
{
    func toString( dateFormat format  : String ) -> String
    {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: self)
    }
    
}

extension Date {
    init?(jsonDate: String) {
        
        let prefix = "/Date("
        let suffix = ")/"
        
        // Check for correct format:
        guard jsonDate.hasPrefix(prefix) && jsonDate.hasSuffix(suffix) else { return nil }
        
        // Extract the number as a string:
        let from = jsonDate.index(jsonDate.startIndex, offsetBy: prefix.characters.count)
        let to = jsonDate.index(jsonDate.endIndex, offsetBy: -suffix.characters.count)
        
        // Convert milliseconds to double
        guard let milliSeconds = Double(jsonDate[from ..< to]) else { return nil }
        
        // Create NSDate with this UNIX timestamp
        self.init(timeIntervalSince1970: milliSeconds/1000.0)
    }
}
