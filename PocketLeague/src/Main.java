import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import java.nio.file.Paths;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.util.Duration;
import javafx.scene.media.Media;  
import javafx.scene.media.MediaPlayer;  
import javafx.scene.media.MediaView;

public class Main extends Application
{
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
	private Line opponentpath;
	private PathTransition opponenttransition;

	private Timeline ballmovement;

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		music();
		goalSound();
		this.primaryStage = primaryStage;

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
		ball.relocate(mainScene.getWidth()/2-ball.getRadius(), mainScene.getHeight()/2-ball.getRadius()/2f);
		g.getChildren().add(ball);

		// Create Players Car
		playercar = new PlayerCar(31, 61);
		playercar.relocate(mainScene.getWidth()/2-playercar.getWidth()/2f, mainScene.getHeight()-playercar.getHeight()-50);
		playercar.setCursor(Cursor.HAND);
		playercar.setOnMouseDragged(playerMouseDrag);
		g.getChildren().add(playercar);

		//Create opponent stick and path
		opponentcar = new PlayerCar(31,61);
		opponentcar.setFill(Color.BLUE);
		opponentcar.relocate(
				mainScene.getX()+opponentcar.getWidth()/2f,
				mainScene.getY() + opponentcar.getHeight()+15);
		g.getChildren().add(opponentcar);

		opponentpath = new Line(opponentcar.getX(),50,720-opponentcar.getWidth(),50);
		opponentpath.setVisible(false);
		g.getChildren().add(opponentpath);

		//Set Path Transition (follow this path essentially)
		opponenttransition = new PathTransition();
		opponenttransition.setNode(opponentcar);
		opponenttransition.setPath(opponentpath); 	//sets path node should follow
		opponenttransition.setDuration(Duration.millis(2000));		//Uses duration class to set time of animation
		opponenttransition.setCycleCount(Timeline.INDEFINITE);      //Sets amount of cycles animation will complete
		opponenttransition.autoReverseProperty().set(true);
		opponenttransition.play();

		// Create Title text
		Text title = new Text("Welcome to Pocket League");
		title.setFont(Font.font("Tahoma", FontWeight.BOLD, 40));
		title.setTextAlignment(TextAlignment.CENTER);
		title.setLayoutX(85);
		title.setLayoutY(320);
		pane.getChildren().add(title);

		// Create player score
		playerscore = new Text("Player Score: " + scoreboard.getPlayerscore());
		playerscore.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		playerscore.setLayoutX(50);
		playerscore.setLayoutY(50);

		// Create opponent score
		opponentscore = new Text("Opponent Score: " + scoreboard.getOpponentscore());
		opponentscore.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		opponentscore.setLayoutX(50);
		opponentscore.setLayoutY(70);

		//Reset button
		Hyperlink reset = new Hyperlink("Reset");
		reset.setLayoutX(50);
		reset.setLayoutY(90);
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

		// New game button
		Button newgame = new Button("New Game");
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
				ballmovement.play();
				pane.getChildren().addAll(playerscore, opponentscore,reset);

			}
		});
	}

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

		Bounds bounds = pane.getBoundsInLocal();
		Shape playerintersect = Shape.intersect(ball, playercar);
		Shape opponentintersect = Shape.intersect(ball, opponentcar);

		//If the ball reaches right or left boundary, bounce ball
		if(ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius()) ||
				ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius()) )
		{
			ball.hitHorizontal();
		}

		//If the ball reaches the top, give player a point
		if(ball.getLayoutY() == (bounds.getMinY() + ball.getRadius()))
		{
			scoreboard.setPlayerscore(scoreboard.getPlayerscore()+1);
			ballmovement.pause();
			ball.relocate(mainScene.getWidth()/2-ball.getRadius(), mainScene.getHeight()/2-ball.getRadius()/2f);
			ball.vertical = Math.abs(ball.vertical);
			reset();
		}

		//If the ball reaches the bottom, give opponent a point
		if(ball.getLayoutY() == (bounds.getMaxY() - ball.getRadius())) 
		{
			scoreboard.setOpponentscore(scoreboard.getOpponentscore()+1);
			ballmovement.pause();
			ball.relocate(mainScene.getWidth()/2-ball.getRadius(), mainScene.getHeight()/2-ball.getRadius()/2f);
			ball.vertical = Math.abs(ball.vertical);
			reset();
		}

		// Ball hits playercar
		if(playerintersect.getBoundsInLocal().getWidth() != -1) 
		{
			ball.hitVertical();
		}

		//Ball hits opponentcar
		if(opponentintersect.getBoundsInLocal().getWidth() != -1) 
		{
			ball.hitVertical();
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
	
	private double clamp( double value, double min, double max ) 
	{
		return Math.max(min, Math.min(max, value));
	}
    
	// Animation Events
	EventHandler<MouseEvent> playerMouseDrag = new EventHandler<MouseEvent> ()
	{
		@Override
		public void handle(MouseEvent m) 
		{
			Rectangle r = ((Rectangle)(m.getSource()));
			double setX = (m.getScreenX()-primaryStage.getX()) - r.getWidth()/2;
			double setY = (m.getScreenY()-primaryStage.getY()) - r.getHeight();
			
			// Don't let the paddle outside of the screen!
			setX = clamp( setX, 0, mainScene.getWidth()-r.getWidth());
			setY = clamp( setY, 0, mainScene.getHeight()-r.getHeight());
			
			// Update position
			r.relocate(setX, setY);
			
			//Play the animation
			ballmovement.play();
		}

	};
	
	EventHandler<KeyEvent> keyPressed = new EventHandler<KeyEvent> ()
	{
		@Override
		public void handle(KeyEvent e)
		{
			System.out.println("You pressed a key");
		}
	};

	//Reset positions
	public void reset() 
	{
		//Display new scores
		playerscore.setText("Player Score: " + scoreboard.getPlayerscore());
		opponentscore.setText("Opponent Score: " + scoreboard.getOpponentscore());

		//Reset ball position and pause it
		ball.relocate(mainScene.getWidth()/2-ball.getRadius(), mainScene.getHeight()/2-ball.getRadius()/2f);
		ballmovement.pause();

		//Reset paddle positions
		playercar.relocate(
				mainScene.getWidth()/2-playercar.getWidth()/2f,
				mainScene.getHeight()-playercar.getHeight()-50
		);
		opponentcar.relocate(
				mainScene.getX()+opponentcar.getWidth()/2f,
				mainScene.getY() + opponentcar.getHeight()+15
				);
	}

	//Player win
	public void playerwin() 
	{
		HBox winbox = new HBox(10);

		Scene win = new Scene(winbox,200,100);

		Text winner = new Text("You win! :)");
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

		Text loser = new Text("You Lose! :(");
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
	
	MediaPlayer music;
	public void music()
	{
		String s = "sfx/song.mp3";
		Media h = new Media(Paths.get(s).toUri().toString());
		music = new MediaPlayer(h);
		music.play();
	}
	
	MediaPlayer goalSound;
	public void goalSound()
	{
		String s = "sfx/goalExplosion.mp3";
		Media h = new Media(Paths.get(s).toUri().toString());
		goalSound = new MediaPlayer(h);
		goalSound.play();
	}
}


