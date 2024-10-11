package org.smartregister.chw.vmmc.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.vmmc.dao.VmmcDao;
import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.domain.VisitDetail;
import org.smartregister.chw.vmmc.model.BaseVmmcVisitAction;
import org.smartregister.chw.vmmc.util.JsonFormUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class VmmcDischargeActionHelper implements BaseVmmcVisitAction.VmmcVisitActionHelper {

    protected String discharge_condition;

    protected String notifiable_adverse_event_occured;

    protected String jsonPayload;

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONObject global = jsonObject.getJSONObject("global");
            JSONArray fields = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);

            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");

            JSONObject dischargeTime = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "discharge_time");
            dischargeTime.put("text", "Discharge Time: " + currentTime.toString(formatter));

            String second_vital_time_done = VmmcSecondVitalActionHelper.second_vital_time;

            LocalTime second_vital_time = LocalTime.parse(second_vital_time_done);

//            JSONObject secondVitalSign = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "second_vital_time");
//            secondVitalSign.put(JsonFormUtils.VALUE, second_vital_time);

            int minutesDifference = Minutes.minutesBetween(second_vital_time, currentTime).getMinutes();

            global.put("duration", minutesDifference);
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
            discharge_condition = JsonFormUtils.getValue(jsonObject, "discharge_condition");
            notifiable_adverse_event_occured = JsonFormUtils.getValue(jsonObject, "notifiable_adverse_event_occured");

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
    public String postProcess(String s) {
        return s;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseVmmcVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(discharge_condition))
            return BaseVmmcVisitAction.Status.PENDING;
        else {
            return BaseVmmcVisitAction.Status.COMPLETED;
        }
    }

    @Override
    public void onPayloadReceived(BaseVmmcVisitAction baseVmmcVisitAction) {
        //overridden
    }
}
