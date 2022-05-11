package game;

import graph.AbstractGame;
import graph.G;
import javax.swing.Timer;
import java.awt.event.*;
import java.awt.*;

public class Tetris extends AbstractGame implements ActionListener {
  private static final int H = 20, W = 10, C = 25; // C is the cell width. W and H are the width and height of the well.
  private static final int xM = 50, yM = 50;
  private static final int iBkCol = 7; // index of the black background color
  private static final int ZAP = 8;
  private static final Color[] colors = {
      Color.RED,
      Color.GREEN,
      Color.BLUE,
      Color.ORANGE,
      Color.CYAN,
      Color.YELLOW,
      Color.MAGENTA,
      Color.BLACK,
      Color.PINK // ZAP color
  };

  private static int time;
  private static Timer timer;
  private static int[][] well;
  private static Shape[] shapes = {Shape.Z, Shape.S, Shape.J, Shape.L, Shape.I, Shape.O, Shape.T};
  private static Shape shape;

  public Tetris() {
    super("Tetris");
    setTimer();
    setInitialShape();
    setInitialWell();
  }

  private void setTimer() {
    time = 1;
    timer = new Timer(30, this);
    timer.start();
  }

  private static void setInitialShape() {
    shape = shapes[G.rnd(7)];
    shape.loc.set(4, 0); // since shape is static, we must reset its location
  }

  private static void setInitialWell() {
    well = new int[W][H];
    clearWell();
  }

  private static void clearWell() { // could build a Well class and move this into that class, so we can call well.clearWell().
    for (int i = 0; i < W; i++) {
      for (int j = 0; j < H; j++) {
        well[i][j] = iBkCol;
      }
    }
  }

  private static void showWell(Graphics g) {
    for (int x = 0; x < W; x++) {
      for (int y = 0; y < H; y++) {
        g.setColor(colors[well[x][y]]);
        int xX = xM + C * x, yY = yM + C * y;
        g.fillRect(xX, yY, C, C);
        g.setColor(colors[iBkCol]);
        g.drawRect(xX, yY, C, C);
      }
    }
  }

  public void actionPerformed(ActionEvent event) {
    repaint();
  }

  public void paintComponent(Graphics g) {
    G.whiteBackground(g);
    unZapWell();
    showWell(g);
    time++;
    if (time == 30) { // every 30 frames, drop the shape
      time = 0;
      shape.drop();
    }
    shape.show(g);
  }

  private static void zapWell() {for (int y = 0; y < H; y++) {zapRow(y);}}

  private static void unZapWell() {
    boolean done = false;
    for (int y = 1; y < H; y++) {
      for (int x = 0; x < W; x++) {
        if (well[x][y] == ZAP) { // look for row above
          well[x][y] = well[x][y - 1];
          well[x][y - 1] = (y - 1 == 0) ? iBkCol : ZAP;
          done = true; // stop here. timer will call this again so we see animation effect
        }
      }
      if (done) return;
    }
  }

  private static void zapRow(int y) { // y is the row number
    for (int x = 0; x < W; x++) {if (well[x][y] == iBkCol) {return;}}
    for (int x = 0; x < W; x++) {well[x][y] = ZAP;}
  }

  @Override
  public void keyPressed(KeyEvent ke) {
    int vk = ke.getKeyCode();
    if (vk == KeyEvent.VK_UP) {shape.safeRotate();}
    if (vk == KeyEvent.VK_LEFT) {shape.slide(G.LEFT);}
    if (vk == KeyEvent.VK_RIGHT) {shape.slide(G.RIGHT);}
    if (vk == KeyEvent.VK_DOWN) {shape.drop();}
    repaint();
  }

  //---------------------Shape-------------------------
  private static class Shape {
    private static Shape Z, S, J, L, I, O, T;
    private G.V[] a = new G.V[4];
    private int iCol; // color index
    private G.V loc = new G.V(0, 0);

    static {
      Z = new Shape(new int[]{0, 0, 1, 0, 1, 1, 2, 1}, 0);
      S = new Shape(new int[]{0, 1, 1, 0, 1, 1, 2, 0}, 1);
      J = new Shape(new int[]{0, 0, 0, 1, 1, 1, 2, 1}, 2);
      L = new Shape(new int[]{0, 1, 1, 1, 2, 1, 2, 0}, 3);
      I = new Shape(new int[]{0, 0, 1, 0, 2, 0, 3, 0}, 4);
      O = new Shape(new int[]{0, 0, 1, 0, 0, 1, 1, 1}, 5);
      T = new Shape(new int[]{0, 1, 1, 0, 1, 1, 2, 1}, 6);
    }
    private static G.V temp = new G.V(0, 0);
    private static Shape cds = new Shape(new int[]{0, 0, 0, 0, 0, 0, 0, 0}, 0);

    private Shape(int[] xy, int iC) {
      for (int i = 0; i < 4; i++) {
        a[i] = new G.V(xy[i * 2], xy[i * 2 + 1]);
      }
      iCol = iC;
    }

    private void show(Graphics g) {
      g.setColor(colors[iCol]);
      for (int i = 0; i < 4; i++) {g.fillRect(x(i), y(i), C, C);}
      g.setColor(Color.BLACK); // border color
      for (int i = 0; i < 4; i++) {g.drawRect(x(i), y(i), C, C);}
    }

    private int x(int i) {return xM + C * (a[i].x + loc.x);}
    private int y(int i) {return yM + C * (a[i].y + loc.y);}

    private void drop() {
      cdsSet(); // make a copy of the current shape into the cds one
      cdsAdd(G.DOWN); // move it
      if (collisionDetected()) {
        copyToWell();
        zapWell();
        dropNewShape();
      } // copy the four cells into the well. set the shape back to 0, 0.
      loc.add(G.DOWN);
    }

    private void copyToWell() {
      for (int i = 0; i < 4; i++) {
        well[a[i].x + loc.x][a[i].y + loc.y] = iCol; // copy four box of shapes into the well
      }
    }

    private static void dropNewShape() { // static method dropping a new random shape
      shape = shapes[G.rnd(7)];
      shape.loc.set(4, 0); // show up in the center of the well at the top
    }

    private void rotate() {
      temp.set(0, 0); // track min x, y
      for (int i = 0; i < 4; i++) {
        a[i].set(-a[i].y, a[i].x); // rotate 90 degree
        if (temp.x > a[i].x) {temp.x = a[i].x;}
        if (temp.y > a[i].y) {temp.y = a[i].y;}
      }
      temp.set(-temp.x, -temp.y);
      for (int i = 0; i < 4; i++) {a[i].add(temp);}
    }

    private void safeRotate() {
      rotate();
      cdsSet();
      if (collisionDetected()) {rotate(); rotate(); rotate();} // rotate back
    }

    private void slide(G.V v) {
      cdsSet(); // make a copy of the current shape into the cds one
      cdsAdd(v); // move it
      if (collisionDetected()) {return;}
      loc.add(v);
    }

    private void cdsSet() {for (int i = 0; i < 4; i++) {cds.a[i].set(a[i]); cds.a[i].add(loc);}}
    private void cdsAdd(G.V v) {for (int i = 0; i < 4; i++) {cds.a[i].add(v);}}
    private static boolean collisionDetected() {
      for (int i = 0; i < 4; i++) {
        G.V v = cds.a[i];
        if (v.x < 0 || v.x >= W || v.y < 0 || v.y >= H) {return true;}
        if (well[v.x][v.y] != iBkCol && well[v.x][v.y] != ZAP) {return true;}
      }
      return false;
    }
  }

  @Override
  public void endGame() {
    timer.stop();
    timer = null;
  }

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
}
