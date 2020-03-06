package videodemos.example.restaurantinspector.Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Inspection{


    private String hazardRating;
    
    private String inspectionDate = "";
    private String inspType = "";
    private int numCritical = 0;
    private int numNonCritical = 0;

    private List<Integer> violationList = new ArrayList<>();

    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate)
    {
        this.inspectionDate = inspectionDate;
    }

    public List<Integer> getViolationList() {
        return violationList;
    }

    public String getInspType() {
        return inspType;
    }

    public void setInspType(String inspType) {
        this.inspType = inspType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public void setNumCritical(int numCritical) {
        this.numCritical = numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public void setNumNonCritical(int numNonCritical) {
        this.numNonCritical = numNonCritical;
    }



    public String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    public void addViolation(int violationCode){
        violationList.add(violationCode);
    }

    @Override
    public String toString() {
        return "Inspection{" +
                "hazardRating='" + hazardRating + '\'' +
                ", inspectionDate='" + inspectionDate + '\'' +
                ", inspType='" + inspType + '\'' +
                ", numCritical=" + numCritical +
                ", numNonCritical=" + numNonCritical +
                ", violationList=" + violationList +
                '}';
    }
}

