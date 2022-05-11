package game;

import graph.AbstractGame;
import graph.G;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

public class Snake extends AbstractGame implements ActionListener {
  private static final int W = 40, H = 30, C = 20, X_OFF = 100, Y_OFF = 100;
  private static final int SNAKE_SIZE = 3;
  private static final int INITIAL_TIME = 100;
  private static final int FOOD_VALUE = 50;
  private static final Color FOOD_COLOR = Color.PINK;

  private static Random RND = new Random();
  private static char turn = 'A'; // L means left, R means right. Anything else is straight.

  private Chain chain = new Chain(SNAKE_SIZE);
  private int rnd(int k) {return RND.nextInt(k);}
  private Timer timer;
  private boolean gameOver = false;
  private int timeToDeath = INITIAL_TIME;
  private Point food = new Point(rnd(W), rnd(H));
  private int foodEaten = 0;

  public Snake() {
    super("Snake");
    timer = new Timer(100, this);
    timer.start();
  }

  private void fillCell(Graphics g, Point p, Color color) {
    g.setColor(color);
    g.fillRect(p.x * C + X_OFF, p.y * C + Y_OFF, C, C);
  }

  public void paintComponent(Graphics g) {
    // check if game is over
    timeToDeath--;
    if (timeToDeath == 0) {gameOver = true;}

    G.whiteBackground(g);
    g.setColor(Color.BLACK);
    g.drawRect(X_OFF, Y_OFF, W * C, H * C);
    g.drawString("Time: " + timeToDeath, 50, 50);
    g.drawString("Food Eaten: " + foodEaten, 250, 50);
    g.drawString("Press left key or right key to turn.", 50, 70);
    g.setColor(Color.RED);
    chain.draw(g);
    fillCell(g, food, FOOD_COLOR);
    if (!gameOver) {
      chain.step();
      if (turn == 'L') {
        chain.left(); turn = 'a';
      }
      else if (turn == 'R') {
        chain.right();
        turn = 'a';
      }
    } else { // Game is over
      drawDeadInfo(g);
      timeToDeath = 1; // make it 1 so that it will decrease to 0 if collapse happens
    }
  }

  private void drawDeadInfo(Graphics g) {
    chain.drawDeadHead(g);
    g.setColor(Color.BLACK);
    g.drawString("Oops! The snake is dead. Press space to restart.", 400, 400);
  }

  @Override
  public void endGame() {
    timer.stop();
    timer = null;
  }

  private void startOver() {
    gameOver = false;
    chain = new Chain(SNAKE_SIZE);
    timeToDeath = INITIAL_TIME;
    foodEaten = 0;
    food = new Point(rnd(W), rnd(H)); // reset food's position
  }

  @Override
  public void actionPerformed(ActionEvent e) {repaint();}

  @Override
  public void keyPressed(KeyEvent e) {
    int vk = e.getKeyCode();
    if (vk == KeyEvent.VK_LEFT) {
      turn = 'L';
    } else if (vk == KeyEvent.VK_RIGHT) {
      turn = 'R';
    } else if (vk == KeyEvent.VK_SPACE) {
      startOver();
    } else {
      turn = 'a';
    }
  }

  @Override
  public void keyTyped(KeyEvent ke) {}

  @Override
  public void keyReleased(KeyEvent e) {}

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

  //-------------------------Chain-------------------------
  private class Chain extends ArrayList<Point> {
    private int head;
    private Point dH = new Point(1,0); //delta H (1,0)

    private Chain(int n) {
      head = 0;
      int x = rnd(W) , y = rnd(H);
      for(int i = 0; i < n ; i++){add(new Point(x, y));}
    }

    private void draw(Graphics g){
      for (Point p : this) {
        g.fillRect(p.x * C + X_OFF, p.y * C + Y_OFF, C, C);
      }
    }

    private void drawDeadHead(Graphics g) {
      g.setColor(Color.YELLOW);
      Point h = get(head);
      g.fillRect(h.x * C + X_OFF, h.y * C + Y_OFF, C, C);
    }

    private void step(){
      int tail = (head + 1) % size();

      Point t = get(tail), h = get(head);
      t.x = h.x + dH.x;
      t.y = h.y + dH.y;

      head = tail;
      detectCollision();
      detectFood();
    }

    private void detectFood() {
      Point h = get(head);
      if (h.x == food.x && h.y == food.y) {
        timeToDeath += FOOD_VALUE; // increase time left
        food.x = rnd(W);
        food.y = rnd(H);
        chain.add(new Point(h.x, h.y));
        foodEaten++; // increase number of food eaten
      }
    }

    private void right(){
      int t = dH.x; dH.x = dH.y; dH.y = t;
      dH.x = -dH.x;
    }

    private void left(){
      int t = dH.x; dH.x = dH.y; dH.y = t;
      dH.y = -dH.y;
    }

    private void detectCollision() {
      Point h = get(head);
      // look for wall collision
      if (h.x < 0 || h.y < 0 || h.x >= W || h.y >= H) {
        gameOver = true;
      }
      // look for self collision
      for (Point p : chain) {
        if (p != h && p.x == h.x && p.y == h.y) {
          gameOver = true;
          break;
        }
      }
    }
  }
}
