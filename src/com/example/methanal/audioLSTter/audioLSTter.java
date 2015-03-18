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
 *  android audio��Ƶͨ����
 * @author huwei
 *
 */
public class audioLSTter implements TransToPeak
{
	/**
	 * ����Ƶ��
	 */
	public int samplerate=44100;//io
	/**
	 * ��������
	 */
	private float sampleT=1/44100;//io
	public static boolean[] abcbuffer=new boolean[1000];
	/**
	 * ������ݵ����ߴ�
	 */
	byte outSize = 100;//o
	
	/**
	 * �����ֵ
	 */
	public short outAmplitude = 3000;//o
	/**
	 * ���Ͳ�����
	 */
	int outBaudrate=9600;//o
	/**
	 * �������
	 */
	public boolean outPolarity=false;//o
	
	/**
	 * ������
	 */
	private AudioTrack audiotrack=null;//o
	
	
	/**
	 * ���ն���
	 */
	Queue<Byte> inBuffer;//=new LinkedList<Byte>();//i
	
	/**
	 * �������ݵ����ߴ�
	 */
	byte inSize = 10;//i
	
	/**
	 * ������ƽ
	 */
	public short inGtrig=2000;// i
	
	/**
	 * ���뼫��
	 */
	public boolean inPolarity=false;//i
	
	/**
	 * �����˲�����
	 */
	public float inGmax=0.75f;//i
	/**
	 * �����˲�����
	 */
	public float inGmin=0.25f;//i
	
	
	/**
	 * ���ղ�����
	 */
	int inBaudrate=1400;//o
	
	/**
	 * ¼������
	 */
	ARThread ARthread=null;//i
	/**
	 * �û�handler ��������ʱ����
	 */
	public Handler clientHandler;//i
	//F_1khz���������1��F_2khz���������0
//	static short [] F_1khz = {1271,3577,2690,2512,3581,3741,4145,4522,4710,4824,4891,5012,4847,4632,4488,4203,3825,3305,2838,2297,1689,1102,549,-75,-742,-1277,-1882,-2524,-3061,-3555,-3914,-4156,-4537,-4704,-4846,-5038,-4841,-4670,-4597,-4322,-3934,-3616,-2993,-2531,-2143,-1386,-843};
//	static short [] F_2khz = {556,3309,5365,5370,5405,5702,5343,4756,3929,2690,1470,161,-1160,-2364,-3438,-4206,-4606,-4795,-4455,-3717,-3134,-1733,-692};
	
	
	
	/*
	 * ����1������
	 * ��λ Hz
	 * */
	int T_1 = 1000;
	
	/*
	 * ����0������
	 * ��λ Hz
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
	 * ���캯��
	 * @param sampleRate ����Ƶ��
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
	 * ����ʹ��
	 * @param outBaudrate ���������
	 * @param outDataSize ������ݴ�С����
	 * @param outPolart �������
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
	 * ����ʧ��
	 */
	public void unToSend()//o
	{
		if(this.audiotrack != null){
			
			this.audiotrack.release();
			this.audiotrack=null;
		}
	}
	
	/**
	 * ����
	 * @param string Ҫ���͵�����
	 */
	public void write(String string)//o
	{
		if(string.length()>this.outSize)//�����²����Ƴ���
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
				//��󷵻�2186
				int writenum = this.audiotrack.write(msg1, 0, msg1.length);
				

//				this.audiotrack.setLoopPoints(0, msg1.length, -1); 
				this.audiotrack.stop();
			}
		}
	}
	//�ر�д��Ƶ����
	public void audiotrackStop(){
		if(this.audiotrack!=null){
			if(this.audiotrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
				this.audiotrack.stop();
			}
		}
	}
	/**
	 * ����
	 * @param bytes ��Ҫ���������
	 * @return ����������
	 * ÿ���ֽ�ͷ��0β��1��Ȼ�󰴶����Ƶ�������
	 */
	private short[] encode(byte[] bytes)//o
	{
		int bitsnum=10*bytes.length+12;//n��byte
		//����һ��У��ͷ����ֹͷ���ݶ�ʧ
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
	 * ����ʹ��
	 * @param inBaudrate ���ղ�����
	 * @param inDataSize ������������
	 * @param inPolart �������ݼ���
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
	 * ����ʧ��
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
	 * LST����
	 * @param audioData ��Ƶ����
	 * @param size ��Ƶ���ݳߴ�
	 * @param Gmax ���ι���������
	 * @param Gmin ���ι���������
	 * @param offset ��ֵƫ��
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
		//Ѱ��ͷ����
		
		//�ҳ������е����ֵ����Сֵ
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
		//�ó��������ݵĶ����Ʊ���
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
		//Ѱ�Ұ�ͷ����
		int index = findStartBit(bits,0);
		
		if(index == -1){
			Log.d("huwei", "û���ҵ���ͷ");
			return;
		}
		TF=0;
		SP=0;
		D=0;
		D_t = 0;
		//�ѵ�һ�����ݵ��±�ֵ��i
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
		//ͨ��������ɸѡ���ó�D1��D2��D1���������ֵΪ1�����������ֵ��-1����С������ֵ��0��ʾ����������֮�䣬
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
	 * ����
	 * @param audioData ��Ƶ����
	 * @param size ��Ƶ���ݳߴ�
	 */
	private void decode(short[] audioData,int size)//i
	{
		LSTdecode(audioData,size,this.inGmax,this.inGmin,(int) (this.samplerate/this.T_0*0.75));
		Message message=this.clientHandler.obtainMessage(1000);
		this.clientHandler.sendMessage(message);
	}
	
	/**
	 * ���ض����п��õ���������
	 * @return ���õ����ݸ���
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
	 * ��ȡ���е�һ���ֽ�
	 * @return һ���ֽ�
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
	 * AudioRecord�߳�
	 * @author huwei
	 *
	 */
	public class ARThread extends Thread//i
	{
		public AudioRecord audioRecord;
		private int guardsize=100;
		private int readsize=100;
		private byte stateFlag=0;//0������״̬��1������״̬
		short[] audioData=null; 
		private int sampleRate;//44100
		private int minBufferSize;//=44100;
		private int reBufferSize;
		public short Gtrig=1500;//������ƽ i
		private short [] shortdata = new short[guardsize];
		private boolean isloopruning=false;
		
		/**
		 * �����ڲ�audioRecord��bufferSizeInBytes��ֵӦ����bufferSizeInBytes���Խ�������
		 * @param sampleRate ����Ƶ��
		 * @param minBufferSize ��С����ߴ磨��byte�ƣ�
		 * @param guardSize �������ʱ�ļ�����ݵĴ�С��ӦС��minBufferSize
		 * @param readSize ���ݶ�ȡʱ��ֵ�Ĵ�С��ӦС��minBufferSize
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
				//��Ҫ��Manifest���Ȩ��<application>�����<uses-permission android:name="android.permission.RECORD_AUDIO" />
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
