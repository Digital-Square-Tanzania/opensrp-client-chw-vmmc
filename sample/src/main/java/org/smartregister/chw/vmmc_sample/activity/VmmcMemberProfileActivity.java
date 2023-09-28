package org.smartregister.chw.vmmc_sample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.factory.FileSourceFactoryHelper;

import org.json.JSONObject;
import org.smartregister.chw.vmmc.VmmcLibrary;
import org.smartregister.chw.vmmc.activity.BaseVmmcProfileActivity;
import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.domain.Visit;
import org.smartregister.chw.vmmc.util.Constants;
import org.smartregister.chw.vmmc_sample.R;

import timber.log.Timber;


public class VmmcMemberProfileActivity extends BaseVmmcProfileActivity {

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, VmmcMemberProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void recordAnc(MemberObject memberObject) {
        VmmcServiceActivity.startVmmcVisitActivity(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    protected MemberObject getMemberObject(String baseEntityId) {
        return EntryActivity.getSampleMember();
    }

    @Override
    protected void setupViews() {
        initializeFloatingMenu();

        Visit serviceVisit = null;
        Visit procedureVisit = null;
        Visit dischargeVisit = null;
        Visit followUpVisit = null;
        // Visit notifiableVisit = null;

        try {
            serviceVisit = VmmcLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.VMMC_SERVICES);
            procedureVisit = VmmcLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.VMMC_PROCEDURE);
            dischargeVisit = VmmcLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.VMMC_DISCHARGE);
            followUpVisit = VmmcLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.VMMC_FOLLOW_UP_VISIT);
            // notifiableVisit = VmmcLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.VMMC_NOTIFIABLE_EVENTS);
        }
        catch (Exception e){
            // implement later
        }


        if (serviceVisit != null) {
                textViewRecordVmmc.setText(org.smartregister.chw.vmmc.R.string.record_vmmc);
            } else if (procedureVisit != null) {
                textViewRecordVmmc.setText(org.smartregister.chw.vmmc.R.string.vmmc_procedure);
            } else if (dischargeVisit != null) {
                textViewRecordVmmc.setText(org.smartregister.chw.vmmc.R.string.vmmc_discharge);
            } else if (followUpVisit != null) {
                textViewRecordVmmc.setText(org.smartregister.chw.vmmc.R.string.vmmc_followup_visit);
            } else {
                textViewRecordVmmc.setText(org.smartregister.chw.vmmc.R.string.record_vmmc);
            }

    }

    @Override
    public void openFollowupVisit() {
        VmmcServiceActivity.startVmmcVisitActivity(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.textview_procedure_vmmc) {
            VmmcProcedureActivity.startVmmcVisitProcedureActivity(this, memberObject.getBaseEntityId(), false);
        }

        if (id == R.id.textview_discharge_vmmc) {
            VmmcDischargeActivity.startVmmcVisitDischargeActivity(this, memberObject.getBaseEntityId(), false);
        }

        if (id == R.id.textview_notifiable_vmmc) {
            VmmcNotifiableAdverseActivity.startVmmcVisitActivity(this, memberObject.getBaseEntityId(), false);
        }
        if (id == R.id.textview_followup_vmmc) {
            VmmcFollowUpActivity.startVmmcVisitActivity(this, memberObject.getBaseEntityId(), false);
        }
        if (id == R.id.continue_vmmc_service) {
            VmmcServiceActivity.startVmmcVisitActivity(this, memberObject.getBaseEntityId(), true);
        }
        if (id == R.id.continue_vmmc_procedure) {
            VmmcProcedureActivity.startVmmcVisitProcedureActivity(this, memberObject.getBaseEntityId(), true);
        }
        if (id == R.id.textview_continue) {
            VmmcDischargeActivity.startVmmcVisitDischargeActivity(this, memberObject.getBaseEntityId(), true);
        }
        else {
            super.onClick(view);
        }
    }

    @Override
    public void startVmmcServiceForm(String baseEntityId) {
        VmmcServiceActivity.startVmmcVisitActivity(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    public void startVmmcFollowUp(String baseEntityId) {
        VmmcFollowUpActivity.startVmmcVisitActivity(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        delayRefreshSetupViews();
    }


    private void delayRefreshSetupViews() {
        try {
            new Handler(Looper.getMainLooper()).postDelayed(this::setupViews, 300);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}