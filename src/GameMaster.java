import java.util.concurrent.ThreadLocalRandom;


public class GameMaster{
    private Board currentBoard;
    
    public GameMaster(){
        currentBoard = new Board();
    }
    
    public int respondToMove(int userMove){
        int myMove;
        if(userMove!=-1){
            MoveTree mtree = new MoveTree(currentBoard);
            myMove = mtree.getBestMove();
        }
        else{
            myMove = 3; //if first move of game, go in middle column by default
        }
        
        return myMove;
        
    }
    
    public int updateBoard(int col, int user){
        return currentBoard.update(col, user);
    }
    
    public int getLevel(int col){
        return currentBoard.getLevel(col);
    }
    
    public static int getRandom(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}