/*
  M. Hollingsworth
  14 Jan 2021
  Driver for Battleship game
*/

import java.io.IOException;
import static java.lang.System.*;

public class Main 
{
  public static void main(String[] args) throws IOException
  {
    try
    {
      Game game = new Game();
      game.play();
    }
    catch(IOException e)
    {
      out.println(Game.CLEARSCREEN);
      out.println("Terminated due to IOException:\n");
      out.println(e.getMessage());
    }
  }
}

/*
TO-DO:
update print status methods to include ship sizes
create game over/winner screen
loc validation on attack method
rework ship arrays/constants
*/