package game;

import graph.AbstractGame;
import graph.G;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Skunk is a dice-rolling game allowing a user to play with the computer AI derived based on Markov chain.
 */
public class Skunk extends AbstractGame {
  private static final String AIName = "Archie";
  private static final G.Button.List cmds = new G.Button.List();
  private static final int xM = 375, yM = 250;
  private static final G.Button PASS = new G.Button(cmds, "Pass") {
    @Override
    public void act() {pass();}
  };

  private static final G.Button ROLL = new G.Button(cmds, "Roll") {
    @Override
    public void act() {roll();}
  };

  private static final G.Button AGAIN = new G.Button(cmds, "Play another game") {
    @Override
    public void act() {playAgain();}
  };

  private static int M, E, H; // define the state of the game (my point, enemy's point, hand point)
  private static boolean myTurn;
  private static int D1, D2; // two dice


  public Skunk() {
    super("Skunk");
    playAgain();
  }

  private static void playAgain() {
    M = 0; E = 0; H = 0;
    myTurn = G.rnd(2) == 0; // 0 or 1
    if (!myTurn) {
      setAIButton(); // set button if the first round if AI's
    }
    PASS.set(xM + 50, yM + 50); ROLL.set(xM + 100, yM + 50); AGAIN.set(-100, -100);
  }

  private static void roll() {
    D1 = G.rnd(6) + 1; // between 1 and 6
    D2 = G.rnd(6) + 1;
    analyzeDice();
  }

  private static void pass() {
    if (myTurn) {M += H;} else {E += H;}
    H = 0;
    ROLL.enabled = true;
    myTurn = !myTurn;
    if (myTurn) {
      PASS.enabled = true;
    }
    roll();
  }

  private static String skunkMsg = "";

  private static void showRoll(Graphics g) {
    g.setColor(Color.BLACK);
    String playerName = myTurn ? "Your Turn" : AIName + "'s' Roll";
    g.drawString(playerName, xM, yM - 20);
    g.drawString("Current Roll: " + D1 + ", " + D2 + skunkMsg, xM, yM + 20); // statically display the roll with msg
  }

  private static void analyzeDice() {
    PASS.enabled = true; ROLL.enabled = true; // enable both button after each roll
    if (D1 == 1 && D2 == 1) {totalSkunked(); skunkMsg = "TOTALLY SKUNKED!";}
    else if (D1 == 1 || D2 == 1) {skunked(); skunkMsg = "Skunked!";}
    else {skunkMsg = ""; normalHand();}
  }

  private static String gameoverMsg() {
    String res = "";
    int total = H + (myTurn ? M : E);
    if (total >= 100) {
      res = (myTurn) ? "You WIN!!" : AIName + "'s win!";
      gameover();
    }
    return res;
  }

  private static void gameover() {
    PASS.set(-100, -100);
    ROLL.set(-100, -100);
    AGAIN.set(100, 100);
  }

  private static String scoreString() {
    return "Hand score so far: " + H + "     your score: " + M + "     " + AIName + "'s score: " + E;
  }

  private static void showScore(Graphics g) {
    g.setColor(Color.BLACK);
    g.drawString(scoreString(), xM, yM + 40);
  }

  @Override
  public void paintComponent(Graphics g) {
    G.whiteBackground(g);
    converge(1000000); // user a counter to count how much coverage is done and stop after 80 clicks
    if (showStrategy) {
      converge(100000);
      showAll(g);
    } else {
      showRoll(g);
      showScore(g);
      if (!gameoverMsg().equals("")) { // game should be over
        G.whiteBackground(g);
        g.setColor(Color.BLACK);
        g.drawString(gameoverMsg(), xM, yM);
      }
      cmds.showAll(g); // must be after showRoll to show correct cmd stage
    }
  }

  private static void totalSkunked() {
    if (myTurn) {M = 0;} else {E = 0;}
    skunked();
  }

  private static void skunked() {
    H = 0; ROLL.enabled = false; PASS.enabled = true;
  }

  private static void normalHand() {
    H += D1 + D2;
    setAIButton();
  }

  private static boolean gottaRoll() {
    wOptimal(E, M, H);
    return ROLL.enabled && !shouldPass;
  }

  private static void setAIButton() {
    if (!myTurn) {
      if (gottaRoll()) {
        PASS.enabled = false;
      } else {
        ROLL.enabled = false;
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent me) {
    int x = me.getX(), y = me.getY();
    if (cmds.clicked(x, y)) {repaint();}
  }

  //----------------------------AI-----------------------------
  private static final double[][][] P = new double[100][100][100];

  private static double p(int m, int e, int h) {
    if (m + h >= 100) {return 1.0;}
    if (e >= 100) {return 0.0;}
    return P[m][e][h];
  }

  private static double wPass(int m, int e, int h) { // win probability if pass
    return 1.0 - p(e, m + h, 0);
  }

  private static double wTotalSkunk(int m, int e, int h) {
    return 1.0 - p(e, 0, 0);
  }

  private static double wSkunk(int m, int e, int h) {
    return 1.0 - p(e, m, 0);
  }

  private static double wRoll(int m, int e, int h) {
    double res = wTotalSkunk(m, e, h) / 36 + wSkunk(m, e, h) / 3.6; // 3.6 is * 10 / 36
    for (int d1 = 2; d1 < 7; d1++) {
      for (int d2 = 2; d2 < 7; d2++) {
        res += p(m, e, h + d1 + d2) / 36;
      }
    }
    return res;
  }

  private static boolean shouldPass; // set by side effect of wOptimal

  private static double wOptimal(int m, int e, int h) {
    double wP = wPass(m, e, h), wR = wRoll(m, e, h);
    return (shouldPass = (wP > wR)) ? wP : wR;
  }

  private static void converge(int n) {
    for (int i = 0; i < n; i++) {
      int m = G.rnd(100), e = G.rnd(100), h = G.rnd(100);
      P[m][e][h] = wOptimal(m, e, h);
    }
  }

  //------------------------------Visualization-------------------------------------
  private static final int W = 7;
  private static boolean showStrategy = false;
  private static final int nC = 45;
  private static Color[] stopColors = new Color[nC];
  private static void showAll(Graphics g) {
    showStop(g);
    showGrid(g);
    showColorMap(g);
  }
  static {
    for (int i = 0; i < nC; i++) {
      stopColors[i] = new Color(G.rnd(255), G.rnd(255), G.rnd(255));
    }
  }

  private static void showColorMap(Graphics g) {
    int x = xM + 100 * W + 30; // 30 is the space between another
    for (int i = 0; i < nC; i++) {
      g.setColor(stopColors[i]);
      g.fillRect(x, yM + 15 * i, 15, 13); // 13 to leave a small gap
      g.setColor(Color.BLACK);
      g.drawString("" + i, x + 20, yM + 15 * i + 10);
    }
  }

  private static void showGrid(Graphics g) {
    g.setColor(Color.BLACK);
    for (int k = 0; k <= 10; k++) {
      int d = 10 * W * k;
      g.drawLine(xM, yM + d, xM + 100 * W, yM + d); // horizontal line
      g.drawLine(xM + d, yM, xM + d, yM + 100 * W); // vertical line
    }
  }

  private static void showStop(Graphics g) {
    for (int m = 0; m < 100; m++) {
      for (int e = 0; e < 100; e++) {
        int k = firstStop(m, e);
        g.setColor(stopColors[k]);
        g.fillRect(xM + W * m, yM + W * e, W, W);
      }
    }
  }

  private static int firstStop(int m, int e) {
    for (int h = 0; h < 100 - m; h++) {
      wOptimal(m, e, h);
      if (shouldPass) {return (h >= nC) ? 0 : h;}
    }
    return 0; // shouldn't pass and keep rolling
  }

  @Override
  public void mouseClicked(MouseEvent e) {}

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
