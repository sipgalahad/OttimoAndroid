//
//  Util.m
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/20/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

#import <Foundation/Foundation.h>

    func getPath(fileName: String) -> String {
        let documentsURL = NSFileManager.defaultManager().URLsForDirectory(.DocumentDirectory, inDomains: .UserDomainMask)[0]
        let fileURL = documentsURL.URLByAppendingPathComponent(fileName)
        return fileURL.path!
    }
