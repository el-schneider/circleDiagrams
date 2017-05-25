class DiagramItem{
  TableRow row;

  float x;
  float y;

  color c;

  float r;

  String strKey;
  int val;

  DiagramItem(TableRow row_, float x_, float y_, color c_, float r_){
    row = row_;

    x = x_;
    y = y_;

    c = c_;

    r = r_;

    strKey = row.getString(0).toUpperCase();
    val = row.getInt(1);
  }

  void displayCircles() {
    noStroke();
    fill(c);
    ellipse(x + 0.5*gridX, y +0.5*gridY, r, r);

    textAlign(CENTER, CENTER);

    fill(0);
    textSize(bigText);
    text(str(val), x, y, gridX, gridY);

    textAlign(CENTER, TOP);
    textSize(smallText);
    text(strKey, x, y + gridY - height/30, gridX, gridY);
  }

  void animIn(int dur){
    animInValues(dur);
    animInCircles(dur*4);
  }

  void animInValues(int dur){
    float value = val;
    val = 0;
    Ani.to(this, dur, random(0, 12), "val", value, Ani.LINEAR);
  }

  void animInCircles(int dur){
    float rad = r;
    r = 0;
    Ani.to(this, dur, random(0, 12), "r", rad, Ani.ELASTIC_OUT);
  }
}
