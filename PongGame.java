package Assignments;


import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.*;
import java.awt.geom.*;
import java.util.*;

/**
 * @author: Derek Duong
 * @date Nov 10 2020
 * This is a two player pong game with player 1 being on the left and player two on the right.
 * The first player to reach 5 points wins. This program uses Swing and JFrames to create its graphics.
 *
 */

//pong game
public class PongGame extends JFrame implements ActionListener , KeyListener{

    //canvas constants
    public static final int CANVAS_WIDTH = 800;
    public static final int CANVAS_HEIGHT = 400;
    public static final Color CANVAS_BACKGROUND = Color.CYAN;

    //Buttons
    JButton btnRestart, btnExit;

    //custom drawing canvas
    private DrawCanvas canvas;

    //ball movement
     int deltax;
     int deltay;

     //paddle height and width
     int paddleWidth;
     int paddleHeight;

     //boolean checkers
     boolean started;
     boolean paused;
     boolean won = false;

     //left and right paddle
    private Rectangle2D.Double paddleLeft;
    private Rectangle2D.Double paddleRight;

    //makes a ball
    private Ellipse2D.Double ball;

    //left and right score
    int scrLeft;
    int scrRight;


    //list too keep track of pressed keys
    ArrayList<String> keyList;

    //timer and random number generator
    Random rand;
    Timer timer;


    /**
     *  constructor to set up gui
     *  and initialize starting values
     */
    public PongGame() {
        //setting up basic values and bounds
        timer = new Timer(10, this);
        rand = new Random();
        paddleWidth = 10;
        paddleHeight = 100;
        keyList = new ArrayList<>();
        paused = false;
        scrLeft = 0;
        scrRight = 0;

        //generates random number from 1 - 10
        int chance = rand.nextInt(11);

        //randomize the direction the ball is going
        deltax = chance > 5? 5:-5;
        deltay = chance > 5? 5:-5;

        //creating paddles
        paddleLeft = new Rectangle2D.Double(10, CANVAS_HEIGHT/2.5,paddleWidth, paddleHeight);
        paddleRight = new Rectangle2D.Double(CANVAS_WIDTH - 20, CANVAS_HEIGHT/2.5, paddleWidth, paddleHeight);

        //sets up ball
        ball = new Ellipse2D.Double(CANVAS_WIDTH/ 2- 10, CANVAS_HEIGHT/2, 20, 20);

        //set up a panel for the buttons
        JPanel btnPanel = new JPanel(null);
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setPreferredSize(new Dimension(100, 30));

        //restart button
        btnRestart = new JButton("Start/Restart");
        btnPanel.add(btnRestart);
        btnRestart.addActionListener(this);
        btnRestart.setBounds((CANVAS_WIDTH/2) - 150, 0, 150, 30);

        //exit button
        btnExit = new JButton("Exit");
        btnPanel.add(btnExit);
        btnExit.addActionListener(this);
        btnExit.setBounds(CANVAS_WIDTH/2, 0, 150, 30);

        // Set up a custom drawing JPanel
        canvas = new PongGame.DrawCanvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        //container layout
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        //adding buttons
        container.add(canvas, BorderLayout.CENTER);
        container.add(btnPanel, BorderLayout.SOUTH);

        //this fires the key event
        addKeyListener(this);
        //handles the close button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //sets title
        setTitle("Pong Game");
        pack();           // pack all the components in the JFrame
        setVisible(true); // show it
        requestFocus();// change the focus to JFrame to receive KeyEvent


    }

    /**
     * This method updates the screen of the game and repaints all of the movements of the game
     * it allows for the game to run
     */
    public void updateScreen() {
        //if the game is not paused
        if (!paused && !won) {

            //keeps ball inside screen (x-values)
            if (ball.x > CANVAS_WIDTH - 20) {
                deltax = -5;
                scrLeft++;//increases left score
            } else if (ball.x < 0) {
                deltax = 5;
                scrRight++;//increases right score
            }

            //keeps ball inside screen (y-values)
            if (ball.y > CANVAS_HEIGHT - 30) {
                deltay = -5;
            } else if (ball.y < 10) {
                deltay = 5;
            }

            //collision check
            if (paddleLeft.intersects(ball.getBounds())) {
                deltax = 5;
            }else if (paddleRight.intersects(ball.getBounds())) {
                deltax = -5;
            }

            //ball movement
            ball.x += deltax;
            ball.y += deltay;

            //move paddle up y-value if up is pressed (right paddle)
            if (keyList.contains(KeyEvent.VK_UP + "")) {
                paddleRight.y-=5;
            }

            //move paddle down y-value if down is pressed (right paddle)
            if (keyList.contains(KeyEvent.VK_DOWN + "")) {
                paddleRight.y+=5;
            }

            //move paddle up y-value if Z is pressed (Left paddle)
            if (keyList.contains(KeyEvent.VK_Q + "")) {
                paddleLeft.y-=5;
            }

            //move paddle down y-value if Z is pressed (Left paddle)
            if (keyList.contains(KeyEvent.VK_Z + "")) {
                paddleLeft.y+=5;
            }

            //resets position of left paddle
            if (paddleLeft.y > CANVAS_HEIGHT - paddleLeft.height) {
                paddleLeft.y = CANVAS_HEIGHT - paddleLeft.height;
            }

            //resets left paddle y value so it doesn't go out of bounds
            if (paddleLeft.y <= 0) {
                paddleLeft.y = 0;
            }
            //resets position of right paddle
            if (paddleRight.y > CANVAS_HEIGHT - paddleLeft.height) {
                paddleRight.y = CANVAS_HEIGHT - paddleLeft.height;
            }

            //resets right paddle y value so it doesn't go out of bounds
            if (paddleRight.y <= 0) {
                paddleRight.y = 0;
            }
        }

        //repaints canvas
        canvas.repaint();
    }

    /**
     * this method reads the actions performed on the buttons and acts accordingly depending on the button pushed
     * @param evt
     */
    @Override
    public void actionPerformed(ActionEvent evt) {

        //if the program hasn't started start is after the btnRestart is pressed
        if (!started && evt.getSource() == btnRestart) {
            started = true;

            //if exit is pressed close window
        } else if (evt.getSource()==btnExit) {
            System.exit(0);

            //update screen
        } else if (evt.getSource()==timer &&  started){
            updateScreen();

            //restart if restart button is pressed
        } else if (started && evt.getSource() == btnRestart) {
            restart();
        }

        requestFocus(); // change the focus to JFrame to receive KeyEvent
    }

    /**
     * This method resets all the values to their initial values to restart the game
     */
    public void restart() {
        //resetting the values
        keyList.clear();
        scrLeft = 0;
        scrRight = 0;
        int chance = rand.nextInt(11);

        //randomize the direction the ball is going in the beginning
        deltax = chance > 5? 5:-5;
        deltay = chance > 5? 5:-5;

        //right paddle position
        paddleRight.x = CANVAS_WIDTH - 20;
        paddleRight.y = CANVAS_HEIGHT/2.5;

        //left paddle position
        paddleLeft.x = 10;
        paddleLeft.y = CANVAS_HEIGHT/2.5;

        //ball position
        ball.x = CANVAS_WIDTH/2 - 10;
        ball.y = CANVAS_HEIGHT/2;

        //boolean values
        started = false;
        won = false;
        paused = false;

        //repaints
        canvas.repaint();

    }

    @Override
    public void keyTyped(KeyEvent e) { }

    /**
     * records the keys pressed in an array list to allow for there to be multiple keys pressed and read
     * also handles the pausing and unpausing of the game
     * @param evt
     */
    @Override
    public void keyPressed(KeyEvent evt) {
        // check if key presses is in the list, if not add it to the list of currently pressed keys
        if (!keyList.contains(evt.getKeyCode()+"")) {
            keyList.add(evt.getKeyCode() + "");
        }

    }

    /**
     * Once a key is pressed and released delete is from the array list of keys pressed
     * @param evt
     */
    @Override
    public void keyReleased(KeyEvent evt) {
        // remove from list of it exists
        if (keyList.contains(evt.getKeyCode()+"")){
            int index = keyList.indexOf(evt.getKeyCode()+"");
            keyList.remove(index);
        }
        //pauses the game
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            paused = !paused;
        }
    }

    /**
     * custom inner class that draws the graphics
     */
    class DrawCanvas extends JPanel{
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            //graphics component
            Graphics2D g2d = (Graphics2D) g;

            //if the state is paused paint specific screen
            if (paused) {
                g2d.setFont(new Font("Goudy Handtooled BT", Font.PLAIN, 80));

                g2d.drawString("Game Paused", (CANVAS_WIDTH/6), CANVAS_HEIGHT/2);
            }
            //if the program is started and not paused
            else if (started) {
                setBackground(CANVAS_BACKGROUND);

                g2d.setPaint(Color.BLACK);
                g2d.fill(paddleLeft);
                g2d.fill(paddleRight);

                g2d.setPaint(Color.white);
                g2d.fill(ball);
                // draw text
                g2d.setFont(new Font("Goudy Handtooled BT", Font.PLAIN, 80));
                g2d.drawString(scrLeft + "", (CANVAS_WIDTH / 2) - 100, 100);
                g2d.drawString(scrRight + "", (CANVAS_WIDTH / 2) + 40, 100);

                g.drawLine(CANVAS_WIDTH / 2, 0, CANVAS_WIDTH / 2, CANVAS_HEIGHT);

                g2d.setFont(new Font("Goudy Handtooled BT", Font.ITALIC, 20));
                g2d.setPaint(Color.BLACK);

                //checks the score and to see if any player has won (score is 5 to win)
                if (scrLeft == 5) {
                    //winner message
                    g2d.drawString("Player 1 Wins!", (CANVAS_WIDTH / 2) - 150, 150);
                    //game doesn't keep going now

                    won = true;

                    //checks right score
                } else if (scrRight == 5) {
                    //winner message
                    g2d.drawString( "Player 2 Wins!", (CANVAS_WIDTH / 2) + 40, 150);
                    //game doesn't keep going now
                    won = true;
                }

                //title screen
            } else {

                //setting the colours
                setBackground(Color.BLACK);

                g2d.setPaint(Color.BLACK);

                g2d.setPaint(Color.white);
                // instructions on title screen
                g2d.setFont(new Font("Goudy Handtooled BT", Font.ROMAN_BASELINE, 80));
                g2d.drawString("PONG", CANVAS_WIDTH/3, 100);
                g2d.setFont(new Font("Goudy Handtooled BT", Font.ROMAN_BASELINE, 20));
                g2d.drawString("Player 1: Use the 'Q' and 'Z' keys to move up or down",50, 150);
                g2d.drawString("Player 2: Use the 'up' and 'down' arrows to move up or down", 50, 200);
                g2d.drawString( "Reach 5 Points to Win!", 50, 300);


            }
        }
    }

    //starter for program
    public static void main(String[] args) {
        PongGame pg = new PongGame();
        pg.timer.start();
    }
}
