package nz.ac.auckland.se206;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.games.Game;
import nz.ac.auckland.se206.profiles.UserProfile;
import nz.ac.auckland.se206.profiles.UserProfileManager;
import nz.ac.auckland.se206.util.SoundUtils;
import nz.ac.auckland.se206.words.CategorySelector;

/** This class will handle any actions on the Stats page. */
public class StatsController {
  @FXML private Label statsTitleLabel;
  @FXML private Label winsLabel;
  @FXML private Label lossesLabel;
  @FXML private Label highestPredictionLabel;
  @FXML private Label easyProgressLabel;
  @FXML private Label mediumProgressLabel;
  @FXML private Label hardProgressLabel;
  @FXML private ImageView userProfileImage;
  @FXML private Accordion wordHistoryAccordion;
  @FXML private ProgressBar easyProgressBar;
  @FXML private ProgressBar mediumProgressBar;
  @FXML private ProgressBar hardProgressBar;

  private UserProfile currentProfile = UserProfileManager.currentProfile;

  /** This method is called when the Stats page is loaded. */
  public void initialize() {
    // Updating the title with the current profile's name
    statsTitleLabel.setText(currentProfile.getUserName() + "'s stats");

    // Updating the image to be the user's chosen avatar
    Image userAvatar =
        new Image(
            this.getClass()
                .getResource(
                    String.format("/images/profileImages/%d.PNG", currentProfile.getProfileIndex()))
                .toString());
    userProfileImage.setImage(userAvatar);

    // Displaying other user-related statistics
    updateStatistics();
  }

  /**
   * This method will update the current statistics, based on the current user profile, and display
   * it on screen.
   */
  private void updateStatistics() {
    // Updating win and loss counts
    winsLabel.setText(currentProfile.getWinsCount() + "");
    lossesLabel.setText(currentProfile.getLossesCount() + "");

    // Updating the highest prediction
    highestPredictionLabel.setText(Math.round(currentProfile.getHighestPrediction()) + "%");

    // Updating the word history and progress
    displayWordHistory();
    updateProgress();
  }

  /**
   * This method will display the word history using the Game statistic history and the FXML
   * components: accordion, and some title and anchored panes.
   */
  private void displayWordHistory() {
    // Getting a copy of the games and their statistics for each related to the
    // user.
    ArrayList<Game> games = new ArrayList<>(currentProfile.getHistoryOfGames());

    // Reversing order of history (of the copy, not the actual history).
    Collections.reverse(games);

    // Iterating through each game
    games.forEach(
        (game) -> {

          // Setting up content for each dropdown pane
          StringBuilder sb = new StringBuilder();
          sb.append("Played on " + game.getTimePlayed() + "\n");
          sb.append("Word Difficulty: ");

          // Adding the full word for each word difficulty
          switch (game.getWordDifficulty()) {
            case E:
              sb.append("Easy");
              break;
            case M:
              sb.append("Medium");
              break;
            case H:
              sb.append("Hard");
              break;
          }

          sb.append("\nResult: " + (game.getResult() ? "Won" : "Lost") + "\n");

          // Display accuracy on winning words
          if (game.getResult()) {
            sb.append(String.format("Accuracy: %.2f%%", game.getAccuracy()));
          }

          // Adding contents to dropdown content
          Label label = new Label(sb.toString());
          label.setPadding(new Insets(20));
          AnchorPane anchorPane = new AnchorPane(label);

          // Creating a titled pane
          TitledPane dropdown = new TitledPane(game.getWord(), anchorPane);
          dropdown.setStyle("-fx-text-fill: " + (game.getResult() ? "green" : "red"));

          // Adding each dropdown to accordion
          wordHistoryAccordion.getPanes().add(dropdown);
        });
  }

  /**
   * This method updates the progress bar and the supporting text, based on how many words the user
   * has played.
   */
  private void updateProgress() {

    try {
      // Creating new category selector instance to get the number of total words
      CategorySelector categorySelector = new CategorySelector();

      // Calculating the number of words played for each difficulty
      int easyWordsPlayed = currentProfile.getWordHistory(CategorySelector.Difficulty.E).size();
      int mediumWordsPlayed = currentProfile.getWordHistory(CategorySelector.Difficulty.M).size();
      int hardWordsPlayed = currentProfile.getWordHistory(CategorySelector.Difficulty.H).size();

      // Calculating total number of words per difficulty
      int easyWordsInTotal = categorySelector.getTotalWordCount(CategorySelector.Difficulty.E);
      int mediumWordsInTotal = categorySelector.getTotalWordCount(CategorySelector.Difficulty.M);
      int hardWordsInTotal = categorySelector.getTotalWordCount(CategorySelector.Difficulty.H);

      // Updating the actual progress bar with the number of words played for each difficulty
      easyProgressBar.setProgress((double) easyWordsPlayed / easyWordsInTotal);
      mediumProgressBar.setProgress((double) mediumWordsPlayed / mediumWordsInTotal);
      hardProgressBar.setProgress((double) hardWordsPlayed / hardWordsInTotal);

      // Updating the label alongside it with the actual value for each of the three difficulties
      easyProgressLabel.setText(Math.round(easyProgressBar.getProgress() * 100) + "%");
      mediumProgressLabel.setText(Math.round(mediumProgressBar.getProgress() * 100) + "%");
      hardProgressLabel.setText(Math.round(hardProgressBar.getProgress() * 100) + "%");

    } catch (IOException | RuntimeException | URISyntaxException | CsvException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * This method is called when the user presses the Go Back button on the Stats page.
   *
   * @param event the ActionEvent handler
   */
  @FXML
  private void onGoBack(ActionEvent event) {
    // Getting scene information
    Button button = (Button) event.getSource();
    Scene currentScene = button.getScene();

    try {
      // Playing the button sound on click
      SoundUtils soundPlayer = new SoundUtils();
      soundPlayer.playButtonSound();

      // Going back to main menu page
      currentScene.setRoot(App.loadFxml("main_menu"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
