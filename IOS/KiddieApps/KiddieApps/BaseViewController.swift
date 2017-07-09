//
//  BaseViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/9/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class BaseViewController: UIViewController {
    var indicator: UIActivityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: UIActivityIndicatorViewStyle.gray);
    override func viewDidLoad() {
        super.viewDidLoad()
        indicator.frame = CGRect(x:0,y:0,width:40,height:40)
        indicator.center = view.center
        view.addSubview(indicator)
        indicator.bringSubview(toFront: view)
        UIApplication.shared.isNetworkActivityIndicatorVisible = true
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
