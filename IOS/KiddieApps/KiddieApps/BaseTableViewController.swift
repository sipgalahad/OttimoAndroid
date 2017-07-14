//
//  BaseTableViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/14/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class BaseTableViewController: UITableViewController {    
    var indicator: UIActivityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: UIActivityIndicatorViewStyle.gray);
    var panel: UIView = UIView()
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let screenSize: CGRect = UIScreen.main.bounds
        let screenWidth = screenSize.width
        let screenHeight = screenSize.height
        
        panel.frame = CGRect(x: 0, y: 0, width: screenWidth, height: screenHeight)
        panel.backgroundColor = UIColor.white;
        panel.center = view.center;
        view.addSubview(panel)
        panel.bringSubview(toFront: view)
        panel.isHidden = true;
        
        indicator.frame = CGRect(x:0,y:0,width:40,height:40)
        indicator.center = view.center
        view.addSubview(indicator)
        indicator.bringSubview(toFront: view)
        UIApplication.shared.isNetworkActivityIndicatorVisible = true
        // Do any additional setup after loading the view.
    }
    
    func showLoadingPanel(){
        panel.isHidden = false;
        indicator.startAnimating();
    }
    
    func hideLoadingPanel(){
        panel.isHidden = true;
        indicator.stopAnimating();
    }
}
