//
//  MyAppointmentViewCell.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/7/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class MyAppointmentViewCell: UITableViewCell {
    @IBOutlet weak var lblStartDate: UILabel!
    @IBOutlet weak var lblParamedicName: UILabel!
    @IBOutlet weak var lblVisitTypeName: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
