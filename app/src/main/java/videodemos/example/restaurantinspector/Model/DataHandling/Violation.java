package videodemos.example.restaurantinspector.Model.DataHandling;

/**
* A way to classify all violations , if any , in a particular inspection
*/
public class Violation {

    private String violation;
    private int idToImage;
    private boolean severityToImage;

    public Violation(String violation, int idToImage, boolean severityToImage) {
        this.violation = violation;
        this.idToImage = idToImage;
        this.severityToImage = severityToImage;
    }

    public String getViolation() {
        return violation;
    }

    public int getIdToImage() {
        return idToImage;
    }

    public boolean isSevere() {
        return severityToImage;
    }


}

