//
//  BasePatientTableViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/14/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class BasePatientTableViewController: BaseTableViewController {
    @IBOutlet weak var btnMenu: UIBarButtonItem!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if revealViewController() != nil {
            btnMenu.target = revealViewController()
            btnMenu.action = #selector(SWRevealViewController.revealToggle(_:))
            view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
            
            var pageType:NSString = "";
            if(UserDefaults.standard.object(forKey: "pageType") != nil){
                pageType = (UserDefaults.standard.object(forKey: "pageType") as? NSString)!;
            }
            if(pageType.isEqual(to: "")){
                self.revealViewController().rearViewController.viewDidLoad();
            }
        }
        
        
        // Do any additional setup after loading the view.
    }
    
    override func showLoadingPanel() {
        super.showLoadingPanel();
        btnMenu.isEnabled = false;
    }
    
    override func hideLoadingPanel() {
        super.hideLoadingPanel();
        btnMenu.isEnabled = true;
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}
