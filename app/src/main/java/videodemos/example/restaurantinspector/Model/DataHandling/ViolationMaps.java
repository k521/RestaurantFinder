package videodemos.example.restaurantinspector.Model.DataHandling;

import android.content.Context;
import java.util.HashMap;

import videodemos.example.restaurantinspector.R;


/**
 * A class used as a library to hold violation information that is displayed in the UI.
 */
public class ViolationMaps {

    private HashMap<Integer, String> violationCodes = new HashMap<Integer, String>();
    private HashMap<Integer, Boolean> severity = new HashMap<Integer, Boolean>();
    private HashMap<Integer, Integer> natureViolation = new HashMap<Integer, Integer>();
    private HashMap<Integer, String> shortViolation = new HashMap<Integer, String>();

    private Context context;

    public ViolationMaps(Context context) {
        this.context = context;
        populateViolations();
        populateSeverity();
        populateShortViolation();
        populateNatureViolation();
    }


    public String getFullViolationCodeDescription(int index) {
        return violationCodes.get(index);
    }

    public String getShortViolationCodeDescription(int index) {
        return shortViolation.get(index);
    }

    public boolean getSeverity(int index) {
        return severity.get(index);
    }

    public int getNatureViolation(int index) {
        return natureViolation.get(index);
    }


    private void populateViolations() {
        violationCodes.put(101, context.getResources().getString(R.string.violationDescription101));
        violationCodes.put(102, context.getResources().getString(R.string.violationDescription102));
        violationCodes.put(103, context.getResources().getString(R.string.violationDescription103));
        violationCodes.put(104, context.getResources().getString(R.string.violationDescription104));
        violationCodes.put(201, context.getResources().getString(R.string.violationDescription201));
        violationCodes.put(202, context.getResources().getString(R.string.violationDescription202));
        violationCodes.put(203, context.getResources().getString(R.string.violationDescription203));
        violationCodes.put(204, context.getResources().getString(R.string.violationDescription204));
        violationCodes.put(205, context.getResources().getString(R.string.violationDescription205));
        violationCodes.put(206, context.getResources().getString(R.string.violationDescription206));
        violationCodes.put(208, context.getResources().getString(R.string.violationDescription208));
        violationCodes.put(209, context.getResources().getString(R.string.violationDescription209));
        violationCodes.put(210, context.getResources().getString(R.string.violationDescription210));
        violationCodes.put(211, context.getResources().getString(R.string.violationDescription211));
        violationCodes.put(212, context.getResources().getString(R.string.violationDescription212));
        violationCodes.put(301, context.getResources().getString(R.string.violationDescription301));
        violationCodes.put(302, context.getResources().getString(R.string.violationDescription302));
        violationCodes.put(303, context.getResources().getString(R.string.violationDescription303));
        violationCodes.put(304, context.getResources().getString(R.string.violationDescription304));
        violationCodes.put(305, context.getResources().getString(R.string.violationDescription305));
        violationCodes.put(306, context.getResources().getString(R.string.violationDescription306));
        violationCodes.put(307, context.getResources().getString(R.string.violationDescription307));
        violationCodes.put(308, context.getResources().getString(R.string.violationDescription308));
        violationCodes.put(309, context.getResources().getString(R.string.violationDescription309));
        violationCodes.put(310, context.getResources().getString(R.string.violationDescription310));
        violationCodes.put(311, context.getResources().getString(R.string.violationDescription311));
        violationCodes.put(312, context.getResources().getString(R.string.violationDescription312));
        violationCodes.put(313, context.getResources().getString(R.string.violationDescription313));
        violationCodes.put(314, context.getResources().getString(R.string.violationDescription314));
        violationCodes.put(315, context.getResources().getString(R.string.violationDescription315));
        violationCodes.put(401, context.getResources().getString(R.string.violationDescription401));
        violationCodes.put(402, context.getResources().getString(R.string.violationDescription402));
        violationCodes.put(403, context.getResources().getString(R.string.violationDescription403));
        violationCodes.put(404, context.getResources().getString(R.string.violationDescription404));
        violationCodes.put(501, context.getResources().getString(R.string.violationDescription501));
        violationCodes.put(502, context.getResources().getString(R.string.violationDescription502));
    }

    private void populateSeverity() {
        severity.put(101, false);
        severity.put(102, false);
        severity.put(103, false);
        severity.put(104, false);
        severity.put(201, true);
        severity.put(202, true);
        severity.put(203, true);
        severity.put(204, true);
        severity.put(205, true);
        severity.put(206, true);
        severity.put(208, false);
        severity.put(209, false);
        severity.put(210, false);
        severity.put(211, false);
        severity.put(212, false);
        severity.put(301, true);
        severity.put(302, true);
        severity.put(303, true);
        severity.put(304, false);
        severity.put(305, false);
        severity.put(306, false);
        severity.put(307, false);
        severity.put(308, false);
        severity.put(309, false);
        severity.put(310, false);
        severity.put(311, false);
        severity.put(312, false);
        severity.put(313, false);
        severity.put(314, false);
        severity.put(315, false);
        severity.put(401, true);
        severity.put(402, true);
        severity.put(403, false);
        severity.put(404, false);
        severity.put(501, false);
        severity.put(502, false);
    }

    private void populateNatureViolation() {
        natureViolation.put(101, R.drawable.miscellaneous_icon);
        natureViolation.put(102, R.drawable.miscellaneous_icon);
        natureViolation.put(103, R.drawable.miscellaneous_icon);
        natureViolation.put(104, R.drawable.miscellaneous_icon);
        natureViolation.put(201, R.drawable.unfit_consumption_icon);
        natureViolation.put(202, R.drawable.miscellaneous_icon);
        natureViolation.put(203, R.drawable.thaw_hazard_icon);
        natureViolation.put(204, R.drawable.unfit_consumption_icon);
        natureViolation.put(205, R.drawable.miscellaneous_icon);
        natureViolation.put(206, R.drawable.miscellaneous_icon);
        natureViolation.put(208, R.drawable.miscellaneous_icon);
        natureViolation.put(209, R.drawable.contamination_icon);
        natureViolation.put(210, R.drawable.thaw_hazard_icon);
        natureViolation.put(211, R.drawable.miscellaneous_icon);
        natureViolation.put(212, R.drawable.miscellaneous_icon);
        natureViolation.put(301, R.drawable.sanitory_condition_icon);
        natureViolation.put(302, R.drawable.sanitory_condition_icon);
        natureViolation.put(303, R.drawable.sanitory_condition_icon);
        natureViolation.put(304, R.drawable.pests_icon);
        natureViolation.put(305, R.drawable.pests_icon);
        natureViolation.put(306, R.drawable.sanitory_condition_icon);
        natureViolation.put(307, R.drawable.working_order);
        natureViolation.put(308, R.drawable.working_order);
        natureViolation.put(309, R.drawable.miscellaneous_icon);
        natureViolation.put(310, R.drawable.miscellaneous_icon);
        natureViolation.put(311, R.drawable.miscellaneous_icon);
        natureViolation.put(312, R.drawable.miscellaneous_icon);
        natureViolation.put(313, R.drawable.miscellaneous_icon);
        natureViolation.put(314, R.drawable.sanitory_condition_icon);
        natureViolation.put(315, R.drawable.working_order);
        natureViolation.put(401, R.drawable.sanitory_condition_icon);
        natureViolation.put(402, R.drawable.sanitory_condition_icon);
        natureViolation.put(403, R.drawable.unhygienic_icon);
        natureViolation.put(404, R.drawable.miscellaneous_icon);
        natureViolation.put(501, R.drawable.miscellaneous_icon);
        natureViolation.put(502, R.drawable.miscellaneous_icon);
    }

    private void populateShortViolation() {
        shortViolation.put(101, context.getResources().getString(R.string.sv1));
        shortViolation.put(102, context.getResources().getString(R.string.sv2));
        shortViolation.put(103, context.getResources().getString(R.string.sv3));
        shortViolation.put(104, context.getResources().getString(R.string.sv4));
        shortViolation.put(201, context.getResources().getString(R.string.sv5));
        shortViolation.put(202, context.getResources().getString(R.string.sv6));
        shortViolation.put(203, context.getResources().getString(R.string.sv7));
        shortViolation.put(204, context.getResources().getString(R.string.sv8));
        shortViolation.put(205, context.getResources().getString(R.string.sv9));
        shortViolation.put(206, context.getResources().getString(R.string.sv10));
        shortViolation.put(208, context.getResources().getString(R.string.sv11));
        shortViolation.put(209, context.getResources().getString(R.string.sv12));
        shortViolation.put(210, context.getResources().getString(R.string.sv13));
        shortViolation.put(211, context.getResources().getString(R.string.sv14));
        shortViolation.put(212, context.getResources().getString(R.string.sv15));
        shortViolation.put(301, context.getResources().getString(R.string.sv16));
        shortViolation.put(302, context.getResources().getString(R.string.sv17));
        shortViolation.put(303, context.getResources().getString(R.string.sv18));
        shortViolation.put(304, context.getResources().getString(R.string.sv19));
        shortViolation.put(305, context.getResources().getString(R.string.sv20));
        shortViolation.put(306, context.getResources().getString(R.string.sv21));
        shortViolation.put(307, context.getResources().getString(R.string.sv22));
        shortViolation.put(308, context.getResources().getString(R.string.sv23));
        shortViolation.put(309, context.getResources().getString(R.string.sv24));
        shortViolation.put(310, context.getResources().getString(R.string.sv25));
        shortViolation.put(311, context.getResources().getString(R.string.sv26));
        shortViolation.put(312, context.getResources().getString(R.string.sv27));
        shortViolation.put(313, context.getResources().getString(R.string.sv28));
        shortViolation.put(314, context.getResources().getString(R.string.sv29));
        shortViolation.put(315, context.getResources().getString(R.string.sv30));
        shortViolation.put(401, context.getResources().getString(R.string.sv31));
        shortViolation.put(402, context.getResources().getString(R.string.sv32));
        shortViolation.put(403, context.getResources().getString(R.string.sv33));
        shortViolation.put(404, context.getResources().getString(R.string.sv34));
        shortViolation.put(501, context.getResources().getString(R.string.sv35));
        shortViolation.put(502, context.getResources().getString(R.string.sv36));
    }


}
