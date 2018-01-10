//
//  LabResultDtViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/19/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class AnnouncementDtViewController: BaseViewController {
    
    public var announcementID:Int = 0;
    @IBOutlet weak var lblRemarks: UILabel!
    var lstLabResultDt:[LaboratoryResultDt] = [];
    override func viewDidLoad() {
        super.viewDidLoad()
        UserDefaults.standard.set("", forKey:"pageType");
        
        let entityHd:Announcement = BusinessLayer.getAnnouncement(AnnouncementID: announcementID)!;
        
        lblRemarks.numberOfLines = 0;
        lblRemarks.lineBreakMode = .byWordWrapping;
        lblRemarks.attributedText = stringFromHtml(string: entityHd.Remarks!)
    }
    
    private func stringFromHtml(string:String) -> NSAttributedString?{
        do{
            let data = string.data(using: String.Encoding.utf8, allowLossyConversion: true)
            if let d = data {
                let str = try NSAttributedString(data: d,
                                                 options: [NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType],
                                                 documentAttributes: nil)
                return str
            }
        }catch{
            
        }
        return nil
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
