package game3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class MyTankGame4 extends JFrame implements ActionListener{
	MyPanel mp = null;

	// 定义开始面板
	MyStartPanel msp = null;
	
	//定义所需要的菜单
	JMenuBar jmb=null;
	JMenu jm1=null;
	JMenuItem jmi1=null;
	JMenuItem jmi2=null;
	JMenuItem jmi3=null;
	JMenuItem jmi4=null;
	JMenuItem jmi5=null;

	public MyTankGame4() {

		jmb=new JMenuBar();
		jm1=new JMenu("游戏(G)");
		jm1.setMnemonic('G');
		
		jmi1=new JMenuItem("开始新游戏(N)");
		jmi1.setMnemonic('N');
		jmi1.addActionListener(this);
		jmi1.setActionCommand("new game");
		
		jmi2=new JMenuItem("暂停(p)");
		jmi2.setMnemonic('P');
		jmi2.addActionListener(this);
		jmi2.setActionCommand("pause");
		
		jmi3=new JMenuItem("退出游戏(E)");
		jmi3.setMnemonic('E');
		jmi3.addActionListener(this);
		jmi3.setActionCommand("exit");
		
		jmi4=new JMenuItem("存盘退出游戏(C)");
		jmi4.setMnemonic('C');
		jmi4.addActionListener(this);
		jmi4.setActionCommand("saveExit");
		
		jmi5=new JMenuItem("继续上局游戏(S)");
		jmi5.setMnemonic('S');
		jmi5.addActionListener(this);
		jmi5.setActionCommand("conGame");
		
		jm1.add(jmi1);
		jm1.add(jmi2);
		jm1.add(jmi3);
		jm1.add(jmi4);
		jm1.add(jmi5);
		jmb.add(jm1);
		
		msp = new MyStartPanel();
		Thread t = new Thread(msp);
		t.start();
		
		this.setJMenuBar(jmb);
		this.add(msp);
		this.setSize(690, 560);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyTankGame4 MyTankGame = new MyTankGame4();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		//对用户的点击做出处理
		if(e.getActionCommand().equals("new game")){
			//创建游戏面板
			 mp=new MyPanel("newGame");
		     //启动mp
		     Thread t=new Thread(mp);
		     t.start();
		     //先删除旧的面板，开始面板
		     this.remove(msp);
		     this.add(mp);
		
		     this.addKeyListener(mp);
		     this.setVisible(true);
		}else if(e.getActionCommand().equals("pause")){
			try {
				shot.class.newInstance().speed=0;
				Tank.class.newInstance().speed=0;
				
			} catch (InstantiationException | IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		else if(e.getActionCommand().equals("exit")){
			try {
				Recorder.keepRecording();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			System.exit(0);
		}
		else if(e.getActionCommand().equals("saveExit")){
			
			Recorder rd=new Recorder();
			rd.setEts(mp.enemy);
			try {
				rd.keepRecAndEnemyTank();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.exit(0);
			//退出
			
		}
		else if(e.getActionCommand().equals("conGame")){
			mp=new MyPanel("con");
		     //启动mp
		     Thread t=new Thread(mp);
		     t.start();
		     //先删除旧的面板，开始面板
		     this.remove(msp);
		     this.add(mp);
		
		     this.addKeyListener(mp);
		     this.setVisible(true);
		}
	}
}

// 提示作用
class MyStartPanel extends JPanel implements Runnable {

	int times = 0;

	public void paint(Graphics g) {
		super.paint(g);
		g.fillRect(0, 0, 480, 380);
		// 提示信息

		// 开关信息的字体
		if (times % 2 == 0) {
			g.setColor(Color.yellow);
			Font myFont = new Font("华文新魏", Font.BOLD, 30);
			g.setFont(myFont);
			g.drawString("state: 1", 200, 140);
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Thread.sleep(125);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			times++;

			// 重画
			this.repaint();
		}
	}
}

class MyPanel extends JPanel implements KeyListener, Runnable {

	// 定义一个我的Tank
	MyTank mytank = null;
	
	// 定义敌人的Tank
	Vector<EnemyTank> enemy = new Vector<EnemyTank>();
	Vector<Node>nodes=new Vector<Node>();
	
	int enemySize = 3;

	Vector<bomb> bombs = new Vector<bomb>();

	// 定义三张图片,组一个炸弹的效果
	Image image1 = null;
	Image image2 = null;
	Image image3 = null;

	public MyPanel(String flag) {
		
		//恢复记录
		try {
			Recorder.getRecoding();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		mytank = new MyTank(200, 200);
		
		
		if (flag.equals("newGame")) {
			// 初始化敌人坦克
			for (int i = 0; i <= enemySize; i++) {
				EnemyTank emy = new EnemyTank((i + 1) * 50, 0);
				emy.setColor(0);
				emy.setDirect(2);
				// 将MyPanel的敌人坦克向量交给该敌人坦克
				emy.setEts(enemy);

				// 启动敌人坦克
				Thread t = new Thread(emy);
				t.start();

				// 给敌人坦克添加一颗子弹
				shot s = new shot(emy.x + 10, emy.y + 30, 2);
				// 加入给敌人坦克
				emy.ss.add(s);
				Thread t2 = new Thread(s);
				t2.start();

				// 加入
				enemy.add(emy);
			}

        }else{
        	try {
				nodes=new Recorder().getNodesAndEnNums();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        	for (int i = 0; i <nodes.size(); i++) {
        	    Node node=nodes.get(i);	
        		
				EnemyTank emy = new EnemyTank(node.x, node.y);
				emy.setColor(0);
				emy.setDirect(node.direct);
				// 将MyPanel的敌人坦克向量交给该敌人坦克
				emy.setEts(enemy);

				// 启动敌人坦克
				Thread t = new Thread(emy);
				t.start();

				// 给敌人坦克添加一颗子弹
				shot s = new shot(emy.x + 10, emy.y + 30, 2);
				// 加入给敌人坦克
				emy.ss.add(s);
				Thread t2 = new Thread(s);
				t2.start();

				// 加入
				enemy.add(emy);
			}

        }
		
		// 初始化图片
		try {
			image1 = ImageIO.read(new File("bomb_1.gif"));
			image2 = ImageIO.read(new File("bomb_2.gif"));
			image3 = ImageIO.read(new File("bomb_3.gif"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		//播放开始背景音乐
		AePlayWave apw=new AePlayWave("E:\\111.wav");
		apw.start();
		
		// image1=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_1.gif"));
		// image2=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_2.gif"));
		// image3=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_3.gif"));
	}
	
	public void showInfo(Graphics g){
		//画出提示信息
		this.drawTank(50, 370, g, 0, 0);
		g.setColor(Color.black);
		g.drawString(Recorder.getEnNum()+"", 80, 390);
		this.drawTank(100, 370, g, 0, 1);
		g.setColor(Color.black);
		g.drawString(Recorder.getMyLife()+"", 130, 390);
		
		//画出玩家的总成绩
		g.setColor(Color.BLACK);
		Font f=new Font("宋体", Font.BOLD, 18);
		g.setFont(f);
		g.drawString("您的总成绩：", 505, 30);
		
		this.drawTank(510, 60, g, 0, 0);
		
		g.setColor(Color.BLACK);
		g.drawString(Recorder.getAllEnNum()+"", 550, 80);
	}

	//重新paint
	public void paint(Graphics g) {
		super.paint(g);
		g.fillRect(0, 0, 500, 350);
		
		//画出提示信息
		this.showInfo(g);
		
		// 画自己的Tank
		if (mytank.islive) {
			this.drawTank(mytank.getX(), mytank.getY(), g, this.mytank.direct, 1);
		}

		for (int i = 0; i < mytank.ss.size(); i++) {
			shot myshot = mytank.ss.get(i);
			// 画自己的子弹
			if (myshot != null && myshot.isLive == true) {
				g.draw3DRect(myshot.x, myshot.y, 2, 2, false);
			}

			if (myshot.isLive == false) {
				// 从ss中删除该子弹
				mytank.ss.remove(myshot);
			}
		}

		// 画出炸弹
		for (int i = 0; i < bombs.size(); i++) {
			System.out.println("炸弹进入");
			// 取出炸弹
			bomb b = bombs.get(i);

			if (b.life > 6) {
				g.drawImage(image1, b.x, b.y, 30, 30, this);
			} else if (b.life > 3) {
				g.drawImage(image2, b.x, b.y, 30, 30, this);
			} else {
				g.drawImage(image3, b.x, b.y, 30, 30, this);
			}
			// 让b的生命值减小
			b.lifeDown();
			// 生命值为0，则炸弹从向量中remove
			if (b.life == 0) {
				bombs.remove(b);
			}

		}

		// 画敌人的Tank
		for (int i = 0; i < enemy.size(); i++) {
			EnemyTank et = enemy.get(i);
			if (et.islive) {
				this.drawTank(et.getX(), et.getY(), g, et.getDirect(), 0);
				// 画出敌人的子弹
				for (int j = 0; j < et.ss.size(); j++) {
					// 取出子弹
					shot enemyshot = et.ss.get(j);
					if (enemyshot.isLive) {
						g.draw3DRect(enemyshot.x, enemyshot.y, 2, 2, false);
					} else {
						// 如果敌人坦克死亡，就从Vector中去除子弹
						et.ss.remove(enemyshot);
					}
				}
			}
		}
	}

	// 判断敌人的子弹是否击中我
	public void hitMe() {
		// 取出每一个敌人坦克
		for (int j = 0; j < this.enemy.size(); j++) {
			// 取出坦克
			EnemyTank et = enemy.get(j);

			// 取出每一颗子弹
			for (int i = 0; i < et.ss.size(); i++) {
				// 取出子弹
				shot enemyshot = et.ss.get(i);

				if (mytank.islive) {
					if(this.hitTank(enemyshot, mytank)){
						
					}
				}
			}
		}
	}

	// 判断是否击中敌人的坦克
	public void hitEnemyTank() {
		for (int i = 0; i < mytank.ss.size(); i++) {
			// 取出子弹
			shot myshot = mytank.ss.get(i);
			// 判断子弹是否有效
			if (myshot.isLive) {
				// 取出坦克与之判断
				for (int j = 0; j < enemy.size(); j++) {
					// 取出坦克
					EnemyTank et = enemy.get(j);

					if (et.islive) {
						if(this.hitTank(myshot, et)){
							//减少敌人数
							Recorder.reduceEnNum();
							//增加我的战绩数
							Recorder.addEnNum();
						}
					}
				}
			}
		}
	}

	// 写一个函数判断子弹是否击中敌人坦克
	public boolean hitTank(shot s, Tank et) {
		
		boolean b1=false;
		
		switch (et.direct) {
		case 0:
		case 2:
			if (s.x > et.x && s.x < et.x + 20 && s.y > et.y && s.y < et.y + 30) {
				// 子弹死亡
				s.isLive = false;
				// 坦克死亡
				et.islive = false;
				b1=true;
				// 创建炸弹，放入Vector中
				bomb b = new bomb(et.x, et.y);
				bombs.add(b);

			}
			break;
		case 1:
		case 3:
			if (s.x > et.x && s.x < et.x + 30 && s.y > et.y && s.y < et.y + 30) {
				// 子弹死亡
				s.isLive = false;
				// 坦克死亡
				s.isLive = false;
				// 创建炸弹，放入Vector中
				b1=true;
				bomb b = new bomb(et.x, et.y);
				bombs.add(b);

			}
		}
		return b1;
	}

	// 画坦克的函数
	public void drawTank(int x, int y, Graphics g, int direct, int type) {
		switch (type) {
		case 0:
			g.setColor(Color.cyan);
			break;
		case 1:
			g.setColor(Color.orange);
			break;
		}
		switch (direct) {
		case 0:
			// 画坦克 向上
			g.fillRect(x, y, 5, 30);
			g.fillRect(x + 20, y, 5, 30);
			g.fillOval(x + 5, y + 5, 15, 20);
			g.fill3DRect(x + 8, y + 10, 10, 10, false);
			g.drawLine(x + 12, y + 10, x + 12, y);
			break;
		case 1:
			// 向右
			g.fillRect(x, y, 30, 5);
			g.fillRect(x, y + 20, 30, 5);
			g.fillOval(x + 5, y + 5, 20, 15);
			g.fill3DRect(x + 10, y + 8, 10, 10, false);
			g.drawLine(x + 8, y + 12, x + 30, y + 12);
			break;
		case 2:
			// 画坦克 向下
			g.fillRect(x, y, 5, 30);
			g.fillRect(x + 20, y, 5, 30);
			g.fillOval(x + 5, y + 5, 15, 20);
			g.fill3DRect(x + 8, y + 10, 10, 10, false);
			g.drawLine(x + 12, y + 10, x + 12, y + 30);
			break;
		case 3:
			// 画坦克 向左
			g.fillRect(x, y, 30, 5);
			g.fillRect(x, y + 20, 30, 5);
			g.fillOval(x + 5, y + 5, 20, 15);
			g.fill3DRect(x + 10, y + 8, 10, 10, false);
			g.drawLine(x + 8, y + 12, x, y + 12);
			break;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		// 设置我的坦克方向
		if (e.getKeyCode() == KeyEvent.VK_W) {
			this.mytank.setDirect(0);
			this.mytank.moveUp();
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			this.mytank.setDirect(2);
			this.mytank.moveDown();
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			this.mytank.setDirect(1);
			this.mytank.moveRight();
		} else if (e.getKeyCode() == KeyEvent.VK_A) {
			this.mytank.setDirect(3);
			this.mytank.moveLeft();
		}

		if (e.getKeyCode() == KeyEvent.VK_J) {
			if (mytank.ss.size() <= 10) {
				this.mytank.shotenemy();
			}
		}

		this.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// 每隔100ms重画子弹
		while (true) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			this.hitEnemyTank();
			this.hitMe();

			this.repaint();
		}
	}
}
