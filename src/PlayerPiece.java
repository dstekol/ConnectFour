/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.1, Apr 2017
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * A basic game object starting in the upper left corner of the game court. It is displayed as a
 * square of a specified color.
 */
public class PlayerPiece extends GameObj {
    public static final int SIZE = 68;
    public static final String USER_PIECE_FILE = "files/BluePiece.jpg";
    public static final String COMPUTER_PIECE_FILE = "files/RedPiece.jpg";
    private BufferedImage img;
    public static final int INIT_POS_X = GameBoard.columnCenters[3];
    public static final int INIT_POS_Y = 0;
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 0;
    public int targetX;
    public int targetY;

    //private Color color;

    /**
    * Note that, because we don't need to do anything special when constructing a Square, we simply
    * use the superclass constructor called with the correct parameters.
    */
    public PlayerPiece(int courtWidth, int courtHeight, int user) {
        super(INIT_VEL_X, INIT_VEL_Y, INIT_POS_X, INIT_POS_Y, SIZE, SIZE, courtWidth, courtHeight);
        try {
            if (img == null) {
                if(user==1){
                    img = ImageIO.read(new File(USER_PIECE_FILE));
                }
                else{
                    img = ImageIO.read(new File(COMPUTER_PIECE_FILE));
                }
                
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }
        targetX = INIT_POS_X;
        targetY = INIT_POS_Y;
        //this.color = color;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(img, this.getPx(), this.getPy(), this.getWidth(), this.getHeight(), null);
    }
}