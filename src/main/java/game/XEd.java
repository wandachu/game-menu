package game;

import graph.AbstractGame;
import graph.G;
import java.awt.*;
import java.awt.event.*;

public class XEd extends AbstractGame {
  private static Exp s;
  private static Exp.View view;

  public XEd() {
    super("XEd");
    Exp n = Exp.newA0(" 3 ");
    Exp v = Exp.newA0(" foobar ");

    Exp m = Exp.newA2(" * ");
    m.kids[0] = v;
    m.kids[1] = v;

    s = Exp.newA2(" hello world this is me!! Do you know who am I? ");

    s.kids[0] = n;
    s.kids[1] = m;
    view = new Exp.View(s, null);
    Key.focus = view;
  }

  @Override
  public void paintComponent(Graphics g) {
    G.whiteBackground(g);
    view.layout(g, 350, 300);
    view.show(g);
  }

  @Override
  public void keyPressed(KeyEvent ke) {
    Key.focus.keyPressed(ke); // may not do anything
    repaint();
  }

  @Override
  public void mouseClicked(MouseEvent me) {
    Key.focus = Key.NO_ONE; // drop the focus to NO_ONE
    view.setFocus(me.getX(), me.getY());
    repaint();
  }

  //---------------------------Key-------------------------------
  private static class Key {
    private static Press NO_ONE = ke -> {};
    private static Press focus = NO_ONE;

    public interface Press {
      void keyPressed(KeyEvent ke);
    }
  }

  //---------------------------Exp-------------------------------
  private static class Exp {
    private String name;
    private int nKids;
    private Exp[] kids;

    private Exp(String name, int nKids) { // will use factory method
      this.name = name;
      this.nKids = nKids;
      this.kids = (nKids > 0) ? new Exp[nKids] : null;
    }

    private static Exp newA0(String name) {return new Exp(name, 0);}
    private static Exp newA1(String name) {return new Exp(name, 1);}
    private static Exp newA2(String name) {return new Exp(name, 2);}

    public String toString() {// Test purpose
      StringBuilder res = new StringBuilder();
      for (int i = 0; i < nKids; i++) {
        res.append(" ").append(kids[i].toString());
      }
      return res + " " + name;
    }

    //-----------------------------View----------------------------
    private static class View implements Key.Press {
      private Exp exp;
      private View dad;
      private int nKids;
      private View[] kids;
      private int x, y, w, h; // bounding box
      private int dX, dY;

      private View(Exp exp, View dad) {
        this.exp = exp;
        this.dad = dad;
        this.nKids = exp.nKids ;
        this.kids = (nKids > 0) ? new View[this.nKids] : null;
        for (int i = 0; i < nKids; i++) {
          kids[i] = new View(exp.kids[i], this);
        }
      }

      private boolean hit(int xx, int yy) {return (xx > x && xx < x + w && yy > y && yy < y + h);}

      private void setFocus(int xx, int yy) { // will not clear focus to NO_ONE
        if (hit(xx, yy)) {
          Key.focus = this;
          for (int i = 0; i < nKids; i++) {kids[i].setFocus(xx, yy);} // recursive.
        }
      }

      @Override
      public void keyPressed(KeyEvent ke) {
        int vk = ke.getKeyCode();
        if (vk == KeyEvent.VK_BACK_SPACE && exp.name.length() > 0) {
          exp.name = exp.name.substring(0, exp.name.length() - 1);
          return;
        }
        if (vk == KeyEvent.VK_LEFT) {left(); return;}
        if (vk == KeyEvent.VK_RIGHT) {right(); return;}
        if (vk == KeyEvent.VK_UP) {up(); return;}
        if (vk == KeyEvent.VK_DOWN) {dn(); return;}
        char c = ke.getKeyChar();
        if (c != KeyEvent.CHAR_UNDEFINED) { // is a printing char
          exp.name += c; // add to the end
        }
      }

      private void up() {if (dad != null) {Key.focus = dad;}}
      private void dn() {if (nKids > 0) {Key.focus = kids[0];}}
      private void left() {
        if (dad != null && dad.kids[0] != this) {
          for (int i = 1; i < dad.nKids; i++) {
            if (dad.kids[i] == this) {Key.focus = dad.kids[i - 1]; return;}
          }
        }
      }
      private void right() {
        if (dad != null && dad.kids[dad.nKids - 1] != this) {
          for (int i = 0; i < dad.nKids; i++) {
            if (dad.kids[i] == this) {Key.focus = dad.kids[i + 1]; return;}
          }
        }
      }

      private int hW(Graphics g) {return g.getFontMetrics().stringWidth(exp.name);}
      private int hH(Graphics g) {return g.getFontMetrics().getHeight();}

      private int width(Graphics g) {
        if (w > -1) {return w;} // 0 is also valid
        w = Math.max(hW(g), kW(g));
        return w;
      }

      private int height(Graphics g) {
        if (h > -1) {return h;}
        h = hH(g) + maxKH(g);
        return h;
      }

      private int kW(Graphics g) { // sum of the kids' width
        if (nKids == 0) {return 0;}
        int res = 0;
        for (int i = 0; i < nKids; i++) {res += kids[i].width(g);}
        return res;
      }

      private int maxKH(Graphics g) { // maximum of the kids' height
        if (nKids == 0) {return 0;}
        int res = 0;
        for (int i = 0; i < nKids; i++) {res = Math.max(res, kids[i].height(g));}
        return res;
      }

      private void nuke() { // set all coordinates to negative
        w = -1; h = -1;
        if (nKids > 0) {for (View kid : kids) {kid.nuke();}}
      }

      private void layout(Graphics g, int xx, int yy) { // upper corner of the box
        nuke();
        height(g);
        width(g);
        locate(g, xx, yy);
      }

      private void locate(Graphics g, int xx, int yy) {
        x = xx; y = yy;
        dX = (w - hW(g)) / 2; dY = g.getFontMetrics().getAscent();
        if (nKids == 0) {return;}
        int kX = (w - kW(g)) / nKids;
        yy += hH(g); // step down
        for (View kid : kids) {
          kid.w += kX;
          kid.locate(g, xx, yy);
          xx += kid.w;
        }
      }

      private void show(Graphics g) {
        g.setColor(Color.CYAN);
        g.drawRect(x, y, w, h);
        g.setColor(Color.BLACK);
        rShow(g); // recursive
      }

      private void rShow(Graphics g) {
        if (Key.focus == this) {g.setColor(Color.ORANGE);}
        g.drawString(exp.name, x + dX, y + dY);
        g.setColor(Color.BLACK); // reset the color to black
        if (nKids > 0) {
          int kY = kids[0].y;
          g.drawLine(x, kY, x + w, kY);
          kids[0].rShow(g);
          for (int i = 1; i < nKids; i++) { // start from the index 1 one.
            int kX = kids[i].x;
            g.drawLine(kX, kY, kX, y + h);
            kids[i].rShow(g);
          }
        }
      }
    }
  }

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
