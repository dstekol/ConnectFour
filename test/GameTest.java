import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.*;
import java.util.*;

public class GameTest{
    int[][] stuffs = {
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0}
    };
    
    private int[][] invertArr(int[][] start){
        int[][] inverted = new int[start[0].length][start.length];
        for(int x=0; x<start.length; x++){
            for(int y=0; y<start[0].length; y++){
                inverted[y][x] = start[x][y];
            }
        }
        return inverted;
    }
    
    private int[] autoLevel(int[][] arr){
        int[] level = new int[arr.length];
        int y;
        for(int x=0; x<arr.length; x++){
            y=0;
            while(y<=5 && arr[x][y]==0){y++;}
            level[x]=y-1;
        }
        return level;
    }

    @Test(timeout=1000)
    public void autoLevelTest(){
        int[][] stuff = {
        {2,0,0,0,0,0,0},
        {3,1,0,0,0,0,0},
        {2,0,0,0,0,0,0},
        {1,0,0,0,0,0,0},
        {2,3,0,0,0,0,0},
        {1,3,0,1,0,0,0}
        };
        int[] lev = new int[] {-1, 0, 5, 4, 5, 5, 5};
        assertEquals("auto level", Arrays.toString(lev), Arrays.toString(autoLevel(invertArr(stuff))));
        
    }
    
    @Test
    public void testNoConnect4(){
        int[][] stuff = {
        {2,0,0,0,0,0,0},
        {2,0,2,0,0,0,0},
        {2,0,0,2,0,2,0},
        {0,0,0,0,0,0,0},
        {0,0,0,2,0,2,0},
        {0,2,2,2,0,0,0}
        };
        Set<Point> t = new TreeSet<>();
        Board b = new Board(invertArr(stuff), 42, new int[] {5,5,5,5,5,5}, t, t);
        assertFalse(Board.isConnect4(b, 3, 5, 2));
        assertFalse(Board.isConnect4(b, 3, 4, 2));
        assertFalse(Board.isConnect4(b, 0, 1, 2));
        assertFalse(Board.isConnect4(b, 3, 2, 2));
        
    }
    
    @Test(timeout=1000)
    public void testIsConnect4(){
        int[][] stuff = {
        {2,0,0,0,0,0,0},
        {2,0,2,0,0,0,0},
        {2,0,0,2,0,2,0},
        {2,0,0,0,2,0,0},
        {0,0,0,2,0,2,0},
        {0,2,2,2,2,0,0}
        };
        Set<Point> t = new TreeSet<>();
        Board b = new Board(invertArr(stuff), 42, new int[] {5,5,5,5,5,5}, t, t);
        assertTrue(Board.isConnect4(b, 3, 5, 2));
        assertTrue(Board.isConnect4(b, 3, 4, 2));
        assertTrue(Board.isConnect4(b, 0, 0, 2));
        assertTrue(Board.isConnect4(b, 3, 2, 2));
        assertFalse(Board.isConnect4(b,6, 0, 2));
    }
    
    @Test(timeout=1000)
    public void testNoThreat(){
        int[][] stuff = {
        {0,2,0,2,0,2,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {2,2,2,0,0,0,0},
        {0,0,0,2,0,0,0},
        {0,2,2,0,2,0,0}
        };
        Set<Point> t = new TreeSet<>();
        Board b = new Board(invertArr(stuff), 42, new int[] {5,5,5,5,5,5}, t, t);
        assertEquals("no threat", t, Board.getNewThreats(b, 2, 5, 2));
        assertEquals("no threat", t, Board.getNewThreats(b, 4, 5, 2));
        assertEquals("no threat", t, Board.getNewThreats(b, 3, 0, 2));
        assertEquals("no threat", t, Board.getNewThreats(b, 1, 3, 2));
    }
    
    @Test
    public void testHorizThreat(){
        int[][] stuff = {
        {0,0,0,2,0,2,2},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,2,2,2,0,0},
        {0,0,0,2,0,0,0},
        {0,0,2,0,2,0,0}
        };
        Set<Point> t = new TreeSet<>();
        Set<Point> t1 = Point.toSet(new Point[] {
            new Point(1,2)
        });
        Set<Point> t2 = Point.toSet(new Point[] {
            new Point(1,3),
            new Point(5,3)
        });
        Set<Point> t3 = Point.toSet(new Point[] {
            new Point(5,2)
        });
        Set<Point> t4 = Point.toSet(new Point[] {
            new Point(1,2),
            new Point(1,3),
            new Point(5,3)
        });
        Set<Point> t5 = Point.toSet(new Point[] {
            new Point(4,0)
        });
        Board b = new Board(invertArr(stuff), 42, new int[] {5,5,5,5,5,5}, t, t);
        assertEquals("diag down threat", t1, Board.getNewThreats(b, 4, 5, 2));
        assertEquals("horiz double threat", t2, Board.getNewThreats(b, 3, 3, 2));
        assertEquals("diag up threat", t3, Board.getNewThreats(b, 2, 5, 2));
        assertEquals("diag down and horiz threat", t4, Board.getNewThreats(b, 2, 3, 2));
        assertEquals("horiz single threat", t5, Board.getNewThreats(b, 3, 0, 2));
    }
    
    
    @Test
    public void testShallowBoardCopy(){
        int[][] stuff = {
        {0,0,0,2,0,2,2},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,2,2,2,0,0},
        {0,0,0,2,0,0,0},
        {0,0,2,0,0,0,0}
        };
        Set<Point> t = new TreeSet<>();
        Set<Point> empty = new TreeSet<>();
        
        Board b = new Board(invertArr(stuff), 42, new int[] {5,5,5,5,5,5}, t, t);
        Board b1 = b.copy();
        b1.update(4, 2);
        Set<Point> t1 = Point.toSet(new Point[] {
            new Point(1,2)
        });
        assertEquals("new threats",t1,  b1.getThreats(2));
        assertEquals("old board threats unchanged", empty, b.getThreats(2));
        assertEquals("old board playboard unchanged", 0, b.getSpot(4, 5));
        assertEquals("new level", b1.getLevel(4), 4);
        assertEquals("old level same", b.getLevel(4), 5);
        
    }
    
    @Test
    public void testRecheckThreats(){
        int[][] stuff = {
        {0,0,0,2,0,2,2},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,2,2,2,0,0},
        {0,0,0,2,0,0,0},
        {0,0,2,0,0,0,0}
        };
        Set<Point> t = new TreeSet<>();
        Set<Point> t1 = new TreeSet<>();
        Set<Point> empty = new TreeSet<>();
        Set<Point> t2 = Point.toSet(new Point[] {
            new Point(1,2)
        });
        Board b = new Board(invertArr(stuff), 42, new int[] {5,2,5,5,5,5}, t, t1);
        b.update(4,2);
        assertEquals("new threats",t2,  b.getThreats(2));
        b.update(1, 3);
        assertEquals("destroyed threats",empty,  b.getThreats(2));
    }
    
    @Test
    public void testEasyWin(){
        int[][] stuff = {
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,2,2,0,2,0}
        };
        Set<Point> t = new TreeSet<>();
        Set<Point> t1 = new TreeSet<>();
        Board b = new Board(invertArr(stuff), 39, new int[] {5,5,4,4,5,4,5}, t, t1);
        MoveTree mtree = new MoveTree(b);
        assertEquals("easy win", 4, mtree.getBestMove());
    }
    
    @Test
    public void testEasyBlock(){
        int[][] stuff = {
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {1,1,1,0,0,0,0}
        };
        Set<Point> t = new TreeSet<>();
        Set<Point> t1 = new TreeSet<>();
        Board b = new Board(invertArr(stuff), 39, new int[] {4,4,4,5,5,5,5}, t, t1);
        MoveTree mtree = new MoveTree(b);
        int move = mtree.getBestMove();
        assertEquals("easy win", 3, move);
    }
    
    @Test
    public void testAnotherEasyBlock(){
        int[][] stuff = {
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,1},
        {0,0,0,0,0,0,2},
        {0,0,0,0,0,0,2},
        {0,1,1,1,0,0,2}
        };
        Set<Point> t = new TreeSet<>();
        Set<Point> t1 = new TreeSet<>();
        Board b = new Board(invertArr(stuff), 35, new int[] {5,4,4,4,5,5,1}, t, t1);
        MoveTree mtree = new MoveTree(b);
        int move = mtree.getBestMove();
        Board b1 = mtree.moveMap[1].conseq[0].b;
        assertTrue(move==0 || move==4);
    }
    
    @Test
    public void testDoubleTrap(){
        int[][] stuff = {
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,1,0,1,0,0,0}
        };
        Set<Point> t = new TreeSet<>();
        Set<Point> t1 = new TreeSet<>();
        Board b = new Board(invertArr(stuff), 40, new int[] {5,4,5,4,5,5,5}, t, t1);
        MoveTree mtree = new MoveTree(b);
        int move = mtree.getBestMove();
        assertTrue(move==0 || move==2 || move==4);
    }
    
    @Test 
    public void testBlockAtLeastOne(){
        int[][] stuff = {
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,2,0,0,1},
        {0,0,1,1,0,0,2},
        {2,1,1,1,2,0,2},
        {2,1,1,1,2,0,2}
        };
        Set<Point> t = new TreeSet<>();
        Set<Point> t1 = new TreeSet<>();
        int[][] stuff1 = invertArr(stuff);
        Board b = new Board(stuff1, 25, autoLevel(stuff1), t, t1);
        MoveTree mtree = new MoveTree(b);
        int move = mtree.getBestMove();
        assertEquals("block at least one", 2, move);
    }
    
    @Test(timeout=2000)
    public void testCreateThreat(){
        int[][] stuff = {
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0},
        {0,0,0,2,1,0,0},
        {0,0,2,0,0,0,0}
        };
        Set<Point> t = new TreeSet<>();
        Set<Point> t1 = new TreeSet<>();
        int[][] stuff1 = invertArr(stuff);
        Board b = new Board(stuff1, 25, autoLevel(stuff1), t, t1);
        MoveTree mtree = new MoveTree(b);
        int move = mtree.getBestMove();
        assertEquals("create threat", 4, move);
    }
    
    @Test
    public void testNetThreatChange(){
        Set<Point> init = Point.toSet( new Point[] {
            new Point(1,1),
            new Point(1,2),
            new Point(2, 1), 
            new Point(3,3)
            }
        );
        Set<Point> fin = Point.toSet( new Point[] {
            new Point(1,3),
            new Point(1,2),
                new Point(2, 1)
            }
        );
        int[] expChange = new int[] {1,2};
        assertEquals("threat change", expChange[0], MoveNode.compareThreatStates(init, fin)[0]);
        assertEquals("threat change", expChange[1], MoveNode.compareThreatStates(init, fin)[1]);
    }
    
    
}