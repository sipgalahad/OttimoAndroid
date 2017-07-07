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
    
    public func getListObject(methodName:String, filterExpression:String, completionHandler: @escaping (_ result:String) -> Void){
        let appToken:String = Constant.APP_TOKEN;
        
        let soapMessage = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<soap:Body>" +
            "<GetMobileListObject xmlns=\"http://tempuri.org/\">" +
            "<appToken>\(appToken)</appToken>" +
            "<methodName>\(methodName)</methodName>" +
            "<filterExpression><![CDATA[\(filterExpression)]]></filterExpression>" +
            "</GetMobileListObject>" +
            "</soap:Body>" +
        "</soap:Envelope>";
        /*let soapMessage = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
         "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
         "<soap:Body>" +
         "<GetAndroidAppVersion2 xmlns=\"http://tempuri.org/\">" +
         "<appToken>\(appToken)</appToken>" +
         "</GetAndroidAppVersion2>" +
         "</soap:Body>" +
         "</soap:Envelope>";*/
        
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
    
    func parser(_ parser: XMLParser, didStartElement elementName: String, namespaceURI: String?, qualifiedName qName: String?, attributes attributeDict: [String : String]) {
        currentElement=elementName;    }
    
    func parser(_ parser: XMLParser, didEndElement elementName: String, namespaceURI: String?, qualifiedName qName: String?) {
        currentElement="";    }
    
    func parser(_ parser: XMLParser, foundCharacters string: String) {
        if(currentElement == "GetMobileListObjectResult" || currentElement == "GetAndroidAppVersion2Result" ){
            jsonResult = string;
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
        return obj;

    }
    
    public static func JSONDateToDateTime(jsonDate:String) -> DateTime{
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
