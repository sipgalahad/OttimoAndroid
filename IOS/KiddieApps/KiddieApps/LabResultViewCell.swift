//
//  LabResultViewCell.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/19/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class LabResultViewCell: UITableViewCell {

    @IBOutlet weak var lblProvider: UILabel!
    @IBOutlet weak var lblResultDate: UILabel!
    @IBOutlet weak var lblResultNo: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
