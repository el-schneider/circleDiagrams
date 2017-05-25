import de.looksgood.ani.*;
import de.looksgood.ani.easing.*;
import java.io.FilenameFilter;
import spout.*;

//spout object
Spout spout;
//spout senderName
String sendername;

int cols = 12;
int rows = 4;
Table table;

float gridX;
float gridY;

float maxR;
float maxValue;

float smallText;
float bigText;

String[] tables;
int tableIndex;

int refreshFrame = 300;

PFont font;

ArrayList<DiagramItem> items;

void setup () {
  size(1280, 400,P2D);
  // size(1920,800,P2D);
  textMode(SHAPE);
  smooth();

  Ani.init(this);
  Ani.setDefaultTimeMode(Ani.FRAMES);


  spout = new Spout(this);
  sendername = "circleDiagrams";
  spout.createSender(sendername, width, height);


  // add all CSV-type files to the tables array
  java.io.File folder = new java.io.File(dataPath(""));
  tables = folder.list(CSV_FILTER);


  gridX = 0.75*width/cols;
  gridY = height/rows;

  maxR = (height/4) * .9;

  smallText = height/37;
  bigText = height/20;

  font = createFont("DINPro-Medium", smallText);
  textFont(font);

  tableIndex = 0;

  initTable(tableIndex);
}

void draw() {
  translate(0.125*width, gridY/2);

  background(255);

  if(frameCount % refreshFrame == 0){
    switchTable();
  }

  for(DiagramItem i : items) {
    i.displayCircles();
  }

  textAlign(LEFT, TOP);
  textSize(height/10);

  String title = table.getColumnTitle(table.getColumnCount() - 1);
  text(title, 20, -height/10, width, gridY);

  spout.sendTexture();
  text(frameRate,10,10);
}

void initTable(int tabI){
  try {
    table = loadTable(tables[tabI], "header");
    fillArrayList(table, items);
  }
  catch(Exception e) {
    println("table not there");
  }

  for(DiagramItem i : items) {
    i.animIn(20);
  }
}

void switchTable() {
  if (tableIndex < tables.length - 1){
    tableIndex++;
  }else{
    tableIndex=0;
  }

  initTable(tableIndex);
}

void mouseClicked(){
  switchTable();

}

void fillArrayList(Table tab,ArrayList<DiagramItem> its){
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
        color c = pickColorFromArray(colorPalette);

        its.add(new DiagramItem(row, x, y, c, r));
      }
    }
  }

  items = its;
}

color pickColorFromArray (color[] ca) {
  int r = floor(random(0, ca.length));
  return ca[r];
}

//colors
color blue = #1356A5;
color yellow = #f7ef00;
color orange = #f79307;
color red = #df4c37;
color pink = #eb619a;
color purple = #8e84b4;
color[] colorPalette = { yellow, orange, red, pink, purple };


static final FilenameFilter CSV_FILTER = new FilenameFilter() {
  final String[] EXTS = {
    "csv"
  };

  @ Override final boolean accept(final File dir, String name) {
    name = name.toLowerCase();
    for (final String ext: EXTS)  if (name.endsWith(ext))  return true;
    return false;
  }
};
