package nz.ac.auckland.se206.words;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import nz.ac.auckland.se206.difficulty.DifficultyLevel;
import nz.ac.auckland.se206.difficulty.DifficultyLevel.Words;
import nz.ac.auckland.se206.profiles.UserProfileManager;

/** This class is for all things to do with word selection for each round. */
public class CategorySelector {

  /** This is the enum for the difficulty levels of the words. */
  public enum Difficulty {
    E,
    M,
    H
  }

  public static String chosenWord;
  public static Difficulty currentDifficulty;

  private Map<Difficulty, List<String>> difficultyToCategories;

  /**
   * This constructor essentially reads the csv file full of words and parses it into a hashmap that
   * has each key as the 3 different difficulties - E, M, and H.
   *
   * @throws IOException if the file cannot be found
   * @throws CsvException if the csv file cannot be read
   * @throws URISyntaxException if the URI is incorrect
   */
  public CategorySelector() throws IOException, CsvException, URISyntaxException {
    difficultyToCategories = new HashMap<>();

    // Initialising each (key,value) pair for hashmap
    for (Difficulty difficulty : Difficulty.values()) {
      difficultyToCategories.put(difficulty, new ArrayList<>());
    }

    // Reading the file and adding each word to the hashmap, according to their difficulty
    for (String[] line : getLines()) {
      difficultyToCategories.get(Difficulty.valueOf(line[1])).add(line[0]);
    }
  }

  /**
   * This method returns a random category (word) when called.
   *
   * @param difficulty the difficulty of the category (E, M or H)
   * @return the chosen category (word) to draw
   */
  public String getRandomCategory(Difficulty difficulty) {

    // Cloning of the word list so the original hashmap is not affected
    List<String> wordListCopy = difficultyToCategories.get(difficulty);

    // Removing all played words from the cloned list
    wordListCopy.removeAll(UserProfileManager.currentProfile.getWordHistory());

    // Returns a random word from the updated word list without played words
    return wordListCopy.get(new Random().nextInt(difficultyToCategories.get(difficulty).size()));
  }

  /**
   * This method returns all the lines from the csv file.
   *
   * @return the list of all lines as strings
   * @throws IOException if the file is not found
   * @throws CsvException all exceptions for opencsv
   * @throws URISyntaxException if a string cannot be used as a URI
   */
  protected List<String[]> getLines() throws IOException, CsvException, URISyntaxException {
    // Getting the csv file with all the word information
    File fileName =
        new File(CategorySelector.class.getResource("/category_difficulty.csv").toURI());

    // Trying to open the file and reading it
    try (FileReader fr = new FileReader(fileName, StandardCharsets.UTF_8);
        CSVReader reader = new CSVReader(fr)) {
      return reader.readAll();
    }
  }

  /**
   * This method sets a new chosen category and updates the static variable.
   *
   * @param difficulty the difficulty of the category (E, M or H)
   */
  public void setNewChosenWord(Difficulty difficulty) {
    // Updating the static variables so that other pages can access this information
    CategorySelector.chosenWord = getRandomCategory(difficulty);
    CategorySelector.currentDifficulty = difficulty;
  }

  /**
   * This method returns the chosen word.
   *
   * @return the chosen word
   */
  public String getChosenWord() {
    return CategorySelector.chosenWord;
  }

  /**
   * This method calculates the amount of words in the hashmap given the difficulty.
   *
   * @param difficulty difficulty of word list desired
   * @return the number of entries in this word list
   */
  public int getTotalWordCount(Difficulty difficulty) {
    return difficultyToCategories.get(difficulty).size();
  }

  /** This method sets the chosen word depending on the current user's difficulty level. */
  public void setWordWithDifficulty() {
    // Getting the difficulty level based on the users' chosen settings.
    Words wordsLevel = UserProfileManager.currentProfile.getDifficultyLevel().getWordsLevel();

    if (wordsLevel == DifficultyLevel.Words.E) {
      // Only the easy words will be chosen
      setNewChosenWord(Difficulty.E);

      // Medium difficulty - easy + medium words
    } else if (wordsLevel == DifficultyLevel.Words.M) {
      // Using weighted randomness to choose between easy or medium word
      double randomNumber = Math.random();

      // 30% easy, 70% medium word
      if (randomNumber < 0.3) {
        setNewChosenWord(Difficulty.E);
      } else {
        setNewChosenWord(Difficulty.M);
      }

      // Hard difficulty - easy + medium + hard words
    } else if (wordsLevel == DifficultyLevel.Words.H) {
      // Easy, medium or hard words
      double randomNumber = Math.random();

      // 10% easy, 30% medium, 60% hard word
      if (randomNumber < 0.1) {
        setNewChosenWord(Difficulty.E);
      } else if (randomNumber < 0.4) {
        setNewChosenWord(Difficulty.M);
      } else {
        setNewChosenWord(Difficulty.H);
      }

      // Master difficulty - hard words only
    } else {
      // Only the hard words will be chosen
      setNewChosenWord(Difficulty.H);
    }
  }
}
