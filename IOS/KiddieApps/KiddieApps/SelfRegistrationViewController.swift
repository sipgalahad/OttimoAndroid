//
//  ChangePasswordViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/28/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class SelfRegistrationViewController: BasePatientPageViewController, UIWebViewDelegate {
    @IBOutlet weak var txtCode: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()        // Do any additional setup after loading the view.
    }
    
    @IBAction func onBtnSaveClick(_ sender: Any) {
        let MRN = UserDefaults.standard.object(forKey: "MRN") as? Int;
        let entity:Patient = BusinessLayer.getPatient(MRN: MRN!)!;
        let code:String = txtCode.text!;
        var webView: UIWebView!
        webView = UIWebView(frame: UIScreen.main.bounds)
        webView.delegate = self
        view.addSubview(webView)
        let strURL = "http://114.199.103.10:8080/KiddielogicTest/BridgingServer/Program/Mobile/SelfRegistration/SelfRegistrationEntry.aspx?id=\(String(describing: entity.MedicalNo!))|\(code)"
        
        if let encoded = strURL.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed),
            let url = URL(string: encoded) {
            let request = URLRequest(url: url)
            webView.loadRequest(request)

        }
            //view.willRemoveSubview(webView)
        
    }
    func webViewDidFinishLoad(_ webView : UIWebView) {
        //Page is loaded do what you want
        self.dismiss(animated: true, completion:  { (result) -> Void in
        })
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
