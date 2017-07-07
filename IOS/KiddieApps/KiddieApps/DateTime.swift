//
//  DateTime.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/21/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import Foundation

public class DateTime : NSObject {
    var Year:Int = 1900;
    var Month:Int = 1;
    var Day:Int = 1;
    var Hour:Int = 0;
    var Minute:Int = 0;
    var Second:Int = 0;
    
    public override init(){
        super.init();
        self.setDefaultDateTime();
    }
    private func setDefaultDateTime(){
        self.Year = 1900;
        self.Month = 1;
        self.Day = 1;
        self.Hour = 0;
        self.Minute = 0;
        self.Second = 0;
    
    }
    /** Assign value from String to DateTime
     *
     * @param value : String yyyy-MM-dd or yyyy-MM-dd HH:mm:ss
     */
    public init(sValue:String){
        super.init();
        if(sValue != ""){
            self.Year = Int(sValue.substring(with: 0..<4))!;
            self.Month = Int(sValue.substring(with: 5..<7))!;
            self.Day = Int(sValue.substring(with: 8..<10))!;
            if(sValue.characters.count > 10){
                self.Hour = Int(sValue.substring(with: 11..<13))!;
                self.Minute = Int(sValue.substring(with: 14..<16))!;
                self.Second = Int(sValue.substring(with: 17..<19))!;
            }
        }
        else{
            self.setDefaultDateTime();
        }
    }
    public init(year:Int, month:Int, day:Int, hour:Int, minute:Int, second:Int){
        self.Year = year;
        self.Month = month;
        self.Day = day;
        self.Hour = hour;
        self.Minute = minute;
        self.Second = second;
    }
    public init(value:DateTime){
        super.init();
        self.setValue(value: value);
    }
    public func setValue(value:DateTime){
        self.Year = value.Year;
        self.Month = value.Month;
        self.Day = value.Day;
        self.Hour = value.Hour;
        self.Minute = value.Minute;
        self.Second = value.Second;
    }
    public static func now() -> DateTime{
        let date = Date()
        let calendar = Calendar.current
        let components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: date)
        return DateTime(year: components.year!, month: components.month!, day: components.day!, hour: components.hour!, minute: components.minute!, second: components.second!);
    }
    public static func tomorrow() -> DateTime{
        let date = Date()
        _ = Calendar.current.date(byAdding: .day, value: 1, to: date)
        let calendar = Calendar.current
        let components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: date)
        return DateTime(year: components.year!, month: components.month!, day: components.day!, hour: components.hour!, minute: components.minute!, second: components.second!);
    }
    /** Print DateTime Value To String
     *
     * @param format : <br>yyyy : Year (2010 / 2009 / ....)
     * 			  	   <br>yy : Year (09 / 08 / ....)
     * 			  	   <br>MMMM : Month (January / February / ....)
     * 				   <br>MMM : Month (Jan / Feb / ...)
     * 				   <br>MM : Month (01 / 02 / ..)
     * 				   <br>dd : Day ( 01 / 02 / ...)
     * 				   <br>HH : Hour ( 01 / 02 / ...)
     * 				   <br>mm : Minute ( 01 / 02 / ...)
     * 				   <br>ss : Second ( 01 / 02 / ...)
     * @return String DateTime
     */
    public func toString(format:String) -> String{
        var result = format;
        result = result.replacingOccurrences(of: "yyyy", with: String(self.Year));
        result = result.replacingOccurrences(of: "yy", with: String(format: "%02d", self.Year % 100));
        result = result.replacingOccurrences(of: "MMMM", with: Constant.MONTHS[self.Month - 1]);
        result = result.replacingOccurrences(of: "MMM", with: Constant.SHORT_MONTHS[self.Month - 1]);
        result = result.replacingOccurrences(of: "MM", with: String(format: "%02d", self.Month));
        result = result.replacingOccurrences(of: "dd", with: String(format: "%02d", self.Day));
    
        result = result.replacingOccurrences(of: "HH", with: String(format: "%02d", self.Hour));
        result = result.replacingOccurrences(of: "mm", with: String(format: "%02d", self.Minute));
        result = result.replacingOccurrences(of: "ss", with: String(format: "%02d", self.Second));
    
        return result;
    }
}
