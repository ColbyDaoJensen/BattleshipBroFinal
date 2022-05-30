/*
  M. Hollingsworth
  14 Jan 2021
  Game class for Battleship game
*/

import static java.lang.System.*;
import java.util.Scanner;
import java.io.IOException;
import java.io.File;

public class Game
{
  private Board[] playerBoards;
  private Board curPlayer, curOpponent;
  private Scanner keyboard;
  private Scanner file;
  private boolean skipSetup;
  private boolean gameOver;
  
  public static final String CLEARSCREEN = "\033[H\033[2J";
  public static final String[] shipNames = {"Carrier",
    "Battleship", "Cruiser", "Submarine", "Destroyer"};
  public static final int[] shipSizes = {5, 4, 3, 3, 2};

  public Game() throws IOException
  {
    playerBoards = new Board[]{new Board(), new Board()};
    curPlayer = playerBoards[0];
    curOpponent = playerBoards[1];
    keyboard = new Scanner(System.in);
    skipSetup = false;
    gameOver = false;
  }

  public void play() throws IOException
  {
    startScreen();

    if(skipSetup)
    {
      playerBoards[0].setName("Player 1");
      playerBoards[0].presetBoard(1);
      playerBoards[1].setName("Player 2");
      playerBoards[1].presetBoard(2);
    }
    else
    {
      setupBoard(1);
      setupBoard(2);
    }
    
    while(!gameOver)
    {
      nextTurnScreen();
      gameOver = takeTurn();
      if(!gameOver)
        swapBoards();
    }
  }

  private void setupBoard(int player) throws IOException
  {
    out.println("*** PLAYER " + player + " SETUP ***\n");
    Board curBoard = playerBoards[player-1];
    boolean orient;

    out.print("Enter your name:\t");
    curBoard.setName(keyboard.nextLine().trim());
    out.println();

    for(int i = 0; i < shipNames.length; i++)
    {
      curBoard.printBoardForOwner();

      out.print("Place your " + shipNames[i].toUpperCase());
      out.println(" (" + shipSizes[i] + " pegs)...");

      out.print("Horizontal or vertical? Enter V or H:\t");
      String choice = keyboard.nextLine().trim();
      if(choice.equalsIgnoreCase("V"))
        orient = Board.VERTICAL;
      else if(choice.equalsIgnoreCase("H"))
        orient = Board.HORIZONTAL;
      else
      {
        out.println();
        i--;
        continue;
      }

      out.print("Enter location of top/leftmost peg:\t\t");
      String loc = keyboard.nextLine().trim();
      if(!curBoard.placeShip(loc, orient, i+1))
      {
        out.println();
        i--;
        continue;
      }

      out.println();
    }

    curBoard.printBoardForOwner();
    out.println("Player " + curBoard.getName() + " setup complete!");
    enterToContinue();
  }

  private void swapBoards()
  {
    Board temp = curPlayer;
    curPlayer = curOpponent;
    curOpponent = temp;
  }

  private boolean takeTurn() throws IOException
  {
    showCurPlayerBoard();
    out.print("---------- " + curPlayer.getName());
    out.println("'S TURN ----------\n");
    curOpponent.printBoardForOpponent();
    curOpponent.printStatusForOpponent();
    
    out.print("Choose a target:\t");
    String loc = keyboard.nextLine().trim();
    boolean hit = curOpponent.attack(loc);

    out.println(CLEARSCREEN);
    out.print("---------- " + loc.toUpperCase() + " IS A ");
    out.println((hit ? "HIT" : "MISS") + " ----------\n");
    curOpponent.printBoardForOpponent();
    curOpponent.printStatusForOpponent();

    out.println();
    enterToContinue();

    if(curOpponent.allSunk())
    {
      gameOverScreen();
      return true;
    }
    return false;
  }

  private void showCurPlayerBoard()
  {
    out.print("---------- " + curPlayer.getName());
    out.println("'S BOARD ----------\n");
    curPlayer.printBoardForOwner();
    curPlayer.printStatusForOwner();
    out.println();
    enterToContinue();
  }

  private void startScreen() throws IOException
  {
    String[] files = {"welcome.txt", "instructions.txt"};
    String code = "";
    for(String fileName : files)
    {
      file = new Scanner(new File(fileName));
      while(file.hasNext())
        out.println(file.nextLine());
      out.println("\n");
      code = enterToContinue();
      file.close();
    }
    if(code.equalsIgnoreCase("skip"))
      skipSetup = true;
  }

  private void nextTurnScreen() throws IOException
  {
    file = new Scanner(new File("nextturn.txt"));
    for(int i = 1; i <= 20; i++)
    {
      if(i == 6)
      {
        String name = curPlayer.getName();
        out.print("     |  ");
        out.print(name + "...");
        for(int j = 0; j < 14 - name.length(); j++)
          out.print(" ");
        out.println("|");
        file.nextLine();
      }
      else
        out.println(file.nextLine());
    }
    out.println("\n");
    enterToContinue();
    file.close();
  }

  private void gameOverScreen() throws IOException
  {
    file = new Scanner(new File("winner.txt"));
    for(int i = 1; i <= 20; i++)
    {
      if(i == 6)
      {
        String name = curPlayer.getName();
        out.print("          |  ");
        out.print(name + "...");
        for(int j = 0; j < 14 - name.length(); j++)
          out.print(" ");
        out.println("|");
        file.nextLine();
      }
      else
        out.println(file.nextLine());
    }
    file.close();
  }

  private String enterToContinue()
  {
    out.print("Press ENTER to continue... ");
    String code = keyboard.nextLine();
    out.println(CLEARSCREEN);
    return code;
  }
}