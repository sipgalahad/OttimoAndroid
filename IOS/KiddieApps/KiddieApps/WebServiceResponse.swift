//
//  WebServiceResponse.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/28/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import Foundation

public class WebServiceResponse{
    public var timeStamp:DateTime = DateTime();
    public var returnObj:Array<BaseClass> = [];
}

public class WebServiceResponsePatient{
    public var timeStamp:DateTime = DateTime();
    public var returnObjPatient:Array<Patient> = [];
    public var returnObjAppointment:Array<Appointment> = [];
    public var returnObjVaccination:Array<VaccinationShotDt> = [];
    public var returnObjLabResultHd:Array<LaboratoryResultHd> = [];
    public var returnObjLabResultDt:Array<LaboratoryResultDt> = [];
    public var returnObjAnnouncement:Array<Announcement> = [];
    public var returnObjImg:String = "";
}

public class WebServiceResponsePatient2{
    public var timeStamp:DateTime = DateTime();
    public var returnObjPatient:Array<Patient> = [];
    public var returnObjAppointment:Array<Appointment> = [];
    public var returnObjVaccination:Array<VaccinationShotDt> = [];
    public var returnObjLabResultHd:Array<LaboratoryResultHd> = [];
    public var returnObjLabResultDt:Array<LaboratoryResultDt> = [];
    public var returnObjAnnouncement:Array<Announcement> = [];
    public var returnObjImg:Array<String> = [];
}
