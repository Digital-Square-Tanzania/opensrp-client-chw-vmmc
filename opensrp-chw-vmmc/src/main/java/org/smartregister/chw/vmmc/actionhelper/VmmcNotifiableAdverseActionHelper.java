package org.smartregister.chw.vmmc.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
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

import java.util.List;
import java.util.Map;

public class VmmcNotifiableAdverseActionHelper implements BaseVmmcVisitAction.VmmcVisitActionHelper {

    protected String was_nae_attended;

    protected String jsonPayload;

    protected Context context;

    protected MemberObject memberObject;

    public VmmcNotifiableAdverseActionHelper(Context context, MemberObject memberObject) {
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

            String method_used_notify = VmmcDao.
                    getMcMethodUsed(memberObject.getBaseEntityId());

            String discharge_date = VmmcDao.
                    getDischargingDate(memberObject.getBaseEntityId());

            global.put("method_used", method_used_notify);

            global.put("discharge_date", discharge_date);

            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");

            String male_circumcision_date = VmmcDao.getMcDoneDate(memberObject.getBaseEntityId());
            LocalDate mcProcedureDate = formatter.parseDateTime(male_circumcision_date).toLocalDate();

            JSONArray fields = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
            JSONObject mcDoneDate = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "mc_procedure_date");
            mcDoneDate.put(JsonFormUtils.VALUE, mcProcedureDate);

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

            was_nae_attended = JsonFormUtils.
                    getValue(jsonObject, "was_nae_attended");

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
        if (StringUtils.isBlank(was_nae_attended))
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
