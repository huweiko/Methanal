package com.example.methanal.audioLSTter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 *  android audio音频通信类
 * @author huwei
 *
 */
public class audioLSTter implements TransToPeak
{
	/**
	 * 采样频率
	 */
	public int samplerate=44100;//io
	/**
	 * 采样周期
	 */
	private float sampleT=1/44100;//io
	public static boolean[] abcbuffer=new boolean[1000];
	/**
	 * 输出数据的最大尺寸
	 */
	byte outSize = 100;//o
	
	/**
	 * 输出幅值
	 */
	public short outAmplitude = 3000;//o
	/**
	 * 发送波特率
	 */
	int outBaudrate=9600;//o
	/**
	 * 输出极性
	 */
	public boolean outPolarity=false;//o
	
	/**
	 * 发音器
	 */
	private AudioTrack audiotrack=null;//o
	
	
	/**
	 * 接收队列
	 */
	Queue<Byte> inBuffer;//=new LinkedList<Byte>();//i
	
	/**
	 * 接收数据的最大尺寸
	 */
	byte inSize = 10;//i
	
	/**
	 * 触发电平
	 */
	public short inGtrig=2000;// i
	
	/**
	 * 输入极性
	 */
	public boolean inPolarity=false;//i
	
	/**
	 * 输入滤波上限
	 */
	public float inGmax=0.75f;//i
	/**
	 * 输入滤波下限
	 */
	public float inGmin=0.25f;//i
	
	
	/**
	 * 接收波特率
	 */
	int inBaudrate=1400;//o
	
	/**
	 * 录音进程
	 */
	ARThread ARthread=null;//i
	/**
	 * 用户handler 输入数据时发出
	 */
	public Handler clientHandler;//i
	//F_1khz代表二进制1，F_2khz代表二进制0
//	static short [] F_1khz = {1271,3577,2690,2512,3581,3741,4145,4522,4710,4824,4891,5012,4847,4632,4488,4203,3825,3305,2838,2297,1689,1102,549,-75,-742,-1277,-1882,-2524,-3061,-3555,-3914,-4156,-4537,-4704,-4846,-5038,-4841,-4670,-4597,-4322,-3934,-3616,-2993,-2531,-2143,-1386,-843};
//	static short [] F_2khz = {556,3309,5365,5370,5405,5702,5343,4756,3929,2690,1470,161,-1160,-2364,-3438,-4206,-4606,-4795,-4455,-3717,-3134,-1733,-692};
	
	
	
	/*
	 * 代表1的周期
	 * 单位 Hz
	 * */
	int T_1 = 1000;
	
	/*
	 * 代表0的周期
	 * 单位 Hz
	 * */
	int T_0 = 2000;
	private Vector<short[]> mSound;
	private int MINIMUM_BUFFER = 3;
	
	
	public synchronized void addSound(short[] sound, int nBytes){
		short[] data = new short[nBytes];
		for (int i = 0; i < nBytes; i++) {
			data[i] = sound[i];
		}
		this.mSound.add(data);
	}
	/**
	 * 构造函数
	 * @param sampleRate 采样频率
	 */
	public audioLSTter(int sampleRate)//io
	{	
		this.samplerate=sampleRate;
		sampleT=1/samplerate;
		this.mSound = new Vector<short[]>();
	}

	
	public void release(){
		unToSend();
		unToReceive();
	}
	
	/**
	 * 发送使能
	 * @param outBaudrate 输出波特率
	 * @param outDataSize 输出数据大小上限
	 * @param outPolart 输出极性
	 */	
	public void enToSend(int outBaudrate,byte outDataSize)//o
	{
		this.outSize=outDataSize;
		this.outBaudrate=outBaudrate;
		this.audiotrack=new AudioTrack(AudioManager.STREAM_MUSIC,
				this.samplerate,//44100,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				AudioTrack.getMinBufferSize(this.samplerate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT),//44100,
				AudioTrack.MODE_STREAM
				);
	}
	
	/**
	 * 发送失能
	 */
	public void unToSend()//o
	{
		if(this.audiotrack != null){
			
			this.audiotrack.release();
			this.audiotrack=null;
		}
	}
	
	/**
	 * 发送
	 * @param string 要发送的数据
	 */
	public void write(String string)//o
	{
		if(string.length()>this.outSize)//串行下不限制长度
			return;
		else
		{
			if(this.audiotrack!=null)
			{
				byte cmd = 5;
				byte []data = protocolAnalyse.encode_protocol(cmd, string.getBytes());
				short[] msg=encode(data);
				short [] msg1=new short[msg.length+1];
				for(int i=0;i<msg.length;i++)
				{
					msg1[i]=(short)msg[i];
				}
				msg1[msg.length]=0;
				this.audiotrack.setStereoVolume(1, 1);
				this.audiotrack.play();
				//最大返回2186
				int writenum = this.audiotrack.write(msg1, 0, msg1.length);
				

//				this.audiotrack.setLoopPoints(0, msg1.length, -1); 
				this.audiotrack.stop();
			}
		}
	}
	//关闭写音频数据
	public void audiotrackStop(){
		if(this.audiotrack!=null){
			if(this.audiotrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
				this.audiotrack.stop();
			}
		}
	}
	/**
	 * 编码
	 * @param bytes 需要编码的数据
	 * @return 编码后的序列
	 * 每个字节头加0尾加1，然后按二进制倒序排列
	 */
	private short[] encode(byte[] bytes)//o
	{
		int bitsnum=10*bytes.length+12;//n个byte
		//加上一个校验头，防止头数据丢失
		boolean[] HeadPackage = {false,false,true,true,true,true,true,true,true,true,true,true};
		boolean bits[]=new boolean[bitsnum];
		short[] F_2khz = protocolAnalyse.getSinData(T_0, this.samplerate, this.outAmplitude);
		short[] F_1khz = protocolAnalyse.getSinData(T_1, this.samplerate, this.outAmplitude);
		
		byte tmp=0;
		int k=0;
		for(int i = 0;i<HeadPackage.length;i++){
			bits[k++] = HeadPackage[i];
		}
		
		for(int i=0;i<bytes.length;i++)
		{
			tmp=bytes[i];
			bits[k++]=false;//0 lsb...msb 1
			for(int j=0;j<8;j++)
			{
				bits[k++]=((tmp&0x80)!=0)?true:false;
				tmp<<=1;
			}
			bits[k++]=true;
		}
		int length = 0;
		for(int i = 0;i<bits.length;i++){
			length += (bits[i])? F_1khz.length:F_2khz.length;
		}
		short[] sound = new short[length];
		int counter = 0; // removing the first block (carrier)
		
		for (int i = 0; i < bits.length ; i++) {
			if(bits[i]){
				for (int j = 0; j < F_1khz.length; j++) {
					sound[counter+j] = F_1khz[j];
				}
				counter += F_1khz.length;
			}else{
				for (int j = 0; j < F_2khz.length; j++) {
					sound[counter+j] = F_2khz[j];
				}
				counter += F_2khz.length;
			}
		}	
	
		return sound;
	}
	
	/**
	 * 接收使能
	 * @param inBaudrate 接收波特率
	 * @param inDataSize 接收数据上限
	 * @param inPolart 接收数据极性
	 */
	public void enToReceive(int inBaudrate,byte inDataSize)//i
	{
		this.inSize=inDataSize;
		this.inBaudrate=inBaudrate;
		inBuffer=new LinkedList<Byte>();
		int size=(int)((this.inSize+4)*samplerate*0.0075);//(this.maxDataSize+2)*10*samplerate*16*0.000045;

		this.ARthread=new ARThread(
				samplerate,
				size*2, //6000
				100,
				size    //3000
				);
		this.ARthread.Gtrig=this.inGtrig;
		//this.ARthread.setName("ARthread");
		this.ARthread.isloopruning=true;
		this.ARthread.start();
		//Thread.activeCount();
	}
	
	/**
	 * 接收失能
	 */
	public void unToReceive()//i
	{
		if(this.ARthread!=null)
		{
			this.ARthread.isloopruning=false;
			this.ARthread=null;
		}
	}
	private int findStartBit(boolean[] bits, int startIndex){
		// find carrier and start bit
		int index = startIndex;
		int highCounter = 0;		
		boolean startBitDetected = false;
		do {
			Boolean bit = bits[index];
			if(!bit){
				highCounter = 0;
			}else{
				if(highCounter >= 10){
					startBitDetected = true;
				}
				else{
					highCounter++;
				}
			}
			index++;
			if (index>=bits.length) return -1; 
		} while (!startBitDetected);
		return index;
	}
	/**
	 * LST解码
	 * @param audioData 音频数据
	 * @param size 音频数据尺寸
	 * @param Gmax 波形过滤上门限
	 * @param Gmin 波形过滤下门限
	 * @param offset 采值偏移
	 */
	private void LSTdecode(short[] audioData,int size,float Gmax,float Gmin,int offset)//i
	{
		byte[] D1=new byte[audioData.length];
		boolean[] D2=new boolean[audioData.length+offset];
		//short[] D3=new short[audioData.length];
		boolean[] bits=new boolean[audioData.length];
		short max=0,min=0,theGmax=0,theGmin=0,bn=0,TF=0,SP=0;
		byte D=0;
		int D_t = 0, j_for =0;
		int gate=0;
		//寻找头数据
		
		//找出数据中的最大值和最小值
		for(int i=0;i<audioData.length;i++)
		{
			if(audioData[i]>max)max=audioData[i];
			if(audioData[i]<min)min=audioData[i];
		}
		
		gate=max-min;//gate -> int
		theGmax=(short)(Gmax*gate+min);
		theGmin=(short)(Gmin*gate+min);
		
		TransToPeak(audioData,true, D1, D2, theGmax, theGmin);
		
		bn=0;
		//得出接收数据的二进制编码
		for(int i=0;i<audioData.length;i++)
		{
			if(D1[i]==1)
			{
				if(D2[i+offset]==true)
				{
					bits[bn]=true;
				}
				else
				{
					bits[bn]=false;
				}
				bn++;
			}
		}
		//寻找包头索引
		int index = findStartBit(bits,0);
		
		if(index == -1){
			Log.d("huwei", "没有找到包头");
			return;
		}
		TF=0;
		SP=0;
		D=0;
		D_t = 0;
		//把第一个数据的下标值给i
		int i=index-1;
		int k =0;
		while(i<bn)
		{
			abcbuffer[k++] = bits[i];
			switch(TF)
			{
			
			case 0:
				if(bits[i]==false)
				{
					TF=1;
				}
				break;
			case 1:
				D_t = 0;
				for(j_for =0;j_for < 8;j_for++){
					D_t = (D_t << 1 ) + ((bits[i+j_for]==true)?1:0);
				}
				D = (byte) D_t;
				if(bits[i+8]==true)SP=1;
				TF=0;
				if(SP==1)
				{
					SP=0;
					i+=8;
					synchronized(this.inBuffer)
	                {
	                	this.inBuffer.offer(D);//STR.add(D);
	                }
	                D=0;
				}
				else
				{
					D=0;
				}
				break;
			}
			i++;
		}
	}
	private void TransToPeak(short[] audioData,boolean h_inPolarity, byte[] D1, boolean[] D2,
			short theGmax, short theGmin) {
		short F;
		F=0;
		//通过上下限筛选，得出D1和D2，D1里面存着数值为1代表大于上限值，-1代表小于下限值，0表示处在上下线之间，
		if(h_inPolarity==true)
		{	
			for(int i=0;i<audioData.length;i++)
			{
					
				if(F==1&&audioData[i]>theGmax){
					F=-1;
					D1[i]=1;D2[i]=true;}
				else if(F==-1&&audioData[i]<theGmin){
					F=1;
					D1[i]=-1;D2[i]=false;}
				else if(F==0&&audioData[i]>theGmax){
					F=-1;
					D1[i]=1;D2[i]=true;}
				else if(F==0&&audioData[i]<theGmin){
					F=1;
					D1[i]=-1;D2[i]=false;}
				else{
					D1[i]=0;D2[i]=(F==-1)?true:false;}
			}
		}
		else
		{
			for(int i=0;i<audioData.length;i++)
			{
				if(F==1&&audioData[i]<theGmin){
					F=-1;
					D1[i]=1;D2[i]=true;}
				else if(F==-1&&audioData[i]>theGmax){
					F=1;
					D1[i]=-1;D2[i]=false;}
				else if(F==0&&audioData[i]<theGmin){
					F=-1;
					D1[i]=1;D2[i]=true;}
				else if(F==0&&audioData[i]>theGmax){
					F=1;
					D1[i]=-1;D2[i]=false;}
				else{
					D1[i]=0;D2[i]=(F==-1)?true:false;}
			}
		}
	}
	
	/**
	 * 解码
	 * @param audioData 音频数据
	 * @param size 音频数据尺寸
	 */
	private void decode(short[] audioData,int size)//i
	{
		LSTdecode(audioData,size,this.inGmax,this.inGmin,(int) (this.samplerate/this.T_0*0.75));
		Message message=this.clientHandler.obtainMessage(1000);
		this.clientHandler.sendMessage(message);
	}
	
	/**
	 * 返回队列中可用的数据数量
	 * @return 可用的数据个数
	 */
	public int available()//i
	{
		int rtn=0;
		synchronized(this.inBuffer)
        {
        	rtn=this.inBuffer.size();//STR.add(D);
        }
		return rtn;
	}
	
	/**
	 * 读取队列的一个字节
	 * @return 一个字节
	 */
	public byte read()//i
	{
		byte D=-1;
		synchronized(this.inBuffer)
        {
        	D=this.inBuffer.poll();//STR.add(D);
        }
		return D;
	}
	private void writeFile(short [] date ) throws IOException{
		FileWriter fw = new FileWriter("/sdcard/hello.txt");
		String []ddate = new String[date.length];
		for(int i =0;i<date.length;i++){
			ddate[i] = ""+date[i];
		}
		for(int i = 0;i<ddate.length;i++)
		{
			fw.write(ddate[i]+",");
		}	
			
		fw.flush();
		fw.close();
	}
	
	private short[] consumeSoundMessage(){
		int counter = 0;
		for (int i = 0; i < MINIMUM_BUFFER+1  ; i++) {
			counter += this.mSound.elementAt(i).length;
		}
		short[] sound = new short[counter];
		
		
		counter = 0; // removing the first block (carrier)
		for (int i = 0; i < MINIMUM_BUFFER+1 ; i++) {
			short[] s = this.mSound.elementAt(i);
			for (int j = 0; j < s.length; j++) {
				sound[counter+j] = s[j];
			}
			counter += s.length;
		}
		this.mSound.clear();
		return sound;
	}
	/**
	 * AudioRecord线程
	 * @author huwei
	 *
	 */
	public class ARThread extends Thread//i
	{
		public AudioRecord audioRecord;
		private int guardsize=100;
		private int readsize=100;
		private byte stateFlag=0;//0：空闲状态，1：接收状态
		short[] audioData=null; 
		private int sampleRate;//44100
		private int minBufferSize;//=44100;
		private int reBufferSize;
		public short Gtrig=1500;//触发电平 i
		private short [] shortdata = new short[guardsize];
		private boolean isloopruning=false;
		
		/**
		 * 建立内部audioRecord的bufferSizeInBytes的值应按照bufferSizeInBytes属性进行设置
		 * @param sampleRate 采样频率
		 * @param minBufferSize 最小缓存尺寸（按byte计）
		 * @param guardSize 监控数据时的监控数据的大小，应小于minBufferSize
		 * @param readSize 数据读取时的值的大小，应小于minBufferSize
		 */
		ARThread(int sampleRate,int minBufferSize,int guardSize,int readSize)
		{
			this.sampleRate=sampleRate;
			this.guardsize=guardSize;
			this.readsize=readSize;
			audioData = new short[readSize];
			//this.minBufferSize = minBufferSize;
			
			this.minBufferSize=AudioRecord.getMinBufferSize(this.sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
			
			if(this.reBufferSize<this.minBufferSize)this.reBufferSize=this.minBufferSize;
			if(this.reBufferSize<minBufferSize)this.reBufferSize=minBufferSize;
			
			try
			{
				this.audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC,
					this.sampleRate,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT,
					this.reBufferSize
				);
				//需要在Manifest添加权限<application>的外边<uses-permission android:name="android.permission.RECORD_AUDIO" />
				this.audioRecord.startRecording();
			}
			catch(Exception e)
			{
				String str=e.getMessage();
			}
		}

		public void run()
		{
			while(isloopruning)
			{
				if(stateFlag==0)
				{
					this.audioRecord.read(audioData, 0, guardsize);
					for (int i = 1; i < guardsize; i++) {
						if(audioData[i]>this.Gtrig){//5000
							stateFlag=1;
							System.arraycopy(audioData, 0, shortdata, 0, guardsize);
							break;
						}
					}
				}
				else
				{
					
					addSound(shortdata,guardsize);
					for(int i =0 ;i<MINIMUM_BUFFER;i++){
						
						int readnum = this.audioRecord.read(audioData, 0, readsize);
						addSound(audioData,readnum);
					}
					short[]LaudioData =  consumeSoundMessage();
					decode(LaudioData,LaudioData.length);
					stateFlag=0;
				}
			}
			this.audioRecord.release();
		}
	}

}
