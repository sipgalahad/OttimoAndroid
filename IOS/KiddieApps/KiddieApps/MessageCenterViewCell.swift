//
//  MessageCenterViewCell.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/8/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class MessageCenterViewCell: UITableViewCell {

    @IBOutlet weak var lblMessage: UILabel!
    @IBOutlet weak var btnCall: UIButton!
    @IBOutlet weak var btnConfirm: UIButton!
    @IBOutlet weak var btnCancel: UIButton!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
