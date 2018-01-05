package samanasoft.android.kiddielogicpatientalarm;
import android.os.Bundle;

import samanasoft.android.framework.Constant;

public class AnnouncementActivity extends BaseAnnouncementActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.customOnCreate(savedInstanceState, Constant.AnnouncementType.ANNOUNCEMENT);
    }
}
