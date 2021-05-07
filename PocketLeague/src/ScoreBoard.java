
/**
 * 	This is the ScoreBoard class that holds the logic and return values for our scoreboard.
 * 	This is so we can keep track of who is winning/losing and who wins/loses the game.
 */
public class ScoreBoard 
{
	int playerscore;
	int opponentscore;
	
	ScoreBoard()
	{
		this.playerscore = 0;
		this.opponentscore = 0;
	}

	public int getPlayerscore() 
	{
		return playerscore;
	}

	public void setPlayerscore(int playerscore) 
	{
		this.playerscore = playerscore;
	}

	public int getOpponentscore() 
	{
		return opponentscore;
	}

	public void setOpponentscore(int opponentscore) 
	{
		this.opponentscore = opponentscore;
	}
}