import java.util.*;

public class MoveTree{
    public MoveNode[] moveMap;
    public final Board startBoard;
    public static final int NUM_COLUMNS = Board.NUM_COLUMNS;
    public static final int NUM_ROWS = Board.NUM_ROWS;
    public static final int COMPUTER_ID = Board.COMPUTER_ID;
    public static final int USER_ID = Board.USER_ID;
    
    public MoveTree(Board b){
        startBoard = b;
        moveMap = new MoveNode[NUM_COLUMNS];
    }
    
    public int getBestMove(){ //builds tree of possible moves and evaluates which one is best
        for(int i=0; i<NUM_COLUMNS; i++){
            if(startBoard.getLevel(i)>=0){
                moveMap[i] = new MoveNode(startBoard.copy(), i, COMPUTER_ID, 0);
            }
            
        }
        MoveNode m = MoveNode.pickBest(moveMap);
        List<MoveNode> maybeMoves= new LinkedList<>();
        MoveNode[] sortedOpt = MoveNode.sortOptions(moveMap);
        for(int i=0; i<sortedOpt.length && sortedOpt[i]!=null && sortedOpt[i].compareTo(m)==0; i++){
            maybeMoves.add(sortedOpt[i]);
        }
        if(maybeMoves.size()>1){
            m = maybeMoves.get(GameMaster.getRandom(0,maybeMoves.size()-1));
        }
        
        int move = m.column;
        if(m.personWin){
            MoveNode m2=MoveNode.pickWorst(moveMap);
            if(m2.winInfo[2]==1){
                move = m2.winInfo[0];
            }
        }
        return move;
    }
}