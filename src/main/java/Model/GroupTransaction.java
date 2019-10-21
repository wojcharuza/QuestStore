package Model;

public class GroupTransaction {
    private String title;
    private int donationValue;

    public GroupTransaction(String title, int donationValue){
        this.title = title;
        this.donationValue = donationValue;

    }

    public String getTitle() {
        return title;
    }

    public int getDonationValue() {
        return donationValue;
    }

    public void setDonationValue(int donationValue) {
        this.donationValue = donationValue;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
