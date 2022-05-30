/*
  M. Hollingsworth
  14 Jan 2021
  Board class for Battleship game
*/

import static java.lang.System.*;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Board
{
  private String name;
  private int[][] board;
  private int[] shipStrengths;
  private Scanner file;

  private final int EMPTY = 0;
  private final int MISS = -1;
  private final int HIT = -2;
  public static final int CARRIER = 1;
  public static final int BATTLESHIP = 2;
  public static final int CRUISER = 3;
  public static final int SUBMARINE = 4;
  public static final int DESTROYER = 5;
  public static final boolean VERTICAL = true;
  public static final boolean HORIZONTAL = false;

  public Board()
  {
    board = new int[10][10];
    shipStrengths = Arrays.copyOf(Game.shipSizes, Game.shipSizes.length);
  }

  public void setName(String name)
  {
    this.name = name.toUpperCase();
  }

  public String getName()
  {
    return name;
  }

  public boolean placeShip(String loc, boolean vert, int type)
  {
    //validation??
    loc = loc.toUpperCase();
    int row = loc.charAt(0) - 65;
    int col = Integer.parseInt(loc.substring(1)) - 1;
    if(vert)
      return placeShipVertical(row, col, type);
    else
      return placeShipHorizontal(row, col, type);
  }

  private int shipTypeSize(int type)
  {
    switch(type)
    {
      case CARRIER:     return 5;
      case BATTLESHIP:  return 4;
      case CRUISER:     return 3;
      case SUBMARINE:   return 3;
      case DESTROYER:   return 2;
      default:          return 0;
    }
  }

  private boolean placeShipVertical(int row, int col, int type)
  {
    int size = shipTypeSize(type);
    if(row + size > board.length)
      return false;
    
    int checksum = 0;
    for(int r = row; r < row + size; r++)
      checksum += board[r][col];
    if(checksum != EMPTY)
      return false;
    
    for(int r = row; r < row + size; r++)
      board[r][col] = type;
    return true;
  }

  private boolean placeShipHorizontal(int row, int col, int type)
  {
    int size = shipTypeSize(type);
    if(col + size > board[0].length)
      return false;

    int checksum = 0;
    for(int c = col; c < col + size; c++)
      checksum += board[row][c];
    if(checksum != EMPTY)
      return false;
    
    for(int c = col; c < col + size; c++)
      board[row][c] = type;
    return true;
  }

  //loc validation??
  public boolean attack(String loc)
  {
    loc = loc.toUpperCase();
    if(loc.length() < 2 || loc.length() > 3)
      return false;
    int row = loc.charAt(0) - 'A';
    int col = Integer.parseInt(loc.substring(1)) - 1;
    if(row < 0 || row > 9 || col < 0 || col > 9)
      return false;
    int val = board[row][col];
    boolean hit = false;

    if(val > 0)
    {
      shipStrengths[val-1]--;
      hit = true;
    }
    if(val != HIT && val != MISS)
      board[row][col] = hit ? HIT : MISS;
    return hit;
  }

  public void printBoardForOpponent()
  {
    out.println("    1 2 3 4 5 6 7 8 9 10");
    for(int r = 0; r < 10; r++)
    {
      out.print((char)(r + 65) + " | ");
      for(int c = 0; c < 10; c++)
      {
        switch(board[r][c])
        {
          case MISS:  out.print("O "); break;
          case HIT:   out.print("X "); break;
          default:    out.print("~ "); break;
        }
      }
      out.println("|");
    }
    out.println();
  }

  public void printBoardForOwner()
  {
    out.println("    1 2 3 4 5 6 7 8 9 10");
    for(int r = 0; r < 10; r++)
    {
      out.print((char)(r + 65) + " | ");
      for(int c = 0; c < 10; c++)
      {
        switch(board[r][c])
        {
          case MISS:
          case EMPTY: out.print("~ ");  break;
          case HIT:   out.print("X ");  break;
          default:    out.print("# ");  break;
        }
      }
      out.println("|");
    }
    out.println();
  }

  public void printStatusForOwner()
  {
    out.println("*** STATUS OF " + name + "'S FLEET ***");
    for(int i = 0; i < Game.shipNames.length; i++)
    {
      out.print(Game.shipNames[i] + ":\t\t");
      out.println(shipStrengths[i]);
    }
    out.println();
  }

  public void printStatusForOpponent()
  {
    out.println("*** STATUS OF " + name + "'S FLEET ***");
    for(int i = 0; i < Game.shipNames.length; i++)
    {
      out.print(Game.shipNames[i] + ":\t\t");
      out.println(shipStrengths[i] == 0 ? "SUNK" : "in play");
    }
    out.println();
  }

  public boolean allSunk()
  {
    for(int strength : shipStrengths)
      if(strength > 0)
        return false;
    return true;
  }

  public void presetBoard(int player) throws IOException
  {
    file = new Scanner(new File("presetBoards.txt"));
    if(player == 2)
      for(int i = 0; i < 10; i++)
        file.nextLine();
    for(int r = 0; r < board.length; r++)
      for(int c = 0; c < board[0].length; c++)
        board[r][c] = file.nextInt();
    file.close();
  }
}