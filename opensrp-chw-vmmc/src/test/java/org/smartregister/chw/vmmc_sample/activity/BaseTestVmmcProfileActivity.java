package org.smartregister.chw.vmmc_sample.activity;

import org.smartregister.chw.vmmc.activity.BaseVmmcProfileActivity;
import org.smartregister.chw.vmmc.domain.Visit;

public class BaseTestVmmcProfileActivity extends BaseVmmcProfileActivity {
    @Override
    public Visit getLastVisit() {
        return null;
    }

    @Override
    public boolean getIsClientUsingVmmcMethod() {
        return false;
    }

    @Override
    public boolean isFirstVisit() {
        return false;
    }

    @Override
    public void startPointOfServiceDeliveryForm() {
        //not required
    }

    @Override
    public void startVmmcCounselingForm() {
        //not required
    }

    @Override
    public void startVmmcScreeningForm() {
        //not required
    }

    @Override
    public void startProvideVmmcMethod() {
        //not required
    }

    @Override
    public void startProvideOtherServices() {
        //not required
    }

    @Override
    public void startVmmcFollowupVisit() {
        //not required
    }
}
