package Model;


public class Level {
  private int levelNumber;
  private int experienceNeeded;

  public Level (Builder builder){
    this.levelNumber = builder.levelNumber;
    this.experienceNeeded = builder.experienceNeeded;
  }

  public int getLevelNumber() {
    return levelNumber;
  }

  public int getExperienceNeeded() {
    return experienceNeeded;
  }


  public static class Builder {
    private int levelNumber;
    private int experienceNeeded;



    public Builder withLevelNumber(int levelNumber) {
      this.levelNumber = levelNumber;
      return this;
    }

    public Builder withExperienceNeeded(int experienceNeeded) {
      this.experienceNeeded = experienceNeeded;
      return this;

    }

    public Level build(){
      return new Level(this);
    }
  }
}
