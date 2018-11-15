import java.util.*;
//import java.awt.Point;

public class MoveNode implements Comparable<MoveNode>{
    public Board b;
    public boolean personWin, myWin;
    public int personThreatsDestroyed, myThreatsDestroyed, personThreatsBuilt, myThreatsBuilt, netThreatChange;
    public Set<Point> initPersonThreats, initMyThreats, finalPersonThreats, finalMyThreats;
    public MoveNode[] conseq;
    public int[] winInfo;
    public final int column;
    public static final int MAX_DEPTH = 4;
    public static final int NUM_COLUMNS = Board.NUM_COLUMNS;
    public static final int NUM_ROWS = Board.NUM_ROWS;
    public static final int COMPUTER_ID = Board.COMPUTER_ID;
    public static final int PERSON_ID = Board.USER_ID;
    boolean feasible;
    
    
    public MoveNode(Board brd, int move, int user, int depth){
        b = brd;
        column = move;
        winInfo = new int[] {0,0,0};
        conseq = new MoveNode[NUM_COLUMNS];
        initPersonThreats = b.getThreats(PERSON_ID);
        initMyThreats = b.getThreats(COMPUTER_ID);
        int result = b.update(move, user);
        finalPersonThreats = b.getThreats(PERSON_ID);
        finalMyThreats = b.getThreats(COMPUTER_ID);
        
        //figure out threat differences
        int[] personThreatResults = compareThreatStates(initPersonThreats, finalPersonThreats);
        personThreatsBuilt = personThreatResults[0];
        personThreatsDestroyed = personThreatResults[1];
        
        int[] myThreatResults = compareThreatStates(initMyThreats, finalMyThreats);
        myThreatsBuilt = myThreatResults[0];
        myThreatsDestroyed = myThreatResults[1];
        
        netThreatChange = myThreatsBuilt + personThreatsDestroyed - personThreatsBuilt - myThreatsDestroyed;
        
        if(result>=0){
            if(result==COMPUTER_ID){
                myWin=true;
                winInfo = new int[] {column, b.getLevel(column)+1, depth};
            }
            else if(result==PERSON_ID){
                personWin=true;
                winInfo = new int[] {column, b.getLevel(column)+1, depth};
            }
            //if tie, just return (can't go deeper)
            return;
        }
        
        int nextId = user==COMPUTER_ID ? PERSON_ID : COMPUTER_ID;
        if(depth!=MAX_DEPTH){
            for(int i=0; i<NUM_COLUMNS; i++){
                if(b.getLevel(i)>=0){
                    conseq[i] = new MoveNode(b.copy(), i, nextId, depth+1);
                }
                
            }
        }
        if(depth<MAX_DEPTH){
            //MoveNode combine;
            if(user==COMPUTER_ID){
                this.combineWith(pickWorst(conseq));
            }
            else{
                this.combineWith(pickBest(conseq));
            } 
        }
        
        
    }
    
    public static MoveNode pickWorst(MoveNode[] mNodes) //out of given movenodes, returns the one with worst outcome
    { 
        MoveNode[] arr = sortOptions(mNodes);
        return arr[0];
    }
    
    public static MoveNode pickBest(MoveNode[] mNodes) //out of given movenodes, returns the one with best outcome
    {
        MoveNode[] arr = sortOptions(mNodes);
        return arr[arr.length-1];
    }
    
    public static MoveNode[] sortOptions(MoveNode[] m){
        List<MoveNode> l = new ArrayList<>();
        for(int i=0; i<m.length; i++){
            if(m[i]!=null){
                l.add(m[i]);
            }
        }
        MoveNode[] arr = l.toArray(new MoveNode[l.size()]);
        Arrays.sort(arr);
        return arr;
    }
    
    private void combineWith(MoveNode m) //combines outcome a given movenode (aka move scenario) with outcome of its consequences
    {
        if(this.personWin || this.myWin || m==null){ 
            return;
        }
        if(m.personWin){
            this.personWin = true;
            this.winInfo = m.winInfo;
        }
        else if(m.myWin){
            this.myWin = true;
            this.winInfo = m.winInfo;
        }
        this.myThreatsBuilt += m.myThreatsBuilt;
        this.myThreatsDestroyed += m.myThreatsDestroyed;
        this.personThreatsBuilt += m.personThreatsBuilt;
        this.personThreatsDestroyed += m.personThreatsDestroyed;
        netThreatChange = myThreatsBuilt + personThreatsDestroyed - personThreatsBuilt - myThreatsDestroyed;
        
        
    }
    
    public static int[] compareThreatStates(Set<Point> initialThreats, Set<Point> finalThreats) //counts how many threats were created and destroyed
    {
        int[] change = {0,0};
        Iterator<Point> iter = initialThreats.iterator();
        while(iter.hasNext()){
            if(!finalThreats.contains(iter.next())){
                change[1]++;
            }
        }
        iter = finalThreats.iterator();
        while(iter.hasNext()){
            if(!initialThreats.contains(iter.next())){
                change[0]++;
            }
        }
        return change;
    }
    
    public int compareTo(MoveNode m){
        if(netThreatChange==m.netThreatChange && m.personWin==personWin && winInfo[2]==m.winInfo[2] && m.myWin==myWin){
            return 0;
        }
        else if(myWin && m.myWin){
            return m.winInfo[2] - winInfo[2];
        }
        else if(personWin && m.personWin){
            return winInfo[2] - m.winInfo[2];
        }
        else if(myWin && !m.myWin){
            return 1;
        }
        else if(m.myWin && !myWin){
            return -1;
        }
        else if(personWin && !m.personWin){
            return -1;
        }
        else if(!personWin && m.personWin){
            return 1;
        }
        else{
            return netThreatChange - m.netThreatChange;
        }
    }
        
}