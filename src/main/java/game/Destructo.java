package game;

import graph.AbstractGame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.Timer;

public class Destructo extends AbstractGame implements ActionListener {
  private static final int nR = 15, nC = 13; // number of rows and columns (rows are actually horizontal x)
  private static final int w = 50, h = 30;
  private static int xM, yM;
  private static final Color[] color = {
      Color.lightGray, // background color
      Color.cyan,
      Color.green,
      Color.yellow,
      Color.red,
      Color.pink
  };
  private static Timer timer;
  private static Random RANDOM = new Random();

  private int rnd(int k) {return RANDOM.nextInt(k);}
  private int[][] grid = new int[nC][nR]; // horizontal is x, vertical is y
  private int brickRemaining = nR * nC;

  public Destructo() {
    super("Destructo");
    xM = 100; yM = 100;
    rndColors(3);
    timer = new Timer(30, this); // 30 is in milliseconds. 30 frames a second - a convenient number
    timer.start();
  }

  public void paintComponent(Graphics g) {
    g.setColor(color[0]); // set background color
    g.fillRect(0, 0, 5000, 5000);
    showGrid(g);
    bubbleSort(); // won't do anything if nothing to bubble sort
    if (slideCol()) { // won't do anything if no empty col
      xM += w / 2;
    }
    g.setColor(Color.black);
    g.drawString("Remaining bricks: " + brickRemaining, 50, 25);
  }

  private void rndColors(int k) {
    for (int c = 0; c < nC; c++) {
      for (int r = 0; r < nR; r++) {
        grid[c][r] = 1 + rnd(k); // bias up to avoid background color (skip 0)
      }
    }
  }

  private void showGrid(Graphics g) {
    for (int c = 0; c < nC; c++) {
      for (int r = 0; r < nR; r++) {
        g.setColor(color[grid[c][r]]);
        g.fillRect(x(c), y(r), w, h); // round by a box
      }
    }
  }

  private int x(int c) {return xM + c * w;}
  private int y(int r) {return yM + r * h;}
  private int c(int x) {return (x - xM) / w;}
  private int r(int y) {return (y - yM) / h;}

  @Override
  public void mouseClicked(MouseEvent me){
    int x = me.getX(), y = me.getY();
    if (x < xM || y < yM) {return;} // first check here to avoid c() and r() math bug
    int r = r(y), c = c(x);
    if (r < nR && c < nC) {
      rcAction(r, c);
    }
  }

  private void rcAction(int r, int c) {
    if (infectable(c, r)) {
      infect(c, r, grid[c][r]);
      repaint();
    }
  }

  private void infect(int c, int r, int v) { // v is the color we are looking for
    if (grid[c][r] != v) {return;} // base case
    grid[c][r] = 0; // change back to background color to kill this cell before infecting neighbors
    brickRemaining--;
    if (r > 0) {infect(c, r - 1, v);} // can infect the r - 1 one
    if (c > 0) {infect(c - 1, r, v);}
    if (r < nR - 1) {infect(c, r + 1, v);}
    if (c < nC - 1) {infect(c + 1, r, v);}
  }

  private boolean infectable(int c, int r) {
    int v = grid[c][r];
    if (v == 0) {return false;} // not allowed to infect background cell
    if (r > 0) {if (grid[c][r - 1] == v) {return true;}} // can infect the r - 1 one
    if (c > 0) {if (grid[c - 1][r] == v) {return true;}}
    if (r < nR - 1) {if (grid[c][r + 1] == v) {return true;}}
    if (c < nC - 1) {return grid[c + 1][r] == v;}
    return false;
  }

  private boolean bubble(int c) {
    boolean res = false;
    for (int r = nR - 1; r > 0; r--) { // check from the last item upwards
      if (grid[c][r] == 0 && grid[c][r - 1] != 0) {
        res = true;
        grid[c][r] = grid[c][r - 1];
        grid[c][r - 1] = 0;
      }
    }
    return res;
  }

  private void bubbleSort() {
    for (int c = 0; c < nC; c++) {
      if (bubble(c)) {break;} // be one col finishes then another starts. Otherwise, together in one repaint routine
    }
  }

  private boolean colIsEmpty(int c) {
    for (int r = 0; r < nR; r++) {if (grid[c][r] != 0) return false;}
    return true;
  }

  private void swapCol(int c) { // c is non-empty, c - 1 is empty
    for (int r = 0; r < nR; r++) {
      grid[c - 1][r] = grid[c][r];
      grid[c][r] = 0;
    }
  }

  public boolean slideCol() {
    boolean res = false;
    for (int c = 1; c < nC; c++) {
      if (colIsEmpty(c - 1) && !colIsEmpty(c)) {
        swapCol(c);
        res = true;
      }
    }
    return res;
  }

  @Override
  public void actionPerformed(ActionEvent e) {repaint();}

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
  public void keyPressed(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {}

  @Override
  public void endGame() {
    timer.stop();
    timer = null;
  }
}
