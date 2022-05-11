package game;

import graph.AbstractGame;
import graph.G;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Maze is a Maze generator using Eller's Algorithm. http://www.neocomputer.org/projects/eller.html.
 */
public class Maze extends AbstractGame {
  private static final int PRECISION = 2;
  private static final int W = 30 * PRECISION, H = 20 * PRECISION;
  private static final int XM = 50, YM = 50, C = 30 / PRECISION;
  private static final int VERTICAL_PROBABILITY = 33;
  private static final int HORIZONTAL_PROBABILITY = 47;
  private static int[] next = new int[W + 1]; // vertex is one more than cell
  private static int[] prev = new int[W + 1];
  private static int y; // mark row
  private static Graphics gg;

  public Maze() {
    super("Maze");
  }

  public void paintComponent(Graphics g) {
    gg = g;
    G.whiteBackground(g);
    g.setColor(Color.BLACK);
    // G.RANDOM.setSeed(100); // seed it
    hRow0(); // draw first row
    mid(); // draw middle part. alternating between vRow and hRow.
    vLast(); // last vertical row
    hLast(); // last horizontal row
  }

  private void hLast() {
    y += C;
    for (int i = 0; i < W; i++) {
      hLine(i);
      merge(i, i + 1);
    }
  }

  private void vLast() {
    drawVBorder();
    for (int i = 0; i < W; i++) {
      if (!sameCycle(i, 0)) {
        merge(i, 0);
        vLine(i);
      }
    }
  }

  private void drawVBorder() {vLine(0); vLine(W);}

  private void hRow0() {
    y = YM; // start at y margin
    singletonCycle(0); // make the first path singleton
    for (int i = 0; i < W; i++) {
      singletonCycle(i + 1); // make 1 to W path singleton
      hLine(i); // draw horizontal line for 0 to W - 1 (totally W)
      merge(i, i + 1); // the first horizontal border is then merged to one set
    }
  }

  private void mid() {
    for (int i = 0; i < H - 1; i++) {
      vRow();
      y += C;
      hRow();
    }
  }

  private void hRow() {
    for (int i = 0; i < W; i++) {hRule(i);}
  }

  private void vRow() {
    for (int i = 1; i < W; i++) { // start from 1 since 0 will be drawn by drawVBorder
      vRule(i);
    }
    drawVBorder();
  }

  private void hRule(int i) { // horizontal rule：could possibly draw if the two spots are not same cycle. Otherwise, would have a dead block
    if (!sameCycle(i, i + 1) && pH()) {hLine(i); merge(i, i + 1);}
  }

  private void vRule(int i) { // vertical rule: if not connected to 0, then must draw vertical to make it goes. Otherwise, would have a dead block
    if (next[i] == i || pV()) {vLine(i);} // singleton or pV. must draw
    else {noVLine(i);} // split
  }

  private void noVLine(int i) {split(i);}

  private int x(int i) {return XM + i * C;}

  private void merge(int i, int j) {
    int iP = prev[i], jP = prev[j];
    next[iP] = j; next[jP] = i;
    prev[i] = jP; prev[j] = iP;
  }

  private void split(int i) {
    int iP = prev[i], iN = next[i];
    next[iP] = iN; prev[iN] = iP;
    singletonCycle(i); // make i a singleton
  }

  private void singletonCycle(int i) {
    next[i] = i; prev[i] = i; // make i a singleton
  }

  private boolean sameCycle(int i, int j) { // if wall i and j are already connected in one cycle
    int n = next[i];
    while (n != i) {
      if (n == j) {return true;}
      n = next[n];
    }
    return false;
  }

  private static boolean pV() {return G.rnd(100) < VERTICAL_PROBABILITY;}

  private static boolean pH() {return G.rnd(100) < HORIZONTAL_PROBABILITY;}

  private void vLine(int i) {gg.drawLine(x(i), y, x(i), y + C);}

  private void hLine(int i) {gg.drawLine(x(i), y, x(i + 1), y);}

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
  public void keyPressed(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {}

  @Override
  public void endGame() {}
}