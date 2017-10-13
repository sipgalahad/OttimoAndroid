//
//  MessageCenterViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/8/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class MessageCenterViewController: BasePatientTableViewController {

    var lstAppointment:[vAppointment] = [];
    override func viewDidLoad() {
        super.viewDidLoad()
        let dtNow = DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB)
        lstAppointment = BusinessLayer.getvAppointmentList(filterExpression: "(GCAppointmentStatus != '\(Constant.AppointmentStatus.VOID)' AND ('\(dtNow)' BETWEEN ReminderDate AND StartDate)) OR (GCAppointmentStatus = '\(Constant.AppointmentStatus.CHECK_IN)' AND StartDate >= '\(dtNow)')");
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        // Return the number of sections.
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // Return the number of rows in the section.
        return lstAppointment.count;
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! MessageCenterViewCell
        
        let appointment:vAppointment = lstAppointment[indexPath.row];
        cell.lblMessage.text = "Mengingatkan \(String(describing: appointment.FullName!)) terjadwal \(String(describing: appointment.VisitTypeName!)) ke \(String(describing: appointment.ParamedicName!)) tgl \(appointment.StartDate!.toString(format: Constant.FormatString.DATE_FORMAT)) jam \(String(describing: appointment.cfStartTime!)) di KiddieCare. Jika setuju tekan tombol 'Confirm'. Jika tidak menjawab dianggap batal";
        
        if(appointment.GCAppointmentStatus != Constant.AppointmentStatus.CHECK_IN){
            cell.btnConfirm.isHidden = true;
            cell.btnCancel.isHidden = true;
        }
        else {
            cell.btnConfirm.isHidden = false;
            cell.btnCancel.isHidden = false;
        }
    
        cell.btnConfirm.tag = indexPath.row;
        cell.btnCancel.tag = indexPath.row;
        cell.btnConfirm.addTarget(self, action: #selector(self.onBtnConfirmClick), for: .touchUpInside)
        cell.btnCancel.addTarget(self, action: #selector(self.onBtnCancelClick), for: .touchUpInside)
        cell.btnCall.addTarget(self, action: #selector(self.onBtnCallClick), for: .touchUpInside)
        return cell;
    }
    
    func onBtnConfirmClick(sender:UIButton){
        let buttonRow = sender.tag;
        let entity:vAppointment = lstAppointment[buttonRow];
        self.showLoadingPanel();
        postAppointmentAnswer(appointmentID: entity.AppointmentID as! Int, GCAppointmentStatus: Constant.AppointmentStatus.CONFIRMED, completionHandler: { (result) -> Void in
            if(result == "1"){
                let appointment:Appointment = BusinessLayer.getAppointment(AppointmentID: entity.AppointmentID as! Int)!;
                appointment.GCAppointmentStatus = Constant.AppointmentStatus.CONFIRMED;
                _ = BusinessLayer.updateAppointment(record: appointment);
                
                let dtNow = DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB)
                self.lstAppointment = BusinessLayer.getvAppointmentList(filterExpression: "(GCAppointmentStatus != '\(Constant.AppointmentStatus.VOID)' AND ('\(dtNow)' BETWEEN ReminderDate AND StartDate)) OR (GCAppointmentStatus = '\(Constant.AppointmentStatus.CHECK_IN)' AND StartDate >= '\(dtNow)')");
                DispatchQueue.main.async() {
                    self.hideLoadingPanel();
                    displayMyAlertMessage(ctrl: self, userMessage: "Konfirmasi Perjanjian Berhasil Dilakukan.");
                    self.tableView.reloadData();
                }
            }
            else{
                DispatchQueue.main.async() {
                    self.hideLoadingPanel();
                    displayMyAlertMessage(ctrl: self, userMessage: "Konfirmasi Perjanjian Gagal. Harap Periksa Koneksi Internet Anda.");
                }
            }
        });
    }
    
    func onBtnCancelClick(sender:UIButton){
        let buttonRow = sender.tag;
        let entity:vAppointment = lstAppointment[buttonRow];
        self.showLoadingPanel();
        postAppointmentAnswer(appointmentID: entity.AppointmentID as! Int, GCAppointmentStatus: Constant.AppointmentStatus.CANCELLED, completionHandler: { (result) -> Void in
            if(result == "1"){
                let appointment:Appointment = BusinessLayer.getAppointment(AppointmentID: entity.AppointmentID as! Int)!;
                appointment.GCAppointmentStatus = Constant.AppointmentStatus.CANCELLED;
                _ = BusinessLayer.updateAppointment(record: appointment);
                
                let dtNow = DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB)
                self.lstAppointment = BusinessLayer.getvAppointmentList(filterExpression: "(GCAppointmentStatus != '\(Constant.AppointmentStatus.VOID)' AND ('\(dtNow)' BETWEEN ReminderDate AND StartDate)) OR (GCAppointmentStatus = '\(Constant.AppointmentStatus.CHECK_IN)' AND StartDate >= '\(dtNow)')");
                DispatchQueue.main.async() {
                    self.hideLoadingPanel();
                    displayMyAlertMessage(ctrl: self, userMessage: "Pembatalan Perjanjian Berhasil Dilakukan.");
                    self.tableView.reloadData();
                }
            }
            else{
                DispatchQueue.main.async() {
                    self.hideLoadingPanel();
                    displayMyAlertMessage(ctrl: self, userMessage: "Pembatalan Perjanjian Gagal. Harap Periksa Koneksi Internet Anda.");
                }
            }
        });
    }
    
    func onBtnCallClick(sender:UIButton){
        if let url = NSURL(string: "tel//+62216452121"), UIApplication.shared.canOpenURL(url as URL) {
            if #available(iOS 10, *){
                UIApplication.shared.open(url as URL);
            }
            else{
                UIApplication.shared.openURL(url as URL);
            }
        }
    }
    
    public func postAppointmentAnswer(appointmentID:Int, GCAppointmentStatus: String, completionHandler: @escaping (_ result:String) -> Void){
        let deviceID = UIDevice.current.identifierForVendor!.uuidString;        WebServiceHelper().PostAppointmentAnswer(appointmentID: appointmentID, deviceID: deviceID, GCAppointmentStatus: GCAppointmentStatus, completionHandler: { (result) -> Void in
            let dict = WebServiceHelper.convertToDictionary(text: result)
            let retval:String = dict?["Result"] as! String;
            completionHandler(retval);
        });
    }


}
