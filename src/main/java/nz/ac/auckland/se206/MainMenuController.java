package nz.ac.auckland.se206;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import nz.ac.auckland.se206.profiles.UserProfile;
import nz.ac.auckland.se206.profiles.UserProfileManager;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** This class is to handle any actions from the Main Menu page. */
public class MainMenuController {

  @FXML private Button speechButton;

  @FXML private ImageView profileImage;

  @FXML private Label greetingLabel;

  @FXML private ImageView emojiImage;

  @FXML private Label gameModeLabel;

  public static int gameMode = 0;

  /**
   * JavaFX calls this method once the GUI elements are loaded.
   *
   * @throws FileNotFoundException {@inheritDoc}
   */
  public void initialize() throws FileNotFoundException {

    // Setting current user to the current profile
    UserProfile currentUser = UserProfileManager.currentProfile;

    // Setting speech button icon
    Image icon = new Image(this.getClass().getResource("/images/sound.png").toString());
    speechButton.setGraphic(new ImageView(icon));

    // Setting the user's selected avatar image and displaying it.
    Image userProfileImage =
        new Image(
            this.getClass()
                .getResource(
                    String.format("/images/profileImages/%d.PNG", currentUser.getProfileIndex()))
                .toString());
    profileImage.setImage(userProfileImage);

    // Welcoming the user using a randomly-generated greeting
    setGreetingText(currentUser.getUserName());

    updateGameMode();
  }

  /**
   * This method changes the scene from the main menu to the waiting screen when the player clicks
   * on the 'Start a new game' button.
   *
   * @param event when the button is pressed
   */
  @FXML
  private void onStartGame(ActionEvent event) {
    // Getting the scene information
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();

    String currentMode = new String();

    // assigning game modes
    if (gameMode == 0 || gameMode == 2) {
      currentMode = "waiting";
    } else if (gameMode == 1) {
      currentMode = "hidden_word";
    }

    // Changing the scene to waiting screen
    try {
      sceneButtonIsIn.setRoot(App.loadFxml(currentMode));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This button leads the user to the statistics page, where they can see their profile stats.
   *
   * @param event the ActionEvent passed by JAVAFX
   */
  @FXML
  private void onSeeStats(ActionEvent event) {
    // getting scene information
    Button button = (Button) event.getSource();
    Scene currentScene = button.getScene();

    // changing to stats page
    try {
      currentScene.setRoot(App.loadFxml("stats"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * This method closes the window when the player clicks on the 'Exit game' Button.
   *
   * @param event when the button is pressed
   */
  @FXML
  private void onExitGame(ActionEvent event) {
    Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    currentStage.close();
  }

  /**
   * This method plays the sound to communicate with the player, when the player clicks the sound
   * button.
   */
  @FXML
  private void onPlaySound() {
    // Making a new thread for playing the sound
    Task<Void> speechTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            TextToSpeech textToSpeech = new TextToSpeech();

            // Notifying the user about the game title, and also telling them about
            // the available options.
            textToSpeech.speak(
                "Quick, draw", "Start a new game, check your stats, or exit the game");
            return null;
          }
        };

    // Starting the thread
    Thread speechThread = new Thread(speechTask);
    speechThread.start();
  }

  /**
   * This method will be called when the user clicks the Switch icon button, and will lead them to
   * the choose profile page.
   *
   * @param event the event handler
   */
  @FXML
  private void onSwitchUser(ActionEvent event) {
    // Getting the scene information
    Button button = (Button) event.getSource();
    Scene currentScene = button.getScene();

    // Change to choose profile page
    try {
      currentScene.setRoot(App.loadFxml("choose_profile"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * This method is called when the user wants to change settings i.e. when they click the button
   *
   * @param event the action event handler result
   */
  @FXML
  private void onChangeSettings(ActionEvent event) {
    // Getting the scene information
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();

    // Changing the scene to waiting screen
    try {
      sceneButtonIsIn.setRoot(App.loadFxml("settings"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is called when the user wants to view the badges by clicking the badges button.
   *
   * @param event this action event handler result
   */
  @FXML
  private void onViewBadges(ActionEvent event) {
    // Getting the scene information
    Button button = (Button) event.getSource();
    Scene currentScene = button.getScene();

    // Change to My badges page
    try {
      currentScene.setRoot(App.loadFxml("badges"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** This method is called when the user presses the left button for choosing game mode. */
  @FXML
  private void onModeLeft() {
    if (gameMode == 0) {
      // if the gamemode is on the first mode (i.e. normal mode), it returns to the last game mode.
      gameMode = 2;
    } else {
      gameMode--;
    }

    updateGameMode();
  }

  /**
   * This method is called when the user clicks on the right button when trying to switch game
   * modes.
   */
  @FXML
  private void onModeRight() {
    if (gameMode == 2) {
      // if the gamemode is the last one, it returns to the first one.
      gameMode = 0;
    } else {
      gameMode++;
    }

    updateGameMode();
  }

  /**
   * This method randomly chooses and displays a fun and trendy greeting from a list to appeal to
   * our target audience, young adult/teenagers.
   *
   * @param username the name of the user
   */
  private void setGreetingText(String username) {
    // Initialising greetings to choose from
    List<String> greetingList = Arrays.asList("Welcome,", "Hello", "Hey", "Hi", "What's up");

    // Randomly selecting the greeting
    Random random = new Random();
    greetingLabel.setText(
        String.format("%s %s", greetingList.get(random.nextInt(greetingList.size())), username));

    // Setting random emoji
    int emojiIndex = random.nextInt(8);
    Image emoji =
        new Image(
            this.getClass()
                .getResource(String.format("/images/emojis/%d.png", emojiIndex))
                .toString());
    emojiImage.setImage(emoji);
  }

  /**
   * This method is called every time we need to update the game mode. When the game mode is 0, it
   * is on 'Normal' mode, and when the game mode is 1, it is on 'Hidden Word' mode, and 2 is for
   * 'Zen' mode.
   */
  private void updateGameMode() {
    // Checking the current game mode and updating the labels and logics accordingly
    if (gameMode == 0) {
      gameModeLabel.setText("Normal");
    } else if (gameMode == 1) {
      gameModeLabel.setText("Hidden Word");
    } else if (gameMode == 2) {
      gameModeLabel.setText("Zen");
    }
  }
}
