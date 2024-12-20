package org.smartregister.chw.vmmc.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.domain.VisitDetail;
import org.smartregister.chw.vmmc.model.BaseVmmcVisitAction;
import org.smartregister.chw.vmmc.util.JsonFormUtils;
import org.smartregister.chw.vmmc.util.VisitUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class VmmcSecondVitalActionHelper implements BaseVmmcVisitAction.VmmcVisitActionHelper {

    protected String jsonPayload;

    private HashMap<String, Boolean> checkObject = new HashMap<>();

    protected Context context;

    protected static String second_vital_time;

    protected MemberObject memberObject;

    public VmmcSecondVitalActionHelper(Context context, MemberObject memberObject) {
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

            String first_vital_sign_time_taken = VmmcFirstVitalActionHelper.time_taken;

            DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");

            LocalTime currentTime = LocalTime.parse(first_vital_sign_time_taken);
            LocalTime newTime = currentTime.plusMinutes(15);
            String newTimeString = newTime.toString(formatter);
            LocalTime first_vital_time = formatter.parseLocalTime(newTimeString);

            JSONArray fields = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
            JSONObject firstVitalTime = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "first_time");
            firstVitalTime.put(JsonFormUtils.VALUE, first_vital_time);

//            global.put("first_vital_sign_time_taken_value", newTimeString);
            global.put("age", age);

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

            checkObject.put("second_vital_pulse_rate", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "second_vital_pulse_rate")));
            checkObject.put("second_vital_sign_systolic", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "second_vital_sign_systolic")));
            checkObject.put("second_vital_sign_diastolic", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "second_vital_sign_diastolic")));
            checkObject.put("second_vital_sign_temperature", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "second_vital_sign_temperature")));
            checkObject.put("second_vital_sign_respiration_rate", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "second_vital_sign_respiration_rate")));
            checkObject.put("second_vital_sign_time_taken", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "second_vital_sign_time_taken")));

            second_vital_time = JsonFormUtils.getValue(jsonObject, "second_vital_sign_time_taken");

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
            JSONObject secondVitalCompletionStatus = JsonFormUtils.getFieldJSONObject(fields, "second_vital_completion_status");
            assert secondVitalCompletionStatus != null;
            secondVitalCompletionStatus.put(com.vijay.jsonwizard.constants.JsonFormConstants.VALUE, VisitUtils.getActionStatus(checkObject));
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
        //overridden
    }
}

