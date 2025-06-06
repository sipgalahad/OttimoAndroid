//
//  Constant.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/21/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import Foundation

public class Constant {
    public static let APP_VERSION = "1.0";
    public static let DB_VERSION = "2";
    
    public class SharedPreference{
        public static let LIST_MRN = "2";
        public static let ANNOUNCEMENT_LASTUPDATEDDATE = "2018-01-01 00:00:00";
    }
    public static let SHORT_MONTHS = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
    public static let MONTHS = [ "January", "February", "March", "April", "May", "June", "July",
    "August", "September", "October", "November", "December" ];
    public class Url
    {
        public static let APP_DATA_URL = "http://192.168.0.102/appdata/ottimo";
        public static let BRIDGING_SERVER = "http://114.199.103.10:8080/Kiddielogic/BridgingServer/Program/Mobile/MobileService.asmx";
        //public static let BRIDGING_SERVER = "http://192.168.0.102/research/Ottimov2.0/BridgingServer/Program/Mobile/MobileService.asmx";
    }
    public static let APP_TOKEN = "40BD001563085FC35165329EA1FF5C5ECBDBBEEF";
    public static let OS_TYPE = "OT038^002";
    public class FormatString
    {
        public static let DATE_FORMAT = "dd-MMM-yyyy";
        public static let DATE_TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss";
        public static let DATE_FORMAT_DB = "yyyy-MM-dd";
        public static let DATE_TIME_FORMAT_DB = "yyyy-MM-dd HH:mm:ss";
    }
    public class Session
    {
        public static let DB_VERSION = "DBVersion";
        public static let APP_VERSION = "AppVersion";
    }

    public class ConstantDate
    {
        public static let DEFAULT_NULL = "01-01-1900";
    }
    public class Sex {
        public static let MALE = "0003^M";
        public static let FEMALE = "0003^F";
    }
    public class AnnouncementType
    {
        public static let ANNOUNCEMENT = "OT040^001";
        public static let NEWS = "OT040^002";
        public static let ADVERTISEMENT = "OT040^003";
    }

    public class AppointmentStatus
    {
        public static let OPEN = "0278^001";
        public static let SEND_CONFIRMATION = "0278^002";
        public static let CONFIRMED = "0278^003";
        public static let CHECK_IN = "0278^004";
        public static let CANCELLED = "0278^005";
        public static let VOID = "0278^999";
    }
    public class LabResultSummary
    {
        public static let LOW = "OT005^001";
        public static let NORMAL = "OT005^002";
        public static let HIGH = "OT005^003";
        public static let ABNORMAL = "OT005^004";
    }
}
