import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
/**
 * This is the PlayerCar class. This class implements Rectangle so that we may
 * have "cars" for our players to control. The rectangle object makes hit detection
 * easy and somewhat reliable.
 */
public class PlayerCar extends Rectangle
{
	PlayerCar(int width, int height)
	{
		super(width,height);
		setFill(Color.BLUE);
	}
}