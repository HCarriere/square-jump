package com.helusion.squarejump;

import processing.core.*; 
import processing.data.*; 
import processing.opengl.*; 

import com.parse.Parse; 
import com.parse.ParseAnalytics; 
import android.os.Bundle; 
import android.view.Gravity; 
import android.view.Window; 
import android.widget.RelativeLayout; 
import com.google.ads.*; 
import android.app.Activity; 
import android.preference.PreferenceManager; 
import android.content.Intent; 
import android.content.SharedPreferences; 
import android.content.Context; 
import android.net.ConnectivityManager; 
import android.net.NetworkInfo; 
import apwidgets.*; 
import android.media.SoundPool; 
import android.content.res.AssetManager; 
import android.media.AudioManager; 
import android.app.AlertDialog; 
import android.content.DialogInterface; 
import android.net.Uri; 

import com.parse.signpost.basic.*; 
import com.parse.gdata.*; 
import com.google.ads.searchads.*; 
import com.google.ads.*; 
import com.parse.internal.*; 
import com.google.ads.mediation.admob.*; 
import com.parse.signpost.http.*; 
import com.parse.codec.net.*; 
import com.parse.auth.*; 
import com.parse.signpost.exception.*; 
import com.google.ads.internal.*; 
import com.parse.codec.language.*; 
import com.parse.os.*; 
import com.parse.oauth.*; 
import com.parse.codec.digest.*; 
import com.parse.*; 
import com.google.ads.doubleclick.*; 
import com.parse.codec.binary.*; 
import com.parse.signpost.signature.*; 
import com.google.ads.util.*; 
import com.parse.signpost.*; 
import com.google.ads.mediation.customevent.*; 
import com.parse.twitter.*; 
import com.parse.signpost.commonshttp.*; 
import com.parse.codec.*; 
import com.parse.entity.mime.content.*; 
import com.parse.entity.mime.*; 
import com.google.ads.mediation.*; 

import android.view.MotionEvent; 
import android.view.KeyEvent; 
import android.graphics.Bitmap; 
import java.io.*; 
import java.util.*; 

public class SquareJump extends PApplet {

//debut de la prog:03-10-2013
//par Herv\u00e9 C.
/*
  SQUARE JUMP

  */









 //6.4.1
//import com.google.android.gms.ads.*;//gp 4.0


















public static final int DIVISION_WIDTH =10;//application terrain tout les ...largeur/x
int LARGEUR_BLOC = 96;
public static final int DIVISION_HEIGHT = 6;//hauteur
public static final int NUMBER_OF_SEGMENTS = 3*(DIVISION_WIDTH*2)+DIVISION_WIDTH; //num * standard +decu

public static final int PLACE_SQUARE = 5;//pos fixe du square, largeur/x
public static final int NUM_BLOC_MAX = DIVISION_WIDTH+5;

//definition des variables globales
int[] ColorDefautTheme = new int[11];
int[] ColorDefautSquare = new int[11];
int ColorBackground = color(255);
int ColorHorloge = color(215, 100);
int ColorTheme;
int ColorSquare;

int SquareSize;

boolean FirstSession = true; //devien false lorsque l'on passe le "tuto", donc lorsque l'on atteind le level (PartieReussie) 3. (PartieReussie>=2)
square Square;
bloc Bloc;
menuPrincipal MenuPrincipal = new menuPrincipal();
messageDelay MessageDelay = new messageDelay("", 0, true);
messageDelay ChallengeAffich = new messageDelay("", 0, true);
animBonusMalus AnimBonusMalus = new animBonusMalus();
int StateGame = 0; /*
etat du jeu: 
 0 <=> menu
 1 <=> jeu lanc\u00e9
 2 <=> pause*/
boolean DeadEventDelay = false;
int[] TerrainGeneration = new int[NUMBER_OF_SEGMENTS];
int ActualTileOn = 1; // le bloc sur lequel on est. sert a la collision. devien actuelle quand elle depasse le pixel du cube le plus en avant.
int Score = 0; 

int PartieReussie=0;
int jumpDoneInLevel=0;
int Speed;
int MalusSpeed=0;
int timeSpentOutOfFrame = 0;
//challenge
boolean ChallengeAccepted = false;
int NumChallenge=0;
int MessageChallengeWin=0;
int numConnection;

int PrixSkill[] = new int[5];

int NbrJumpDone=0;
int BonusPris=0;
int MalusPris=0;
int FrameInTheAir=0;
int[][] ChallStats = new int[18][2]; //0=> stats a atteindre 1=> reward

int numCristauxDebutPartie;
int jumpInSession;

//------------
int BlocBehindSquare, BlocBeforeSquare;
int AvancementX;
int PosSquareY;
PFont myFont;

String language = Locale.getDefault().getLanguage();
Locale l = new Locale("", language);
String country = l.getDisplayCountry();
int maxScoresAffich=5;
int[] BestsScores = new int[maxScoresAffich];
String[] BestsCountry = new String[maxScoresAffich];
boolean canLoadParse=false;
int PlaceJoueur=0;
int[] dailyBestsScores = new int[maxScoresAffich];
String[] dailyBestsCountry = new String[maxScoresAffich];
int thisTime;
/*  SAVES */
int hourClic;

int BestScore;
int pointMonaie;
int ColorThemeSelected;
int ColorSquareSelected;
int Acceleration;
int Gravity;
int MusicOn; // 1 oui, 0 non
int SoundOn;//1 oui, 0 non

int JumpTime;
int PointsMonaieUp;
int Luck;
int GhostModeMax;
int MalusTime;

int ColorBuy1;
int ColorBuy2;
int ColorBuy3; //0 non achet\u00e9, 1 achet\u00e9 ok
int ColorBuy4;
int ColorBuy5;

int ColorMAJAdding = 5;//max 5
SharedPreferences.Editor editor;

/*-----*/

boolean publicityEnabled = false;

class MyAdListener  implements AdListener {
  public void onReceiveAd(Ad ad) {
    publicityEnabled = true;

  }
  public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode error) {

  }
  public void onPresentScreen(Ad ad) {
    
  }
  public void onDismissScreen(Ad ad) {
    
  }
  public void onLeaveApplication(Ad ad) {
    clicOnAd(20); 
  }
}
MyAdListener adListener;

private  AdView adView;
RelativeLayout adsLayout;
RelativeLayout.LayoutParams lp2;

@Override
public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);

  adListener = new MyAdListener();
  //publicite
  Window window = getWindow();
  adsLayout = new RelativeLayout(this);
  lp2 = new RelativeLayout.LayoutParams(
  RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

  lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
  adView = new AdView(this, AdSize.BANNER, "ca-app-pub-5710405932894501/3759500873");

  adView.setAdListener(adListener);
  AdRequest adRequest = new AdRequest();
  // adRequest.addTestDevice(adRequest.TEST_EMULATOR);         // Emulator
  //adRequest.addTestDevice("7914A752D1812C22FC2CCC468A6C5CDE");     // Test Android Device
  adView.loadAd(adRequest);
  adsLayout.addView(adView, lp2);
  window.addContentView(adsLayout, lp2); 

  //saves
  SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

  numConnection = sharedPref.getInt("numConnection", 0);

  hourClic = sharedPref.getInt("hourClic", 0);
  BestScore = sharedPref.getInt("BestScore2", 0);
  pointMonaie = sharedPref.getInt("pointMonaie", 0);
  ColorThemeSelected = sharedPref.getInt("ColorThemeSelected", rand(0, 5));
  ColorSquareSelected = sharedPref.getInt("ColorSquareSelected", ColorThemeSelected);
  Acceleration = sharedPref.getInt("Acceleration", 4);
  Gravity = sharedPref.getInt("Gravity", 5);
  MusicOn=sharedPref.getInt("MusicOn", 1); // 1 oui, 0 non
  SoundOn=sharedPref.getInt("SoundOn", 1);
  
  JumpTime=sharedPref.getInt("JumpTime", 0);
  PointsMonaieUp=sharedPref.getInt("PointsMonaieUp", 0);
  Luck=sharedPref.getInt("Luck", 0);
  GhostModeMax=sharedPref.getInt("GhostModeMax", 0);
  MalusTime=sharedPref.getInt("MalusTime", 0);

  ColorBuy1=sharedPref.getInt("ColorBuy1", 0);
  ColorBuy2=sharedPref.getInt("ColorBuy2", 0);
  ColorBuy3=sharedPref.getInt("ColorBuy3", 0);
  ColorBuy4=sharedPref.getInt("ColorBuy4", 0);
  ColorBuy5=sharedPref.getInt("ColorBuy5", 0);

  editor = sharedPref.edit();

  Parse.initialize(this, "N84khIllDzSMrBfUqSLALBpzvvFRZBKvktlx28I2", "kBPw4BaTyrZWRV4ZeCoHzj1aL8qnxl2IAFJk5Jpx");
}


public void onDestroy() {
  super.onDestroy();

  if (soundPool!=null) { //must be checked because or else crash when return from landscape mode
    soundPool.release(); //release the player
  }
  if (player!=null) {
    player.release();
  }
  if (adView != null) {
    adView.destroy();
  }
} 

public void onPause() {
  super.onPause();

  //StateGame = 2; // on fait pause
  //on pause le son
  if (player!=null) {
    player.pause();
  }
}

public void onResume() {
  super.onResume();

  if (player!=null) {
    player.start();
  }
}

public void edit(String cle, int valeur) {
  editor.putInt(cle, valeur);
  editor.commit();
}

public void editString(String cle, String chaine) {
  editor.putString(cle, chaine);
  editor.commit();
}


//SOUNDS
boolean MusicStarted =  false;
APMediaPlayer player;
SoundPool soundPool; 
AssetManager assetManager; 
int touc1, touc2, touc3, levelUpSound, deathSound, bonusghost, maluss, bonuss;
String[] MusicTrack = new String[2];

public void playSoundPool(int sound) {
  if (SoundOn==1) soundPool.play(sound, 1, 1, 0, 0, 1);
}
//---------------*/


public void setup() {
  orientation(LANDSCAPE);
  //size(400,280); //samsung gs2 800 480
  LARGEUR_BLOC = width/8;
  soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
  assetManager = this.getAssets(); 
  MusicTrack[0] = "Fireproof_Babies-Why_I_m_unlovable.mp3";
  MusicTrack[1] = "stellarartwars-Island_Journey.mp3";
  try {
    touc1 = soundPool.load(assetManager.openFd("touc1.mp3"), 0); //load the files 
    touc2 = soundPool.load(assetManager.openFd("touc2.mp3"), 0);
    touc3 = soundPool.load(assetManager.openFd("touc3.mp3"), 0);
    bonusghost = soundPool.load(assetManager.openFd("bonusghost.mp3"), 0);
    maluss = soundPool.load(assetManager.openFd("malus.mp3"), 0);
    bonuss = soundPool.load(assetManager.openFd("bonus.mp3"), 0);
    levelUpSound = soundPool.load(assetManager.openFd("levelup.mp3"), 0);
    deathSound = soundPool.load(assetManager.openFd("death.mp3"), 0);
  }
  catch(Exception e) {
  }

  player = new APMediaPlayer(this); //musique longues*/
  frameRate(48);
  noSmooth();
  myFont = createFont("Roboto-Light.ttf", 32);
  textFont(myFont);
  SquareSize = height/14;
  PosSquareY = height-height/DIVISION_HEIGHT-SquareSize;
  BlocBehindSquare = ceil((width/PLACE_SQUARE) / LARGEUR_BLOC) +2;
  BlocBeforeSquare = ceil(width/LARGEUR_BLOC - ((width/PLACE_SQUARE) / LARGEUR_BLOC))+2;
  noStroke();
  //PRESENTS DE BASE
  ColorDefautTheme[0] = color(51, 181, 229);// bleu
  ColorDefautTheme[1] = color(170, 102, 204);//violet
  ColorDefautTheme[2] = color(153, 204, 0);//vert
  ColorDefautTheme[3] = color(255, 187, 51);//orange
  ColorDefautTheme[4] = color(100);
  ColorDefautTheme[5] = color(185, 122, 87);//marron

  ColorDefautSquare[0] = color(0, 133, 184);//bleu
  ColorDefautSquare[1] = color(133, 31, 184);//violet
  ColorDefautSquare[2] = color(10, 150, 50);//vert
  ColorDefautSquare[3] = color(235, 116, 0);//orange
  ColorDefautSquare[4] = color(0);
  ColorDefautSquare[5] = color(101, 58, 29);//marron

  // ACHETABLES
  ColorDefautTheme[6] = color(255, 68, 68);
  ColorDefautTheme[7] = color(205, 174, 201);
  ColorDefautTheme[8] = color(108, 255, 182);
  ColorDefautTheme[9] = color(200, 200, 50);
  ColorDefautTheme[10] = color(255, 255, 128);

  ColorDefautSquare[6] = color(184, 0, 0);
  ColorDefautSquare[7] = color(255, 121, 165);
  ColorDefautSquare[8] = color(0, 215, 167);
  ColorDefautSquare[9] = color(0, 50, 25);
  ColorDefautSquare[10] = color(200, 200, 0);




  ColorTheme = ColorDefautTheme[ColorThemeSelected];
  ColorSquare = ColorDefautSquare[ColorSquareSelected];

  edit("ColorThemeSelected", ColorThemeSelected);
  edit("ColorSquareSelected", ColorSquareSelected);

  //challenge repartition
  int multiplierChal = PApplet.parseInt(map(BestScore, 0, 200000, 1, 6));
  ChallStats[0][0] = 20*multiplierChal; //Jump more than X time
  ChallStats[0][1] = 4*multiplierChal; //reward
  ChallStats[1][0] = 35*multiplierChal; //Jump more than X time
  ChallStats[1][1] = 6*multiplierChal; 
  ChallStats[2][0] = 50*multiplierChal; //Jump more than X time
  ChallStats[2][1] = 8*multiplierChal; 

  ChallStats[3][0] = 3*multiplierChal; // X bonus pris
  ChallStats[3][1] = 6*multiplierChal; 
  ChallStats[4][0] = 6*multiplierChal; // X bonus pris
  ChallStats[4][1] = 10*multiplierChal; 

  ChallStats[5][0] = 3*multiplierChal; // X malus pris
  ChallStats[5][1] = 5*multiplierChal; 
  ChallStats[6][0] = 5*multiplierChal; // X malus pris
  ChallStats[6][1] = 8*multiplierChal; 

  ChallStats[7][0] = 60*multiplierChal; // X sec dans les air
  ChallStats[7][1] = 8*multiplierChal; 

  ChallStats[8][0] = 3*multiplierChal;  // atteindre le level X
  ChallStats[8][1] = 6*multiplierChal; 
  ChallStats[9][0] = 6*multiplierChal; // atteindre le level X
  ChallStats[9][1] = 8*multiplierChal; 
  
  ChallStats[10][0] = 4*multiplierChal; // bonus+malus > x
  ChallStats[10][1] = 6*multiplierChal;
  ChallStats[11][0] = 8*multiplierChal; // bonus+malus > x
  ChallStats[11][1] = 15*multiplierChal;
  
  ChallStats[12][0] = 4000*multiplierChal; // score > x
  ChallStats[12][1] = 6*multiplierChal;
  ChallStats[13][0] = 8000*multiplierChal; // score > x
  ChallStats[13][1] = 10*multiplierChal;
  ChallStats[14][0] = 12000*multiplierChal; // score > x
  ChallStats[14][1] = 16*multiplierChal;
  
  ChallStats[15][0] = 3*multiplierChal; // disparaitre de la map > x sec 
  ChallStats[15][1] = 6*multiplierChal;
  ChallStats[16][0] = 5*multiplierChal; // disparaitre de la map > x sec 
  ChallStats[16][1] = 10*multiplierChal;
  
  ChallStats[17][0] = 1; // changer couleur 
  ChallStats[17][1] = 10*multiplierChal;

  //////PRIX SKILLS ///// commencent par X et augmentent de X 
  PrixSkill[0] = 10;//saut maxi
  PrixSkill[1] = 200;//multi points
  PrixSkill[2] = 35;//luck
  PrixSkill[3] = 6;//ghost time
  PrixSkill[4] = 8;//malus time

  //remplir les tableau de rien
  for (int i=0;i<maxScoresAffich;i++) {
    BestsScores[i]=0;
    BestsCountry[i] = "";
    dailyBestsScores[i] = 0;
    dailyBestsCountry[i] = " - ";
  }
  meilleursScoreParse();
  dailyScoresParse();

  numConnection+=1; 
  edit("numConnection", numConnection); //au bout de 7 : demande de rate apr\u00e8s deco
  //println(numConnection);
  thisTime = (year()-1999)*372*24 + (month() -1)*31*24 + (day() -1)*24 + hour();
  
}

public String horloge() {
  String h="";
  String m="";
  if (hour()<10) h = "0"+hour();
  else h=""+hour();
  if (minute()<10) m = "0"+minute();
  else m=""+minute();
  return h+":"+m;
}

public void draw() {
  background(ColorBackground);
  fill(ColorHorloge);
  textSize(width/3);
  textAlign(CENTER, CENTER);
  text(horloge(), width/2, height/2);

  //dessinTerrain(AvancementX);
  ChallengeAffich.dessin();
  if (StateGame==0) {
    MenuPrincipal.dessin();
  }
  if (StateGame!=0) {
    dessinTerrain();
    dessinInterface();
    if (StateGame==1) {
      AvancementX+=(Speed+MalusSpeed);
      Score +=1;
      if (Score>50 && !ChallengeAccepted) {
        NumChallenge = floor(random(0, 18)); // 18 challenge
        NbrJumpDone=0;
        BonusPris=0;
        MalusPris=0;
        FrameInTheAir=0; //ras des variables
        ChallengePannel();
      }
      if (AvancementX >= 100 && !MusicStarted && MusicOn == 1) {
        player.start();
        player.setLooping(false);
        player.setVolume(0.8f, 0.8f);
        MusicStarted = true;
      }
    }
  }
  MessageDelay.dessin();
}

public int Divheight(int division) {
  return height - height/DIVISION_HEIGHT*division;
}

public void initialisationJeu() {
  LARGEUR_BLOC = width/8;
  SquareSize = height/14;
  /*println("LB= "+LARGEUR_BLOC+" et width/"+width/LARGEUR_BLOC);
   println("SS= "+SquareSize+" et height/"+height/SquareSize);*/
  //cacher pub
  runOnUiThread(new Runnable() {
    public void run() {
      adsLayout.removeView(adView);
    }
  }
  );
  //----
  MusicStarted = false;
  DeadEventDelay = false;
  PartieReussie = 0;
  Speed = (width/100)+PApplet.parseInt(map(Acceleration, 0, 6, 1, 3)); //initialisation de speed (8)
  Score = -6; // decalage anti cheat (ici : 6) -> arbitraire
  AvancementX = 0;
  TerrainGeneration = terrainGeneration(NUMBER_OF_SEGMENTS);
  StateGame = 1;
  MessageDelay = new messageDelay("", 0, true);
  ChallengeAffich = new messageDelay("", 0, true);
  Bloc = new bloc(TerrainGeneration);
  AnimBonusMalus = new animBonusMalus();
  Square = new square(width/PLACE_SQUARE,0);
  ChallengeAccepted = false;
  numCristauxDebutPartie = pointMonaie;
  jumpInSession=0;
  timeSpentOutOfFrame=0;
  //init de la musique
  int randMusique = rand(0, 1);
  if (MusicOn == 1) {
    player.setMediaFile(MusicTrack[randMusique]);
    //prechargement de la musique, on la lancera a AvancementX = LARGEUR_BLOC*10
  }
}

public void deadEvent() {
  if (BestScore<Score+6+PartieReussie) {
    BestScore = Score+6+PartieReussie;
    edit("BestScore2", Score+6+PartieReussie);
    //envoi du score sur le cloud Parse //////////////////////////////
    if (BestScore>=10000) {
      canLoadParse = true;
      ParseObject gameScore = new ParseObject("GameScore");
      gameScore.put("score", BestScore);
      gameScore.put("region", country);
      gameScore.saveEventually();
      
    }
    if (numConnection<400 && BestScore>=7000){ 
        alertDialogRate2();
    }
  }
  MessageDelay = new messageDelay(messageMauvais(), 120, false);
  StateGame = 2;
  DeadEventDelay = true;
}

public void exitToMenu() {
  //montrer pub
  runOnUiThread(new Runnable() {
    public void run() {
      adsLayout.addView(adView, lp2);
    }
  }
  );
  //----
  MenuPrincipal = new menuPrincipal();
  StateGame = 0;
  player.pause();
}

public void mousePressed() {
  MenuPrincipal.touch();

  interfaceTouch();
}

public void onBackPressed() {
  if (StateGame==1) {
    StateGame=2;
  }
  else if (StateGame==2) {
    player.setVolume(0.8f, 0.8f);
    if (!DeadEventDelay)
      StateGame=1;
    else
      exitToMenu();
  }
  else if (StateGame==0) {
    //exit
    if (MenuPrincipal.StateMenu()==0) {
      if (numConnection==10){ 
        alertDialogRate();
      }
      
      else{
        exit();
      }
    }
    //exit to main menu
    if (MenuPrincipal.StateMenu()==1) {
      MenuPrincipal.exitToMainMenu();
    }
    if (MenuPrincipal.StateMenu()==2) {
      MenuPrincipal.exitConfirmBox();
    }
  }
}

public void keyPressed() {
  if (key == CODED && keyCode == KeyEvent.KEYCODE_BACK) {
    keyCode = 1;
  }
}

/**/
class square {
  int posx;
  int posy;
  boolean twirl = false;
  float rot=0;
  float coeffSaut = 0;
  boolean canJump = false;
  boolean alreadyPressed = true;
  int timeSpentPushed = 0;
  int maxTimeSaut = 30+JumpTime+constrain(PartieReussie, 0, 10)*2;
  int GMMax = GhostModeMax*3 + 300;
  int ghostModeTimer = 0; //si plus grand que 0, ghostMode activ\u00e9
  int malusMax = 100 - MalusTime*3;
  int plombTimer = 0;
  int speedTimer = 0;
  boolean havePlaySound = false;
  boolean hasJump = false;
  

  square(int _posy, int _ghostModeTimer) {
    posx = width/PLACE_SQUARE;
    posy = _posy;
    MalusSpeed = 0;
    ghostModeTimer = _ghostModeTimer;
  }

  public void dessin() {  
    if (posy<-SquareSize) {
      //affichage d'un triangle indiquant sa position
      strokeWeight(1);
      stroke(ColorSquare);
      noFill();
      triangle(posx-SquareSize/3, SquareSize/3, posx+SquareSize/3, SquareSize/3, posx, 1);
      noStroke();
      if (NumChallenge == 15) {
        timeSpentOutOfFrame+=1; 
        if (timeSpentOutOfFrame>=ChallStats[15][0]*48) challengeWin(ChallStats[15][1]);
      }
      if (NumChallenge == 16) {
        timeSpentOutOfFrame+=1; 
        if (timeSpentOutOfFrame>=ChallStats[16][0]*48) challengeWin(ChallStats[16][1]);
      }
    }
    //No GhostMode
    if (ghostModeTimer<=0) {
      fill(ColorSquare);
    }
    //ghostMode
    else {
      if (StateGame==1)
        ghostModeTimer-=1;
      fill(0, map(ghostModeTimer, 0, GMMax, 50, 200));
      rect(0, /*height-10*/0, map(ghostModeTimer, 0, GMMax, 0, width), 10); //modif -> visibilit\u00e9
      fill(ColorSquare, map(ghostModeTimer, 0, GMMax, 200, 70));
    }
    //mode plomb
    if (plombTimer>0) {
      stroke(0);
      strokeWeight(2);
      if (StateGame==1)
        plombTimer -= 1;
    }
    //mode speed
    if (speedTimer>0) {
      if (StateGame==1)
        speedTimer -= 1;
      fill(155, 68, 68);
    }
    else
      MalusSpeed = 0;
    if (twirl) {
      pushMatrix();
      rectMode(CENTER);
      translate(posx+SquareSize/2, posy+SquareSize/2);
      rotate(rot);
      rect(0, 0, SquareSize, SquareSize);
      popMatrix();

      if (StateGame == 1) {
        rot+=0.314f;
        if (rot>PI) rot=0;
      }
      rotate(0);
      rectMode(CORNER);
      translate(0, 0);
    }
    else {
      rect(posx, posy, SquareSize, SquareSize);
    }
    if (StateGame == 1) {
      this.collision();
      this.jump();
      posy+=coeffSaut;
    }
    noStroke();
  }
  public float EquationSaut(float f) {
    float r=0;
    //f de 0 \u00e0 80
    //r fort au debut puis descend \u00e0 0 vers 50. progressif
    r = (((height*40)/exp(sqrt(f+28)))); //
    if (plombTimer>0) {
      r = r/1.5f;
    }
    return -r;
  }
  public void jump() {
    if (mousePressed) {
      if (/*timeSpentPushed>0 &&*/ !alreadyPressed) {
        if (SoundOn == 1 && !havePlaySound && ghostModeTimer<=0) {
          int randSound = rand(1, 3);
          if (randSound == 1) playSoundPool(touc1);
          else if (randSound == 2) playSoundPool(touc2);
          else playSoundPool(touc3);
          havePlaySound=true;
        }
        if (!hasJump) {
          if (ghostModeTimer<=0) {
            jumpDoneInLevel++;
            jumpInSession++;
          }
          if (ChallengeAccepted && NumChallenge>=0 && NumChallenge<=2 && ghostModeTimer<=0) {
            NbrJumpDone++; 
            if (NumChallenge==0 && NbrJumpDone==ChallStats[0][0]) {
              challengeWin(ChallStats[0][1]);
            }
            else if (NumChallenge==1 && NbrJumpDone==ChallStats[1][0]) {
              challengeWin(ChallStats[1][1]);
            }
            else if (NumChallenge==2 && NbrJumpDone==ChallStats[2][0]) {
              challengeWin(ChallStats[2][1]);
            }
          }
          timeSpentPushed=maxTimeSaut;
          hasJump= true;
        }
        coeffSaut = EquationSaut((maxTimeSaut+20)-timeSpentPushed);
        twirl=true;
      }
    }
    else {
      timeSpentPushed=0;
      alreadyPressed = false; //true auparavant
    }
    if (coeffSaut<0) {
      timeSpentPushed-=1;
    }
  }



  public void collision() {
    int actualBorder = TerrainGeneration[min(ActualTileOn, NUMBER_OF_SEGMENTS-1)];
    if (actualBorder<-1) {
      actualBorder = abs(actualBorder)-10;
      if (posy>Divheight(actualBorder+2)) {
        //challenge du malus
        if (ChallengeAccepted) {
          MalusPris+=1;
          if (NumChallenge==5) {
            if (MalusPris==ChallStats[5][0]) challengeWin(ChallStats[5][1]);
          }
          if (NumChallenge==6) {
            if (MalusPris==ChallStats[6][0]) challengeWin(ChallStats[6][1]);
          }
          if (NumChallenge == 10) {
            if (MalusPris + BonusPris == ChallStats[10][0]) challengeWin(ChallStats[10][1]);
          }
          if (NumChallenge == 11) {
            if (MalusPris + BonusPris == ChallStats[11][0]) challengeWin(ChallStats[11][1]);
          }
        }
        //malus
        int randMalus = rand(1, 100);
        if (randMalus<=40) {
          plombTimer = malusMax;
          MessageDelay = new messageDelay("Heavy mode!", 100, false);
          AnimBonusMalus = new animBonusMalus(false, malusMax, actualBorder+2);
        }
        else if (randMalus>40 && randMalus <=95) {
          speedTimer = malusMax;
          MalusSpeed = 3; 
          MessageDelay = new messageDelay("Speed up!", 100, false); 
          AnimBonusMalus = new animBonusMalus(false, malusMax, actualBorder+2);
        }
        else {
          if (NumChallenge==17) {
            challengeWin(ChallStats[17][1]);
          }
          MessageDelay = new messageDelay("Color change!", 100, false);
          ColorThemeSelected = rand(0, 5);
          ColorSquareSelected = ColorThemeSelected;
          ColorTheme = ColorDefautTheme[ColorThemeSelected];
          ColorSquare = ColorDefautSquare[ColorSquareSelected];
          AnimBonusMalus = new animBonusMalus(false, 50, actualBorder+2);
        }
        playSoundPool(maluss);
        //malus
        TerrainGeneration[ActualTileOn] = actualBorder;
      }
    }
    if (actualBorder>=10) {
      actualBorder = actualBorder-10;
      if (posy<Divheight(4)) {
        //challenge du bonus
        if (ChallengeAccepted) {
          BonusPris+=1;
          if (NumChallenge==3) {
            if (BonusPris==ChallStats[3][0]) challengeWin(ChallStats[3][1]);
          }
          if (NumChallenge==4) {
            if (BonusPris==ChallStats[4][0]) challengeWin(ChallStats[4][1]);
          }
          if (NumChallenge == 10) {
            if (MalusPris + BonusPris == ChallStats[10][0]) challengeWin(ChallStats[10][1]);
          }
          if (NumChallenge == 11) {
            if (MalusPris + BonusPris == ChallStats[11][0]) challengeWin(ChallStats[11][1]);
          }
        }
        //bonus
        int randBonus = rand(1, 100);
        if (randBonus<=30) {
          if (ghostModeTimer<=0) {
            playSoundPool(bonusghost);
            ghostModeTimer=GMMax;
            MessageDelay = new messageDelay("Ghost mode!", 100, true);
            AnimBonusMalus = new animBonusMalus(true, 100, 0);
          }
          else {
            playSoundPool(bonuss);
            pointMonaie+=1+PointsMonaieUp;
            edit("pointMonaie", pointMonaie);
            MessageDelay = new messageDelay("+"+(1+PointsMonaieUp)+" \u25ca !", 100, true);
            AnimBonusMalus = new animBonusMalus(true, 30, 0);
          }
        }
        else if (randBonus>30 && randBonus<=70) {
          playSoundPool(bonuss);
          pointMonaie+=1+PointsMonaieUp;
          edit("pointMonaie", pointMonaie);
          MessageDelay = new messageDelay("+"+(1+PointsMonaieUp)+" \u25ca !", 100, true);
          AnimBonusMalus = new animBonusMalus(true, 30, 0);
        }
        else if (randBonus>70 && randBonus<=90) {
          playSoundPool(bonuss);
          pointMonaie+=2+PointsMonaieUp;
          edit("pointMonaie", pointMonaie);
          MessageDelay = new messageDelay("+"+(2+PointsMonaieUp)+" \u25ca !", 100, true);
          AnimBonusMalus = new animBonusMalus(true, 60, 0);
        }
        else {
          playSoundPool(bonuss);
          pointMonaie+=3+PointsMonaieUp;
          edit("pointMonaie", pointMonaie);
          MessageDelay = new messageDelay("+"+(3+PointsMonaieUp)+" \u25ca !", 100, true);
          AnimBonusMalus = new animBonusMalus(true, 90, 0);
        }

        //bonus
        TerrainGeneration[ActualTileOn] = actualBorder;
      }
    }
    actualBorder = Divheight(actualBorder);

    //calcul si bonus/malus
    //si en l'air
    if (posy+SquareSize+coeffSaut<actualBorder) {
      if (ChallengeAccepted && NumChallenge == 7) {
        FrameInTheAir++;
        if (FrameInTheAir >= ChallStats[7][0]*48) {
          challengeWin(ChallStats[7][1]);
        }
      }
      if (timeSpentPushed<=0) {
        coeffSaut += height/(((13-Gravity)/3)*(height/4));
        alreadyPressed=true;
        twirl=true;
      }
    }
    else {
      havePlaySound = false;
      hasJump=false;
      /*---commenter ici pour desactiver saut repet\u00e9---*/
      timeSpentPushed = maxTimeSaut;
      alreadyPressed = false;
      /*--------*/
      twirl = false;
      coeffSaut = 0;
      //alignement
      if (posy+SquareSize<actualBorder) {
        posy = actualBorder-SquareSize;
      }
    }
    if (posy+SquareSize/2+coeffSaut>actualBorder || posy>Divheight(1) ) {
      if (ghostModeTimer<=0 || posy>Divheight(1)) {
        playSoundPool(deathSound);

        deadEvent(); //mort
      }
    }
  }
  public int posy() {
    return posy;
  }
  public int getGhostModeTimer() {
    return ghostModeTimer;
  }
}

class bloc {
  int[] seq;
  int blocOn=0;
  bloc (int[] _sequence) {
    seq = _sequence;
  }

  public void dessin() {
    fill(ColorTheme);
    for (int i=max(0,ActualTileOn-BlocBehindSquare);i<min(ActualTileOn+BlocBeforeSquare+3,NUMBER_OF_SEGMENTS);i++) { //NUMBER_OF_SEGMENTS
      //si <-1: malus si >10 : bonus
      if (seq[i]<-1) {
        fill(255, 68, 68, 155); //rouge
        rect(i*LARGEUR_BLOC-AvancementX, Divheight(abs(seq[i])-8), LARGEUR_BLOC, height);
        fill(255);
        textSize(width/16);
        textAlign(CENTER,CENTER);
        text("?",i*LARGEUR_BLOC-AvancementX+LARGEUR_BLOC/2,Divheight(abs(seq[i])-8)+(height/DIVISION_HEIGHT)/2);
        fill(ColorTheme);
        rect(i*LARGEUR_BLOC-AvancementX, Divheight(abs(seq[i])-10), LARGEUR_BLOC, height);
      }
      else if (seq[i]>=10) {
        fill(153, 204, 0, 155); // vert
        rect(i*LARGEUR_BLOC-AvancementX, 0, LARGEUR_BLOC, 2*(height/DIVISION_HEIGHT));
        fill(255);
        textSize(width/16);
        textAlign(CENTER,CENTER);
        text("?",i*LARGEUR_BLOC-AvancementX+LARGEUR_BLOC/2,1.5f*(height/DIVISION_HEIGHT));
        fill(ColorTheme);
        rect(i*LARGEUR_BLOC-AvancementX, Divheight(seq[i]-10), LARGEUR_BLOC, height);
      }
      //si normal
      else {
        rect(i*LARGEUR_BLOC-AvancementX, Divheight(seq[i]), LARGEUR_BLOC, height);
      }
    }
    //rect de la fin
    rect((NUMBER_OF_SEGMENTS-1)*LARGEUR_BLOC-AvancementX, Divheight(seq[NUMBER_OF_SEGMENTS-1]), width*2, height);
    
    ActualTileOn = (AvancementX+SquareSize+(width/PLACE_SQUARE))/(LARGEUR_BLOC);
    /*if(blocOn<ActualTileOn && StateGame==1){
      Score++;
      blocOn+=1;
    }*/
  }
}

class animBonusMalus {
  boolean isBonus = true; //else = malus
  int pSize = max(1, SquareSize/2);
  int maxParticles = (((height/DIVISION_HEIGHT)*2)*LARGEUR_BLOC)/(pSize*pSize);
  float tabAnim[][] = new float[4][maxParticles];
  int posx = (width/PLACE_SQUARE);
  int timer;
  int place;

  animBonusMalus(boolean _isBonus, int _timer, int _place) {
    isBonus = _isBonus;
    timer = _timer;
    place = _place;
    if (isBonus) {
      int countI = 0;
      for (int i=0;i<((height/DIVISION_HEIGHT)*2)/pSize;i++) {
        for (int j=1;j<=LARGEUR_BLOC/pSize;j++) {
          tabAnim[0][countI] = posx+(LARGEUR_BLOC)-(LARGEUR_BLOC/pSize)*j;
          tabAnim[1][countI] = i*(((height/DIVISION_HEIGHT)*2)/pSize);
            tabAnim[2][countI] = random(-3, 3)-Speed;
            tabAnim[3][countI] = random(-3, -1);
          countI++;
          //rect(tabAnim[0][i], tabAnim[1][i], SquareSize/2, SquareSize/2);
        }
      }
    }
    else {
      int countI = 0;
      for (int i=0;i<((height/DIVISION_HEIGHT)*2)/pSize;i++) {
        for (int j=1;j<=LARGEUR_BLOC/pSize;j++) {
          tabAnim[0][countI] = posx+(LARGEUR_BLOC)-(LARGEUR_BLOC/pSize)*j;
          tabAnim[1][countI] = Divheight(place)+i*(((height/DIVISION_HEIGHT)*2)/pSize);
            tabAnim[2][countI] = random(-1, 1)-Speed;
            tabAnim[3][countI] = random(1, 6);
          countI++;
          //rect(tabAnim[0][i], tabAnim[1][i], SquareSize/2, SquareSize/2);
        }
      }
    }
  }
  animBonusMalus() {
  }

  public void dessin() {
    if (timer>0) {
      if (isBonus) fill(153, 204, 0, timer);
      else fill(255, 68, 68, timer);

      for (int i=0;i<maxParticles;i++) {
        if (StateGame==1) {
          tabAnim[0][i] += tabAnim[2][i]/*(posx-tabAnim[0][i])*/;
          tabAnim[1][i] += tabAnim[3][i]/*(Square.posy()-tabAnim[1][i])*/;
        }
        rect(tabAnim[0][i], tabAnim[1][i], pSize, pSize);
      }
      if (StateGame==1)
        timer -=1;
    }
  }
}


public void dessinInterface() {

  if (StateGame==1) {
    //bouton pause
    fill(200);
    if (!DeadEventDelay) {
      rect(width/1.08f, height/18.46f, width/80, height/16);
      rect(width/1.08f+width/40, height/18.46f, width/80, height/16);
    }
    //score
    fill(ColorSquare);
    textSize(width/30);
    textAlign(LEFT, TOP);
    text("Score: "+(Score+6+PartieReussie), width/32, height/24);
    textSize(width/40);
    text("Level: "+(PartieReussie+1), width/32, height/10.5f);
    //challenge score
    if (NumChallenge==12 && Score+6+PartieReussie>=ChallStats[12][0]) {
      challengeWin(ChallStats[12][1]);
    }
    if (NumChallenge==13 && Score+6+PartieReussie>=ChallStats[13][0]) {
      challengeWin(ChallStats[13][1]);
    }
    if (NumChallenge==14 && Score+6+PartieReussie>=ChallStats[14][0]) {
      challengeWin(ChallStats[14][1]);
    }
  }
  if (StateGame==2) { //mode pause
    player.setVolume(0.2f, 0.2f); // ajustage du volume du son
    fill(200, 150);
    rect(0, 0, width, height);
    //pause
    if (!DeadEventDelay) {
      fill(100);
      rect(width/1.08f, height/18.46f, width/80, height/16);
      rect(width/1.08f+width/40, height/18.46f, width/80, height/16);
    }
    //text pointMonaie
    fill(ColorSquare);
    textSize(width/30);
    textAlign(CENTER, CENTER);
    if (!DeadEventDelay)
      text(pointMonaie+" x \u25ca", width/1.09f, height/1.11f);
    //score
    fill(ColorSquare);
    textSize(width/30);
    textAlign(LEFT, TOP);
    text("Score: "+(Score+6+PartieReussie), width/32, height/24); //on ajoute un coefficient : plus difficile a cheat enginer 
    textSize(width/40);
    text("Level: "+(PartieReussie+1), width/32, height/10.5f);
    //bloc PAUSED
    fill(ColorSquare);
    //rect(width/2 - width/8, height/6, width/4, height/6);
    rect(0, height/6, width, height/6);
    fill(ColorTheme);
    //bloc RESUME
    if (!DeadEventDelay) rect(width/2 - width/8, height/2.6f, width/4, height/7);
    //bloc RESTART
    rect(width/2 - width/8, height/1.87f, width/4, height/7);
    //bloc QUIT
    rect(width/2 - width/8, height/1.46f, width/4, height/7);
    //txt PAUSED
    textSize(width/16);
    textAlign(CENTER, TOP);
    if (!DeadEventDelay)
      text("Paused", width/2, height/5.5f);

    if (DeadEventDelay) {
      //panneau dead
      text("Dead", width/2, height/5.5f);
      textAlign(CENTER, CENTER);
      textSize(width/34);
      //fill(ColorSquare);
      //rect(width/1.4,height/2.8,width/2,height/2.8);
      fill(ColorSquare);
      text("\u2022 Score: "+(Score+6+PartieReussie) +"  (Best: "+BestScore+")  \u2022  Level: "+(PartieReussie+1)+"  \u2022  Jumps: "+jumpInSession +(pointMonaie-numCristauxDebutPartie > 0? "  \u2022  Diamonds: "+(pointMonaie-numCristauxDebutPartie) : ""), width/2, height/2.5f);
      /*text("Score: "+(Score+6+PartieReussie), width/1.3, height/4.1+height/6);
       text("Level: "+(PartieReussie+1), width/1.3, height/3.15+height/6);
       text("Jumps: "+jumpInSession, width/1.3, height/2.56+height/6);
       if (pointMonaie-numCristauxDebutPartie == 1) {
       text("+"+(pointMonaie-numCristauxDebutPartie)+" Diamond!", width/1.3, height/2.16+height/6);
       }
       if (pointMonaie-numCristauxDebutPartie > 1) {
       text("+"+(pointMonaie-numCristauxDebutPartie)+" Diamonds!", width/1.3, height/2.16+height/6);
       }*/
    }
    textAlign(CENTER, TOP);
    //txt RESUME
    fill(ColorSquare);
    textSize(width/20);
    if (!DeadEventDelay) text("Resume", width/2, height/2.5f);
    //txt Restart
    text("Restart", width/2, height/1.8f);
    //txt Quit
    text("Quit", width/2, height/1.43f);
    if (ChallengeAccepted && NumChallenge>-1 && !DeadEventDelay) {
      String challDescr ="";
      int challReward=0;
      switch(NumChallenge) {
      case 0:
        challDescr = "Jump more than "+ChallStats[0][0]+" times before dying. ("+NbrJumpDone+"/"+ChallStats[0][0]+")";
        break;
      case 1:
        challDescr = "Jump more than "+ChallStats[1][0]+" times before dying. ("+NbrJumpDone+"/"+ChallStats[1][0]+")";
        break;
      case 2:
        challDescr = "Jump more than "+ChallStats[2][0]+" times before dying. ("+NbrJumpDone+"/"+ChallStats[2][0]+")";
        break;
      case 3:
        challDescr = "Take "+ChallStats[3][0]+" bonus. ("+BonusPris+"/"+ChallStats[3][0]+")";
        break;
      case 4:
        challDescr = "Take "+ChallStats[4][0]+" bonus. ("+BonusPris+"/"+ChallStats[4][0]+")";
        break;
      case 5:
        challDescr = "Take "+ChallStats[5][0]+" malus. ("+MalusPris+"/"+ChallStats[5][0]+")";
        break;
      case 6:
        challDescr = "Take "+ChallStats[6][0]+" malus. ("+MalusPris+"/"+ChallStats[6][0]+")";
        break;
      case 7:
        challDescr = "Stay more than "+ChallStats[7][0]+" sec in the air. ("+(FrameInTheAir/48)+"/"+ChallStats[7][0]+")";
        break;
      case 8:
        challDescr = "Reach the level "+ChallStats[8][0]+". ("+(PartieReussie+1)+"/"+ChallStats[8][0]+")";
        break;
      case 9:
        challDescr = "Reach the level "+ChallStats[9][0]+". ("+(PartieReussie+1)+"/"+ChallStats[9][0]+")";
        break;
      case 10:
        challDescr = "Take "+ChallStats[NumChallenge][0]+" bonus or malus. ("+ (BonusPris+MalusPris) +"/"+ChallStats[NumChallenge][0]+")";
        break;
      case 11:
        challDescr = "Take "+ChallStats[NumChallenge][0]+" bonus or malus. ("+ (BonusPris+MalusPris) +"/"+ChallStats[NumChallenge][0]+")";
        break;
      case 12:
        challDescr = "Make more than "+ChallStats[NumChallenge][0]+" points. ("+(Score+6+PartieReussie)+"/"+ChallStats[NumChallenge][0]+")";
        break;
      case 13:
        challDescr = "Make more than "+ChallStats[NumChallenge][0]+" points. ("+(Score+6+PartieReussie)+"/"+ChallStats[NumChallenge][0]+")";
        break;
      case 14:
        challDescr = "Make more than "+ChallStats[NumChallenge][0]+" points. ("+(Score+6+PartieReussie)+"/"+ChallStats[NumChallenge][0]+")";
        break;
      case 15:
        challDescr = "Get out of the frame for "+ChallStats[NumChallenge][0]+" seconds. ("+(timeSpentOutOfFrame/48)+"/"+ChallStats[NumChallenge][0]+")";
        break;
      case 16:
        challDescr = "Get out of the frame for "+ChallStats[NumChallenge][0]+" seconds. ("+(timeSpentOutOfFrame/48)+"/"+ChallStats[NumChallenge][0]+")";
        break;
      case 17:
        challDescr = "Change the color in game.";
        break;
      }

      challReward = ChallStats[NumChallenge][1];
      fill(ColorSquare);
      textSize(width/35);
      textAlign(CENTER, CENTER);
      text(challDescr+"\nReward: "+challReward, width/2, height/9.5f);
    }
  }
  if (MessageChallengeWin>0) {
    fill(ColorSquare, min(MessageChallengeWin*8, 255));
    textSize(width/20);
    textAlign(CENTER, CENTER);
    text("Mission accomplished!", width/2, height/4);
    MessageChallengeWin--;
  }
}

public void interfaceTouch() {
  if (StateGame == 1) {
    //TOUCH EVENT INGAME interface
    //pause
    if (mouseX>width/1.12f && mouseY<height/8) {
      StateGame = 2;
    }
  }
  if (StateGame == 2) {
    //TOUCH EVENT PAUSEMENU interface
    //resume&restart&quit
    if (mouseX>width/2 - width/8 && mouseX < width/2 + width/8) {
      //resume
      if (mouseY>height/2.6f && mouseY<height/1.87f && !DeadEventDelay) {
        player.setVolume(0.8f, 0.8f);
        StateGame=1;
      }
      //restart
      if (mouseY>height/1.87f && mouseY<height/1.46f) {
        initialisationJeu();
      }
      //quit
      if (mouseY>height/1.46f && mouseY<height/1.46f+height/7) {
        exitToMenu();
      }
    }
  }
}

class messageDelay {
  String message;
  int delay;
  boolean colorgb;//color good bad
  boolean chall=false;
  int animEntree = 0;
  boolean messageSystem = false;

  messageDelay(String _message, int _delay, boolean _colorgb) {
    message = _message;
    delay = _delay;
    colorgb = _colorgb;
  } 
  messageDelay(String _message, int _delay, boolean _colorgb, boolean _chall) {
    message = _message;
    delay = _delay;
    colorgb = _colorgb;
    chall = _chall;
    animEntree = 30;
  } 
  messageDelay(String _message, int _delay) {
    message = _message;
    delay = _delay; 
    animEntree=30;
    messageSystem = true;
  }


  public void dessin() {
    if (delay>0) {
      if (StateGame != 0 || messageSystem) {
        if (colorgb) fill(ColorSquare, min(delay*3, 255)); //good
        else fill(100, min(delay*3, 255)); //bad
        textAlign(CENTER, CENTER);

        if (!chall && !messageSystem) {
          textSize(width/message.length());
          text(message, width/2, height/2);
        }
        if (chall || messageSystem) {
          textSize(width/22);
          fill(ColorTheme, min(delay*3, 180));
          rect(0, height/6.5f-animEntree*5, width, width/8);
          //inverse de la couleur theme(comment)
          //fill(map(red(ColorTheme),0,255,255,0),map(green(ColorTheme),0,255,255,0),map(blue(ColorTheme),0,255,255,0),min(delay*3,255));
          fill(ColorSquare);
          text(message, width/2, height/4-animEntree*5);
          if (animEntree>0) animEntree-=2;
        }
        delay-=1;
      }
    }
  }
}

public String messageBien() {
  int r = rand(0, 10);
  String mB="";
  if (r==0) mB= "Awesome!";
  else if (r==1) mB= "Great!";
  else if (r==2) mB= "Good!";
  else if (r==3) mB= "Wow!";
  else if (r==4) mB= "Success!";
  else if (r==5) mB= "Not bad.";
  else if (r==6) mB= "Beautiful!";
  else if (r==7) mB= "Cool!";
  else if (r==8) mB= "Super!";
  else if (r==9) mB= "Well!";
  else mB= "Awesome!";
  return mB;
}

public String messageMauvais() {
  int r = rand(0, 5);
  String mB="";
  if (r==0) mB= "Too bad!";
  else if (r==1) mB= "Game over.";
  else if (r==2) mB= "Loser.";
  else if (r==3) mB= "Very bad!";
  else if (r==4) mB= "Game over!";
  else mB= "Game over.";
  return mB;
}




public void ChallengePannel() {
  String challDescr ="";
  int challReward=0;
  switch(NumChallenge) {
  case 0:
    challDescr = "Jump more than "+ChallStats[NumChallenge][0]+" times before dying.";
    break;
  case 1:
    challDescr = "Jump more than "+ChallStats[NumChallenge][0]+" times before dying.";
    break;
  case 2:
    challDescr = "Jump more than "+ChallStats[NumChallenge][0]+" times before dying.";
    break;
  case 3:
    challDescr = "Take "+ChallStats[NumChallenge][0]+" bonus.";
    break;
  case 4:
    challDescr = "Take "+ChallStats[NumChallenge][0]+" bonus.";
    break;
  case 5:
    challDescr = "Take "+ChallStats[NumChallenge][0]+" malus.";
    break;
  case 6:
    challDescr = "Take "+ChallStats[NumChallenge][0]+" malus.";
    break;
  case 7:
    challDescr = "Stay more than "+ChallStats[NumChallenge][0]+" sec in the air.";
    break;
  case 8:
    challDescr = "Reach the level "+ChallStats[NumChallenge][0]+".";
    break;
  case 9:
    challDescr = "Reach the level "+ChallStats[NumChallenge][0]+".";
    break;
  case 10:
    challDescr = "Take "+ChallStats[NumChallenge][0]+" bonus or malus.";
    break;
  case 11:
    challDescr = "Take "+ChallStats[NumChallenge][0]+" bonus or malus.";
    break;
  case 12:
    challDescr = "Make more than "+ChallStats[NumChallenge][0]+" points.";
    break;
  case 13:
    challDescr = "Make more than "+ChallStats[NumChallenge][0]+" points.";
    break;
  case 14:
    challDescr = "Make more than "+ChallStats[NumChallenge][0]+" points.";
    break;
  case 15:
    challDescr = "Get out of the frame for "+ChallStats[NumChallenge][0]+" seconds.";
    break;
  case 16:
    challDescr = "Get out of the frame for "+ChallStats[NumChallenge][0]+" seconds.";
    break;
  case 17:
    challDescr = "Change the color in game.";
    break;
  }
  challReward = ChallStats[NumChallenge][1];
  ChallengeAffich = new messageDelay(challDescr+"\n"+"Reward: "+challReward+" \u25ca", 250, true, true);

  ChallengeAccepted = true;
}

public void challengeWin(int price) {
  pointMonaie+=price;
  edit("pointMonaie", pointMonaie);
  MessageChallengeWin = 100;
  NumChallenge = -1;
}

public void meilleursScoreParse() {
  ///retrouver les meilleurs scores
  ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
  query.orderByDescending("score");
  query.setLimit(maxScoresAffich);
  query.selectKeys(Arrays.asList("score", "region"));
  query.findInBackground(new FindCallback<ParseObject>() {
    public void done(List<ParseObject> scoreList, ParseException e) {
      if (e==null) {
        for (int i=0;i<maxScoresAffich;i++) {
          //ParseObject obj = scoreList.get(i);
          BestsScores[i]= scoreList.get(i).getInt("score");
          BestsCountry[i] = scoreList.get(i).getString("region");
        }
      }
    }
  }
  ); 

  ParseQuery<ParseObject> query2 = ParseQuery.getQuery("GameScore");
  query2.whereGreaterThan("score", BestScore);
  query2.countInBackground(new CountCallback() {
    public void done(int count, ParseException e) {
      if (e == null) {
        // The count request succeeded. Log the count
        PlaceJoueur = count+1;
      }
    }
  }
  );
}

public void dailyScoresParse() {
  /*

   Date midnight = new Date();
   midnight.setHours(0);
   midnight.setMinutes(0);
   midnight.setSeconds(0);
   
   Date elevenfiftynine = new Date();
   elevenfiftynine.setHours(23);
   elevenfiftynine.setMinutes(59);
   elevenfiftynine.setSeconds(59);
   
   
   //meilleurs scores au dessus de lastDate
   ParseQuery<ParseObject> query2 = ParseQuery.getQuery("GameScore");
   query2.orderByDescending("score");
   query2.setLimit(maxScoresAffich);
   query2.whereGreaterThan("createdAt", midnight);
   query2.whereLessThan("createdAt", elevenfiftynine);
   query2.selectKeys(Arrays.asList("score", "region"));
   query2.findInBackground(new FindCallback<ParseObject>() {
   public void done(List<ParseObject> scoreList, ParseException e) {
   if (e==null) {
   for (int i=0;i<min(scoreList.size(),maxScoresAffich);i++) {
   //ParseObject obj = scoreList.get(i);
   dailyBestsScores[i]= scoreList.get(i).getInt("score");
   dailyBestsCountry[i] = scoreList.get(i).getString("region");
   }
   }
   }
   }
   );*/
}
public boolean difClicHour() {

  boolean canClicAd=false;
  if (hourClic+8 < thisTime) {
    canClicAd = true;
  }
  return canClicAd;
}

public void clicOnAd(int num) {
  if (difClicHour()==true) {

    hourClic = thisTime;
    edit("hourClic", hourClic);

    publicityEnabled = false;
    playSoundPool(bonuss);
    pointMonaie+=num;
    edit("pointMonaie", pointMonaie);
    //println("clic "+hourClic);
    MessageDelay = new messageDelay("+"+num+" \u25ca !", 120);
  }
}

public void alertDialogRate() {
  if (publicityEnabled) {
    MyAlertDialog AD = new MyAlertDialog();
    AD.popup(this, "Rate us!", "If you like this game, rate us 5 stars!");
  }
  else {
    numConnection -=1;
    edit("numConnection", numConnection);
    exit();
  }
}
public void alertDialogRate2() {
  if (publicityEnabled) {
    MyAlertDialog2 AD = new MyAlertDialog2();
    AD.popup(this, "Rate us!", "If you like this game, rate us 5 stars!");
  }
}

public class MyAlertDialog {

  public void popup(Activity _parent, String _title, String _message) {
    final Activity parent = _parent;
    final String message = _message;
    final String title = _title;

    parent.runOnUiThread(new Runnable() {
      public void run() {
        new AlertDialog.Builder(parent)
          .setTitle(title)
          .setMessage(message)
          .setPositiveButton("Rate!", 
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, 
          int which) {
            //monaie
            //playSoundPool(bonuss);
            //pointMonaie+=20;
            //edit("pointMonaie", pointMonaie);
            //MessageDelay = new messageDelay("+20 \u25ca !", 120);

            ///////// LINK
            final String appPackageName = "com.helusion.squarejump";
            numConnection+=500; 
            edit("numConnection", numConnection);
            try {
              startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } 
            catch (android.content.ActivityNotFoundException anfe) {
              startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            numConnection+=500; 
            edit("numConnection", numConnection);
            exit();
          }
        }
        )
          .setNegativeButton("Close", 
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, 
          int which) {
            //EXIT
            numConnection+=500; 
            edit("numConnection", numConnection); 
            exit();
          }
        }
        ).show();
      }
    }
    );
  }
}

public class MyAlertDialog2 {

  public void popup(Activity _parent, String _title, String _message) {
    final Activity parent = _parent;
    final String message = _message;
    final String title = _title;

    parent.runOnUiThread(new Runnable() {
      public void run() {
        new AlertDialog.Builder(parent)
          .setTitle(title)
          .setMessage(message)
          .setPositiveButton("Rate!", 
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, 
          int which) {
            //monaie
            //playSoundPool(bonuss);
            //pointMonaie+=20;
            //edit("pointMonaie", pointMonaie);
            //MessageDelay = new messageDelay("+20 \u25ca !", 120);

            ///////// LINK
            final String appPackageName = "com.helusion.squarejump";
            numConnection+=500; 
            edit("numConnection", numConnection);
            try {
              startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } 
            catch (android.content.ActivityNotFoundException anfe) {
              startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            numConnection+=500; 
            edit("numConnection", numConnection);
            exit();
          }
        }
        )
          .setNegativeButton("Close", 
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, 
          int which) {
            //EXIT
            numConnection+=500; 
            edit("numConnection", numConnection);
          }
        }
        ).show();
      }
    }
    );
  }
}

class menuPrincipal {
  int xrect = -1000;
  int yrect = 0;
  int lrect = 3;
  boolean delayInitJeu=false;
  int tailleSquare;
  int xSquare;
  int ySquare;
  int echelleAnimation = 0;
  boolean home = false; //false=principal, true = home
  boolean skills = false;
  boolean worldScore = false;
  int decalageHome=0;
  int decalageSkills = 0;
  int decalageWorldScore = 0;
  float decH; 
  float decS;
  float decWS;
  int iConfirmBox = 0;//0=rien, 1=jumptime, 2=point multi..; etc
  int iConfirmBoxColor = 0;
  menuPrincipal() {
  } 


  public void dessin() {
    this.dessinPrincipal();
    if (home || decalageHome>0)
      this.dessinHome();
    if (skills || decalageSkills>0)
      this.dessinSkills();
    if (worldScore || decalageWorldScore>0)
      this.dessinWorldScore();

    if (delayInitJeu) {
      //animation starting
      if (yrect>height/DIVISION_HEIGHT && echelleAnimation<width) {
        yrect+=height/48*2;
        echelleAnimation+=width/53*2;
      }
      if (tailleSquare>SquareSize) {
        tailleSquare-=height/80*1.1f;
      }
      if (xSquare<width/PLACE_SQUARE) {
        xSquare+=height/160*2;
        ySquare+=height/160*2;
      }

      if (xSquare>=width/PLACE_SQUARE && tailleSquare<=SquareSize) {
        delayInitJeu=false;
        initialisationJeu();
      }
    }
    else {
      xSquare=width/80;
      ySquare=height/24;
      tailleSquare=width/4;
    }
    //HOME
    if (home) {
      if (decalageHome<width*0.72f) { //seuil
        decalageHome+=width/53*2; //vitesse
      }
    }
    else {
      if (decalageHome>0) {
        decalageHome-=width/53*2;
      }
      else if (decalageHome<0) {
        decalageHome+=1;
      }
    }
    //SKILLS
    if (skills) {
      if (decalageSkills<width*0.72f) {
        decalageSkills+=width/53*2;
      }
    }
    else {
      if (decalageSkills>0) {
        decalageSkills-=width/53*2;
      }
      else if (decalageSkills<0) {
        decalageSkills+=1;
      }
    }
    //world score
    if (worldScore) {
      if (decalageWorldScore<width*0.72f) {
        decalageWorldScore+=width/53*2;
      }
    }
    else {
      if (decalageWorldScore>0) {
        decalageWorldScore-=width/53*2;
      }
      else if (decalageWorldScore<0) {
        decalageWorldScore+=1;
      }
    }
    /* SECURITE */
    if (home) {
      skills=false;
      worldScore=false;
    }
    if (skills) {
      worldScore=false;
      home=false;
    }
    if (worldScore) {
      skills=false;
      home=false;
    }
    /* */
  }

  public void dessinPrincipal() {
    
    if (xrect<=-height) {
      xrect=PApplet.parseInt(random(1, 3)*100+width);
      yrect=Divheight(rand(2, 3));
      lrect = rand(2, 6);
    }
    //text pointMonaie
    fill(ColorSquare);
    textSize(width/30);
    textAlign(CENTER, CENTER);
    text(pointMonaie+" x \u25ca", width/1.09f+echelleAnimation, height/16.5f-decalageWorldScore*2);
    //square play
    fill(ColorSquare);
    rect(xSquare-(decalageHome*2+decalageSkills*2+decalageWorldScore*2), ySquare, tailleSquare, tailleSquare);
    fill(ColorTheme);
    //rect home
    rect(2*width/3+echelleAnimation-(decalageHome+decalageSkills*2), height/6-decalageWorldScore*2, width/3+5, height/5);
    //rect skills
    rect(2*width/3+echelleAnimation-(decalageHome*2+decalageSkills), height/2.5f-decalageWorldScore*2, width/3+5, height/5);
    //rect bas
    rect(0, height-height/DIVISION_HEIGHT, width, height/DIVISION_HEIGHT);
    //rectMove
    rect(xrect, yrect+(decalageHome*2+decalageSkills*2+decalageWorldScore*2), width/DIVISION_WIDTH*lrect, height);
    //txt Home
    fill(ColorSquare);
    textSize(width/20);
    textAlign(LEFT, CENTER);
    text("Options", width/1.29f+echelleAnimation-(decalageHome+decalageSkills*2), height/4-decalageWorldScore*2);
    //skills
    text("Skills", width/1.29f+echelleAnimation-(decalageHome*2+decalageSkills), height/2.07f-decalageWorldScore*2);
    //best score
    //+podium
    rect(width/1.7f+width/54-(decalageHome*2+decalageSkills*2), height/1.31f-width/50+echelleAnimation, width/50, width/50);
    rect(width/1.7f-(decalageHome*2+decalageSkills*2), height/1.31f+echelleAnimation, width/18, width/50);
    text("Best: "+BestScore, 2*width/3-(decalageHome*2+decalageSkills*2), 3*height/4+echelleAnimation);
    fill(ColorTheme);
    //titre Square Jump
    textSize(width/15);
    text("Square \nJump", 2*width/80+tailleSquare-echelleAnimation-(decalageHome*2+decalageSkills*2+decalageWorldScore*2), height/5);
    //play
    textSize(max(width/12-echelleAnimation, 0));
    textAlign(CENTER, CENTER);
    text("Play", width/80+tailleSquare/2-(decalageHome*2+decalageSkills*2+decalageWorldScore*2), height/24+width/9);
    //txt Quit
    fill(ColorSquare);
    textSize(width/20);
    text("Quit", width/1.1f-echelleAnimation-(decalageHome*2+decalageSkills*2), height/1.11f+decalageWorldScore);
    xrect-=4;
  }

  public void dessinHome() {
    decH = width*0.72f-decalageHome;
    
    //visual "clic to get cristals !"
    if (publicityEnabled && difClicHour()==true) {
      textAlign(LEFT, BOTTOM);
      fill(ColorSquare);
      textSize(width/35);
      text("Clic on the ad to get 20 diams !", 3-decH, height-53);
    }
    
    //acceleration txt
    textAlign(RIGHT, TOP);
    fill(ColorSquare);
    textSize(width/24);
    text("Speed", width/1.75f+decH, height/11.42f);
    //Gravity txt
    text("Gravity", width/1.75f+decH, height/5);
    //accleration cases
    for (int f=1;f<=6;f+=1) {
      if (f<=Acceleration) fill(ColorTheme);
      else fill(ColorHorloge);
      rect(width/1.7f+decH+(f*width/30), height/9, width/50, height/20);
    }
    //gravity cases
    for (int f=1;f<=10;f+=1) {
      if (f<=Gravity) fill(ColorTheme);
      else fill(ColorHorloge);
      rect(width/1.7f+decH+(f*width/30), height/4.7f, width/50, height/20);
    }
    //color txt
    fill(ColorSquare);
    textAlign(RIGHT, CENTER);
    text("Color", width/2.5f+decH, height/1.85f);
    //Theme txt
    textAlign(LEFT, CENTER);
    text("Theme", width/2.3f+decH, height/2.1f);
    //Square txt
    text("Square", width/2.3f+decH, height/1.6f);
    //Dessin Square
    rect(width/1.15f+decH, height/1.5f, height/DIVISION_HEIGHT, height/DIVISION_HEIGHT);


    //colorTheme cases
    for (int c=0;c<6+ColorMAJAdding;c++) {
      fill(ColorDefautTheme[c]);
      if (ColorThemeSelected != c)
        rect(width/1.7f+decH+(c*width/28), height/2.17f, width/50, height/20);
      else
        rect(width/1.7f+decH+(c*width/28)-width/160, height/2.17f-height/96, width/50+width/80, height/20+height/48);
      if (c>5) {
        if (ColorBuy1==0 && c==6 || ColorBuy2==0 && c==7 || ColorBuy3==0 && c==8 || ColorBuy4==0 && c==9 || ColorBuy5==0 && c==10) {
          noFill();
          stroke(0 );
          strokeWeight(2);
          rect(width/1.7f+decH+(c*width/28), height/2.17f, width/50, height/20);
          line(width/1.7f+decH+(c*width/28) + width/100, height/2.17f+height/20, width/1.7f+decH+(c*width/28) + width/100, height/1.64f);
          noStroke();
        }
      }
    }
    //colorSquare cases
    for (int c=0;c<6+ColorMAJAdding;c++) {
      fill(ColorDefautSquare[c]);
      if (ColorSquareSelected != c)
        rect(width/1.7f+decH+(c*width/28), height/1.64f, width/50, height/20);
      else
        rect(width/1.7f+decH+(c*width/28)-width/160, height/1.64f-height/96, width/50+width/80, height/20+height/48);
      if (c>5) {
        if (ColorBuy1==0 && c==6 || ColorBuy2==0 && c==7 || ColorBuy3==0 && c==8 || ColorBuy4==0 && c==9 || ColorBuy5==0 && c==10) {
          noFill();
          stroke(0);
          strokeWeight(2);
          rect(width/1.7f+decH+(c*width/28), height/1.64f, width/50, height/20);
          noStroke();
        }
      }
    }

    //music on/off
    textAlign(CENTER, CENTER);
    textSize(width/30);
    fill(ColorSquare);
    if (MusicOn==1) {
      text("Music: ON", width/1.38f+decH, height/1.10f);
    }
    else {
      text("Music: OFF", width/1.38f+decH, height/1.10f);
    }
    //sound on/off
    if (SoundOn==1) {
      text("Sound: ON", width/1.10f+decH, height/1.10f);
    }
    else {
      text("Sound: OFF", width/1.10f+decH, height/1.10f);
    }

    if (iConfirmBoxColor==1)
      confirmBoxColor(ColorDefautTheme[6], ColorDefautSquare[6], 250);
    if (iConfirmBoxColor==2)
      confirmBoxColor(ColorDefautTheme[7], ColorDefautSquare[7], 350);
    if (iConfirmBoxColor==3)
      confirmBoxColor(ColorDefautTheme[8], ColorDefautSquare[8], 500);
    if (iConfirmBoxColor==4)
      confirmBoxColor(ColorDefautTheme[9], ColorDefautSquare[9], 700);
    if (iConfirmBoxColor==5)
      confirmBoxColor(ColorDefautTheme[10], ColorDefautSquare[10], 1000);
  }

  public void confirmBox(String nomConfirm, String resum, int prix) {
    fill(200, 150);
    rect(0, 0, width, height);
    fill(ColorSquare);
    rect(width/4.2f, height/7.5f, width-width/2.1f, height/4.5f);
    fill(255);
    textSize(width/35);
    textAlign(CENTER, CENTER);
    text("Are you sure you want to get \n\""+nomConfirm+"\"\n for "+prix +" \u25ca?", width/2, height/4.3f);
    //descr
    rect(width/4.2f, height/7.5f+height/4.5f, width-width/2.1f, height/3);
    fill(ColorSquare);
    text(resum, width/2, height/2.2f);
    //no
    textSize(width/20);
    text("No", width/2.9f, height/1.63f);
    //yes
    //rect(width/1.72,height/1.8,width/5.8,height/8.5);
    //fill(ColorTheme);
    text("Yes", width/1.5f, height/1.63f);
  }

  public void confirmBoxColor(int theColor, int theColor2, int prix) {
    fill(200, 150);
    rect(0, 0, width, height);
    fill(theColor);
    rect(width/4.2f, height/7.5f, width-width/2.1f, height/4.5f);
    //inverse de la couleur 1
    fill(map(red(theColor), 0, 255, 255, 0), map(green(theColor), 0, 255, 255, 0), map(blue(theColor), 0, 255, 255, 0));
    textSize(width/35);
    textAlign(CENTER, CENTER);
    text("Are you sure you want to get \n this color set for "+prix +" \u25ca?", width/2, height/4.3f);
    fill(theColor2);
    rect(width/4.2f, height/7.5f+height/4.5f, width-width/2.1f, height/3);
    //inverse de la cuoleur 2
    fill(map(red(theColor2), 0, 255, 255, 0), map(green(theColor2), 0, 255, 255, 0), map(blue(theColor2), 0, 255, 255, 0));
    if (pointMonaie <prix)
      text("You don't have enough money!", width/2, height/2.2f);
    //no
    textSize(width/20);
    text("No", width/2.9f, height/1.63f);
    //yes
    //rect(width/1.72,height/1.8,width/5.8,height/8.5);
    //fill(ColorTheme);
    if (pointMonaie >=prix)
      text("Yes", width/1.5f, height/1.63f);
  }

  public void dessinSkills() {
    decS = width*0.72f-decalageSkills;
    
    //visual "clic to get cristals !"
    if (publicityEnabled && difClicHour()==true) {
      textAlign(LEFT, BOTTOM);
      fill(ColorSquare);
      textSize(width/35);
      text("Clic on the ad to get 20 diams !", 3-decS, height-53);
    }
    //dessins rect SKILLS
    if (pointMonaie>=   JumpTime*PrixSkill[0]+PrixSkill[0]       && JumpTime<25) fill(ColorTheme); 
    else fill(175);
    rect(width/1.7f+decS, height/7.5f, width, height/9);
    if (pointMonaie>=   PrixSkill[1]*PointsMonaieUp+PrixSkill[1] && PointsMonaieUp<2) fill(ColorTheme); 
    else fill(175);
    rect(width/1.7f+decS, height/7.5f*2, width, height/9);
    if (pointMonaie>=   Luck*PrixSkill[2]+PrixSkill[2]           && Luck<4) fill(ColorTheme); 
    else fill(175);
    rect(width/1.7f+decS, height/7.5f*3, width, height/9);
    if (pointMonaie>=   GhostModeMax*PrixSkill[3]+PrixSkill[3]   && GhostModeMax<100) fill(ColorTheme); 
    else fill(175);
    rect(width/1.7f+decS, height/7.5f*4, width, height/9);
    if (pointMonaie>=   MalusTime*PrixSkill[4]+PrixSkill[4]      && MalusTime<10) fill(ColorTheme); 
    else fill(175);
    rect(width/1.7f+decS, height/7.5f*5, width, height/9);

    //txt chiffres avantages
    fill(ColorSquare);
    textAlign(RIGHT, CENTER);
    textSize(width/40);
    text("+"+ PApplet.parseFloat(2*JumpTime)/100+" s", width/1.8f+decS, height/5.7f); //jump time
    text("+"+ PointsMonaieUp, width/1.8f+decS, height/5.7f+height/7.5f); //points multiplier PointsMonaieUp
    text("+"+ PApplet.parseFloat(100*Luck)/10 +" %", width/1.8f+decS, height/5.7f+height/7.5f*2); //Luck
    text("+"+ PApplet.parseFloat(6*GhostModeMax)/100+" s", width/1.8f+decS, height/5.7f+height/7.5f*3); //GhostModeMax (revien a 2*3*x / 100)
    text("-"+ PApplet.parseFloat(6*MalusTime)/100+" s", width/1.8f+decS, height/5.7f+height/7.5f*4);//MalusTime
    //txt avantages
    fill(red(ColorSquare)-50, green(ColorSquare)-50, blue(ColorSquare)-50);
    textAlign(LEFT, CENTER);
    textSize(width/25);
    text("Jump time", width/1.68f+decS, height/5.7f); //jump time
    text("Diamonds adder", width/1.68f+decS, height/5.7f+height/7.5f); // PointsMonaieUp
    text("Luck", width/1.68f+decS, height/5.7f+height/7.5f*2); //Luck
    text("Ghost time", width/1.68f+decS, height/5.7f+height/7.5f*3); //GhostModeMax 
    text("Malus time", width/1.68f+decS, height/5.7f+height/7.5f*4);//MalusTime
    //prix skills
    textSize(width/35);
    if (JumpTime<25) text((JumpTime*PrixSkill[0]+PrixSkill[0])+"x\u25ca", width/1.1f+decS, height/5.7f); //JumpTime
    else text("MAX", width/1.1f+decS, height/5.7f);
    if (PointsMonaieUp<2) text((PrixSkill[1]*PointsMonaieUp+PrixSkill[1])+"x\u25ca", width/1.1f+decS, height/5.7f+height/7.5f); // PointsMonaieUp
    else text("MAX", width/1.1f+decS, height/5.7f+height/7.5f);
    if (Luck<4) text((Luck*PrixSkill[2]+PrixSkill[2])+"x\u25ca", width/1.1f+decS, height/5.7f+height/7.5f*2); //Luck
    else text("MAX", width/1.1f+decS, height/5.7f+height/7.5f*2);
    if (GhostModeMax<100) text((GhostModeMax*PrixSkill[3]+PrixSkill[3])+"x\u25ca", width/1.1f+decS, height/5.7f+height/7.5f*3); //GhostModeMax 
    else text("MAX", width/1.1f+decS, height/5.7f+height/7.5f*3);
    if (MalusTime<10) text((MalusTime*PrixSkill[4]+PrixSkill[4])+"x\u25ca", width/1.1f+decS, height/5.7f+height/7.5f*4);//MalusTime
    else text("MAX", width/1.1f+decS, height/5.7f+height/7.5f*4);

    if (iConfirmBox==1)
      confirmBox("Jump time", "Increase the time you can \nspend in the air.", (JumpTime*PrixSkill[0]+PrixSkill[0]));
    if (iConfirmBox==2)
      confirmBox("Diamonds adder", "Increase the amount of diamonds you \nget by finishing a level.", (PrixSkill[1]*PointsMonaieUp+PrixSkill[1]));
    if (iConfirmBox==3)
      confirmBox("Luck", "Increase the chances of having a\n bonus when playing.", Luck*PrixSkill[2]+PrixSkill[2]);
    if (iConfirmBox==4)
      confirmBox("Ghost time", "Increase the duration of the \n Ghost mode", GhostModeMax*PrixSkill[3]+PrixSkill[3]);
    if (iConfirmBox==5)
      confirmBox("Malus time", "Decrease the duration of the malus", MalusTime*PrixSkill[4]+PrixSkill[4]);
  }
  //DESSIN WORLDSCORE
  public void dessinWorldScore() {
    decWS = width*0.72f-decalageWorldScore;
    //tableau des scores:
    if (BestsScores[0] != 0) {
      int numAffichable = maxScoresAffich;
      int tailleBloc = height/24;

      fill(ColorSquare);
      textSize(width/26);
      textAlign(CENTER, CENTER);
      text("Bests scores ever", width/2+decWS*2, height/16);
      textAlign(RIGHT, CENTER);
      // text("Daily bests score",width-5+decWS*2,height/16);
      //block color\u00e9s
      fill(ColorTheme, 100);
      for (int i=0;i<numAffichable;i+=2) {
        //// rect(2, height/9.6f+i*tailleBloc*2-decWS, 2*width/5, tailleBloc*2);
        rect(width/3.33f, height/9.6f+i*tailleBloc*2-decWS, 2*width/5, tailleBloc*2);

        // rect(width - 2*width/5 -2 , height/9.6f+i*tailleBloc*2-decWS, 2*width/5, tailleBloc*2);
      }
      //indice
      fill(ColorSquare);
      textSize(width/40);
      for (int i=0;i<numAffichable;i++) {
        textAlign(LEFT, CENTER);
        ////  text(i+1, 4, height/9.6f+i*tailleBloc*2+tailleBloc-decWS);
        text(i+1, width/3.33f+2, height/9.6f+i*tailleBloc*2+tailleBloc-decWS);

        // textAlign(RIGHT, CENTER);
        // text(i+1, width-4, height/9.6f+i*tailleBloc*2+tailleBloc-decWS);
      }
      ///score
      for (int i=0;i<numAffichable;i++) {
        textAlign(CENTER, CENTER);
        ////  text(BestsScores[i], width/5, height/9.6f+i*tailleBloc*2+tailleBloc-decWS);
        text(BestsScores[i], width/2, height/9.6f+i*tailleBloc*2+tailleBloc-decWS);

        // text(dailyBestsScores[i], width - width/5, height/9.6f+i*tailleBloc*2+tailleBloc-decWS);
      }
      //pays
      for (int i=0;i<numAffichable;i++) {
        textAlign(RIGHT, CENTER);
        ////  text(BestsCountry[i], 2*width/5-5, height/9.6f+i*tailleBloc*2+tailleBloc-decWS);
        text(BestsCountry[i], width/3.33f+2*width/5, height/9.6f+i*tailleBloc*2+tailleBloc-decWS);

        //  textAlign(LEFT,CENTER);
        //  text(dailyBestsCountry[i], width - 2*width/5 +3, height/9.6f+i*tailleBloc*2+tailleBloc-decWS);
      }
      //selfScore PlaceJoueur
      textSize(width/32);
      textAlign(CENTER, CENTER);
      text("Your rank: "+PlaceJoueur, width/2, height/1.45f+decWS);
    }
    else {
      fill(ColorSquare);
      textSize(width/26);
      textAlign(CENTER, CENTER);
      text("Please check your internet connection.", width/2, height/2+decWS);
    }
  }

  public void touch() {
    decH = width*0.72f-decalageHome;
    decS = width*0.72f-decalageSkills;
    decWS = width*0.72f-decalageWorldScore;
    if (StateGame==0) {
      ////MENU PRINCIP////
      if (!home && !skills && !worldScore && decalageHome<=0) {

        //bouton play
        if (mouseX>width/80 && mouseX<width/80+width/4 && mouseY>height/24 && mouseY<height/24+width/4) {
          delayInitJeu=true;
        }
        //bouton home 
        if (mouseX>2*width/3 && mouseY>height/6 && mouseY<height/6+height/5) {
          home=true;
        }
        //bouton skills
        if (mouseX>2*width/3 && mouseY>height/2.5f && mouseY<height/2.5f+height/5) {
          skills=true;
        }
        //bouton worldScore
        if (mouseX>width/1.52f && mouseX<width/1.18f && mouseY>height/1.50f && mouseY<height/1.24f) {
          if (canLoadParse) {
            meilleursScoreParse();
            dailyScoresParse();
            canLoadParse = false;
          }

          worldScore = true;
        }
        //bouton achat diams
        if (mouseX>width/1.2f && mouseY<height/6.8f) {
        }
        //bouton quit
        if (mouseX>width/1.20f && mouseY>Divheight(1)) {
          if (numConnection==10) 
            alertDialogRate();
          else
            exit();
        }
      }

      //////HOME/////
      if (home && decalageHome>width/2) {
        if (iConfirmBoxColor<=0) {
          //escape to menu
          if (mouseX<width/3.6f && mouseY>height/6 && mouseY<height/6+height/5) {
            home=false;
          }
          //bouton achat diams
          if (mouseX>width/1.2f && mouseY<height/6.8f) {
          }
          //Accleration detec
          if (mouseY>height/9 && mouseY<height/9+height/20) {
            for (int d=1;d<=6;d++) {
              if (mouseX>width/1.7f+decH+(d*width/30)-width/160 && mouseX<width/1.7f+decH+(d*width/30)+width/50+width/160) {
                if (Acceleration != d) {
                  Acceleration = d;
                  playSoundPool(touc1);
                  edit("Acceleration", Acceleration);
                }
              }
            }
          }
          //Gravity detec
          if (mouseY>height/4.7f && mouseY<height/4.7f+height/20) {
            for (int d=1;d<=10;d++) {
              if (mouseX>width/1.7f+decH+(d*width/30)-width/160 && mouseX<width/1.7f+decH+(d*width/30)+width/50+width/160) {
                if (Gravity != d) {
                  Gravity = d;
                  playSoundPool(touc1);
                  edit("Gravity", Gravity);
                }
              }
            }
          }
          //colorTheme detec 
          if (mouseY>height/2.17f && mouseY<height/2.17f+height/20) {
            for (int c=0;c<6+ColorMAJAdding;c++) {
              if (mouseX>width/1.7f+decH+(c*width/28)-width/160 && mouseX<width/1.7f+decH+(c*width/28)+height/20+width/160) {
                if (ColorThemeSelected!=c) {
                  if (c<=5) {
                    ColorThemeSelected = c;
                    ColorTheme = ColorDefautTheme[c];
                    playSoundPool(touc1);
                    edit("ColorThemeSelected", ColorThemeSelected);
                  }
                  //ACHATS COULEURS
                  else {
                    if (c==6) {
                      if (ColorBuy1==0) {
                        iConfirmBoxColor=1;
                      }
                      else {
                        ColorThemeSelected = c;
                        ColorTheme = ColorDefautTheme[c];
                        playSoundPool(touc1);
                        edit("ColorThemeSelected", ColorThemeSelected);
                      }
                    }
                    if (c==7) {
                      if (ColorBuy2==0) {
                        iConfirmBoxColor=2;
                      }
                      else {
                        ColorThemeSelected = c;
                        ColorTheme = ColorDefautTheme[c];
                        playSoundPool(touc1);
                        edit("ColorThemeSelected", ColorThemeSelected);
                      }
                    }
                    if (c==8) {
                      if (ColorBuy3==0) {
                        iConfirmBoxColor=3;
                      }
                      else {
                        ColorThemeSelected = c;
                        ColorTheme = ColorDefautTheme[c];
                        playSoundPool(touc1);
                        edit("ColorThemeSelected", ColorThemeSelected);
                      }
                    }
                    if (c==9) {
                      if (ColorBuy4==0) {
                        iConfirmBoxColor=4;
                      }
                      else {
                        ColorThemeSelected = c;
                        ColorTheme = ColorDefautTheme[c];
                        playSoundPool(touc1);
                        edit("ColorThemeSelected", ColorThemeSelected);
                      }
                    }
                    if (c==10) {
                      if (ColorBuy5==0) {
                        iConfirmBoxColor=5;
                      }
                      else {
                        ColorThemeSelected = c;
                        ColorTheme = ColorDefautTheme[c];
                        playSoundPool(touc1);
                        edit("ColorThemeSelected", ColorThemeSelected);
                      }
                    }
                  }
                }
              }
            }
          }
          //colorSquare detec
          if (mouseY>height/1.64f && mouseY<height/1.64f+height/20) {
            for (int c=0;c<6+ColorMAJAdding;c++) {
              if (mouseX>width/1.7f+decH+(c*width/28)-width/160 && mouseX<width/1.7f+decH+(c*width/28)+height/20+width/160) {
                if (ColorSquareSelected!=c) {
                  if (c<=5) {
                    ColorSquareSelected = c;
                    ColorSquare = ColorDefautSquare[c];
                    playSoundPool(touc1);
                    edit("ColorSquareSelected", ColorSquareSelected);
                  }
                  //ACHATS COULEURS
                  else {
                    if (c==6) {
                      if (ColorBuy1==0) {
                        iConfirmBoxColor=1;
                      }
                      else {
                        ColorSquareSelected = c;
                        ColorSquare = ColorDefautSquare[c];
                        playSoundPool(touc1);
                        edit("ColorSquareSelected", ColorSquareSelected);
                      }
                    }
                    if (c==7) {
                      if (ColorBuy2==0) {
                        iConfirmBoxColor=2;
                      }
                      else {
                        ColorSquareSelected = c;
                        ColorSquare = ColorDefautSquare[c];
                        playSoundPool(touc1);
                        edit("ColorSquareSelected", ColorSquareSelected);
                      }
                    }
                    if (c==8) {
                      if (ColorBuy3==0) {
                        iConfirmBoxColor=3;
                      }
                      else {
                        ColorSquareSelected = c;
                        ColorSquare = ColorDefautSquare[c];
                        playSoundPool(touc1);
                        edit("ColorSquareSelected", ColorSquareSelected);
                      }
                    }
                    if (c==9) {
                      if (ColorBuy4==0) {
                        iConfirmBoxColor=4;
                      }
                      else {
                        ColorSquareSelected = c;
                        ColorSquare = ColorDefautSquare[c];
                        playSoundPool(touc1);
                        edit("ColorSquareSelected", ColorSquareSelected);
                      }
                    }
                    if (c==10) {
                      if (ColorBuy5==0) {
                        iConfirmBoxColor=5;
                      }
                      else {
                        ColorSquareSelected = c;
                        ColorSquare = ColorDefautSquare[c];
                        playSoundPool(touc1);
                        edit("ColorSquareSelected", ColorSquareSelected);
                      }
                    }
                  }
                }
              }
            }
          }
          //music on off
          if (mouseX>width/1.6f && mouseX<width/1.23f && mouseY>height/1.16f) {
            if (MusicOn==1) MusicOn=0;
            else MusicOn = 1;
            playSoundPool(touc1);
            edit("MusicOn", MusicOn);
          }
          //sound on/off
          if (mouseX>width/1.23f && mouseY>height/1.16f) {
            if (SoundOn==1) SoundOn=0;
            else SoundOn = 1;
            playSoundPool(touc1);
            edit("SoundOn", SoundOn);
          }
        }
        else {
          //iConfirmBoxColor> 0
          //no
          if (mouseX>width/4.2f && mouseX<width/2.28f && mouseY>height/1.8f && mouseY<height/1.45f) {
            iConfirmBoxColor = 0;
          }
          //yes
          if (mouseX>width/1.7f && mouseX<width/1.3f && mouseY>height/1.8f && mouseY<height/1.45f) {
            if (iConfirmBoxColor==1 && pointMonaie>=250) {
              ColorBuy1 = 1;
              edit("ColorBuy1", ColorBuy1);
              pointMonaie-=250;
              playSoundPool(levelUpSound);
              edit("pointMonaie", pointMonaie);
              iConfirmBoxColor=0;
            }
            if (iConfirmBoxColor==2 && pointMonaie>=350) {
              ColorBuy2 = 1;
              edit("ColorBuy2", ColorBuy2);
              pointMonaie-=350;
              playSoundPool(levelUpSound);
              edit("pointMonaie", pointMonaie);
              iConfirmBoxColor=0;
            }
            if (iConfirmBoxColor==3 && pointMonaie>=500) {
              ColorBuy3 = 1;
              edit("ColorBuy3", ColorBuy3);
              pointMonaie-=350;
              playSoundPool(levelUpSound);
              edit("pointMonaie", pointMonaie);
              iConfirmBoxColor=0;
            }
            if (iConfirmBoxColor==4 && pointMonaie>=700) {
              ColorBuy4 = 1;
              edit("ColorBuy4", ColorBuy4);
              pointMonaie-=250;
              playSoundPool(levelUpSound);
              edit("pointMonaie", pointMonaie);
              iConfirmBoxColor=0;
            }
            if (iConfirmBoxColor==5 && pointMonaie>=1000) {
              ColorBuy5 = 1;
              edit("ColorBuy5", ColorBuy5);
              pointMonaie-=300;
              playSoundPool(levelUpSound);
              edit("pointMonaie", pointMonaie);
              iConfirmBoxColor=0;
            }
          }
        }
      }
      //SKILLS
      if (skills && decalageSkills>width/2) {
        if (iConfirmBox<=0) {
          //escape to menu
          if (mouseX<width/3.6f && mouseY>height/2.5f && mouseY<height/2.5f+height/5) {
            skills=false;
          }
          //bouton achat diams
          if (mouseX>width/1.2f && mouseY<height/6.8f) {
          }
          //boutons skills
          //jump time
          if (mouseX>width/1.7f+decS && mouseY>height/7.5f && mouseY<height/7.5f+height/9&&JumpTime<25 &&pointMonaie>=JumpTime*PrixSkill[0]+PrixSkill[0]) {
            iConfirmBox=1;
            playSoundPool(touc2);
          }
          //point mult
          if (mouseX>width/1.7f+decS && mouseY>height/7.5f*2 && mouseY<height/7.5f*2+height/9&&PointsMonaieUp<2&&pointMonaie>=PrixSkill[1]*PointsMonaieUp+PrixSkill[1]) {
            iConfirmBox=2;
            playSoundPool(touc2);
          }
          //luck
          if (mouseX>width/1.7f+decS && mouseY>height/7.5f*3 && mouseY<height/7.5f*3+height/9&& Luck<4&&pointMonaie>=Luck*PrixSkill[2]+PrixSkill[2]) {
            iConfirmBox=3;
            playSoundPool(touc2);
          }
          //ghost time
          if (mouseX>width/1.7f+decS && mouseY>height/7.5f*4 && mouseY<height/7.5f*4+height/9&&GhostModeMax<100&&pointMonaie>=GhostModeMax*PrixSkill[3]+PrixSkill[3]) {
            iConfirmBox=4;
            playSoundPool(touc2);
          }
          //malus time
          if (mouseX>width/1.7f+decS && mouseY>height/7.5f*5 && mouseY<height/7.5f*5+height/9&&MalusTime<10&&pointMonaie>=MalusTime*PrixSkill[4]+PrixSkill[4]) {
            iConfirmBox=5;
            playSoundPool(touc2);
          }
        }
        else {
          //no
          if (mouseX>width/4.2f && mouseX<width/2.28f && mouseY>height/1.8f && mouseY<height/1.45f) {
            iConfirmBox = 0;
          }
          //yes
          if (mouseX>width/1.7f && mouseX<width/1.3f && mouseY>height/1.8f && mouseY<height/1.45f) {
            if (iConfirmBox==1) {
              pointMonaie-=(JumpTime*PrixSkill[0]+PrixSkill[0]);
              JumpTime+=1;
              edit("JumpTime", JumpTime);
            }
            if (iConfirmBox==2) {
              pointMonaie -= PrixSkill[1]*PointsMonaieUp+PrixSkill[1];
              PointsMonaieUp+=1;
              edit("PointsMonaieUp", PointsMonaieUp);
            }
            if (iConfirmBox==3) {
              pointMonaie -=Luck*PrixSkill[2]+PrixSkill[2];
              Luck+=1;
              edit("Luck", Luck);
            }
            if (iConfirmBox==4) {
              pointMonaie -=GhostModeMax*PrixSkill[3]+PrixSkill[3];
              GhostModeMax+=1;
              edit("GhostModeMax", GhostModeMax);
            }
            if (iConfirmBox==5) {
              pointMonaie -=MalusTime*PrixSkill[4]+PrixSkill[4];
              MalusTime+=1;
              edit("MalusTime", MalusTime);
            }
            playSoundPool(levelUpSound);
            edit("pointMonaie", pointMonaie);
            iConfirmBox=0;
          }
        }
      }
      //WORLD SCORE
      if (worldScore && decalageWorldScore>width/2) {
        //bouton worldScore EXIT
        if (mouseX>width/1.52f && mouseX<width/1.18f && mouseY>height/1.50f && mouseY<height/1.24f) {
          worldScore = false;
        }
      }
    }
  }
  public void exitToMainMenu() {
    skills=false;
    home = false;
    worldScore = false;
  }

  public void exitConfirmBox() {
    iConfirmBox = 0;
    iConfirmBoxColor=0;
  }

  public int StateMenu() {
    int r=0;
    if (!home && !skills && !worldScore) {
      r=0;
    }
    if (home || skills || worldScore) {
      r=1;
    }
    if (iConfirmBox!=0 || iConfirmBoxColor!=0) {
      r=2;
    }
    return r;
  }
}


public int[] generationSequence() {
  //retourne une sequence de bloc, aleatoirement
  int[] seq = new int[20]; //sequence de 20 . commencent avec 2 bloc a 1 obligatoirement. ne JAMAIS terminer par 0.terminer a 1 si poss
  //si 0<=x<=5 alors pas de bonus ni malus sur la case
  //si x = -(x+10), alors il PEUT y avoir un malus (par ex si x = -13, la case est a 3 et il peu y avoir un malus)
  //pareil pour bonus sauf que x = x+10. donc si par exemple x = 12, la case est a 2 et il peu y avoir un bonus.
  if (/*FirstSession && */PartieReussie==0) {
    //--------FACILE-------------//
    int rgs = rand(0, 5);
    switch (rgs) {
    case 0: 
    seq=st20(1, 1, 1, -11, 1, 1, 2, 2, 1, 1, 1, 1, 13, 3, 3, 3, 1, 1, 1, 1);//0
      break;
    case 1:
    seq=st20(1, 1, 1, 2, 2, 2, 2, 4, 2, 2, 2, 12, 2, 2, 1, 1, 1, 1, 1, 1);//1
      break;
    case 2: 
    seq=st20(1, 1, 1, 1, -11, 1, 1, 1, 0, 0, 1, 1, 1, 1, 3, 3, 2, 2, 1, 1);//2
      break;
    case 3: 
    seq=st20(1, 1, 1, 1, 12, 1, 1, 1, 2, 2, 4, 4, -11, 1, 1, 1, 0, 0, 1, 1);//3
      break;
    case 4: 
    seq=st20(1, 1, 1, 1, -12, 2, 2, 2, 3, 13, 3, 2, 12, 1, 1, 1, 1, 1, 1, 1);//4
      break;
    case 5:
    seq=st20(1, 1, 1, -11, 1, 1, 2, 2, 1, 1, 1, 1, 13, 3, 3, 3, 1, 1, 1, 1);//5
      break;
    }
  }
  if (/*FirstSession && */PartieReussie==1) {
    //------------------MOYEN------------------//
    int rgs = rand(0, 5);
    switch (rgs) {
    case 0: 
    seq=st20(1, 1, 2, 2, -12, 2, 2, 4, 2, 0, 4, 4, 4, 3, 2, 12, 2, 1, 1, 1);//0
      break;
    case 1: 
    seq=st20(1, 1, 2, -12, 2, 3, 3, 3, 1, 1, 1, 0, 0, 1, 11, 1, 1, 2, 1, 1);//1
      break;
    case 2:
    seq=st20(1, 1, -11, 1, 2, 3, 3, 1, 1, 1, 2, 13, 3, 1, 1, 2, 0, 1, 1, 1);//2
      break;
    case 3:
    seq=st20(1, 1, 1, 2, 2, 2, 4, 1, 1, -11, 3, 1, 1, 1, 0, 11, 0, 1, 1, 1);//3
      break;
    case 4: 
    seq=st20(1, 1, 2, -13, 2, 1, 1, 2, 2, 0, 3, 3, 3, 13, 1, 2, 1, 1, 1, 1);//4
      break;
    case 5: 
    seq=st20(1, 1, 0, 0, 1, 1, -11, 1, 1, 2, 2, 2, 1, 1, 3, 3, 3, 3, 1, 1);//5
      break;
    }
  }
  if (/*!FirstSession || */PartieReussie>=2) {
    //----------------DUR/NORMAL--------------------
    int rgs = rand(0, 15);
    switch (rgs) {
    case 0: 
      seq=st20(1, 1, 1, -12, 2, 0, 11, 2, 2, 0, 2, 2, 4, 2, 2, 11, 1, 2, 2, 1);///0
      break;
    case 1: 
      seq=st20(1, 1, 3, 3, 5, 5, 0, -12, 2, 11, 1, 0, 1, -10, 12, 2, 3, 5, 0, 1);///1
      break;
    case 2: 
      seq=st20(1, 1, 2, -12, 4, 5, 5, 3, 4, 4, 2, 1, 11, 1, 3, 1, 3, 1, 4, 1);///2
      break;
    case 3: 
      seq=st20(1, 1, 2, 2, 0, 0, -11, 1, 12, 2, 1, 2, 0, 1, 1, 0, 12, -11, 1, 1);///3
      break;
    case 4: 
      seq=st20(1, 1, 2, 3, 3, -13, 2, 3, 4, 2, 3, 0, 0, 4, 4, 1, 2, 13, 2, 1);///4
      break;
    case 5: 
      seq=st20(1, 1, 2, 3, -13, 3, 5, 2, -12, 4, 3, 11, 2, 3, 1, 1, 2, 1, 1, 1);///5
      break;
    case 6: 
      seq=st20(1, 1, -11, 3, 3, 5, 5, 1, 1, 13, 3, 1, 1, 2, 3, 13, -11, 2, 2, 1);///6
      break;
    case 7: 
      seq=st20(1, 1, 2, 2, 4, 4, 0, 0, 4, 3, 4, -13, 3, 4, 1, 1, 11, 2, 2, 1);///7
      break;
    case 8: 
      seq=st20(1, 1, 12, 3, 3, 3, 2, 3, 5, 5, -12, 1, 0, 12, 2, 2, 4, 3, 0, 1);///8
      break;
    case 9: 
      seq=st20(1, 1, 1, 3, 12, -11, 3, 4, 3, 12, 3, 0, 4, 2, -12, 4, 2, 3, 3, 1);///9
      break;
    case 10: 
      seq=st20(1, 11, 0, 1, 12, 2, 4, 2, -11, 1, 3, 2, 1, 4, -13, 3, 0, 0, 3, 1);///10
      break;
    case 11: 
      seq=st20(1, 1, 1, 0, 0, 12, 2, 0, 0, 4, 4, 3, 12, 0, 0, 3, 1, -12, 3, 1);///11
      break;
    case 12: 
      seq=st20(1, 1, 2, -12, 4, 4, 4, 0, 0, 4, 4, 3, 2, 12, 2, 4, 4, 2, 1, 1);///12
      break;
    case 13: 
      seq=st20(1, 1, 10, 0, 1, 1, 0, 0, 2, 2, -12, 0, 1, 1, 2, 2, 3, 13, 1, 1);///13
      break;
    case 14: 
      seq=st20(1, 1, 3, 3, 4, 5, 0, 1, -11, 12, 2, 2, 0, 2, 2, 3, 13, 1, 1, 1);///14
      break;
    case 15: 
      seq=st20(1, 1, -11, 3, 13, 3, 4, 1, 0, 3, 3, 0, 0, 3, 13, 3, -12, 2, 1, 1);///15

      break;
    }
  }
  return seq;
}

public int[] st20(int x0, int x1, int x2, int x3, int x4, int x5, int x6, int x7, int x8, int x9, int x10, int x11, int x12, int x13, int x14, int x15, int x16, int x17, int x18, int x19) {
  int[] tabl = {
    x0, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12, x13, x14, x15, x16, x17, x18, x19
  };
  return tabl;
}

public void dessinTerrain() {
  //rect de la fin
  Bloc.dessin();
  Square.dessin();
  AnimBonusMalus.dessin();
  
  if (AvancementX>NUMBER_OF_SEGMENTS*(LARGEUR_BLOC)) {
    PartieReussie += 1;
    if(PartieReussie==2) FirstSession = false;
    TerrainGeneration = terrainGeneration(NUMBER_OF_SEGMENTS);
    Bloc = new bloc(TerrainGeneration);
    pointMonaie += 1+PointsMonaieUp;
    if(PartieReussie > 5)
      pointMonaie += 1;
     if(PartieReussie > 10)
      pointMonaie += 1;
    edit("pointMonaie", pointMonaie);
    if (Speed<18) {
      for (int i = 1;i<=20;i++) {
        if (PartieReussie==(7-Acceleration)*i) {
          Speed+=1;
        }
      }
    }
    
    AvancementX = 0;
    Square = new square(Square.posy(),Square.getGhostModeTimer());
    MessageDelay = new messageDelay(messageBien(),100,true);
    if(ChallengeAccepted){
      if(NumChallenge == 8 && PartieReussie==ChallStats[8][0]-1){
        challengeWin(ChallStats[8][1]);
      }
      if(NumChallenge == 9 && PartieReussie==ChallStats[9][0]-1){
        challengeWin(ChallStats[9][1]);
      }
    }
    //flating score: max screen = 1725 px
    Score = PartieReussie*(1725+PartieReussie*9) - jumpDoneInLevel*5  + Acceleration*125;
    jumpDoneInLevel=0;
  }
}


public int rand(int mini, int maxi) {
  float f=0;
  if (mini!=maxi) {
    f = random(mini-0.1f, maxi+1);
    f= min(maxi, f);
    f=max(mini, f);
  }
  if (mini==maxi) {
    f=mini;
  }
  return PApplet.parseInt(f);
}

/*GENERATION DU TERRAIN
 tg[0][x] = bas
 tg[1][x] = haut
 */

public int[] terrainGeneration(int numSegments) {
  int[] tg = new int[numSegments];

  int lastMove = 0;
  int r = 0;
  int randx = 0;

  for (int j=0;j<10;j++) {
    tg[j] = 1;
  }

  for (int i=10;i<numSegments;i+=20) {
    int[] tgs = generationSequence();
    for (int d=i;d<i+20;d++) {
      tg[d] = tgs[d-i];
    }
  }
  //algo bonus malus (chance)
  for(int bm = 0; bm<numSegments;bm++){
    //malus: 2 sur 5 chance
    if(tg[bm] < -1){
      if(rand(0,4)>1){
        tg[bm] = abs(tg[bm])-10; //annulation
      }
    }
    //bonus: 1+lucky chance sur 10
    if(tg[bm] >=10){
      if(rand(0,12)>1+Luck){ // de base 10 mais facile avec...
        tg[bm] -= 10; // annulation
      }
    }
  }
  
  return tg;
}


}
