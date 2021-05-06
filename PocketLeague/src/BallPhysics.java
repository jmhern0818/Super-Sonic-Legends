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

public class BallPhysics extends Application 
{
    public static double SCENE_WIDTH = 720;
    public static double SCENE_HEIGHT = 960;

    public static int SPRITE_COUNT = 1;

    public static Point2D FORCE_WIND = new Point2D(0, 0);
    public static Point2D FORCE_GRAVITY = new Point2D(0, 0.1);

    public static double SPRITE_ACCELERATION_SCALE = 0.5;
    public static double SPRITE_MAX_SPEED = 4;


    public static Random random = new Random();

    Pane playfield;

    List<Sprite> allSprites = new ArrayList<>();

    AnimationTimer gameLoop;

    Scene scene;

    @Override
    public void start(Stage primaryStage) 
    {

        // create containers
        BorderPane root = new BorderPane();

        // entire game as layers
        StackPane layerPane = new StackPane();

        // playfield for our Sprites
        playfield = new Pane();
        playfield.setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);

        layerPane.getChildren().addAll(playfield);

        root.setCenter(layerPane);

        scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

        // add content
        prepareGame();

        // run animation loop
        startGame();

    }

    private void prepareGame() 
    {
        // add sprites
        for (int i = 0; i < SPRITE_COUNT; i++) 
        {
            addSprite();
        }

    }

    private void startGame() 
    {

        // start game
        gameLoop = new AnimationTimer() 
        {
            @Override
            public void handle(long now) 
            {
                // physics: apply forces
                allSprites.forEach(s -> s.applyForce(FORCE_GRAVITY));
                allSprites.forEach(s -> s.applyForce(FORCE_WIND));

                // move
                allSprites.forEach(Sprite::move);

                // check boundaries
                allSprites.forEach(Sprite::checkBounds);

                // update in fx scene
                allSprites.forEach(Sprite::display);

            }
        };
        gameLoop.start();
    }

    private void addSprite() 
    {
        // random location
        double x = 720.0/2.0;
        double y = 960.0/2.0;

        // create sprite data
        Point2D location = new Point2D(x, y);
        Point2D velocity = new Point2D(0, 0);
        Point2D acceleration = new Point2D(0, 0);
        double mass = 50; // at least 20 pixels, max 50 pixels

        // create sprite and add to layer
        Sprite sprite = new Sprite(playfield, location, velocity, acceleration, mass);

        // register sprite
        allSprites.add(sprite);
    }

    public static class Sprite extends Region 
    {

        Point2D location;
        Point2D velocity;
        Point2D acceleration;

        double mass;
        double maxSpeed = SPRITE_MAX_SPEED;

        Node view;

        // view dimensions
        double width = 30;
        double height = width;
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double radius = width / 2.0;

        Pane layer;

        public Sprite( Pane layer, Point2D location, Point2D velocity, Point2D acceleration, double mass) 
        {

            this.layer = layer;

            this.location = location;
            this.velocity = velocity;
            this.acceleration = acceleration;
            this.mass = mass;

            // initialize view depending on mass
            width = mass;
            height = width;
            centerX = width / 2.0;
            centerY = height / 2.0;
            radius = width / 2.0;

            // create view
            Circle circle = new Circle( radius);
            circle.setCenterX(radius);
            circle.setCenterY(radius);

            circle.setStroke(Color.BLACK);
            circle.setFill(Color.GREY);

            this.view = circle;

            // add view to this node
            getChildren().add(view);

            layer.getChildren().add(this);
        }

        public void applyForce(Point2D force) 
        {
            Point2D f = force.multiply( 1.0 / mass);

            acceleration = acceleration.add(f);
        }

        /**
         * Move sprite
         */
        public void move() 
        {

            // set velocity depending on acceleration
            velocity = velocity.add(acceleration);

            // limit velocity to max speed
            if( velocity.magnitude() > maxSpeed) 
            {
                velocity = velocity.normalize().multiply(maxSpeed);
            }

            // change location depending on velocity
            location = location.add(velocity);

            // clear acceleration
            acceleration = new Point2D( 0, 0);
        }

        /**
         * Ensure sprite can't go outside bounds
         */
        public void checkBounds() 
        {

            double locationX = location.getX();
            double locationY = location.getY();
            double velocityX = velocity.getX();
            double velocityY = velocity.getY();

            if (location.getX() > layer.getWidth() - radius) 
            {
                locationX = layer.getWidth() - radius;
                velocityX *= -1;
            } else if (location.getX() < 0 + radius) 
            {
                velocityX *= -1;
                locationX = 0 + radius;
            }

            // reverse direction to bounce off floor
            if (locationY > layer.getHeight() - radius) 
            {
                velocityY *= -1;
                locationY = layer.getHeight() - radius;
            }

            location = new Point2D( locationX, locationY);
            velocity = new Point2D( velocityX, velocityY);
        }

        /**
         * Update node position
         */
        public void display() 
        {
            relocate(location.getX() - centerX, location.getY() - centerY);
        }
    }


    public static void main(String[] args) 
    {
        launch(args);
    }
}