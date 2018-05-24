package ball;

import java.awt.BasicStroke; 
import java.awt.Color;  
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;



public class play extends JFrame{
	private static final long serialVersionUID = 1L;
	char[] level;
	int x,y,xo,yo,gx=-1,gy=-1,move=0,dir,focus=-1,nextFocus;
	BufferedReader br;
	State state;
	int win=0;
	ArrayList<Level> levelList = new ArrayList<Level>();
	ArrayList<Step> stepRecord = new ArrayList<Step>();
	int page=0;
	int step;
	create c;
	Level newLevel;
	ImageIcon icon = new ImageIcon(getClass().getResource("icon.png"));;
	int msg=0;
	int w;
	String k="標楷體";
	String h="微軟正黑體";
	Queue<Way> ball=new LinkedList<>();
	ArrayList<int[]> way=new ArrayList<>();
	int replay=0;
	his[] history={};
	int[] ba,goal;
	int[] m={0,1,0,-1};
	boolean ing=false;
	int turn;
	int lastStart,lastGoal;
	int people=2;
	boolean mp=false;
	char[][] levelColor=new char[][]{"0202002020300000000400000000003000000004000000000000000000003000000004000000000030000000040101001010".toCharArray(),
									"0202020202030000000004000000000003000000000400000000000300000000040000000000030000000004000000000003000000000401010101010".toCharArray()};
	Color[] color=new Color[]{Color.blue,new Color(0x009100),new Color(0xFF8000),new Color(0x6F00D2),new Color(0x0000E3)};
	String[] stage={"0101001010100000000100000000001000000001000033000000003300001000000001000000000010000000010101001010",
					"0101010101010000000001000000000001000000000100000000000100003000010000000000010000000001000000000001000000000101010101010"};
	Level[] playLevel=new Level[]{new Level("第一關", 10, 10, stage[0]),
								new Level("第二關", 11, 11, stage[1])};
	char[][] stageColor=new char[][]{levelColor[0].clone(),levelColor[1].clone()};
	char[] words=new char[]{'0','綠','橘','紫','藍'};
	
	
	public static void main(String[] args) {
		play p = new play(); 
		p.set();
		p.run();
	}
	char ballColor;

	public void set() {
		setSize(640, 640);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setLocationRelativeTo(null);
		setFocusable(true);
		setTitle("迷蹤球");
		setIconImage(icon.getImage());
		if(System.getProperty("os.name").equals("Mac OS X"))
		{
			com.apple.eawt.Application application = com.apple.eawt.Application.getApplication();
			application.setDockIconImage(icon.getImage());
			k="Kaiti TC";
			h="Heiti TC";
		}
		
		if(!new File("Level").exists())createFile();
		
		state=State.menu;
		
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				requestFocus();

				if(gx!=-1||replay!=0)return;
				if(mp&&stageColor[page][focus]!=(int)(step/2)%(people+2)+49)return;
				if(key==KeyEvent.VK_UP)move=-x;
				if(key==KeyEvent.VK_RIGHT)move=1;
				if(key==KeyEvent.VK_DOWN)move=x;
				if(key==KeyEvent.VK_LEFT)move=-1;
			}

			public void keyReleased(KeyEvent e) {
				move=0;
			}
		});
		
		addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				int mx=e.getX()*640/getWidth();
				int my=e.getY()*640/getHeight();//System.out.println(mx+" "+my);
				if(state==State.menu)
				{
					if(c!=null)return;
					if(mx>180&&mx<460&&my>300&&my<360)
					{
						state=State.select;
						mp=false;
						read();
						readRecord();
					}
					if(mx>180&&mx<460&&my>380&&my<440)
					{
						state=State.select;
						mp=true;
						page=0;
						levelList.clear();
						levelList.add(playLevel[0]);
						levelList.add(playLevel[1]);
					}
					if(mx>180&&mx<460&&my>460&&my<520)c=new create();
					if(mx>180&&mx<460&&my>540&&my<600)state=State.info;
				}
				
				if(state==State.select)
				{
					if(mx>270&&mx<370&&my>590&&my<630)state=State.menu;
					if(levelList.size()==0)return;
					if(mx>30&&mx<120&&my>540&&my<580&&page!=0){page--;if(mp)changePeople();}
					if(mx>520&&mx<610&&my>540&&my<580&&page!=levelList.size()-1){page++;if(mp)changePeople();}
					if(mx>250&&mx<390&&my>530&&my<580)
					{
						state=State.game;
						x=levelList.get(page).x;
						y=levelList.get(page).y;
						level=levelList.get(page).level;
						step=0;
						w=50;
						if(x>10||y>10)
						{
							if(x>y)w=500/x;
							else w=500/y;
						}
						xo=(640-w*x)/2;
						yo=(640-w*y)/2;
						win=0;
					}
					if(mp)
					{
						
						if(mx>15&&mx<95&&my>140&&my<190){people=0;changePeople();}
						if(mx>15&&mx<950&&my>220&&my<270){people=1;changePeople();}
						if(mx>15&&mx<95&&my>300&&my<350){people=2;changePeople();}
						
						
					}
					
				}
				
				if(state==State.create)
				{
					if(mx>xo&&mx<640-xo&&my>yo&&my<640-yo)
					{
						int f=(int)((mx-xo)/w)+(int)((my-yo)/w)*x;
						if(e.getButton()==MouseEvent.BUTTON3)level[f]++;
						else level[f]--;
						if(level[f]=='4')level[f]='0';
						if(level[f]=='0'-1)level[f]='3';
					}
					if(mx>20&&mx<110&&my>590&&my<630)state=State.menu;
					if(mx>530&&mx<620&&my>590&&my<630)
					{
						save(newLevel);
						msg=50;
					}
				}
				
				if(state==State.info)
				{
					if(mx>20&&mx<110&&my>590&&my<630)state=State.menu;
				}
				
				if(state==State.game)
				{
					if(mx>580&&mx<630&&my>400&&my<480&&!mp)//解題
						{
							if(replay==0)
							{	
								if(ing)
								{
									for(int i:ba)level[i]++;
									ing=false;
								}
								else if(gx==-1)solve();
							}
						}
					
					if(ing)return;
					
					if(mx>10&&mx<60&&my>400&&my<480&&focus!=lastStart&&step!=0)//復原
					{
						if(gx==-1)
						{
							level[lastGoal]--;
							if(mp)stageColor[page][lastGoal]='0';
						}
						gx=-1;
						replay=0;
						level[lastStart]++;
						if(mp)stageColor[page][lastStart]=ballColor;
						focus=lastStart;
						step--;
					}
				
					if(mx>xo&&mx<640-xo&&my>yo&&my<640-yo&&replay==0)//遊戲
					{
						int f=(int)((mx-xo)/w)+(int)((my-yo)/w)*x;
						if(level[f]=='1'||level[f]=='4')focus=f;
						else
						{
							if(gx!=-1||focus==-1)return;
							if(mp&&stageColor[page][focus]!=(int)(step/2)%(people+2)+49)return;
							int a=xo+(focus%x)*w+w/2;
							int b=yo+(int)(focus/x)*w+w/2;
							if(mx-a>Math.abs(my-b))move=1;
							else if(mx-a<-Math.abs(my-b))move=-1;
							else if(my-b>0)move=x;
							else move=-x;
						}
						
					}
					
					if(mx>20&&mx<110&&my>590&&my<630)//返回
					{
						if(!mp)
						{
							read();
							readRecord();
						}
						else 
						{
							level=playLevel[page].level=stage[page].toCharArray();
							stageColor[page]=levelColor[page].clone();
							changePeople();
						}
						
						state=State.select;
						focus=-1;
						gx=-1;
						replay=0;
					}
					
					if(mx>530&&mx<620&&my>590&&my<630)//重新
					{
						if(!mp)
						{
							read();
							readRecord();
							level=levelList.get(page).level;
						}
						else 
						{
							level=playLevel[page].level=stage[page].toCharArray();
							stageColor[page]=levelColor[page].clone();
							changePeople();
						}
						
						step=0;
						focus=-1;
						win=0;
						gx=-1;
						replay=0;
					}
					
				}
			}

			public void mousePressed(MouseEvent e) {}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {}

			public void mouseExited(MouseEvent e) {}
		});
	}
	
	public void changePeople()
	{
		int add[]={10,19,11,21};
		for(int i=0;i<2;i++)
		{
			char a,b;
			if(i<people)
			{
				a='1';
				b=(char)(i+51);
			}
			else 
			{
				a='0';
				b='0';
			}
			if(page==0)
			{
				levelList.get(0).level[add[page*2+i]]=a;
				levelList.get(0).level[add[page*2+i]+20]=a;
				levelList.get(0).level[add[page*2+i]+50]=a;
				levelList.get(0).level[add[page*2+i]+70]=a;
				stageColor[0][add[page*2+i]]=b;
				stageColor[0][add[page*2+i]+20]=b;
				stageColor[0][add[page*2+i]+50]=b;
				stageColor[0][add[page*2+i]+70]=b;
			}
			else
			{
				for(int j=0;j<5;j++)levelList.get(1).level[add[page*2+i]+j*22]=a;
				for(int j=0;j<5;j++)stageColor[1][add[page*2+i]+j*22]=b;
			}
		}
	}
	
	public void run()
	{
		long last=System.currentTimeMillis();
		while(true)
		{
			
		long now=System.currentTimeMillis();
		if(now-last>=20)
		{
			if(ing)cal();
			else game();
			last=now;
			repaint();
		}
		
		}
				
	}
	
	public void game()
	{
		if(state==State.game)
		{
			if(gx==-1){ if(detect(focus,move))go(); }
			else ani();
		}
		get();
		if(c!=null)if(!c.isVisible())c=null;
		if(msg!=0)msg--;
	}
	
	public boolean detect(int i,int m)
	{
		if(i==-1)return false;
		if(m==-x&&i>=x)if(level[i-x]=='0'||level[i-x]=='3')return true;
		if(m==1&&i%x!=x-1)if(level[i+1]=='0'||level[i+1]=='3')return true;
		if(m==x&&i<x*y-x)if(level[i+x]=='0'||level[i+x]=='3')return true;
		if(m==-1&&i%x!=0)if(level[i-1]=='0'||level[i-1]=='3')return true;
		return false;
		
	}
	
	public void go()
	{
		gx=focus%x*50;
		gy=(int)(focus/x)*50;
		level[focus]--;
		if(mp)
		{
			ballColor=stageColor[page][focus];
			stageColor[page][focus]='0';
		}
		lastStart=focus;
		nextFocus=focus+move;
		dir=move;
		focus=-1;
		step++;
		move=0;
	}
	
	public void ani()
	{
		if(dir==-x)gy-=10;
		if(dir==1)gx+=10;
		if(dir==x)gy+=10;
		if(dir==-1)gx-=10;
		
		if(gx%50==0&&gy%50==0)
		{
			if(detect(nextFocus,dir))nextFocus+=dir;
			else
			{
				gx=-1;
				if(focus==-1)focus=nextFocus;
				level[nextFocus]++;
				if(mp)
				{
					stageColor[page][nextFocus]=ballColor;
				}
				lastGoal=nextFocus;
				
				if(replay>=history.length&&replay!=0)replay=-1;
				if(replay>0)
				{
					focus=history[replay].ball;
					move=history[replay].dir;
					replay++;
				}
				
				win();
			}
			
		}
	}
	
	public void read()
	{
		levelList.clear();
		if(!new File("Level").exists())createFile();
		
		try {
			for(String path:new File("Level/").list())
			{
				try{
				br = new BufferedReader(new FileReader("Level/"+path));
				int a=Integer.parseInt(br.readLine());
				int b=Integer.parseInt(br.readLine());
				String c=br.readLine();
				for(int i=0;i<a*b-c.length();i++)
				{
					c+="0";
				}
				
				levelList.add(new Level(path,a,b,c));
				} catch (Exception e) {}
			}
			if(levelList.size()!=0)br.close();
		} catch (IOException e) {}
	}
	
	public void readRecord()
	{
		String s;
		stepRecord.clear();
		try {
			br = new BufferedReader(new FileReader("Level/紀錄.txt"));
			try{
			while((s=br.readLine())!=null)
			{
				int n=s.lastIndexOf(':');
				stepRecord.add(new Step(s.substring(0, n),s.substring(n+1)));
			}
			}catch (Exception e) {}
			br.close();
			} 
		catch (IOException e) {}
		
	}
	
	public int score(int i)
	{
		for(Step s:stepRecord)
		{
			if(s.name.equals(levelList.get(i).name))
			{
				return s.step;
			}
		}
		return 999999;
	}
	
	public void solve()
	{
		ing=true;
		m[0]=-x;
		m[2]=x;
		ball.clear();
		way.clear();
		int b=0,g=0;
		for(char i:level)
		{
			if(i=='1'||i=='4')b++;
			if(i=='3'||i=='4')g++;
		}
		ba=new int[b];
		goal=new int[g];
		
		if(ba.length<goal.length||goal.length==0)
		{
			msg=100;
			ing=false;
			return;
		}
		
		b=0;
		g=0;
		for(int i=0;i<level.length;i++)
		{
			if(level[i]=='1'||level[i]=='4')
			{
				ba[b]=i;
				b++;
				level[i]--;
			}
			if(level[i]=='3'||level[i]=='4')
			{
				goal[g]=i;
				g++;
			}
		}
		his[] e={};
		
		ball.offer(new Way(ba,e));
		way.add(ba.clone());
	}
		
	public void cal()
	{
		int t=0;
		while(t<5000&&ing)
		{
			t++;
			
			Way w=ball.poll();
			if(w==null)
			{
				msg=100;
				for(int i:ba)level[i]++;
				ing=false;
				return;
			}
			
			
			int a=0;
			for(int i:goal)
			{
				for(int j:w.ball)
				{
					if(i==j)
					{
						a++;
						break;
					}
				}
			}
			if(a==goal.length)
			{
				ing=false;
				for(int i:ba)level[i]++;
				
				if(w.way.length==0)return;
				replay=1;
				focus=w.way[0].ball;
				move=w.way[0].dir;
				history=w.way;
				return;
			}
			
			for(int i:w.ball)level[i]++;
			
			for(int i=0;i<w.ball.length;i++)
			{
				for(int j=0;j<4;j++)
				{
					if(!detect(w.ball[i], m[j]))continue;
					
					int[] l=w.ball.clone();
					int k=l[i];
					l[i]=moveBall(l[i], m[j]);
					Arrays.sort(l);
					if(have(l))continue;
					
					his[] h=Arrays.copyOf(w.way, w.way.length+1);
					h[w.way.length]=new his(k,m[j]);
					ball.offer(new Way(l, h));
					way.add(l.clone());
				}
			}
			
			for(int i:w.ball)level[i]--;
			
			if(t==5000)turn=w.way.length;
		}
	}
	
	
	public int moveBall(int i,int m)
	{
		while(detect(i,m))
		{
			i+=m;
		}
		return i;
	}
	
	public boolean have(int[] a)
	{
		for(int i=0;i<way.size();i++)
		{
			if(Arrays.equals(a, way.get(i)))return true;
		}
		return false;
	}
	
	public void win()
	{
		boolean a=false;
		for(char i:level)
		{
			if(!mp){if(i=='3')return;}
			else if(i=='4'){a=true;break;}
		}
		if(!a&&mp)return;
		
		if(step<score(page)&&replay!=-1&&!mp)
		{
			win=2;
			saveRecord();
		}
		else win=1;
		replay=0;
		
		if(mp)win=stageColor[page][focus];
		if(replay==-1)replay=0;
	}
	
	public void get()
	{
		if(c==null)return;
		if(c.newlevel==null)return;
		newLevel=c.newlevel;
		x=newLevel.x;
		y=newLevel.y;
		level=newLevel.level;
		w=50;
		if(x>10||y>10)
		{
			if(x>y)w=500/x;
			else w=500/y;
		}
		xo=(640-w*x)/2;
		yo=(640-w*y)/2;
		state=State.create;
	}
	
	public void save(Level l)
	{
		BufferedWriter out;
		try{ 
			 new File("Level/"+l.name+".txt").delete();
			 out = new BufferedWriter(new FileWriter("Level/"+l.name+".txt",true));
			 out.write(l.x+"");
			 out.newLine();
			 out.write(l.y+"");
			 out.newLine();
			 out.write(l.level);
			 out.close();
		}catch(IOException ioe){}
	}
	
	public void saveRecord()
	{
		BufferedWriter out;
		try{ 
			 
			 if(score(page)==999999)
			 {
				 out = new BufferedWriter(new FileWriter("Level/紀錄.txt",true));
				 out.write(levelList.get(page).name+":"+step);
				 out.newLine();
				 
			 }
			 else
			 {
				 new File("Level/紀錄.txt").delete();
				 out = new BufferedWriter(new FileWriter("Level/紀錄.txt",true));
				 for(Step s:stepRecord)
				 {
					 if(s.name.equals(levelList.get(page).name))s.step=step;
					 out.write(s.name+":"+s.step);
					 out.newLine();
				 }
			 }
			 out.close();
		}catch(IOException ioe){}
		
	}
	
	public void createFile()
	{
		new File("Level").mkdir();
		String c;
		c="0002100000003002002010001";
		save(new Level("hard 1-1",5,5,c));
		c="0010003000200300000130120";
		save(new Level("hard 1-2",5,5,c));
		c="0001003000000003000201003001002000000003000001000";
		save(new Level("hard 1-3",7,7,c));
		c="002001200020200302000002000000102200";
		save(new Level("隨興創作1",6,6,c));
		c="0022020100000000200000020000300020020202000002000000000010200000";
		save(new Level("隨興創作2",8,8,c));
		c="0002000100000000200000000000020220030000000020020000000010020000";
		save(new Level("隨興創作3",8,8,c));
		c="0000021200200020000000003020002000000000001222000";
		save(new Level("隨興創作4",7,7,c));
		c="0000000001003001000000010";
		save(new Level("normal 1-1",5,5,c));
		c="0000000000020000021000002002000000100002012030200000000000000000202000000000000000200002000200000020";
		save(new Level("normal 1-2",10,10,c));
		c="1000020000000020001200003000200002000000000220001";
		save(new Level("normal 1-3",7,7,c));
		c="002000000000020001000000000000000000000020000000000000000000000000000020000020000000000000000000002000000000000030000020000000000000000000000000000000002120000000000000200000001000002000000020000020000000000000000000002000000";
		save(new Level("normal 1-4",15,15,c));
		c="0002100000002020020000000000000000000200000000002020000200000000000020000000000000003000000000002000000000002000000002000000000010000000000200000000000002001000000000020";
		save(new Level("normal 1-5",13,13,c));
		c="020000001000000000002002022000000000000030000020000000000002000000200000100020001";
		save(new Level("normal 1-6",9,9,c));
		c="0000000000021000000200000000000000000000000000020000000010000000002000000020000000003000000200000000200000000000000000000002010000000000000000000000000000000200000020000";
		save(new Level("normal 1-7",13,13,c));
		c="000000001020000000000200000200002002000030000000002002000201000000000002000000001";
		save(new Level("normal 1-8",9,9,c));
		c="100200021000200000000000000020000000000000200000020200000000020002000000000200021222200022000000000000000002000000000000000002000030000000000000200002020000000000000000000";
		save(new Level("normal 1-9",9,19,c));
		
	}
	
	public void paint(Graphics paint) {
		Image offscreen = createImage(640, 640);
		Graphics2D g = (Graphics2D)offscreen.getGraphics();
		
		if(state==State.menu)
		{
			g.setStroke(new BasicStroke(5));
			g.setColor(new Color(0xF0F0F0));
			g.fillRect(0, 0, 640, 700);
			g.setColor(new Color(0xD0D0D0));
			g.fillRect(180, 300, 280, 60);
			g.fillRect(180, 380, 280, 60);
			g.fillRect(180, 460, 280, 60);
			g.fillRect(180, 540, 280, 60);
			g.setColor(new Color(0xADADAD));
			g.drawRect(180, 300, 280, 60);
			g.drawRect(180, 380, 280, 60);
			g.drawRect(180, 460, 280, 60);
			g.drawRect(180, 540, 280, 60);
			g.setColor(new Color(0x3C3C3C));
			g.setFont(new Font(k, Font.PLAIN,100)); 
			g.drawString("迷蹤球", 130, 200);
			g.setFont(new Font(h, Font.PLAIN, 45)); 
			g.drawString("單人", 275, 347);
			g.drawString("多人", 275, 427);
			g.drawString("製作", 275, 507);
			g.drawString("介紹", 275, 587);
			g.drawImage(icon.getImage(), 450, 150, 100,100,null);
		}
		
		if(state==State.select)
		{
			g.setColor(Color.red);
			g.fillRect(270, 590, 100, 40);
			g.setColor(Color.white);
			g.setFont(new Font(h, Font.PLAIN, 35)); 
			g.drawString("返回",285,621);
			
			if(levelList.size()==0)
			{
				g.setColor(Color.black);
				g.setFont(new Font("h", Font.PLAIN, 50)); 
				g.drawString("你沒有任何關卡",140,280);
			}
			else{
			g.drawImage(stage(levelList.get(page)),110,90,422,422,null);
			g.setColor(Color.blue);
			g.fillRect(30, 540, 90, 40);
			g.fillRect(520, 540, 90, 40);
			g.setColor(new Color(0,182,0));
			g.fillRect(250, 530, 140, 50);
			g.setColor(Color.white);
			g.setFont(new Font(h, Font.PLAIN, 45)); 
			g.drawString("開始",275,570);
			g.setFont(new Font(h, Font.PLAIN, 35)); 
			g.drawString("上頁",40,571);
			g.drawString("下頁",530,571);
			g.setColor(Color.black);
			drawCenteredString(g,levelList.get(page).name,320,95);
			g.drawString(page+1+"/"+levelList.size(),470,630);
			g.setFont(new Font(h, Font.PLAIN, 35)); 
			if(!mp)
			{
				if(score(page)==999999)g.drawString("最佳紀錄:無", 10, 620);
				else g.drawString("最佳紀錄:"+score(page), 10, 620);
			}
			else
			{
				g.setColor(new Color(0xEAC100));
				g.fillRect(15, 140, 80, 50);
				g.fillRect(15, 220, 80, 50);
				g.fillRect(15, 300, 80, 50);
				g.setColor(Color.white);
				g.drawString("兩人",21,177);
				g.drawString("三人",21,257);
				g.drawString("四人",21,337);
				g.setColor(Color.red);
				g.setStroke(new BasicStroke(3));
				g.drawRect(15, 140+people*80, 80, 50);
				
			}
			}
		}
		
		if(state==State.create)
		{
			g.drawImage(stage(newLevel),70,70,500,500,null);
			g.setColor(Color.black);
			g.setFont(new Font(h, Font.PLAIN, 40));
			g.drawString("檔名："+newLevel.name+".txt", 120, 620);
			g.setFont(new Font(h, Font.PLAIN, 30));
			if(msg!=0)g.drawString("已儲存", 435, 620);
			g.setColor(Color.blue);
			g.fillRect(530, 590, 90, 40);
			g.setColor(Color.red);
			g.fillRect(20, 590, 90, 40);
			g.setColor(Color.yellow);
			g.setFont(new Font(h, Font.PLAIN, 30));
			g.drawString("儲存", 547, 620);
			g.drawString("返回", 33, 620);
			g.setColor(Color.red);
			g.setFont(new Font(h, Font.PLAIN, 25));
			g.drawString("檔案將存在與主程式同層的Level資料夾", 100, 53);
		}
		
		if(state==State.info)
		{
			g.setColor(new Color(0xF0F0F0));
			g.fillRect(0, 0, 640, 640);
			g.setColor(Color.red);
			g.fillRect(20, 590, 90, 40);
			g.setColor(Color.yellow);
			g.setFont(new Font(h, Font.PLAIN, 30)); 
			g.drawString("返回", 33, 620);
			g.setColor(new Color(0x3C3C3C));
			g.drawString("迷蹤球的規則很簡單，玩家要控制藍色小球", 40, 70);
			g.drawString("上下左右移動，球會一直移動直到撞到東西", 40, 110);
			g.drawString("如邊界、牆壁和其他球才停止。", 40, 150);
			g.drawString("只要把球停在所有終點的格子上就贏了。", 40, 190);
			g.drawString("遊玩時用滑鼠指定要移動哪顆球，並用方向", 40, 270);
			g.drawString("鍵或滑鼠來控制。", 40, 310);
			g.drawString("製作關卡時用滑鼠左鍵和右鍵來更改格子內", 40, 390);
			g.drawString("容物。", 40, 430);
			g.drawString("　球：", 250, 480);
			g.drawString("牆壁：", 250, 530);
			g.drawString("終點：", 250, 580);
			g.drawString("企劃：樊宗祐", 440, 580);
			g.drawString("程式：盧昭華", 440, 620);
			g.setColor(Color.blue);
			g.fillOval(350, 460, 20, 20);
			g.setColor(Color.black);
			g.fillRect(340, 500, 40, 40);
			g.setColor(Color.green);
			g.fillRect(340, 550, 40, 40);
			
		}
		
		if(state==State.game)
		{
			g.drawImage(stage(levelList.get(page)),70,70,500,500,null);
		
			if(win>0)
			{
				g.setColor(Color.BLUE);
				g.setFont(new Font(h, Font.PLAIN, 100)); 
				if(!mp)g.drawString("你贏了",160,200);
				else
				{
					g.setColor(Color.red);
					g.drawString(words[win-48]+"色贏了",125,200);
				}
				if(win==2)
				{
					g.setColor(Color.red);
					g.drawString("新紀錄！",130,400);
				}
			}
			
			g.setColor(Color.red);
			g.fillRect(20, 590, 90, 40);
			g.setColor(Color.BLUE);
			g.fillRect(530, 590, 90, 40);
			if(!mp)
			{
				if(ing)g.setColor(new Color(0xCC0080));
				else g.setColor(new Color(0x73B839));
				g.fillRect(580, 400, 50, 80);
			}
			g.setColor(new Color(0x46A3FF));
			g.fillRect(10, 400, 50, 80);
			g.setColor(Color.black);
			g.setFont(new Font(h, Font.PLAIN, 40)); 
			if(!mp)if(score(page)==999999)g.drawString("最佳紀錄:無", 150, 620);
			else g.drawString("最佳紀錄:"+score(page), 150, 620);
			else g.drawString(words[(int)(step/2)%(people+2)+1]+"方還剩"+(2-step%2)+"步", 150, 620);
			g.drawString(step+"", 430, 620);
			if(msg!=0)g.drawString("無解", 280, 60);
			if(ing)g.drawString("計算中：第"+turn+"步", 180, 60);
			g.setColor(Color.yellow);
			g.setFont(new Font(h, Font.PLAIN, 30)); 
			g.drawString("返回", 33, 620);
			g.drawString("重新", 547, 620);
			if(!mp)if(ing)
			{
				g.drawString("停", 590, 430);
				g.drawString("止", 590, 470);
			}
			else
			{
				g.drawString("解", 590, 430);
				g.drawString("題", 590, 470);
			}
			g.drawString("復", 20, 430);
			g.drawString("原", 20, 470);
			
		}
		
		paint.drawImage(offscreen, 0, 0, getWidth(), getHeight(),null);
	}
	
	public Image stage(Level l)
	{
		char[] level=l.level; 
		int x=l.x; 
		int y=l.y;
		int w=500;
		if(x>10||y>10)
		{
			if(x>y)w+=(x-10)*50;
			else w+=(y-10)*50;
		}
		
		Image stage = createImage(w+2, w+2);
		Graphics2D g = (Graphics2D)stage.getGraphics();
		
		g.translate(1, 1);
		
		int xo=(w-50*x)/2;
		int yo=(w-50*y)/2;
		
		g.setStroke(new BasicStroke(3));
		
		for(int i=0;i<=x;i++)
		{
			g.drawLine(xo+i*50, yo, xo+i*50, w-yo);
		}
		for(int i=0;i<=y;i++)
		{
			g.drawLine(xo, yo+i*50, w-xo, yo+i*50);
		}
		
		for(int i=0;i<x*y;i++)
		{
			if(level[i]=='2')
			{
				g.setColor(Color.black);
				g.fillRect(xo+2+i%x*50,yo+2+(int)(i/x)*50,47,47);
			}
			
			if(level[i]=='3'||level[i]=='4')
			{
				g.setColor(Color.green);
				g.fillRect(xo+2+i%x*50,yo+2+(int)(i/x)*50,47,47);
			}
			
			if(level[i]=='1'||level[i]=='4')
			{
				if(mp)
				{
					g.setColor(color[stageColor[page][i]-48]);
				}
				else g.setColor(Color.blue);
				g.fillOval(xo+15+i%x*50,yo+15+(int)(i/x)*50,20,20);
			}
		}
		
		if(gx!=-1)
		{
			g.setColor(Color.blue);
			if(mp)g.setColor(color[ballColor-48]);
			g.fillOval(xo+gx+15,yo+gy+15,20,20);
		}
		
		g.setStroke(new BasicStroke(2));	
		if(focus!=-1)
		{
			g.setColor(Color.red);
			g.drawRect(xo+3+focus%x*50,yo+3+(int)(focus/x)*50,45,45);
		}
		return stage;
	}
	
	public void drawCenteredString(Graphics2D g, String text, int x, int y) {
	    FontMetrics metrics = g.getFontMetrics(g.getFont());
	    x-=metrics.stringWidth(text)/2;
	    y-=metrics.getHeight()/2;
	    g.drawString(text, x, y);
	}
	
	

}
