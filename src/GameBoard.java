/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.1, Apr 2017
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * GameCourt
 * 
 * This class holds the primary game logic for how different objects interact with one another. Take
 * time to understand how the timer interacts with the different methods and how it repaints the GUI
 * on every tick().
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {


    
    private static JLabel status; // Current status text, i.e. "Running..."

    // Game constants
    public static final int COURT_WIDTH = 500;
    public static final int COURT_HEIGHT = 500;
    public static final int COMPUTER_ID = Board.COMPUTER_ID;
    public static final int PERSON_ID = Board.USER_ID;
    public static final int VELOCITY = 10;
    public static final int MAX_COLUMN = 6;
    public static final int[] columnCenters = {8, 78 ,148 ,219, 291, 360, 432};
    public static final int[] rowCenters = {75, 140, 205, 270, 338, 405};
    public static int pendingWin=-1;
    public static boolean active; //whether game should respond to key events
    public static boolean playing = false; //whether game is playing
    
    private boolean gameStarted;
    private Set<PlayerPiece> placedPieces = new HashSet<>();
    private boolean pendingComputerMove;
    private int currentColumn, lastPlayerMove;
    private BackgroundBoard bBoard;
    private GameMaster master;
    private PlayerPiece currentPiece;

    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 25;

    public GameBoard(JLabel stat) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        

        // The timer is an object which triggers an action periodically with the given INTERVAL. We
        // register an ActionListener with this timer, whose actionPerformed() method is called each
        // time the timer triggers. We define a helper method called tick() that actually does
        // everything that should be done in a single timestep.
        javax.swing.Timer timer = new javax.swing.Timer(INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        timer.start(); // MAKE SURE TO START THE TIMER!

        // Enable keyboard focus on the court area.
        // When this component has the keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        // This key listener allows the square to move as long as an arrow key is pressed, by
        // changing the square's velocity accordingly. (The tick method below actually moves the
        // square.)
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(active){
                    active=false;
                    if (e.getKeyCode() == KeyEvent.VK_LEFT && currentColumn>0) {
                        currentPiece.setVx(-1*VELOCITY);
                        currentColumn--;
                        currentPiece.targetY = currentPiece.getPy();
                        currentPiece.targetX = columnCenters[currentColumn];
                    } 
                    else if (e.getKeyCode() == KeyEvent.VK_RIGHT && currentColumn<MAX_COLUMN) {
                        currentPiece.setVx(VELOCITY);
                        currentColumn++;
                        currentPiece.targetY = currentPiece.getPy();
                        currentPiece.targetX = columnCenters[currentColumn];
                    } 
                    else if (e.getKeyCode() == KeyEvent.VK_ENTER && master.getLevel(currentColumn)>-1) {
                        currentPiece.setVy(VELOCITY);
                        currentPiece.targetX = currentPiece.getPx();
                        currentPiece.targetY = rowCenters[master.getLevel(currentColumn)];
                        pendingComputerMove = true;
                        lastPlayerMove = currentColumn;
                    } 
                    else{
                        active=true;
                    }
                }
                if(!gameStarted && e.getKeyCode() == KeyEvent.VK_SPACE){
                    gameStarted = true;
                    active = true;
                    playing = true;
                    status.setText("Game started");
                    if(GameMaster.getRandom(1,2)==2){ //randomly decide who goes first
                        doComputerMove();
                    }
                    else{
                        currentPiece = new PlayerPiece(COURT_WIDTH, COURT_HEIGHT, 1);
                        currentColumn = 3;
                    }
                }
                
                
            }
            
            public void keyReleased(KeyEvent e) {
            }
        });

        status = stat;
    }

    /**
     * (Re-)set the game to its initial state.
     */
    public void reset() {
        lastPlayerMove = -1;
        pendingWin = -1;
        master = new GameMaster();
        currentColumn = 3;
        active = false;
        gameStarted = false;
        pendingComputerMove=false;
        
        playing = false;
        bBoard = new BackgroundBoard(COURT_WIDTH, COURT_HEIGHT);
        

        // Make sure that this component has the keyboard focus
        requestFocusInWindow();
    }

    /**
     * This method is called every time the timer defined in the constructor triggers.
     */
    void tick() {
        if (playing) {
            if(withinRange(currentPiece.getPx(), currentPiece.getPy(), currentPiece.targetX, currentPiece.targetY) && (currentPiece.getVx()!=0 || currentPiece.getVy()!=0)){
                boolean isMoveHappening = currentPiece.getVy()!=0;
                currentPiece.setVy(0);
                currentPiece.setVx(0);
                if(isMoveHappening){
                    if(pendingComputerMove){
                        placedPieces.add(currentPiece);
                        endGameIfOver(master.updateBoard(currentColumn, Board.USER_ID));
                        if(playing){
                            doComputerMove();
                        }
                        
                    }
                    else{
                        placedPieces.add(currentPiece);
                        endGameIfOver(master.updateBoard(currentColumn, Board.COMPUTER_ID));
                        if(playing){
                            currentPiece = new PlayerPiece(COURT_WIDTH, COURT_HEIGHT, 1);
                            currentColumn = 3;
                            active=true;
                        }
                        
                    }
                }
                else{
                    active = true;
                }
                
                
            }
            currentPiece.move();

            // update the display
            repaint();
        }
    }
    
    private void doComputerMove(){

        currentPiece = new PlayerPiece(COURT_WIDTH, COURT_HEIGHT, 2);
        int move = master.respondToMove(lastPlayerMove);
        currentColumn=move;
        currentPiece.targetY = rowCenters[master.getLevel(move)];
        currentPiece.setPx(columnCenters[move]);
        currentPiece.setVy(VELOCITY);
        currentPiece.targetX = currentPiece.getPx();
        
        pendingComputerMove=false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(playing){
            currentPiece.draw(g);
        }
        drawAll(placedPieces, g);
        bBoard.draw(g);
        
        
    }
    
    private void drawAll(Set<PlayerPiece> pieces, Graphics g){
        Iterator<PlayerPiece> iter = pieces.iterator();
        while(iter.hasNext()){
            iter.next().draw(g);
        }
    }
    
    public boolean withinRange(int x, int y, int targetX, int targetY){
        return 2*Math.abs(x-targetX)<=VELOCITY && 2*Math.abs(y-targetY)<=VELOCITY;
    }
            
    public static void checkEndgame(int gameStatus){
        pendingWin = gameStatus;
    }
    
    public void endGameIfOver(int gameStatus){
        if(gameStatus==Board.USER_ID){
            playing = false;
            status.setText("Aw man... You win  :(");
        }
        else if(gameStatus==Board.COMPUTER_ID){
            playing = false;
            status.setText("Ha! You lose!");
        }
        else if(gameStatus==0){
            playing = false;
            status.setText("Oh well... It's a tie.");
        }
        else{
            status.setText("There are "+(-1*gameStatus)+" total moves left.");
        }
    }
    

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }
}