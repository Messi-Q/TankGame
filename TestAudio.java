package game3;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class TestAudio {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AePlayWave apw=new AePlayWave("E:\\111.wav");
		apw.start();
	}

}

class AePlayWave1 extends Thread{
	private String filename;
	public AePlayWave1(String wavfile){
		filename=wavfile;
	}
	
	public void run(){
		File soundFile=new File(filename);
		
		AudioInputStream audioInputStream=null;//ÒôÆµÁ÷
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
