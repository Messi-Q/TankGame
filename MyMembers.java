package game3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

//播放声音的类
class AePlayWave extends Thread{
	private String filename;
	public AePlayWave(String wavfile){
		filename=wavfile;
	}
	
	public void run(){
		File soundFile=new File(filename);
		
		AudioInputStream audioInputStream=null;//音频流
		try {
			audioInputStream=AudioSystem.getAudioInputStream(soundFile);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}
		
		AudioFormat format=audioInputStream.getFormat();
		SourceDataLine auline=null;
		DataLine.Info info=new DataLine.Info(SourceDataLine.class, format);
		
		try {
			auline=(SourceDataLine)AudioSystem.getLine(info);
			auline.open(format);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}
		
		auline.start();
		int nBytesRead=0;
		byte[]abData=new byte[512];
		
		try {
			while(nBytesRead!=-1){
				nBytesRead=audioInputStream.read(abData, 0, abData.length);
				if(nBytesRead>=0){
					auline.write(abData, 0, nBytesRead);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}finally{
			auline.drain();
			auline.close();
		}
		
	}
	
}


class Node{
	int x;
	int y;
	int direct; 
	public Node(int x,int y,int direct){
		this.x=x;
		this.y=y;
		this.direct=direct;
	}
}

//记录类，同时可以保存玩家设置
class Recorder{
	//记录每关有多少敌人
	private static int enNum=20;
	//设置我有多少可以用的命
	private static int myLife=3;
	//记录总共消灭了多少敌人
	private static int allEnNum=0;
	//从文件中恢复记录点
	static Vector<Node> nodes=new Vector<Node>();
	
	private static FileWriter fw=null;
	private static BufferedWriter bw=null;
	private static FileReader fr=null;
	private static BufferedReader br=null;
	
	private static Vector<EnemyTank>ets=new Vector<EnemyTank>();
	
	public Vector<EnemyTank> getEts() {
		return ets;
	}

	public void setEts(Vector<EnemyTank> ets) {
		this.ets = ets;
	}

	public Vector<Node> getNodesAndEnNums() throws IOException{
		try {
			fr=new FileReader("E:\\Tank.txt");
			br=new BufferedReader(fr);
			String n="";
			n=br.readLine();
			allEnNum=Integer.parseInt(n);
			while((n=br.readLine())!=null){
				String []xyz=n.split(" ");
				for(int i=0;i<xyz.length;i++){
					Node node=new Node(Integer.parseInt(xyz[0]),Integer.parseInt(xyz[1]),Integer.parseInt(xyz[2]));
				    nodes.add(node);
				}
			}	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			br.close();
			fr.close();
		}
		return nodes;
	}
	
	//保存敌人坦克的坦克坐标和方向
	public static void keepRecAndEnemyTank() throws IOException{
		try {
			//创建
			fw=new FileWriter("E:\\Tank.txt");
			bw=new BufferedWriter(fw);
			
			bw.write(allEnNum+"\r\n");
			
			//保存当前还活着的坦克坐标和方向
			for(int i=0;i<ets.size();i++){
				//取出第一个坦克
				EnemyTank et=ets.get(i);
				if(et.islive){
					String recode=et.x+" "+et.y+" ";
					bw.write(recode+"\r\n");
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			bw.close();//后开先关
			fw.close();
		}
		
	}
	
	//从文件中读取，记录
	public static void getRecoding() throws IOException{
		try {
			fr=new FileReader("E:\\Tank.txt");
			br=new BufferedReader(fr);
			String n=br.readLine();
			allEnNum=Integer.parseInt(n);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			br.close();
			fr.close();
		}
	}
	//把玩家机会敌人坦克数量保存到文件中
	public static void keepRecording() throws IOException{
		try {
			//创建
			fw=new FileWriter("E:\\Tank.txt");
			bw=new BufferedWriter(fw);
			
			bw.write(allEnNum+"\r\n");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			bw.close();//后开先关
			fw.close();
		}
	}
	
	public static int getAllEnNum() {
		return allEnNum;
	}
	public static void setAllEnNum(int allEnNum) {
		Recorder.allEnNum = allEnNum;
	}
	public static int getEnNum() {
		return enNum;
	}
	public static void setEnNum(int enNum) {
		Recorder.enNum = enNum;
	}
	public static int getMyLife() {
		return myLife;
	}
	public static void setMyLife(int myLife) {
		Recorder.myLife = myLife;
	}
	
	//减少敌人的数量
	public static void reduceEnNum(){
		enNum--;
	}
	//消灭敌人
	public static void addEnNum(){
		allEnNum++;
	}
	
}

//炸弹类
class bomb{
	int x,y;
	
	//炸弹的生命
	int life=9;
	boolean isLive=true;
	public bomb(int x,int y){
		this.x=x;
		this.y=y;
	}
	
	public void lifeDown(){
		if(life>0){
			life--;
		}else{
			this.isLive=false; 
		}
	}
}

//子弹类
class shot implements Runnable{
	int x;
	int y;
	int direct;
	double speed=2.0;
	boolean isLive=true;
	public shot(int x,int y,int direct){
		this.x=x;
		this.y=y;
		this.direct=direct;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				// TODO: handle exception
			}			
			switch(direct){
			case 0:
				//向上
				y-=speed;
				break;
			case 1:
				//向右
				x+=speed;
				break;
			case 2:
				//向下
				y+=speed;
				break;
			case 3:
				//向左
				x-=speed;
				break;
			}
			
		//	System.out.println("子弹坐标x="+x+"子弹坐标y="+y);
			
			if(x<0 || x>450 || y<0 || y>300){
				this.isLive=false;
				break;
			}
		}
	}
}

//坦克类
class Tank {
	int x=0;
	int y=0;
	
	//0为向上  1为向右  2为向下   3为向左
	int direct=0;
	
	double speed=1.2;
	
	int color;
	
	boolean islive=true;
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public int getDirect() {
		return direct;
	}

	public void setDirect(int direct) {
		this.direct = direct;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Tank(int x,int y){
		this.x=x;
		this.y=y;
	}
}

//我的坦克
class MyTank extends Tank{
	Vector<shot> ss=new Vector<shot>();
	shot s=null;

	public MyTank(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}
	
	public void shotenemy(){
		switch(this.direct){
		case 0:
			s=new shot(x+10,y,0);
			ss.add(s);
			break;
		case 1:
			s=new shot(x+30,y+10,1);
			ss.add(s);
			break;
		case 2:
			s=new shot(x+10,y+30,2);
			ss.add(s);
			break;	
		case 3:
			s=new shot(x,y+10,3);
			ss.add(s);
			break;
		}
		
		Thread t=new Thread(s);
		t.start();
		
	}
	
	public void moveUp(){
		y-=speed;
	}
	public void moveRight(){
		x+=speed;
	}
	public void moveDown(){
		y+=speed;
	}
	public void moveLeft(){
		x-=speed;
	}
}

//敌人的坦克
class EnemyTank extends Tank implements Runnable{
	
	int times=0;
	
	//定义一个向量可以访问MyPanel上的所有敌人坦克
	Vector<EnemyTank>ets=new Vector<EnemyTank>();
	
	//定义一个向量存放敌人的子弹
	Vector<shot> ss=new Vector<shot>();
	
	public EnemyTank(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub	
	}
	
	//得到MyPanel上的敌人坦克向量
	public void setEts(Vector<EnemyTank> vv){
		this.ets=vv;
	}
	
	//判断是否碰到别的敌人坦克
	public boolean isTouchOtherEnemy(){
		boolean b=false;
		
		switch(this.direct){
		case 0:
			//我的坦克向上
			//取出所有敌人坦克
			for(int i=0;i<ets.size();i++){
				//取出第一个坦克
				EnemyTank et=ets.get(i);
				if(et!=this){
					//如果敌人坦克向上或向下
					if(et.direct==0 || et.direct==2){
						if(this.x>=et.x && this.x<=et.x+20 && this.y>=et.y && this.y<=et.y+30){
							return true;
						}
						if(this.x+20>=et.x && this.x+20<=et.x+20 && this.y>=et.y && this.y<=et.y+30){
							return true;
						}
					}
					if(et.direct==1 || et.direct==3){
						if(this.x>=et.x && this.x<=et.x+30 && this.y>=et.y && this.y<=et.y+20){
							return true;
						}
						if(this.x+20>=et.x && this.x+20<=et.x+30 && this.y>=et.y && this.y<=et.y+20){
							return true;
						}
					}
				}
			}
			break;
		case 1:
			//坦克向右
			//取出所有敌人坦克
			for(int i=0;i<ets.size();i++){
				//取出第一个坦克
				EnemyTank et=ets.get(i);
				if(et!=this){
					//如果敌人坦克向上或向下
					if(et.direct==0 || et.direct==2){
						if(this.x+30>=et.x && this.x+30<=et.x+20 && this.y>=et.y && this.y<=et.y+30){
							return true;
						}
						if(this.x+30>=et.x && this.x+30<=et.x+20 && this.y+20>=et.y && this.y+20<=et.y+30){
							return true;
						}
					}
					if(et.direct==1 || et.direct==3){
						if(this.x+30>=et.x && this.x+30<=et.x+30 && this.y>=et.y && this.y<=et.y+20){
							return true;
						}
						if(this.x+30>=et.x && this.x+30<=et.x+30 && this.y+20>=et.y && this.y+20<=et.y+20){
							return true;
						}
					}
				}
			}
			break;
		case 2:
			//坦克向下
			//取出所有敌人坦克
			for(int i=0;i<ets.size();i++){
				//取出第一个坦克
				EnemyTank et=ets.get(i);
				if(et!=this){
					//如果敌人坦克向上或向下
					if(et.direct==0 || et.direct==2){
						if(this.x>=et.x && this.x<=et.x+20 && this.y+30>=et.y && this.y+30<=et.y+30){
							return true;
						}
						if(this.x+20>=et.x && this.x+20<=et.x+20 && this.y+30>=et.y && this.y+30<=et.y+30){
							return true;
						}
					}
					if(et.direct==1 || et.direct==3){
						if(this.x>=et.x && this.x<=et.x+30 && this.y+30>=et.y && this.y+30<=et.y+20){
							return true;
						}
						if(this.x+20>=et.x && this.x+20<=et.x+30 && this.y+30>=et.y && this.y+30<=et.y+20){
							return true;
						}
					}
				}
			}
			break;
		case 3:
			//坦克向左
			//取出所有敌人坦克
			for(int i=0;i<ets.size();i++){
				//取出第一个坦克
				EnemyTank et=ets.get(i);
				if(et!=this){
					//如果敌人坦克向上或向下
					if(et.direct==0 || et.direct==2){
						if(this.x>=et.x && this.x<=et.x+20 && this.y>=et.y && this.y<=et.y+30){
							return true;
						}
						if(this.x>=et.x && this.x<=et.x+20 && this.y>=et.y && this.y<=et.y+30){
							return true;
						}
					}
					if(et.direct==1 || et.direct==3){
						if(this.x>=et.x && this.x<=et.x+30 && this.y>=et.y && this.y<=et.y+20){
							return true;
						}
						if(this.x>=et.x && this.x<=et.x+30 && this.y+20>=et.y && this.y+20<=et.y+20){
							return true;
						}
					}
				}
			}
			break;
		}
		
		return b;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
		
			switch(this.direct){
				case 0:
					//向上
					for(int i=0;i<40;i++){
						if(y>0 && !this.isTouchOtherEnemy()){
					        y-=speed;
						}
					 	try {
							Thread.sleep(40);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}  
					}
					break;
				case 1:
					//向右
					for(int i=0;i<40;i++){
					    if(x<450 && !this.isTouchOtherEnemy()){
						    x+=speed;
					    }
					 	try
					 	{
							Thread.sleep(30);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}  
					}
					break;
				case 2:
					//向下
					for(int i=0;i<35;i++){
					  if(y<300 && !this.isTouchOtherEnemy()){
						    y+=speed;
					    }
					 	try {
							Thread.sleep(40);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}  
					}
					break;
				case 3:
					//向左
					for(int i=0;i<30;i++){
					    if(x>0 && !this.isTouchOtherEnemy()){
						    x-=speed;
					    }
					 	try {
							Thread.sleep(50);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}  
					}
					break;
			}
			
			this.times++;
			
			if(times%2==0){
				//判断是否需要给坦克加入新的子弹
					if(islive){
						if(ss.size()<5){
							shot s=null;
							//没有子弹
							//添加
							switch(direct){
							case 0:
								s=new shot(x+10,y,0);
								ss.add(s);
								break;
							case 1:
								s=new shot(x+30,y+10,1);
								ss.add(s);
								break;
							case 2:
								s=new shot(x+10,y+30,2);
								ss.add(s);
								break;	
							case 3:
								s=new shot(x,y+10,3);
								ss.add(s);
								break;
							}
							
							//启动子弹线程
							Thread t=new Thread(s);
							t.start();
					}
				}
			}
			
			
			//让坦克随机产生一个新的方向
			this.direct=(int)(Math.random()*4);
			
			//判断敌人坦克是否死亡
			if(this.islive==false){
				break;
			}
		}	
	}	
}
