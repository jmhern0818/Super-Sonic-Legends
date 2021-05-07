import javafx.scene.shape.Circle;
/**
 * This is the Ball class. This class implements Circle so that we may
 * have a ball for our players to hit and score. The Circle object makes hit detection
 * easy and reliable.
 */
 class Ball extends Circle
{
	double horizontal = 4.0; 
	double vertical = 2.0; 
	
	Ball(double radius)
	{
		super(radius);
	}

	public boolean isRight() 
	{
		return horizontal > 0.0;
	}

	public boolean isDown() 
	{
		return vertical > 0.0;
	}
	
	public void hitHorizontal() 
	{
		horizontal = -horizontal;
	}
	
	public void hitVertical() 
	{
		vertical = -vertical;
	}

	public void step() 
	{
		setLayoutX(getLayoutX() + horizontal);
		setLayoutY(getLayoutY() + vertical);
	}
	
}