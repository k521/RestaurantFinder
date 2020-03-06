package videodemos.example.restaurantinspector.Model;

public class Violation {

    private String violation;
    private String idToImage;
    private boolean severityToImage;

    public Violation(String violation, String idToImage, boolean severityToImage){
        this.violation = violation;
        this.idToImage = idToImage;
        this.severityToImage = severityToImage;
    }

    public String getViolation() {
        return violation;
    }

    public String getIdToImage() {
        return idToImage;
    }

    public boolean getSeverityToImage() {
        return severityToImage;
    }
    public void setViolation(String violation) {
        this.violation = violation;
    }

    public void setIdToImage(String idToImage) {
        this.idToImage = idToImage;
    }

    public void setSeverityToImage(boolean severityToImage) {
        this.severityToImage = severityToImage;
    }

}
