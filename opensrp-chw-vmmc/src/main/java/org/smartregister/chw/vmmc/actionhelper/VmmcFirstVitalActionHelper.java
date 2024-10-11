package org.smartregister.chw.vmmc.actionhelper;

import android.content.Context;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.vmmc.dao.VmmcDao;
import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.domain.VisitDetail;
import org.smartregister.chw.vmmc.model.BaseVmmcVisitAction;
import org.smartregister.chw.vmmc.util.JsonFormUtils;
import org.smartregister.chw.vmmc.util.VisitUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class VmmcFirstVitalActionHelper implements BaseVmmcVisitAction.VmmcVisitActionHelper {

    protected static String time_taken;

    protected String jsonPayload;

    private HashMap<String, Boolean> checkObject = new HashMap<>();

    protected Context context;

    protected MemberObject memberObject;

    public VmmcFirstVitalActionHelper(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONObject global = jsonObject.getJSONObject("global");

            int age = new Period(new DateTime(memberObject.getAge()),
                    new DateTime()).getYears();

            LocalTime currentTime = LocalTime.now();

            String mc_done_time = VmmcDao.
                    getMcDoneTime(memberObject.getBaseEntityId());
            LocalTime mc_time = LocalTime.parse(mc_done_time);

            JSONArray fields = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
            JSONObject secondVitalSign = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "mc_time");
            secondVitalSign.put(JsonFormUtils.VALUE, mc_time);


            int minutesDifference = Minutes.minutesBetween(mc_time, currentTime).getMinutes();

            global.put("duration", minutesDifference);

            global.put("age", age);
//            global.put("mc_time", mc_time);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            checkObject.clear();

            checkObject.put("first_vital_sign_pulse_rate", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "first_vital_sign_pulse_rate")));
            checkObject.put("first_vital_sign_systolic", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "first_vital_sign_systolic")));
            checkObject.put("first_vital_sign_diastolic", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "first_vital_sign_diastolic")));
            checkObject.put("first_vital_sign_temperature", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "first_vital_sign_temperature")));
            checkObject.put("first_vital_sign_respiration_rate", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "first_vital_sign_respiration_rate")));
            checkObject.put("first_vital_sign_time_taken", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "first_vital_sign_time_taken")));

            time_taken = JsonFormUtils.getValue(jsonObject, "first_vital_sign_time_taken");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseVmmcVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    @Override
    public String getPreProcessedSubTitle() {
        return null;
    }

    @Override
    public String postProcess(String jsonPayload) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonPayload);
            JSONArray fields = JsonFormUtils.fields(jsonObject);
            JSONObject firstVitalCompletionStatus = JsonFormUtils.getFieldJSONObject(fields, "first_vital_completion_status");
            assert firstVitalCompletionStatus != null;
            firstVitalCompletionStatus.put(com.vijay.jsonwizard.constants.JsonFormConstants.VALUE, VisitUtils.getActionStatus(checkObject));
        } catch (JSONException e) {
            Timber.e(e);
        }

        if (jsonObject != null) {
            return jsonObject.toString();
        }
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseVmmcVisitAction.Status evaluateStatusOnPayload() {
        String status = VisitUtils.getActionStatus(checkObject);

        if (status.equalsIgnoreCase(VisitUtils.Complete)) {
            return BaseVmmcVisitAction.Status.COMPLETED;
        }
        if (status.equalsIgnoreCase(VisitUtils.Ongoing)) {
            return BaseVmmcVisitAction.Status.PARTIALLY_COMPLETED;
        }
        return BaseVmmcVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseVmmcVisitAction baseVmmcVisitAction) {
        Timber.v("onPayloadReceived");
    }
}

