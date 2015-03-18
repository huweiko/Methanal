package com.example.methanal.audioLSTter;

import android.util.Log;

public class protocolAnalyse{
	private static String TAG = "protocolAnalyse";
//	���룬����Э��ͷ�ļ�������λ
	public static byte[] encode_protocol(byte cmd ,byte [] buffer){
		int i =0;
		byte[] data = new byte[buffer.length+4];
		data[0] = 0x5A;  //Э��ͷ
		data[1] = (byte) buffer.length;//�����buffer����
		data[2] = cmd;    //����
		for(i =0;i < buffer.length;i++){
			data[3+i] = buffer[i];
		}
		int dataLength = i+3;
		int CRC = 0;
		for(i = 0;i<dataLength;i++){
			CRC += data[i];
		}
		data[i] = (byte) (CRC%127);
		return data;
	}
	//���룬ȥ��Э��ͷ��У��λ����������
	public static int decode_protocal(byte [] buffer,byte[] data){
		int result = -1;
		if((buffer[0] != 0x5A) || (buffer.length < 4)){
			Log.e(TAG, "��ͷ����");
			return -1;
		}
		int CRC = 0;
		int dataLength = buffer[1];
		Log.e(TAG, new String(buffer));
		Log.e(TAG, "dataLength = "+ dataLength + ";buffer.length = "+buffer.length);
		if(dataLength+4 > buffer.length){
			Log.e(TAG, "���ݲ�����");
			return -1;
		}
		for(int i = 0;i<dataLength+3;i++){
			CRC += buffer[i];
		}
		if((CRC%127) == buffer[dataLength+3]){
			System.arraycopy(buffer, 3, data, 0, buffer[1]);
			result = buffer[2];
		}else{
			Log.e(TAG, "����У�����");
			return -1;
		}
		Log.e(TAG, "�����ɹ�");
		return result;
	}
	/*
	 * ��ȡ���Ҳ���������
	 * frequency ����
	 * samplerate ������
	 * outAmplitude ���ҷ�ֵ
	 * ����ֵ ���õ�����������
	 * */
	public static short[] getSinData(int frequency,int samplerate,int outAmplitude ){
		if(frequency == 0){
			Log.e(TAG, "���ڲ���Ϊ��");
			return null;
		}
		int pointNum = samplerate/frequency;
        short[] data = new short[pointNum];
		for(int x=0;x<pointNum;x++){
        	data[x]=(short)(Math.sin(2*x*Math.PI/pointNum + Math.PI)*outAmplitude);
        }  
		return data;
	}
}