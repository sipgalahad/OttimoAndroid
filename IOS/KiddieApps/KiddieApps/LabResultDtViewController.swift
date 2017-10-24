//
//  LabResultDtViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/19/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class LabResultDtViewController: BaseViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var tblView: UITableView!
    public var labResultID:Int = 0;
    @IBOutlet weak var lblResultNo: UILabel!
    @IBOutlet weak var lblResultDate: UILabel!
    @IBOutlet weak var lblProvider: UILabel!
    @IBOutlet weak var lblRemarks: UILabel!
    var lstLabResultDt:[LaboratoryResultDt] = [];
    override func viewDidLoad() {
        super.viewDidLoad()
        UserDefaults.standard.set("", forKey:"pageType");

        let entityHd:LaboratoryResultHd = BusinessLayer.getLaboratoryResultHd(ID: labResultID)!;
        lblResultNo.text = entityHd.ReferenceNo;
        lblResultDate.text = entityHd.ResultDate?.toString(format: Constant.FormatString.DATE_FORMAT);
        lblProvider.text = entityHd.ProviderName;
        lblRemarks.text = entityHd.Remarks;
        lstLabResultDt = BusinessLayer.getLaboratoryResultDtList(filterExpression: "ID = \(String(describing: labResultID))");
        
        tblView.delegate = self
        tblView.dataSource = self
        tblView.isScrollEnabled = true

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    // MARK: - Table view data source
    
    func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return lstLabResultDt.count;
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! LabResultDtViewCell
        
        let entityDt:LaboratoryResultDt = lstLabResultDt[indexPath.row];
        cell.lblFraction.text = entityDt.FractionName1;
        cell.lblResult.text = entityDt.ResultSummary;
        
        let metricResultValue:Double = entityDt.MetricResultValue as! Double;
        let labTestResultTypeID:Int = entityDt.LabTestResultTypeID as! Int;
        var result = "";
        if (labTestResultTypeID == 1){
            result = entityDt.TextValue!;
        }
        else if (labTestResultTypeID == 2)
        {
            if (metricResultValue == 0){
                result = "\(String(describing: entityDt.InternationalResultValue!)) (I)";
            }
            else{
                result = String(describing: entityDt.MetricResultValue!);
            }
        }
        else{
            result = entityDt.LabTestResultTypeDtName!;
        }
        if (entityDt.GCResultSummary != Constant.LabResultSummary.NORMAL)
        {
            if (entityDt.GCResultSummary == Constant.LabResultSummary.HIGH){
                result += " ?";
            }
            else {
                result += " ?";
            }
            
            cell.lblFraction.textColor = UIColor.red;
            cell.lblResult.textColor = UIColor.red;
        }
        cell.lblResult.text = result;
        
        return cell
    }

}
