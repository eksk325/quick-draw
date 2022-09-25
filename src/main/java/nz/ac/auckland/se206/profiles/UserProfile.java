package nz.ac.auckland.se206.profiles;

import java.util.ArrayList;

public class UserProfile {

  private String userName;
  private int profileID;
  private int wins = 0;
  private int losses = 0;
  private ArrayList<String> wordHistory = new ArrayList<>();
  private double highestPredictionPercentage = 0;

  public UserProfile(String userName, int profileID) {
    this.userName = userName;
    this.profileID = profileID;
  }

  public String getUserName() {
    return this.userName;
  }

  public int getWinsCount() {
    return this.wins;
  }

  public int getLossesCount() {
    return this.losses;
  }

  public void incrementWinsCount() {
    this.wins++;
  }

  public void incrementLossesCount() {
    this.losses++;
  }

  public ArrayList<String> getWordHistory() {
    return this.wordHistory;
  }

  /**
   * This method adds to the user's existing word history arraylist.
   *
   * @param word the word the user has played
   */
  public void addWordToHistory(String word) {
    this.wordHistory.add(word);
  }

  public double getHighestPrediction() {
    return this.highestPredictionPercentage;
  }

  public void setHighestPredictionPercentage(double percentage) {
    this.highestPredictionPercentage = percentage;
  }
}
