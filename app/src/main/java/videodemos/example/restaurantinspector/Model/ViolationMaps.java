package videodemos.example.restaurantinspector.Model;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import java.util.HashMap;

import videodemos.example.restaurantinspector.R;

public class ViolationMaps {

    private static ViolationMaps instance;
    private static ViolationMaps monthInstance;

    public static ViolationMaps getInstance(){
        if(instance == null){
            populateViolations();
            populateSeverity();
            populateShortViolation();
            populateNatureViolation();
            populateMonths();
        }
        return instance;
    }
    public static HashMap<Integer, String> violationCodes = new HashMap<Integer, String>();
    public static HashMap<Integer,String> months = new HashMap<>();

    public static HashMap<Integer, Boolean> severity = new HashMap<Integer, Boolean>();

    public static HashMap<Integer, Integer> natureViolation = new HashMap<Integer, Integer>();

    public static HashMap<Integer, String> shortViolation = new HashMap<Integer, String>();


    private static  void populateMonths(){
        months.put(1,"January");
        months.put(2,"February");
        months.put(3,"March");
        months.put(4,"April");
        months.put(5,"May");
        months.put(6,"June");
        months.put(7,"July");
        months.put(8,"August");
        months.put(9,"September");
        months.put(10,"October");
        months.put(11,"November");
        months.put(12,"December");

    }

    private static void populateViolations(){
        violationCodes.put(101,"Not Critical,Plans/construction/alterations not in accordance with the Regulation [s. 3; s. 4],Not Repeat");
        violationCodes.put(102,"Not Critical,Operation of an unapproved food premises [s. 6(1)],Not Repeat");
        violationCodes.put(103,"Not Critical,Failure to hold a valid permit while operating a food service establishment [s. 8(1)],Not Repeat");
        violationCodes.put(104,"Not Critical,Permit not posted in a conspicuous location [s. 8(7)],Not Repeat");
        violationCodes.put(201,"Critical,Food contaminated or unfit for human consumption [s. 13],Not Repeat");
        violationCodes.put(202,"Critical,Food not processed in a manner that makes it safe to eat [s. 14(1)],Not Repeat");
        violationCodes.put(203,"Critical,Food not cooled in an acceptable manner [s. 12(a)],Not Repeat");
        violationCodes.put(204,"Critical,Food not cooked or reheated in a manner that makes it safe to eat [s. 14(1)],Not Repeat");
        violationCodes.put(205,"Critical,Cold potentially hazardous food stored/displayed above 4 °C. [s. 14(2)],Not Repeat");
        violationCodes.put(206,"Critical,Hot potentially hazardous food stored/displayed below 60 °C. [s. 14(2)],Not Repeat");
        violationCodes.put(208,"Not Critical,Foods obtained from unapproved sources [s. 11],Not Repeat");
        violationCodes.put(209,"Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat");
        violationCodes.put(210,"Not Critical,Food not thawed in an acceptable manner [s. 14(2)],Not Repeat");
        violationCodes.put(211,"Not Critical,Frozen potentially hazardous food stored/displayed above -18 °C. [s. 14(3)],Not Repeat");
        violationCodes.put(212,"Not Critical,Operator has not provided acceptable written food handling procedures [s. 23],Not Repeat");
        violationCodes.put(301,"Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1)],Not Repeat");
        violationCodes.put(302,"Critical,Equipment/utensils/food contact surfaces not properly washed and sanitized [s. 17(2)],Not Repeat");
        violationCodes.put(303,"Critical,Equipment/facilities/hot & cold water for sanitary maintenance not adequate [s. 17(3); s. 4(1)(f)],Not Repeat");
        violationCodes.put(304,"Not Critical,Premises not free of pests [s. 26(a)],Not Repeat");
        violationCodes.put(305,"Not Critical,Conditions observed that may allow entrance/harbouring/breeding of pests [s. 26(b)(c)],Not Repeat");
        violationCodes.put(306,"Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],Not Repeat");
        violationCodes.put(307,"Not Critical,Equipment/utensils/food contact surfaces are not of suitable design/material [s. 16; s. 19],Not Repeat");
        violationCodes.put(308,"Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat");
        violationCodes.put(309,"Not Critical,Chemicals cleansers & similar agents stored or labeled improperly [s. 27],Not Repeat");
        violationCodes.put(310,"Not Critical,Single use containers & utensils are used more than once [s. 20],Not Repeat");
        violationCodes.put(311,"Not Critical,Premises not maintained as per approved plans [s. 6(1)(b)],Not Repeat");
        violationCodes.put(312,"Not Critical,Items not required for food premises operation being stored on the premises [s. 18],Not Repeat");
        violationCodes.put(313,"Not Critical,Live animal on the premises excluding guide animal in approved areas [s. 25(1)],Not Repeat");
        violationCodes.put(314,"Not Critical,Operator has not provided acceptable written sanitation procedures [s. 24],Not Repeat");
        violationCodes.put(315,"Not Critical,Refrigeration units and hot holding equipment lack accurate thermometers [s. 19(2)],Not Repeat");
        violationCodes.put(401,"Critical,Adequate handwashing stations not available for employees [s. 21(4)],Not Repeat");
        violationCodes.put(402,"Critical,Employee does not wash hands properly or at adequate frequency [s. 21(3)],Not Repeat");
        violationCodes.put(403,"Not Critical,Employee lacks good personal hygiene clean clothing and hair control [s. 21(1)],Not Repeat");
        violationCodes.put(404,"Not Critical,Employee smoking in food preparation/processing/storage areas [s. 21(2)],Not Repeat");
        violationCodes.put(501,"Not Critical,Operator does not have FOODSAFE Level 1 or Equivalent [s. 10(1)],Not Repeat");
        violationCodes.put(502,"Not Critical,In operator’s absence no staff on duty has FOODSAFE Level 1 or equivalent [s. 10(2)],Not Repeat");
    }

    private static void populateSeverity(){
        //TODO: Add boolean as second field based on criticallity
        severity.put(101, false);
        severity.put(102,false);
        severity.put(103,false);
        severity.put(104,false);
        severity.put(201, true);
        severity.put(202,true);
        severity.put(203,true);
        severity.put(204,true);
        severity.put(205,true);
        severity.put(206,true);
        severity.put(208,false);
        severity.put(209,false);
        severity.put(210,false);
        severity.put(211,false);
        severity.put(212,false);
        severity.put(301,true);
        severity.put(302,true);
        severity.put(303,true);
        severity.put(304,false);
        severity.put(305,false);
        severity.put(306,false);
        severity.put(307,false);
        severity.put(308,false);
        severity.put(309,false);
        severity.put(310,false);
        severity.put(311,false);
        severity.put(312,false);
        severity.put(313,false);
        severity.put(314,false);
        severity.put(315,false);
        severity.put(401,true);
        severity.put(402,true);
        severity.put(403,false);
        severity.put(404,false);
        severity.put(501,false);
        severity.put(502,false);
      }

      private static void populateNatureViolation(){
        //TODO: Add boolean as second field based on criticallity
        natureViolation.put(101, R.drawable.miscellaneous_icon);
        natureViolation.put(102, R.drawable.miscellaneous_icon);
        natureViolation.put(103,R.drawable.miscellaneous_icon);
        natureViolation.put(104,R.drawable.miscellaneous_icon);
        natureViolation.put(201, R.drawable.unfit_consumption_icon);
        natureViolation.put(202,R.drawable.miscellaneous_icon);
        natureViolation.put(203,R.drawable.thaw_hazard_icon);
        natureViolation.put(204,R.drawable.unfit_consumption_icon);
        natureViolation.put(205,R.drawable.miscellaneous_icon);
        natureViolation.put(206,R.drawable.miscellaneous_icon);
        natureViolation.put(208,R.drawable.miscellaneous_icon);
        natureViolation.put(209,R.drawable.contamination_icon);
        natureViolation.put(210,R.drawable.thaw_hazard_icon);
        natureViolation.put(211,R.drawable.miscellaneous_icon);
        natureViolation.put(212,R.drawable.miscellaneous_icon);
        natureViolation.put(301,R.drawable.sanitory_condition_icon);
        natureViolation.put(302,R.drawable.sanitory_condition_icon);
        natureViolation.put(303,R.drawable.sanitory_condition_icon);
        natureViolation.put(304,R.drawable.pests_icon);
        natureViolation.put(305,R.drawable.pests_icon);
        natureViolation.put(306,R.drawable.sanitory_condition_icon);
        natureViolation.put(307,R.drawable.working_order);
        natureViolation.put(308,R.drawable.working_order);
        natureViolation.put(309,R.drawable.miscellaneous_icon);
        natureViolation.put(310,R.drawable.miscellaneous_icon);
        natureViolation.put(311,R.drawable.miscellaneous_icon);
        natureViolation.put(312,R.drawable.miscellaneous_icon);
        natureViolation.put(313,R.drawable.miscellaneous_icon);
        natureViolation.put(314,R.drawable.sanitory_condition_icon);
        natureViolation.put(315,R.drawable.working_order);
        natureViolation.put(401,R.drawable.sanitory_condition_icon);
        natureViolation.put(402,R.drawable.sanitory_condition_icon);
        natureViolation.put(403,R.drawable.unhygienic_icon);
        natureViolation.put(404,R.drawable.miscellaneous_icon);
        natureViolation.put(501,R.drawable.miscellaneous_icon);
        natureViolation.put(502,R.drawable.miscellaneous_icon);
      }
    public static int daysInBetween(String dateInput) {
        DateTimeFormatter dateFormat = DateTimeFormat
                .forPattern("G,C,Y,x,w,e,E,Y,D,M,d,a,K,h,H,k,m,s,S,z,Z");

        Log.d("Input date is ", dateInput);
        String year = dateInput.substring(0,4);
        String month = dateInput.substring(4,6);
        String date = dateInput.substring(6,8);
        String dateInQuestion = year + "-" + month + "-" + date;
        LocalTime localTime = new LocalTime();
        LocalDate localDate = new LocalDate();
        DateTime dateTime = new DateTime();
        LocalDateTime localDateTime = new LocalDateTime();
        DateTimeZone dateTimeZone = DateTimeZone.getDefault();
        int days = Days.daysBetween(DateTime.parse(dateInQuestion),dateTime).getDays();
        return days;
    }
        private static void populateShortViolation(){
        //TODO: Add boolean as second field based on criticallity
        shortViolation.put(101, "Plans/construction/alterations not up to standard.");
        shortViolation.put(102,"Operation of unapproved food premises. ");
        shortViolation.put(103,"No valid permit for this restaurant.");
        shortViolation.put(104,"Permit not seen easily.");
        shortViolation.put(201, "Food contaminated or unfit for consumption.");
        shortViolation.put(202,"Food not processed in manner for safe eating.");
        shortViolation.put(203,"Food not cooled properly.");
        shortViolation.put(204,"Food not cooked or heated for safe eating.");
        shortViolation.put(205,"Cold food not stored properly.");
        shortViolation.put(206,"Hot food not stored properly.");
        shortViolation.put(208,"Food obtained from unapproved sources.");
        shortViolation.put(209,"Food not protected from contamination.");
        shortViolation.put(210,"Food not thawed properly.");                      //
        shortViolation.put(211,"Frozen food not stored properly.");               //
        shortViolation.put(212,"Food handling procedures not provided.");         //
        shortViolation.put(301,"Equipment/utensils/food not sanitary.");          //
        shortViolation.put(302,"Equipment/utensils/food not properly washed/sanitized."); //
        shortViolation.put(303,"Equipment/facilities/Water for sanitation not proper.");
        shortViolation.put(304,"Location contains pests.");                       //
        shortViolation.put(305,"Conditions observed may cultivate pests.");       //
        shortViolation.put(306,"Food premise not sanitary.");                     //
        shortViolation.put(307,"Equipment/utensils/food contact surfaces bad design/material");   //
        shortViolation.put(308,"Equipment/utensils/food contact surfaces not working.");          //
        shortViolation.put(309,"Chemical cleansers stored or labelled improperly.");              //
        shortViolation.put(310,"Single use items used more than once.");                           //
        shortViolation.put(311,"Premise not maintained according to plan.");                      //
        shortViolation.put(312,"Items unrelated to food business on premise.");                   //
        shortViolation.put(313, "Live animals on premise.");                                      //
        shortViolation.put(314,"Approved sanitation procedures not provided.");                   //
        shortViolation.put(315,"Inaccurate thermometers.");                                       //
        shortViolation.put(401,"Proper handwashing station not present.");                        //
        shortViolation.put(402,"Hands not washed adequately or frequently enough.");              //
        shortViolation.put(403,"Employees lack hygiene.");                                        //
        shortViolation.put(404,"Employees smoking in not proper areas.");                         //
        shortViolation.put(501,"FOODSAFE level 1 or Equivalent not present.");                    //
        shortViolation.put(502,"In Operator's absence, nobody has FOODSAFE level 1 or Equivalent."); //
     }

//    private static void populateSeverity(){
//        //TODO: Add boolean as second field based on criticallity
//        severity.put(101, false);
//        severity.put(102,"Not Critical,Operation of an unapproved food premises [s. 6(1)],Not Repeat");
//        severity.put(103,"Not Critical,Failure to hold a valid permit while operating a food service establishment [s. 8(1)],Not Repeat");
//        severity.put(104,"Not Critical,Permit not posted in a conspicuous location [s. 8(7)],Not Repeat");
//        severity.put(201,"Critical,Food contaminated or unfit for human consumption [s. 13],Not Repeat");
//        severity.put(202,"Critical,Food not processed in a manner that makes it safe to eat [s. 14(1)],Not Repeat");
//        severity.put(203,"Critical,Food not cooled in an acceptable manner [s. 12(a)],Not Repeat");
//        severity.put(204,"Critical,Food not cooked or reheated in a manner that makes it safe to eat [s. 14(1)],Not Repeat");
//        severity.put(205,"Critical,Cold potentially hazardous food stored/displayed above 4 °C. [s. 14(2)],Not Repeat");
//        severity.put(206,"Critical,Hot potentially hazardous food stored/displayed below 60 °C. [s. 14(2)],Not Repeat");
//        severity.put(208,"Not Critical,Foods obtained from unapproved sources [s. 11],Not Repeat");
//        severity.put(209,"Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat");
//        severity.put(210,"Not Critical,Food not thawed in an acceptable manner [s. 14(2)],Not Repeat");
//        severity.put(211,"Not Critical,Frozen potentially hazardous food stored/displayed above -18 °C. [s. 14(3)],Not Repeat");
//        severity.put(212,"Not Critical,Operator has not provided acceptable written food handling procedures [s. 23],Not Repeat");
//        severity.put(301,"Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1)],Not Repeat");
//        severity.put(302,"Critical,Equipment/utensils/food contact surfaces not properly washed and sanitized [s. 17(2)],Not Repeat");
//        severity.put(303,"Critical,Equipment/facilities/hot & cold water for sanitary maintenance not adequate [s. 17(3); s. 4(1)(f)],Not Repeat");
//        severity.put(304,"Not Critical,Premises not free of pests [s. 26(a)],Not Repeat");
//        severity.put(305,"Not Critical,Conditions observed that may allow entrance/harbouring/breeding of pests [s. 26(b)(c)],Not Repeat");
//        severity.put(306,"Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],Not Repeat");
//        severity.put(307,"Not Critical,Equipment/utensils/food contact surfaces are not of suitable design/material [s. 16; s. 19],Not Repeat");
//        severity.put(308,"Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat");
//        severity.put(309,"Not Critical,Chemicals cleansers & similar agents stored or labeled improperly [s. 27],Not Repeat");
//        severity.put(310,"Not Critical,Single use containers & utensils are used more than once [s. 20],Not Repeat");
//        severity.put(311,"Not Critical,Premises not maintained as per approved plans [s. 6(1)(b)],Not Repeat");
//        severity.put(312,"Not Critical,Items not required for food premises operation being stored on the premises [s. 18],Not Repeat");
//        severity.put(313,"Not Critical,Live animal on the premises excluding guide animal in approved areas [s. 25(1)],Not Repeat");
//        severity.put(314,"Not Critical,Operator has not provided acceptable written sanitation procedures [s. 24],Not Repeat");
//        severity.put(315,"Not Critical,Refrigeration units and hot holding equipment lack accurate thermometers [s. 19(2)],Not Repeat");
//        severity.put(401,"Critical,Adequate handwashing stations not available for employees [s. 21(4)],Not Repeat");
//        severity.put(402,"Critical,Employee does not wash hands properly or at adequate frequency [s. 21(3)],Not Repeat");
//        severity.put(403,"Not Critical,Employee lacks good personal hygiene clean clothing and hair control [s. 21(1)],Not Repeat");
//        severity.put(404,"Not Critical,Employee smoking in food preparation/processing/storage areas [s. 21(2)],Not Repeat");
//        severity.put(501,"Not Critical,Operator does not have FOODSAFE Level 1 or Equivalent [s. 10(1)],Not Repeat");
//        severity.put(502,"Not Critical,In operator’s absence no staff on duty has FOODSAFE Level 1 or equivalent [s. 10(2)],Not Repeat");
  //  }
}
