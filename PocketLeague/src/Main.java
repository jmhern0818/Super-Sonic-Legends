import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.scene.Parent;
import javafx.animation.PathTransition;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
///////
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
///////
/**
 * This is the main class. This holds all of the game logic and implements
 * the classes to create our game!
 * @author Jose Hernandez
 * @author Alex Olivarez
 * @author Ivan Castro
 * @author Sebastian Castellanos
 */
public class Main extends Application
{
	//declaring variables
	private static final double W = 720.0;
	private static final double H = 960.0;
	private Scene mainScene;
	private Stage primaryStage;
	private Pane pane;

	private PlayerCar playercar;
	private Ball ball;
	private ScoreBoard scoreboard;
	private Arena background;

	private Text playerscore;
	private Text opponentscore;

	private PlayerCar opponentcar;

	private Shape playerintersect;
	private Shape opponentintersect;
	private Bounds bounds;
	
	private Timeline ballmovement;
	private AnimationTimer ballmovements;

	//Playercar Variables for movement
	boolean running, goNorth, goSouth, goEast, goWest;
	//Opponentcar Variables for movement
	boolean runnings, goNorths, goSouths, goEasts, goWests;
	
	boolean kickOff = false;
	boolean redTouched = false;
	boolean blueTouched = false;
	/**
	   * This creates the main stage/visuals for gameplay 
	   * as well as the controls (WASD AND ARROW KEYS).
	   */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		music();
		this.primaryStage = primaryStage;
	//	bounds = pane.getBoundsInLocal();
		//playerintersect = Shape.intersect(ball, playercar);
		//opponentintersect = Shape.intersect(ball, opponentcar);

		// Setup the main stage
		pane = new Pane();
		mainScene = new Scene(pane, 720, 960);
		pane.setMaxSize(mainScene.getWidth(), mainScene.getWidth());

		// Create a background & scoreboard
		background = new Arena();
		scoreboard = new ScoreBoard();
		pane.getChildren().add(background.getIv());

		// Create group
		Group g = new Group(); //hold ball and stick
		pane.getChildren().add(g);

		// Create ball (game object)
		ball = new Ball(20);
		ball.setFill(Color.GREY);
		//ball.relocate(mainScene.getWidth()/2, mainScene.getHeight()/2);
		ball.relocate(mainScene.getWidth()/2-ball.getRadius(), mainScene.getHeight()/2-ball.getRadius()/2f);
		g.getChildren().add(ball);
		
		// Create Ball (Physics)
		

		// Create Players Car
		playercar = new PlayerCar(31, 61);
		playercar.relocate(mainScene.getWidth()/2-playercar.getWidth()/2f, mainScene.getHeight()-playercar.getHeight()-160);
		//playercar.setCursor(Cursor.HAND);
		//playercar.setOnMouseDragged(playerMouseDrag);
		g.getChildren().add(playercar);

		//Create Opponent car
		opponentcar = new PlayerCar(31, 61);
		opponentcar.setFill(Color.RED);
		opponentcar.relocate(mainScene.getWidth()/2-playercar.getWidth()/2f, mainScene.getHeight()-playercar.getHeight()-750);
		//opponentcar.setCursor(Cursor.HAND);
		//opponentcar.setOnMouseDragged(playerMouseDrag);
		g.getChildren().add(opponentcar);

		// Create Title text
		Text title = new Text("Welcome to Pocket League");
		title.setFont(Font.font("Tahoma", FontWeight.BOLD, 40));
		title.setTextAlignment(TextAlignment.CENTER);
		title.setLayoutX(85);
		title.setLayoutY(320);
		pane.getChildren().add(title);

		// Create player score
		playerscore = new Text("Player Score: " + scoreboard.getPlayerscore());
		playerscore.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
		playerscore.setFill(Color.WHITE);
		playerscore.setLayoutX(40);
		playerscore.setLayoutY(30);

		// Create opponent score
		opponentscore = new Text("Opponent Score: " + scoreboard.getOpponentscore());
		opponentscore.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
		opponentscore.setFill(Color.WHITE);
		opponentscore.setLayoutX(40);
		opponentscore.setLayoutY(50);

		//Reset button
		Hyperlink reset = new Hyperlink("Reset");
		reset.setLayoutX(40);
		reset.setLayoutY(60);
		reset.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent e)
			{
				scoreboard.setOpponentscore(0);
				scoreboard.setPlayerscore(0);
				reset();
			}
		});

		// Finalize scene
		primaryStage.setTitle("Pocket League");
		primaryStage.setScene(mainScene);
		primaryStage.setResizable(false);
		primaryStage.sizeToScene();
		primaryStage.show();

		// Create gameloop here
		ballmovement = new Timeline();
		ballmovement.getKeyFrames().add(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent t)
			{
				step();
			}
		}));
		ballmovement.setCycleCount(Timeline.INDEFINITE);
		
		//Tried making a ballmovement an animationTimer instead of Timeline to fix kickoff issue, but no avail :(
		ballmovements = new AnimationTimer()
		{
			@Override
			public void handle(long now)
			{
				step();
			}
		};

		
		//New game button
		Button newgame = new Button("Start Game");
		Platform.runLater(() ->
		{
			newgame.setLayoutX(pane.getWidth()/2-newgame.getWidth()/2);
			newgame.setLayoutY(640);
		});

		pane.getChildren().add(newgame);
		newgame.setOnAction(new EventHandler<ActionEvent> ()
		{
			@Override
			public void handle(ActionEvent arg0)
			{
				title.setVisible(false);
				newgame.setVisible(false);
				scoreboard.setOpponentscore(0);
				scoreboard.setPlayerscore(0);
				reset();
				//System.out.println("Button was pressed");
				pane.getChildren().addAll(playerscore, opponentscore,reset);
			}
		});
		
		//Car Movements
		AnimationTimer timer = new AnimationTimer()
		{
            @Override
            public void handle(long now)
            {

                double dx = 0.0, dy = 0.0;
                double dxz = 0.0, dyz = 0.0;

                if (goNorth) dy -= 2.0;
                if (goNorths) dyz -= 2.0;

                if (goSouth) dy += 2.0;
                if (goSouths) dyz += 2.0;

                if (goEast)  dx += 2.0;
                if (goEasts) dxz += 2.0;

                if (goWest)  dx -= 2.0;
                if (goWests) dxz -= 2.0;

                if (running) {dx *= 3; dy *= 3;}
                if (runnings) {dxz *= 3; dyz *= 3;}
                	
                movePlayerCarBy(dx, dy);
                moveOpponentCarBy(dxz, dyz);
            }
        };

        mainScene.setOnKeyReleased(new EventHandler<KeyEvent>()
        {
        	@Override
        	public void handle(KeyEvent event)
        	{
        		switch (event.getCode())
        		{
                	case UP:
                		//System.out.println("UP released!");
                		goNorth = false;
                		break;
                	case W:
                		//System.out.println("W released!");
                		goNorths = false;
                		break;
                	case DOWN:
                		//System.out.println("DOWN released!");
                		goSouth = false;
                		break;
                	case S:
                		//System.out.println("S released!");
                		goSouths = false;
                		break;
                	case LEFT:
                		//System.out.println("LEFT released!");
                		goWest  = false;
                		break;
                	case A:
                		//System.out.println("A released!");
                		goWests = false;
                		break;
                	case RIGHT:
                		//System.out.println("RIGHT released!");
                		goEast = false;
                		break;
                	case D:
                		//System.out.println("D released!");
                		goEasts = false;
                		break;
                	case SHIFT:
                		//System.out.println("SHIFT released!");
                		running = false;
                		break;
        		}
        	}
        });

        mainScene.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
        	@Override
        	public void handle(KeyEvent event)
        	{
        		switch (event.getCode())
        		{
           			case UP:
           				//System.out.println("UP Pressed!");
           				ballmovement.play();
           				goNorth = true;
           				break;
           			case W:
           				//System.out.println("W Pressed!");
           				goNorths = true;
           				break;
           			case DOWN:
           				//System.out.println("DOWN Pressed!");
           				goSouth = true;
           				break;
           			case S:
           				//System.out.println("S Pressed");
           				ballmovement.play();
           				goSouths = true;
           				break;
           			case LEFT:
           				//System.out.println("LEFT Pressed!");
           				goWest  = true;
           				break;
           			case A:
           				//System.out.println("A Pressed!");
           				goWests = true;
           				break;
           			case RIGHT:
           				//System.out.println("RIGHT Pressed!");
           				goEast = true;
           				break;
           			case D:
           				//System.out.println("D Pressed!");
           				goEasts = true;
           				break;
           			case SHIFT:
           				//System.out.println("SHIFT Pressed!");
           				running = true;
           				break;
        		}
        	}
        });
        timer.start();
	}
	
	//Main method, runs our game for us.
	public static void main(String[] args)
	{
		launch();
	}

	/**
	 * This is the main games step loop. All of the game logic happens here.
	 */

	private void step()
	{
		ball.step();

		bounds = pane.getBoundsInLocal();
		playerintersect = Shape.intersect(ball, playercar);
		opponentintersect = Shape.intersect(ball, opponentcar);
		
		//If the ball reaches right or left boundary, bounce ball
		if(ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius()) || ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius()))
		{
			ball.hitHorizontal();
		}

		//If the ball reaches the top, give player a point
		if(ball.getLayoutY() == (bounds.getMinY() + ball.getRadius()))
		{
			scoreboard.setPlayerscore(scoreboard.getPlayerscore()+1);
			ballmovement.pause();
			ballmovements.stop();
			ball.relocate(mainScene.getWidth()/2-ball.getRadius(), mainScene.getHeight()/2-ball.getRadius()/2f);
			ball.vertical = Math.abs(ball.vertical);
			//System.out.println("Red team scored!");
			goalSound();
			reset();
		}
		
		//If the ball reaches the bottom, give opponent a point
		if(ball.getLayoutY() == (bounds.getMaxY() - ball.getRadius()))
		{
			scoreboard.setOpponentscore(scoreboard.getOpponentscore()+1);
			ballmovement.pause();
			ballmovements.stop();
			ball.relocate(mainScene.getWidth()/2-ball.getRadius(), mainScene.getHeight()/2-ball.getRadius()/2f);
			ball.vertical = Math.abs(ball.vertical);
			//System.out.println("Blue team Scored!");
			goalSound();
			reset();
		}
		
		// Ball hits PlayerCar
		if(playerintersect.getBoundsInLocal().getWidth() != -1)
		{
			//System.out.println("Red team touched the ball!");
			ball.hitVertical();
			kickOff = true;
			redTouched = true;
		}

		//Ball hits OpponentCar
		if(opponentintersect.getBoundsInLocal().getWidth() != -1)
		{
			//System.out.println("Blue team touched the ball!");
			ball.hitVertical();
			kickOff = true;
		}

		//If player score reaches 10, reset game and display winning screen
		if(scoreboard.getPlayerscore() == 10)
		{
			playerwin();
			reset();
		}

		//If opponent score reaches 10, reset game and display loser screen
		if(scoreboard.getOpponentscore() == 10)
		{
			opponentwin();
			reset();
		}

		// Update score text
		playerscore.setText("Player Score: " + scoreboard.getPlayerscore());
		opponentscore.setText("Opponent Score: " + scoreboard.getOpponentscore());
	}

	/**
	 * Simple function to clamp a value with a min and max.
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */

	private double clamp(double value, double min, double max)
	{
		return Math.max(min, Math.min(max, value));
	}

	//Animation Events
	EventHandler<MouseEvent> playerMouseDrag = new EventHandler<MouseEvent> ()
	{
		@Override
		public void handle(MouseEvent m)
		{
			Rectangle r = ((Rectangle)(m.getSource()));
			double setX = (m.getScreenX()-primaryStage.getX()) - r.getWidth()/2;
			double setY = (m.getScreenY()-primaryStage.getY()) - r.getHeight();

			// Don't let the paddle outside of the screen!
			setX = clamp(setX, 0, mainScene.getWidth()-r.getWidth());
			setY = clamp(setY, 0, mainScene.getHeight()-r.getHeight());

			// Update position
			r.relocate(setX, setY);

			//Play the animation
			ballmovement.play();
		}
	};

	//Player car Movement
	private void movePlayerCarBy(double dx, double dy)
	{
        if (dx == 0.0 && dy == 0.0)
        	return;

        double cx = playercar.getBoundsInLocal().getWidth()  / 2.0;
        double cy = playercar.getBoundsInLocal().getHeight() / 2.0;

        double x = cx + playercar.getLayoutX() + dx;
        double y = cy + playercar.getLayoutY() + dy;

        movePlayerCarTo(x, y);
    }

	private void movePlayerCarTo(double x, double y)
	{
        double cx = playercar.getBoundsInLocal().getWidth()  / 2.0;
        double cy = playercar.getBoundsInLocal().getHeight() / 2.0;

        if (x - cx >= 0 && x + cx <= W && y - cy >= 0 && y + cy <= H)
        {
            playercar.relocate(x - cx, y - cy);
        }
	}

	//Opponent Car Movement
	private void moveOpponentCarBy(double dx, double dy)
	{
        if (dx == 0.0 && dy == 0.0)
        	return;

        double cx = opponentcar.getBoundsInLocal().getWidth()  / 2.0;
        double cy = opponentcar.getBoundsInLocal().getHeight() / 2.0;

        double x = cx + opponentcar.getLayoutX() + dx;
        double y = cy + opponentcar.getLayoutY() + dy;

        moveOpponentCarTo(x, y);
    }

	private void moveOpponentCarTo(double x, double y)
	{
        double cx = opponentcar.getBoundsInLocal().getWidth()  / 2.0;
        double cy = opponentcar.getBoundsInLocal().getHeight() / 2.0;

        if (x - cx >= 0 && x + cx <= W && y - cy >= 0 && y + cy <= H)
        {
            opponentcar.relocate(x - cx, y - cy);
        }
	}

	//Reset positions
	public void reset()
	{
		//Display new scores
		playerscore.setText("Player Score: " + scoreboard.getPlayerscore());
		opponentscore.setText("Opponent Score: " + scoreboard.getOpponentscore());

		//Reset ball position and pause it
		ball.relocate(mainScene.getWidth()/2-ball.getRadius(), mainScene.getHeight()/2-ball.getRadius()/2f);
		ballmovement.pause();

		//Reset car positions
		playercar.relocate(mainScene.getWidth()/2-playercar.getWidth()/2f, mainScene.getHeight() - playercar.getHeight()-160);
		opponentcar.relocate(mainScene.getX()+341, opponentcar.getHeight()+88);

		//System.out.println("Game has been reset!");
	}

	//Player win
	public void playerwin()
	{
		HBox winbox = new HBox(10);

		Scene win = new Scene(winbox,200,100);

		Text winner = new Text("Red Team wins!");
		winner.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		winner.setTextAlignment(TextAlignment.CENTER);
		winner.setLayoutX(100);
		winner.setLayoutY(50);
		winbox.getChildren().add(winner);

		Stage secondaryStage = new Stage();
		secondaryStage.setTitle("You win!");
		secondaryStage.setScene(win);
		secondaryStage.show();
		//Set scores to 0
		scoreboard.setOpponentscore(0);
		scoreboard.setPlayerscore(0);
	}

	//Opponent win
	public void opponentwin()
	{
		HBox losebox = new HBox(10);

		Scene lose = new Scene(losebox,150,150);

		Text loser = new Text("Blue Team Wins!");
		loser.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		loser.setTextAlignment(TextAlignment.CENTER);

		losebox.getChildren().addAll(loser);

		Stage secondaryStage = new Stage();
		secondaryStage.setTitle("You Lose!");
		secondaryStage.setScene(lose);
		secondaryStage.show();
		//Set scores to 0
		scoreboard.setOpponentscore(0);
		scoreboard.setPlayerscore(0);
	}
	
	//game music!
	MediaPlayer music;
	public void music()
	{
		String s = "sfx/song.mp3";
		Media h = new Media(Paths.get(s).toUri().toString());
		music = new MediaPlayer(h);
		music.play();
	}
	
	//sound for goal
	MediaPlayer goalSound;
	public void goalSound()
	{
		String s = "sfx/goalExplosion.mp3";
		Media h = new Media(Paths.get(s).toUri().toString());
		goalSound = new MediaPlayer(h);
		//System.out.println("BWAHHH");
		goalSound.play();
	}
}