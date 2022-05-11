package game;

import graph.AbstractGame;
import graph.G;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Sokoban extends AbstractGame {
  private int currentLevel;
  private static final Point LEFT = new Point(-1, 0);
  private static final Point RIGHT = new Point(1, 0);
  private static final Point UP = new Point(0, -1);
  private static final Point DOWN = new Point(0, 1);
  private Board board = new Board();

  public Sokoban() {
    super("Sokoban");
    currentLevel = 1;
    startGame();
  }

  private void startGame() {
    if (currentLevel == 1) {
      board.loadStringArray(level1);
    } else {
      board.loadStringArray(level2);
    }
  }

  public void paintComponent(Graphics g) {
    G.whiteBackground(g);
    drawInstruction(g);
    board.show(g);
    if (board.done()) {
      g.setColor(Color.RED);
      g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
      g.drawString("Nice Job!!", 450, 300);
      if (currentLevel == 1) {
        g.drawString("Press Space to try the next level", 450, 350);
        currentLevel++;
      }
    }
  }

  private static void drawInstruction(Graphics g) {
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial ", Font.PLAIN, 15));
    g.drawString("Use arrow keys to move the warehouse keeper. Press space to restart current level.", 20, 20);
  }

  @Override
  public void keyPressed(KeyEvent ke) {
    int vk = ke.getKeyCode(); // virtual key
    if (vk == KeyEvent.VK_LEFT) { board.go(LEFT);}
    if (vk == KeyEvent.VK_RIGHT) {board.go(RIGHT);}
    if (vk == KeyEvent.VK_UP) {board.go(UP);}
    if (vk == KeyEvent.VK_DOWN) {board.go(DOWN);}
    if (vk == KeyEvent.VK_SPACE) {
      startGame();
    }
    repaint();
  }

  //------------------------Board---------------------------
  private static class Board {
    private static final int N = 25;
    private char[][] b = new char[N][N];
    private Point person = new Point(0, 0);
    private static String boardStates = " WPCGgE";
    private static boolean onGoal = false; // track if player in on goal square
    private static Point dest = new Point(0, 0); // destination
    private static Color[] colors = {
        Color.white, // background color
        Color.darkGray,
        Color.green,
        Color.orange,
        Color.cyan,
        Color.blue,
        Color.red
    };
    private static final int xM = 50, yM = 50, W = 40;

    private Board() {clear();} // clear out the board (all white)

    private char ch(Point p) {return b[p.x][p.y];}
    private void set(Point p, char c) {b[p.x][p.y] = c;}
    private void movePerson() { // simple move to an empty or goal square
      boolean res = (ch(dest) == 'G');
      set(person, onGoal ? 'G' : ' '); // set value on square person is leaving
      set(dest, 'P');
      person.setLocation(dest);
      onGoal = res;
    }
    private void go(Point p) {
      dest.setLocation(person.x + p.x, person.y + p.y);
      if (ch(dest) == 'W' || ch(dest) == 'E') {return;} // don't walk into the wall
      if (ch(dest) == ' ' || ch(dest) == 'G') {movePerson(); return;}
      if (ch(dest) == 'C' || ch(dest) == 'g') { // move container. g is when a container is on the goal square
        dest.setLocation(dest.x + p.x, dest.y + p.y); // change dest to box dest
        if (ch(dest) != ' ' && ch(dest) != 'G') {return;} // illegal move
        set(dest, (ch(dest) == 'G') ? 'g' : 'C'); // put box in final spot
        dest.setLocation(dest.x - p.x, dest.y - p.y); // change dest to person dest
        set(dest, ch(dest) == 'g' ? 'G' : ' '); // change back the square where the box was
        movePerson();
      }
    }
    private boolean done() {
      for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
          if (b[j][i] == 'C') {
            return false;
          }
        }
      }
      return true;
    }
    private void clear() {for (int i = 0; i < N; i++) {for (int j = 0; j < N; j++) {b[i][j] = ' ';}}}
    private void show(Graphics g) {
      for (int r = 0; r < N; r++) {
        for (int c = 0; c < N; c++) {
          int ndx = boardStates.indexOf(b[c][r]); // index of the color[] aligns
          g.setColor(colors[ndx]);
          g.fillRect(xM + c * W, yM + r * W, W, W);
        }
      }
    }
    private void loadStringArray(String[] a) {
      person.setLocation(0, 0); // initially put at here which is a wall
      for (int r = 0; r < a.length; r++) {
        String s = a[r];
        for (int c = 0; c < s.length(); c++) {
          char ch = s.charAt(c);
          b[c][r] = (boardStates.indexOf(ch) > -1) ? ch : 'E'; // detect illegal characters
          if (ch == 'P' && person.x == 0) { // first time see this
            person.x = c;
            person.y = r;
          }
        }
      }
    }

  } //---------------------------Board------------------------------

  // Level maps
  private static String[] level1 = {
      "  WWWWW",
      "WWW   W",
      "WGPC  W",
      "WWW CGW",
      "WGWWC W",
      "W W G WW",
      "WC gCCGW",
      "W   G  W",
      "WWWWWWWW"
  };

  private static String[] level2 = {
      "    WWWWWW",
      "    WW   W",
      "    WWC  W",
      "  WWWW  CWW",
      "  WW  C C W",
      "WWWW W WW W   WWWWWW",
      "WW   W WW WWWWW  GGW",
      "WW C  C          GGW",
      "WWWWWW WWW WPWW  GGW",
      "    WW     WWWWWWWWW",
      "    WWWWWWWW"
  };

  @Override
  public void mouseClicked(MouseEvent e) {}

  @Override
  public void mousePressed(MouseEvent e) {}

  @Override
  public void mouseReleased(MouseEvent e) {}

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mouseDragged(MouseEvent e) {}

  @Override
  public void mouseMoved(MouseEvent e) {}

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {}

  @Override
  public void endGame() {}
}
