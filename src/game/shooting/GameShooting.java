package game.shooting;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class GameShooting extends JFrame{
    
    private Input input;
    private JLabel   drawpane= new JLabel(),Airplane,startback,startpane=new JLabel();
    private JPanel   contentpane,control;
    private int MonsterX=900,MonsterY=0,AirplaneX=0,AirplaneY=200,score=0;
    private monster m;
    private bullet b=new bullet();
    ArrayList<monster> M=new ArrayList<monster>();
    ArrayList<bullet> B=new ArrayList<bullet>();
    JButton OutButton=new JButton(),start=new JButton("start"),stop=new JButton("stop");
    private int stackcount=0,move=1,countlevel=0,levelsum=1;
    private ArrayList<String> FullWords=new ArrayList<String>(),AnswerWords=new ArrayList<String>(),TranslateWords=new ArrayList<String>(),BlankWords=new ArrayList<String>();
    private JTextField scoreText=new JTextField("0",5),stack=new JTextField("0",10),level=new JTextField("1",10);
    private JToggleButton []   tb;
    private ButtonGroup        bgroup;
    private boolean mode=false,is_no=false;
    private MySoundEffect      hitSound, backgroundSound,attacksound;
    private String str="";
    private Object data[][];

    public GameShooting()
    {
        setTitle("Game Shooting");
        setBounds(50, 50, 900, 500);
        setResizable(true);
	setVisible(true);
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        contentpane = (JPanel)getContentPane(); 
	contentpane.setLayout( new FlowLayout() );
        addcomponent();
    }
    
    public void addcomponent()
    {
        MyImageIcon airplaneImg = new MyImageIcon("Spacecraft.png").resize(100, 100);
        MyImageIcon startbackground = new MyImageIcon("start.jpg").resize(600, 500);
        MyImageIcon dummybackground = new MyImageIcon("airplane.png").resize(200, 200);
        
        drawpane.setIcon(startbackground);
        drawpane.setLayout(null);
        
        Airplane = new JLabel(airplaneImg);
        Airplane.setBounds(AirplaneX,AirplaneY, 100, 100);
        addKeyBinding(Airplane,KeyEvent.VK_W,"top",(evt)->up());
        addKeyBinding(Airplane,KeyEvent.VK_S,"down",(evt)->down());
        addKeyBinding(Airplane,KeyEvent.VK_D,"shoot",(evt)->shoot());
        drawpane.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) 
                {
                    shoot();
                } 
                else if (e.getButton() == MouseEvent.BUTTON3) 
                {
                    input=new Input(stackcount,FullWords,BlankWords,TranslateWords,AnswerWords);
                    stackcount=0;
                    FullWords.clear();
                    AnswerWords.clear();
                    TranslateWords.clear();
                    BlankWords.clear();
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
        });
        drawpane.add(Airplane);
       
        OutButton = new JButton("StackOut");
        control=new JPanel();
        addaction(OutButton,start);
        control.add(OutButton);
        control.add(new JLabel("    stack   "));
        stack.setPreferredSize( new Dimension( 0, 30 ) );
        control.add(stack);
        control.add(new JLabel("    level   "));
        level.setPreferredSize( new Dimension( 0, 30 ) );
        control.add(level);
        tb = new JToggleButton[2];
        bgroup = new ButtonGroup();      
        tb[0] = new JRadioButton("Normal");   tb[0].setName("Normal");
        tb[1] = new JRadioButton("Stack");  tb[1].setName("Stack"); 
	tb[0].setSelected(true);
        for (int i=0; i < 2; i++)
        {
            bgroup.add( tb[i] );
        }
        additem(tb);
        control.add(tb[0]);
        control.add(tb[1]);
        control.add(new JLabel("    score   "));
        scoreText.setPreferredSize( new Dimension( 0, 30 ) );
        control.add(scoreText);
        contentpane.add(control,BorderLayout.NORTH);
      
        contentpane.add(drawpane,BorderLayout.WEST);
        JPanel startbar=new JPanel();
        startbar.setLayout(new FlowLayout());
        startback=new JLabel();
        startback.setLayout(null);
  
        startbar.add(startback);
        startbar.add(start);
        stop.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                new stopbutton();
            }
        
        });
        startbar.add(stop);
        //startbar.add(scoretable);
        contentpane.add(startbar,BorderLayout.NORTH);
        hitSound   = new MySoundEffect("hit.wav");
        attacksound= new MySoundEffect("attack.wav");
	backgroundSound = new MySoundEffect("background.wav"); backgroundSound.playLoop();
        stack.setEditable(false);
        level.setEditable(false);
        scoreText.setEditable(false);
        repaint();
        validate();
    }

    private void up() {
        AirplaneY-=10;
        Airplane.setBounds(AirplaneX,AirplaneY, 100, 100);
    }

    private void down() {
        AirplaneY+=10;
        Airplane.setBounds(AirplaneX,AirplaneY, 100, 100);
    }

    private void shoot() {
        attacksound.playOnce();
        MyImageIcon bulletImg = new MyImageIcon("star.png").resize(20, 20);
        b=new bullet(bulletImg,AirplaneX+100,AirplaneY+20);
        b.setBounds(AirplaneX+100,AirplaneY+20, 20, 20);
        drawpane.add(b);
        B.add(b);
    }

   
    class monster extends JLabel implements ActionListener{
        private Timer t=new Timer(5,this);
        private int x=10,y=10;
        public String w1,w2,w3,w4;
        monster(MyImageIcon mi,int x1,int y1,String str1,String str2,String str3,String str4)
        {
            super(mi);
            x=x1;
            y=y1;
            w1=str1;
            w2=str2;
            w3=str3;
            w4=str4;
            t.start();
        }
        public void deletemonster(){
            x=-1200; 
            countlevel++;
            if(countlevel==20)
            {
                countlevel=0;
                new playagain();
            }
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            x-=move;
            if(x==0)
            {
                countlevel++;
            }
            if(countlevel==20)
            {
                //levelsum++;
                level.setText(""+levelsum);
                countlevel=0;
                if(stackcount>=1)
                {
                    
                    new Input(stackcount,FullWords,BlankWords,TranslateWords,AnswerWords);
                    FullWords.clear();
                    AnswerWords.clear();
                    TranslateWords.clear();
                    BlankWords.clear();
                    stackcount=0;
                    new playagain();
                }
            }
            setBounds(x, y, 100, 100);
            collision(M,B);
        }
    }
    
   public void additem(JToggleButton[] JT)
   {
            JT[0].addItemListener(new ItemListener(){
                @Override
                public void itemStateChanged(ItemEvent e) {
                     mode=false;
                }
            });
            JT[1].addItemListener(new ItemListener(){
                @Override
                public void itemStateChanged(ItemEvent e) {
                     mode=true;
                }
            });
    } 
    class bullet extends JLabel implements ActionListener{
        
        private Timer t=new Timer(5,this);
        
        private int x,y;
        
        bullet(){}
        
        bullet(MyImageIcon mi,int x1,int y1)
        {
            super(mi);
            x=x1;
            y=y1;
            t.start();
        }
        public void deletebullet(){x=1200;}
        @Override
        public void actionPerformed(ActionEvent e) {
            x+=move;
            setBounds(x, y, 50, 50);
            if(x>=500)
            {
                x=100000;
            }
        }
    }
    
    public void MonsterThread(JLabel JL,String str)
    {
        Thread monsterthread = new Thread() {
            @Override
            public void run()
            {
                MyImageIcon monsterImg = new MyImageIcon("meteor3.png").resize(100, 100);
                final String FILE_ENCODE = "UTF-8";
                String data = null;
		BufferedReader dataIns = null;
		String file = str;
                
                try{
                    dataIns = new BufferedReader(new InputStreamReader(new FileInputStream(file), FILE_ENCODE));
                    int i=100;
                    while((data = dataIns.readLine()) != null)
                    {
                        Random rand = new Random();
                        int n = rand.nextInt(300)+100;
                        String buf[]=data.split(",");
                        m=new monster(monsterImg,MonsterX+i,n,buf[0].trim(),buf[1].trim(),buf[2].trim(),buf[3].trim());
                        m.setBounds(MonsterX+200,n, 100, 100);
                        JL.add(m);
                        M.add(m);
                        i+=200;
                    }
                }catch(Exception ex){}
            }
	}; 
        monsterthread.start();
    }
    synchronized public void collision(ArrayList<monster> M,ArrayList<bullet> B)
    {
        for(int i=0;i<M.size();i++)
        {
                for(int j=0;j<B.size();j++)
                {
                    if ( M.get(i).getBounds().intersects(B.get(j).getBounds()))
                    {
                        hitSound.playOnce();
                        FullWords.add(M.get(i).w1);
                        BlankWords.add(M.get(i).w2);
                        TranslateWords.add(M.get(i).w3);
                        AnswerWords.add(M.get(i).w4);
                        B.get(j).deletebullet();
                        M.get(i).deletemonster();
                        B.remove(j);
                        M.remove(i);
                        stackcount++;
                        if(mode==false)
                        {
                            input=new Input(stackcount,FullWords,BlankWords,TranslateWords,AnswerWords);
                            stackcount=0;
                            FullWords.clear();
                            AnswerWords.clear();
                            TranslateWords.clear();
                            BlankWords.clear();
                            move=0;
                        }
                        else if(stackcount==2)
                        {
                            input=new Input(stackcount,FullWords,BlankWords,TranslateWords,AnswerWords);
                            stackcount=0;
                            FullWords.clear();
                            AnswerWords.clear();
                            TranslateWords.clear();
                            BlankWords.clear();
                            move=0;
                        }
                    } 
                }
        }
    }
    
    public void addaction(JButton JB,JButton JB2)
    {
        JB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                input=new Input(stackcount,FullWords,BlankWords,TranslateWords,AnswerWords);
                stackcount=0;
                FullWords.clear();
                AnswerWords.clear();
                TranslateWords.clear();
                BlankWords.clear();
            }
        });
        JB2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                startbutton sb=new startbutton();
            }
        });
    }
    
    class Input extends JFrame
    {
        private JLabel JL1[]=new JLabel[30],JL2[]=new JLabel[30];
        private JTextField JT[]=new JTextField[30];
        private JPanel JP=new JPanel(),JP2=new JPanel();
        private JButton JB=new JButton("Submit");
        private ArrayList<String> str=new ArrayList<String>(),str2=new ArrayList<String>();
        Input(){}
        public Input(int n,ArrayList<String> s1,ArrayList<String> s2,ArrayList<String> s3,ArrayList<String> s4)
        {
            move=0;
            setTitle("Input");
            setBounds(50, 50, 400, 130+(40*n));
            setResizable(false);
            setVisible(true);
            setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
            JP.setLayout(new FlowLayout());
            JP2.setLayout(new BoxLayout(JP2, BoxLayout.PAGE_AXIS));
            for(int i=0;i<n;i++)
            {
                str.add(s4.get(i));
                str2.add(s1.get(i));
                JL1[i]=new JLabel(""+(i+1)+") "+s2.get(i)+"  "+s3.get(i));
                stack.setText(s2.get(i));
                JT[i]=new JTextField(10);
                JP2.add(JL1[i],BorderLayout.WEST);
                JP2.add(JT[i],BorderLayout.WEST);
                JP.add(JP2);
            }
            JB.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    for(int i=0;i<n;i++)
                    {
                        String s1=str.get(i).toLowerCase(),s2=JT[i].getText().trim().toLowerCase(),s3=str2.get(i).toLowerCase();
                        if(s1.equals(s2) || s3.equals(s2))
                        {
                            score++;
                            scoreText.setText(""+score);
                        }
                    }
                    move=1;
                    dispose();
                }
            
            });
            JP.add(JB);
            add(JP);
            
        }
    }
    
    class startbutton extends JFrame{
        private JComboBox      combo;
        private JPanel select=new JPanel();
        private JRadioButton JR[]=new JRadioButton[5];
        private JButton JB=new JButton("Exit");
        private ButtonGroup bg=new ButtonGroup();
        MyImageIcon backgroundImg = new MyImageIcon("bg.png").resize(600, 500);
        private String s="living";
        public startbutton()
        {
            setTitle("Set Detail");
            setBounds(50, 50, 200, 240);
            setResizable(false);
            setVisible(true);
            String[] type = { "living", "social", "sciene","life","mix"};
            combo = new JComboBox(type);
            combo.addItemListener(new ItemListener(){
                @Override
                public void itemStateChanged(ItemEvent e) {
                   if(e.getSource()==combo)
                   {
                        if(combo.getSelectedItem().equals("living"))
                        {
                              s="living";  
                        }
                        else if(combo.getSelectedItem().equals("social")){
                              s="social";  
                        }
                        else if(combo.getSelectedItem().equals("sciene")){
                              s="sciene";  
                        }
                        else if(combo.getSelectedItem().equals("life")){
                              s="life";  
                        }
                        else{
                              s="mix";  
                        }
                    }
                }
            });
            select.add(combo);
            select.add(new JLabel(" "));
            
            for(int i=0;i<5;i++)
            {
                JR[i]=new JRadioButton("level"+(i+1));
                JR[i].setName(""+(i+1));
                bg.add(JR[i]);
                select.add(JR[i]);
            }
            JB.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    for(int i=0;i<5;i++)
                    {
                        if(JR[i].isSelected())
                        {
                            str+=s;
                            str+=JR[i].getName();
                        }
                    }
                    dispose();
                    MonsterThread(drawpane,str+".txt");
                    start.setEnabled(false);
                    drawpane.setIcon(backgroundImg);
                    //System.out.println(str);
                }
            
            });
            select.add(new JLabel(" "));
            select.add(JB);
            select.setLayout(new BoxLayout(select, BoxLayout.PAGE_AXIS));
            add(select);
        }
    }
    
    class stopbutton extends JFrame
    {
        JPanel body=new JPanel(),username=new JPanel(),unicode=new JPanel();
        JLabel scoreend;
        JButton save;
        JTextField t1=new JTextField(10),t2=new JTextField(10);
        public stopbutton()
        {
            setTitle("Update score");
            setBounds(100, 100, 320, 150);
            setResizable(false);
            setVisible(true);
            contentpane = (JPanel)getContentPane(); // ใส่ content เข้าไปใน JFrame
            contentpane.setLayout(new BoxLayout(contentpane, BoxLayout.PAGE_AXIS));
            scoreend=new JLabel("   Your score = "+score+"     ");
            contentpane.add(scoreend);
            username.add(new JLabel("Your ID :            "));
            username.add(t1);
            unicode.add(new JLabel("Your unicode : "));
            unicode.add(t2);
            save=new JButton("save");
            save.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    move=0;
                    updatescore(t1.getText(),t2.getText(),score);
                    if(is_no==false)
                    {
                         System.exit(0);
                    }
                    else{
                        move=1;
                        dispose();
                    }
                }
            
            });
            contentpane.add(username);
            contentpane.add(unicode);
            contentpane.add(save);
        }
    }
    
    public void addKeyBinding(JComponent comp,int KeyCode,String id,ActionListener actionlistener)
    {
        InputMap im=comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap ap=comp.getActionMap();
        
        im.put(KeyStroke.getKeyStroke(KeyCode, 0, false),id);
        ap.put(id , new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                actionlistener.actionPerformed(e);
            }
        });
        
    }
    
    class playagain extends JFrame
    {
        JPanel again=new JPanel();
        JLabel JL=new JLabel("Do you want to play again ? ");
        JButton yes,no;
        MyImageIcon startbackground = new MyImageIcon("start.jpg").resize(600, 500);
        public playagain()
        {
            setTitle("Playagain");
            setBounds(100, 100, 200, 100);
            setResizable(false);
            setVisible(true);
            again.setLayout(new FlowLayout());
            again.add(JL);
            yes=new JButton("Yes");
           
            yes.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    is_no=true;
                    new stopbutton();
                    start.setEnabled(true);
                    dispose();
                    drawpane.setIcon(startbackground);
                    scoreText.setText("0");
                    score=0;
                    stackcount=0;
                    stack.setText("0"); 
                }
            });
            no=new JButton("No");
            no.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    is_no=false;
                    new stopbutton();
                    dispose();
                }
                
            });
            again.add(yes);
            again.add(no);
            add(again);
        }
    }
    
    public void updatescore(String s1,String s2,int score)
    {
	String file = str+"score.txt";
     
	try 
        {
	  FileWriter write=new FileWriter(file,true);
          BufferedWriter bufferWritter = new BufferedWriter(write);
          bufferWritter.write(s1+","+s2+","+score+"\n");
          bufferWritter.close();
	}
	catch(Exception e) {
	  System.err.println("An error occurs. End program.");
	  System.exit(-1);
	}
    }
    
    class scoreupdate implements Comparable<scoreupdate> {
        String name,unicode;
        int score;
        scoreupdate(){}
        public scoreupdate(String s1,String s2,int n)
        {
            name=s1;
            unicode=s2;
            score= n;
        }
        @Override
        public int compareTo(scoreupdate o) {
           return this.score-o.score;
        }
        
    }
        
    public static void main(String[] args) {
       new GameShooting();
    }
    
    class MyImageIcon extends ImageIcon
{
    public MyImageIcon(String fname)  { super(fname); }
    public MyImageIcon(Image image)   { super(image); }

    public MyImageIcon resize(int width, int height)
    {
	Image oldimg = this.getImage();
	Image newimg = oldimg.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new MyImageIcon(newimg);
    }
}
    
    
    class MySoundEffect
{
    private java.applet.AudioClip audio;

    public MySoundEffect(String filename)
    {
	try
	{
            java.io.File file = new java.io.File(filename);
            audio = java.applet.Applet.newAudioClip(file.toURL());
	}
	catch (Exception e) { e.printStackTrace(); }
    }
    public void playOnce()   { audio.play(); }
    public void playLoop()   { audio.loop(); }
    public void stop()       { audio.stop(); }
}
    
}
