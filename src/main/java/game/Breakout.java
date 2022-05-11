package game;

import graph.AbstractGame;
import graph.G;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Timer;

public class Breakout extends AbstractGame implements ActionListener {
  private static final int H = 16, W = 50, PW = 100, nBrick = 13, PV = 16;
  private static final int LEFT = 100, RIGHT = LEFT + nBrick * W, TOP = 50, BOT = 700;
  private static final int GAP = 3 * H;
  private static final int MAX_LIFE = 3;
  private static final int TIME_DELAY = 30;

  private static Paddle paddle;
  private static Ball ball;
  private static int lives;

  private static int score;
  private static int rowCount;
  private Timer timer;

  public Breakout() {
    super("Breakout");
    timer = new Timer(TIME_DELAY, this); // start timer
    timer.start();
    startGame();
  }

  public void paintComponent(Graphics g) {
    G.whiteBackground(g);
    g.setColor(Color.BLACK);
    g.fillRect(LEFT, TOP, RIGHT - LEFT, BOT - TOP);
    g.drawString("Lives: " + lives, LEFT + 20, 30);
    g.drawString("Score: " + score, RIGHT - 80, 30);
    paddle.show(g);
    ball.show(g);
    Brick.List.show(g);
  }

  private static void startGame() {
    lives = MAX_LIFE;
    score = 0;
    rowCount = 0;
    paddle = new Paddle();
    ball = new Ball();
    startNewRows();
  }

  private static void startNewRows() {
    rowCount++;
    Brick.List.ALL.clear();
    Brick.newBrickRows(rowCount);
    ball.init();
  }

  @Override
  public void keyPressed(KeyEvent ke) {
    int vK = ke.getKeyCode();
    if (vK == KeyEvent.VK_LEFT) {paddle.left();}
    if (vK == KeyEvent.VK_RIGHT) {paddle.right();}
    if (ke.getKeyChar() == ' ') {paddle.dxStuck = -1;}
    repaint();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    ball.move();
    repaint();
  }

  //-------------------------Brick-------------------------------
  private static class Brick extends G.VS {
    private static Color[] colors = {Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN};
    private Color color;

    private Brick(int x, int y) {
      super(x, y, W, H);
      color = colors[G.rnd(colors.length)];
      Brick.List.ALL.add(this);
    }

    private void show(Graphics g) {
      fill(g, color);
      draw(g, Color.BLACK);
    }

    @Override
    public boolean hit(int x, int y) {
      return (x < loc.x + W && x + H > loc.x && y > loc.y && y < loc.y + H);
    }

    private void destroy() {
      ball.dy = -ball.dy;
      Brick.List.ALL.remove(this);
      score += 17;
      if (Brick.List.ALL.isEmpty()) {startNewRows();}
    }

    private static void newBrickRows(int n) {
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < nBrick; j++) {
          new Brick(LEFT + j * W, TOP + GAP + i * H);
        }
      }
    }

    //----------------------List------------------------------
    private static class List extends ArrayList<Brick> {
      private static List ALL = new List();

      private static void show(Graphics g) {for (Brick b : ALL) {b.show(g);}}

      private static void ballHitBrick() {
        int x = ball.loc.x, y = ball.loc.y;
        for (Brick b : ALL) {
          if (b.hit(x, y)) {b.destroy(); return;} // must return since we cannot change the list while iterating it
        }
      }
    }
  }

  //-------------------------Ball--------------------------------
  private static class Ball extends G.VS {
    private static final int DY_START = -11;
    private Color color = Color.WHITE;
    private int dx = 11, dy = DY_START; // velocity

    private Ball() {
      super(LEFT, BOT - 2 * H, H, H);
    }

    private void init() {
      paddle.dxStuck = PW / 2 - H / 2;
      loc.set(paddle.loc.x + paddle.dxStuck, BOT - 2 * H);
      dx = 0; dy = DY_START;
    }

    private void show(Graphics g) {fill(g, color);}

    private void move() {
      if (paddle.dxStuck < 0) {
        loc.x += dx; loc.y += dy;
        wallBounce();
        Brick.List.ballHitBrick();
      }
    }

    private void wallBounce() {
      if (loc.x < LEFT) {loc.x = LEFT; dx = -dx;}
      if (loc.x + H > RIGHT) {loc.x = RIGHT - H; dx = -dx;}
      if (loc.y < TOP) {loc.y = TOP; dy = -dy;}
      if (loc.y > BOT - 2 * H) {paddle.hitBall();}
    }

  }

  //-------------------------Paddle------------------------------
  private static class Paddle extends G.VS {
    private Color color = Color.YELLOW;
    private int dxStuck = 10;

    private Paddle() {
      super(LEFT, BOT - H, PW, H);
    }

    private void show(Graphics g) {fill(g, color);}

    private void left() {
      loc.x -= PV;
      limitX();
    }

    private void right() {
      loc.x += PV;
      limitX();
    }

    private void limitX() {
      if (loc.x < LEFT) {loc.x = LEFT;}
      if (loc.x + PW > RIGHT) {loc.x = RIGHT - PW;}
      if (dxStuck >= 0) {
        ball.loc.set(loc.x + dxStuck, BOT - 2 * H);
      }
    }

    private void hitBall() {
      if (ball.loc.x < loc.x || ball.loc.x > loc.x + PW) {
        // ball did not hit paddle. life lost
        lives--;
        if (lives == 0) {startGame();}
        else {ball.init();}
      } else {
        // ball needs to bounce
        ball.dy = -ball.dy;
        ball.dx += dxAdjust();
      }
    }

    private int dxAdjust() {
      int cp = paddle.loc.x + PW / 2; // center position
      return (ball.loc.x + H / 2 - cp) / 10;
    }
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

  @Override
  public void endGame() {
    timer.stop();
    timer = null;
  }
}
