import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import de.looksgood.ani.*; 
import de.looksgood.ani.easing.*; 
import java.io.FilenameFilter; 
import spout.*; 
import processing.video.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class circleDiagrams extends PApplet {







//spout object
Spout spout;
//spout senderName
String sendername;

Movie transition;

int cols = 12;
int rows = 4;
Table table;

float gridX;
float gridY;

float maxR;
float maxValue;

String title;
float titleFactor;
float smallText;
float bigText;

String[] tables;
int tableIndex;

int refreshFrame = 400;

PFont bigFont;
PFont smallFont;
PFont titleFont;

ArrayList<DiagramItem> items;

public void setup () {
  
  // size(1920,800,P2D);
  textMode(MODEL);
  

  Ani.init(this);
  Ani.setDefaultTimeMode(Ani.FRAMES);

  transition = new Movie(this, "arrow-field.mov");
  transition.stop();

  spout = new Spout(this);
  sendername = "circleDiagrams";
  spout.createSender(sendername, width, height);


  // add all CSV-type files to the tables array
  java.io.File folder = new java.io.File(dataPath(""));
  tables = folder.list(CSV_FILTER);


  gridX = 0.75f*width/cols;
  gridY = height/rows;

  maxR = (height/4) * .9f;

  //create font at high resolution so it sizes better
  smallFont = createFont("DINPro-Medium", height/37);
  bigFont = createFont("DINPro-Medium", height/20);
  titleFont = createFont("DINPro-Medium", height/10);
  textFont(bigFont);

  tableIndex = 0;

  initTable(tableIndex);
}

public void draw() {
  pushMatrix();
  translate(0.125f*width, gridY/2);
  background(255);

  if(frameCount % refreshFrame == 0){
    switchTable();
  }

  for(DiagramItem i : items) {
    i.displayCircles();
  }


  drawTitle();

  popMatrix();


  //make transition only show when playing
  if(transition.time() > 0){
    blendMode(MULTIPLY);
    image(transition, 0, 0,width, height);

    if (transition.time() == transition.duration()){
      transition.stop();
      transition.jump(0);
    }
  }

  spout.sendTexture();
  fill(255,0,0);
  textSize(12);
  text(frameRate,10,10);
}

public void drawTitle(){
  float uPosA = 0;
  float uPosB = 0;
  String tempTitle = title;

  textAlign(LEFT, TOP);
  textFont(titleFont);

  int a = tempTitle.indexOf("<u>");
  if(a > -1){
    tempTitle = tempTitle.replaceAll("<u>","");

    String subA = tempTitle.substring(0, a);
    println(subA);
    uPosA = textWidth(subA);
  }

  int b = tempTitle.indexOf("</u>");
  if(b > -1){
    tempTitle = tempTitle.replaceAll("</u>","");

    String subB = tempTitle.substring(a,b);
    uPosB = textWidth(subB);

  }
  float space = textWidth("  ");

  text(tempTitle, 20, -height/10, width, gridY);

  strokeWeight(5);
  strokeCap(SQUARE);
  stroke(0);
  line(uPosA + space, 0, uPosA + ((uPosB + space) * titleFactor), 0);
}

public void movieEvent(Movie m) {
  m.read();
}

public void initTable(int tabI){
  try {
    table = loadTable(tables[tabI], "header");
    fillArrayList(table, items);
    title = table.getColumnTitle(table.getColumnCount() - 1);
  }
  catch(Exception e) {
    println("table not there");
  }

  for(DiagramItem i : items) {
    i.animIn(20);
  }

  //animate title underline
  titleFactor = 0;
  Ani.to(this, 20, "titleFactor", 1, Ani.QUAD_OUT);

  transition.play();
}

public void switchTable() {

  if (tableIndex < tables.length - 1){
    tableIndex++;
  }else{
    tableIndex=0;
  }

  initTable(tableIndex);
}

public void mouseClicked(){
  switchTable();

}

public void fillArrayList(Table tab,ArrayList<DiagramItem> its){
  tab.setColumnType(1, "int");

  //sort by value
  tab.sort(1);
  maxValue = tab.getInt(tab.getRowCount() - 1, "students");

  //sort alphabetically
  tab.sort(0);

  its = new ArrayList<DiagramItem>();
  for (int i = 0; i < rows; i++) {
    for (int j = 0; j < cols; j++) {
      int index = j + i * cols;

      if (index < table.getRowCount()) {
        float x = j*gridX;
        float y = i*gridY;
        TableRow row = table.getRow(index);
        float r = map(row.getInt(1), 0, maxValue, 0, maxR);
        int c = pickColorFromArray(colorPalette);

        its.add(new DiagramItem(row, x, y, c, r));
      }
    }
  }

  items = its;
}

public int pickColorFromArray (int[] ca) {
  int r = floor(random(0, ca.length));
  return ca[r];
}

//colors
int blue = 0xff1356A5;
int yellow = 0xfff7ef00;
int orange = 0xfff79307;
int red = 0xffdf4c37;
int pink = 0xffeb619a;
int purple = 0xff8e84b4;
int[] colorPalette = { yellow, orange, red, pink, purple };


static final FilenameFilter CSV_FILTER = new FilenameFilter() {
  final String[] EXTS = {
    "csv"
  };

  public @Override final boolean accept(final File dir, String name) {
    name = name.toLowerCase();
    for (final String ext: EXTS)  if (name.endsWith(ext))  return true;
    return false;
  }
};
class DiagramItem{
  TableRow row;

  float x;
  float y;

  int c;

  float r;

  String strKey;
  int val;

  DiagramItem(TableRow row_, float x_, float y_, int c_, float r_){
    row = row_;

    x = x_;
    y = y_;

    c = c_;

    r = r_;

    strKey = row.getString(0).toUpperCase();
    val = row.getInt(1);
  }

  public void displayCircles() {
    noStroke();
    fill(c);
    ellipse(x + 0.5f*gridX, y +0.5f*gridY, r, r);

    textAlign(CENTER, CENTER);

    fill(0);
    textFont(bigFont);
    text(str(val), x, y, gridX, gridY);

    textAlign(CENTER, TOP);
    textFont(smallFont);
    text(strKey, x, y + gridY - height/30, gridX, gridY);
  }

  public void animIn(int dur){
    animInValues(dur);
    animInCircles(dur*4);
  }

  public void animInValues(int dur){
    float value = val;
    val = 0;
    Ani.to(this, dur, random(0, 12), "val", value, Ani.LINEAR);
  }

  public void animInCircles(int dur){
    float rad = r;
    r = 0;
    Ani.to(this, dur, random(0, 12), "r", rad, Ani.ELASTIC_OUT);
  }
}
  public void settings() {  size(1280, 400,P2D);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "circleDiagrams" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
