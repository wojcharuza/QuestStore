package Model;


public class Card {

    private String title;
    private String description;
    private String imagePath;
    private String cardType;
    private int coolcoinValue;

    public Card(Builder builder){
        this.title = builder.title;
        this.description = builder.description;
        this.imagePath = builder.imagePath;
        this.cardType = builder.cardType;
        this.coolcoinValue = builder.coolcoinValue;
    }


    public String getDescription() {
        return description;
    }

    public int getCoolcoin_value() {
        return coolcoinValue;
    }

    public String getCardType() {
        return cardType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getTitle() {
        return title;
    }



    public static class Builder{
        private String title;
        private String description;
        private String imagePath;
        private String cardType;
        private int coolcoinValue;

        public Builder withTitle (String title){
            this.title = title;
            return this;
        }
        public Builder withDescription (String description){
            this.description = description;
            return this;
        }
        public Builder withImagePath (String imagePath) {
            this.imagePath = imagePath;
            return this;
        }
        public Builder withCardType (String type){
            this.cardType = type;
            return this;
        }
        public Builder withCoolcoinValue (int value){
            this.coolcoinValue = value;
            return this;
        }
        public Card build (){
            return new Card(this);
        }
    }
}