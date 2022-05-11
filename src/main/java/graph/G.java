package graph;

import java.util.Random;
import java.util.ArrayList;
import java.awt.FontMetrics;
import java.awt.*;

/**
 * G is a helper class containing graphical related helper functions and variables.
 */
public class G {
  public static Random RANDOM = new Random();
  public static int rnd(int k) {return RANDOM.nextInt(k);}
  public static G.V LEFT = new G.V(-1, 0), RIGHT = new G.V(1, 0), UP = new G.V(0, -1), DOWN = new G.V(0, 1);
  public static void whiteBackground(Graphics g) {
    g.setColor(Color.white);
    g.fillRect(0, 0, 5000, 5000);
  }

  //------------------V------------------------
  public static class V {
    public int x = 0, y = 0;

    public V(int x, int y) {this.x = x; this.y = y;}
    public V(V v) {x = v.x; y = v.y;}
    public V() {}

    public void add(V v) {x += v.x; y += v.y;}
    public void set(V v) {x = v.x; y = v.y;}
    public void set(int x, int y) {this.x = x; this.y = y;}
  }

  //-----------------VS------------------------
  public static class VS {
    public V loc, size;

    public VS(int x, int y, int w, int h) {
      loc = new V(x, y);
      size = new V(w, h);
    }

    public void fill(Graphics g, Color c) {
      g.setColor(c);
      g.fillRect(loc.x, loc.y, size.x, size.y);
    }

    public void draw(Graphics g, Color c) {
      g.setColor(c);
      g.drawRect(loc.x, loc.y, size.x, size.y);
    }

    public boolean hit(int x, int y) {return x > loc.x && y > loc.y && x < loc.x + size.x && y < loc.y + size.y;}
  }

  //------------------Button-----------------------
  public static abstract class Button {
    public abstract void act();

    public static final V margin = new V(5, 3);

    public boolean enabled = true, bordered = true;
    public String text = "";
    public VS vs = new VS(0, 0, 0, 0); // set this later on
    public int dyText = 0; // how far we go from left top corner down to draw string
    public static LookAndFeel lnf = new LookAndFeel(); // default look and field

    public Button(Button.List list, String text) {
      this.text = text;
      if (list != null) { // we allow user to pass in null
        list.add(this);
      }
    }

    public void show(Graphics g) {
      // test if the size has been set.
      if (vs.size.x == 0) {setSize(g);}
      vs.fill(g, lnf.back);
      if (bordered) {vs.draw(g, (enabled) ? lnf.border : lnf.disabled);}
      g.setColor(enabled ? lnf.text : lnf.disabled);
      g.drawString(text, vs.loc.x + lnf.margin.x, vs.loc.y + dyText);
    }

    public void setSize(Graphics g) {
      FontMetrics fm = g.getFontMetrics();
      vs.size.set(2 * lnf.margin.x + fm.stringWidth(text), 2 * lnf.margin.y + fm.getHeight());
      dyText = lnf.margin.y + fm.getAscent();

    }

    public void set(int x, int y) {vs.loc.set(x, y);}

    public boolean hit(int x, int y) {return vs.hit(x, y);} // if mouse land on the button

    public void click() {if (enabled) {act();}}

    //---------------------LookAndFeel----------------------
    public static class LookAndFeel {
      public static Color text = Color.BLACK, back = Color.WHITE, border = Color.BLACK, disabled = Color.LIGHT_GRAY;
      public static final V margin = new V(5, 3);
    }

    //-----------------------List-------------------------
    public static class List extends ArrayList<Button> {
      public Button hit(int x, int y) {
        for (Button b : this) {if (b.hit(x, y)) {return b;}}
        return null;
      }

      public boolean clicked(int x, int y) {
        Button b = hit(x, y);
        if (b == null) return false;
        b.click();
        return true;
      }

      public void showAll(Graphics g) {for (Button b : this) {b.show(g);}}
    }
  }
}

