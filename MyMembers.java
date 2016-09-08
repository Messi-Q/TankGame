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

//������������
class AePlayWave extends Thread{
	private String filename;
	public AePlayWave(String wavfile){
		filename=wavfile;
	}
	
	public void run(){
		File soundFile=new File(filename);
		
		AudioInputStream audioInputStream=null;//��Ƶ��
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

//��¼�࣬ͬʱ���Ա����������
class Recorder{
	//��¼ÿ���ж��ٵ���
	private static int enNum=20;
	//�������ж��ٿ����õ���
	private static int myLife=3;
	//��¼�ܹ������˶��ٵ���
	private static int allEnNum=0;
	//���ļ��лָ���¼��
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
	
	//�������̹�˵�̹������ͷ���
	public static void keepRecAndEnemyTank() throws IOException{
		try {
			//����
			fw=new FileWriter("E:\\Tank.txt");
			bw=new BufferedWriter(fw);
			
			bw.write(allEnNum+"\r\n");
			
			//���浱ǰ�����ŵ�̹������ͷ���
			for(int i=0;i<ets.size();i++){
				//ȡ����һ��̹��
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
			bw.close();//���ȹ�
			fw.close();
		}
		
	}
	
	//���ļ��ж�ȡ����¼
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
	//����һ������̹���������浽�ļ���
	public static void keepRecording() throws IOException{
		try {
			//����
			fw=new FileWriter("E:\\Tank.txt");
			bw=new BufferedWriter(fw);
			
			bw.write(allEnNum+"\r\n");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			bw.close();//���ȹ�
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
	
	//���ٵ��˵�����
	public static void reduceEnNum(){
		enNum--;
	}
	//�������
	public static void addEnNum(){
		allEnNum++;
	}
	
}

//ը����
class bomb{
	int x,y;
	
	//ը��������
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

//�ӵ���
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
				//����
				y-=speed;
				break;
			case 1:
				//����
				x+=speed;
				break;
			case 2:
				//����
				y+=speed;
				break;
			case 3:
				//����
				x-=speed;
				break;
			}
			
		//	System.out.println("�ӵ�����x="+x+"�ӵ�����y="+y);
			
			if(x<0 || x>450 || y<0 || y>300){
				this.isLive=false;
				break;
			}
		}
	}
}

//̹����
class Tank {
	int x=0;
	int y=0;
	
	//0Ϊ����  1Ϊ����  2Ϊ����   3Ϊ����
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

//�ҵ�̹��
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

//���˵�̹��
class EnemyTank extends Tank implements Runnable{
	
	int times=0;
	
	//����һ���������Է���MyPanel�ϵ����е���̹��
	Vector<EnemyTank>ets=new Vector<EnemyTank>();
	
	//����һ��������ŵ��˵��ӵ�
	Vector<shot> ss=new Vector<shot>();
	
	public EnemyTank(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub	
	}
	
	//�õ�MyPanel�ϵĵ���̹������
	public void setEts(Vector<EnemyTank> vv){
		this.ets=vv;
	}
	
	//�ж��Ƿ�������ĵ���̹��
	public boolean isTouchOtherEnemy(){
		boolean b=false;
		
		switch(this.direct){
		case 0:
			//�ҵ�̹������
			//ȡ�����е���̹��
			for(int i=0;i<ets.size();i++){
				//ȡ����һ��̹��
				EnemyTank et=ets.get(i);
				if(et!=this){
					//�������̹�����ϻ�����
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
			//̹������
			//ȡ�����е���̹��
			for(int i=0;i<ets.size();i++){
				//ȡ����һ��̹��
				EnemyTank et=ets.get(i);
				if(et!=this){
					//�������̹�����ϻ�����
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
			//̹������
			//ȡ�����е���̹��
			for(int i=0;i<ets.size();i++){
				//ȡ����һ��̹��
				EnemyTank et=ets.get(i);
				if(et!=this){
					//�������̹�����ϻ�����
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
			//̹������
			//ȡ�����е���̹��
			for(int i=0;i<ets.size();i++){
				//ȡ����һ��̹��
				EnemyTank et=ets.get(i);
				if(et!=this){
					//�������̹�����ϻ�����
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
					//����
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
					//����
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
					//����
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
					//����
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
				//�ж��Ƿ���Ҫ��̹�˼����µ��ӵ�
					if(islive){
						if(ss.size()<5){
							shot s=null;
							//û���ӵ�
							//���
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
							
							//�����ӵ��߳�
							Thread t=new Thread(s);
							t.start();
					}
				}
			}
			
			
			//��̹���������һ���µķ���
			this.direct=(int)(Math.random()*4);
			
			//�жϵ���̹���Ƿ�����
			if(this.islive==false){
				break;
			}
		}	
	}	
}
