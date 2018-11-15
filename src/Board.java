import java.util.*;
//import java.awt.Point;

public class Board
    {
    
    public static final int USER_ID = 1;
    public static final int COMPUTER_ID = 2;
    public static final int NUM_COLUMNS = 7;
    public static final int NUM_ROWS = 6;
    
    public int[][] board; //change to private
    public int numMovesLeft;
    public Set<Point> personThreats, myThreats;
    int[] level;
     


    public Board()
    {
        board = new int[NUM_COLUMNS][NUM_ROWS];
        numMovesLeft = NUM_ROWS*NUM_COLUMNS;
        level = new int[NUM_COLUMNS];
        Arrays.fill(level, NUM_ROWS - 1);
        personThreats = new TreeSet<>();
        myThreats = new TreeSet<>();
    }
    
    public Board(int[][] b, int numLeft, int[] lev, Set<Point> pThreats, Set<Point> mThreats) //private constructor for creating shallow copy
    {
        level = Arrays.copyOf(lev, lev.length);
        board = new int[NUM_COLUMNS][NUM_ROWS];
        numMovesLeft = numLeft;
        for(int i=0; i<b.length; i++){
            board[i] = Arrays.copyOf(b[i], b[i].length);
        }
        personThreats = new TreeSet<>();
        myThreats = new TreeSet<>();
        personThreats.addAll(pThreats);
        myThreats.addAll(mThreats);
    }

    public int getNumMovesLeft() //returns how many moves are left in game
    {
        return numMovesLeft;
    }

    public int update(int lastMove, int user) //returns code to signify game status (if over, num moves, etc)
    {
        if(lastMove < 0 || lastMove > NUM_COLUMNS || level[lastMove] == -1)
        {
            throw new IllegalArgumentException();
        }
        //System.out.println(lastMove+", "+level[lastMove]);
        board[lastMove][level[lastMove]] = user;
        int[] moveCoords = {lastMove, level[lastMove]};
        level[lastMove]--;
        numMovesLeft--;
        if(user==COMPUTER_ID){
            myThreats.addAll(getNewThreats(this, moveCoords[0], moveCoords[1], user));
        }
        else{
            personThreats.addAll(getNewThreats(this, moveCoords[0], moveCoords[1], user));
        }
        recheckOldThreats(COMPUTER_ID);
        recheckOldThreats(USER_ID);
        if(isConnect4(this, lastMove, level[lastMove]+1, user)){
            //int i=1/0;
            return user;
            
        }
        else{
            //int i=1/0;
            return -1*numMovesLeft;
        }
        
    }

    public Set<Point> getThreats(int user) //gets list of threats belonging to specified user id
    {
        Set<Point> threats = new TreeSet<>();
        if(user==USER_ID){
            threats.addAll(personThreats); //not encapsulated
        }
        else{
            threats.addAll(myThreats); //not encapsulated
        }
        return threats;
    }
    
    public int getLevel(int col){
        return level[col];
    }//gets current level in specified column

    public int getSpot(int x, int y) //returns which user occupies a specified place on the board
    {
        return board[x][y];
    }
    
    public void setSpot(int x, int y, int value) //returns which user occupies a specified place on the board
    {
        board[x][y]=value;
    }

    public int[] getDimensions() //gets board dims
    {
        return new int[] {NUM_COLUMNS, NUM_ROWS};
    }

    public Board copy() //return shallow copy of board
    {
        return new Board(board, numMovesLeft, level, personThreats, myThreats);
    }
    
    public static boolean isConnect4(Board b, int x, int y, int user) //returns true if there is a connect 4 around the specified coords
    {
        if(!checkWithinLimits(x, y)){
            throw new IllegalArgumentException();
        } //throws exception if out of bounds

        //check for vertical
        if (y <= 2)
        {

            if (b.getSpot(x, y + 1) == user && b.getSpot(x, y + 2) == user && b.getSpot(x, y + 3) == user)
            {
                return true;
            }
        }

        //check for horizontal
        int leftCount = 0, rightCount = 0;
        boolean leftLock = false, rightLock = false;
        for(int i=1; i<=3; i++)
        {
            if(checkWithinLimits(x-i, y) && !leftLock && b.getSpot(x - i, y) == user)
            {
                leftCount++;
            }
            else
            {
                leftLock = true;
            }
            if (checkWithinLimits(x + i, y) && !rightLock && b.getSpot(x + i, y) == user)
            {
                rightCount++;
            }
            else
            {
                rightLock = true;
            }
        }
        if (leftCount + rightCount >= 3)
        {
            return true;
        }

        //check for diagonal up
        leftCount = 0;
        rightCount = 0;
        leftLock = false;
        rightLock = false;
        for (int i = 1; i <= 3; i++)
        {
            if (checkWithinLimits(x - i, y - i) && !leftLock && b.getSpot(x - i, y - i) == user)
            {
                leftCount++;
            }
            else
            {
                leftLock = true;
            }
            if (checkWithinLimits(x + i, y + i) && !rightLock && b.getSpot(x + i, y + i) == user)
            {
                rightCount++;
            }
            else
            {
                rightLock = true;
            }
        }
        if (leftCount + rightCount >= 3)
        {
            return true;
        }

        //check for diagonal down
        leftCount = 0;
        rightCount = 0;
        leftLock = false;
        rightLock = false;
        for (int i = 1; i <= 3; i++)
        {
            if (checkWithinLimits(x - i, y + i) && !leftLock && b.getSpot(x - i, y + i) == user)
            {
                leftCount++;
            }
            else
            {
                leftLock = true;
            }
            if (checkWithinLimits(x + i, y - i) && !rightLock && b.getSpot(x + i, y - i) == user)
            {
                rightCount++;
            }
            else
            {
                rightLock = true;
            }
        }
        if (leftCount + rightCount >= 3)
        {
            return true;
        }

        return false;



    }
    
    private static boolean checkWithinLimits(int x, int y) //throws exception if coords are out of bounds
    {
        return (x >= 0 && y >= 0 && x < NUM_COLUMNS && y < NUM_ROWS);
    }
    
    public void recheckOldThreats(int userId) //checks whether any existing threats have been destroyed
    {
        Set<Point> threats = userId==COMPUTER_ID ? myThreats : personThreats;
        Iterator<Point> iter = threats.iterator();
        Point currentSpot;
        Set<Point> goneList = new TreeSet<>();
        while(iter.hasNext()){
            currentSpot = iter.next();
            if(board[currentSpot.x][currentSpot.y]!=0){
                goneList.add(currentSpot);
            }
        }
        iter = goneList.iterator();
        while(iter.hasNext()){
            threats.remove(iter.next());
        }
    }
    
    public static Set<Point> getNewThreats(Board b, int x, int y, int user) //returns set of threats around specified coords
    {
        checkWithinLimits(x, y); //throws exception if out of bounds
        Set<Point> threatHoles = new TreeSet<>();
        Set<Point> possibleThreatHoles = new TreeSet<>();
        Set<Point> tempThreatHoles = new TreeSet<>();
        int temp = b.getSpot(x,y);
        b.setSpot(x,y,user);
        //check for horizontal


        int leftCount = 0, rightCount = 0;
        int leftLock = 0, rightLock = 0;
        for (int i = 1; i <= 3; i++)
        {
            if (checkWithinLimits(x - i, y) && leftLock<2 && b.getSpot(x - i, y) == user)
            {
                leftCount++;
            }
            else
            {
                leftLock++;
                tempThreatHoles.add(new Point(x - i, y ));

            }
            if (checkWithinLimits(x + i, y) && rightLock<2 && b.getSpot(x + i, y) == user)
            {
                rightCount++;
            }
            else
            {
                rightLock++;
                tempThreatHoles.add(new Point(x + i, y ));


            }
        }
        if (leftCount + rightCount >= 2 && tempThreatHoles.size()>0)
        {
            possibleThreatHoles.addAll(tempThreatHoles);
        }

        //check for diagonal up
        //threatHoles = new List<int[]>();
        leftCount = 0;
        rightCount = 0;
        leftLock = 0;
        rightLock = 0;
        tempThreatHoles = new TreeSet<>();

        for (int i = 1; i <= 3; i++)
        {
            if (checkWithinLimits(x - i, y - i) && leftLock<2 && b.getSpot(x - i, y - i) == user)
            {
                leftCount++;
            }
            else
            {
                leftLock++;
                tempThreatHoles.add(new Point(x - i, y - i));
            }
            if (checkWithinLimits(x + i, y + i) && rightLock<2 && b.getSpot(x + i, y + i) == user)
            {
                rightCount++;

            }
            else
            {
                rightLock++;
                tempThreatHoles.add(new Point(x + i, y + i ));
            }
        }
        if (leftCount + rightCount >= 2 && tempThreatHoles.size()>0)
        {
            possibleThreatHoles.addAll(tempThreatHoles);
        }

        //check for diagonal down
        //threatHoles = new List<int[]>();
        leftCount = 0;
        rightCount = 0;
        leftLock = 0;
        rightLock = 0;
        tempThreatHoles = new TreeSet<Point>();

        for (int i = 1; i <= 3; i++)
        {
            if (checkWithinLimits(x - i, y + i) && leftLock<2 && b.getSpot(x - i, y + i) == user)
            {
                leftCount++;
            }
            else
            {
                leftLock++;
                tempThreatHoles.add(new Point(x - i, y + i ));
            }
            if (checkWithinLimits(x + i, y - i) && rightLock<2 && b.getSpot(x + i, y - i) == user)
            {
                rightCount++;
            }
            else
            {
                rightLock++;
                tempThreatHoles.add(new Point(x + i, y - i ));
            }
        }
        if (leftCount + rightCount >= 2 && tempThreatHoles.size()>0)
        {
            possibleThreatHoles.addAll(tempThreatHoles);
        }

        Iterator<Point> iter = possibleThreatHoles.iterator();
        Point coords;
        Set<Point> existing = b.getThreats(user);
        while(iter.hasNext()){
            coords = iter.next();
            if(checkWithinLimits(coords.x, coords.y) && isConnect4(b, coords.x, coords.y, user) && coords.y<5 && b.getSpot(coords.x, coords.y+1)==0 && !existing.contains(coords)){
                threatHoles.add(coords);
            }
        }
        b.setSpot(x,y,temp);
        return threatHoles;
    }
}