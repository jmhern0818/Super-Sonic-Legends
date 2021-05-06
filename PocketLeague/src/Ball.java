import javafx.scene.shape.Circle;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.collision.shapes.*;
//import dev.DeveloperWASDControl;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Ball extends Circle
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
