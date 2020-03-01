package videodemos.example.restaurantinspector.Model;

import java.util.HashMap;

public class Violations {

    private static Violations instance;

    public static Violations getInstance(){
        if(instance == null){
            populateViolations();
        }
        return instance;
    }
   public static HashMap<Integer, String> violationCodes = new HashMap<Integer, String>();

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
}
