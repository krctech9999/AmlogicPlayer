/*
 */

package com.example.amlogicplayer;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Base32 - encodes and decodes RFC3548 Base32
 * (see http://www.faqs.org/rfcs/rfc3548.html )
 *
 * @author Robert Kaye
 * @author Gordon Mohr
 */
public class novaToken {
    private static String TAG="novaToken";
    protected   static   char  hexDigits[] = {  '0' ,  '1' ,  '2' ,  '3' ,  '4' ,  '5' ,  '6' ,
            '7' ,  '8' ,  '9' ,  'a' ,  'b' ,  'c' ,  'd' ,  'e' ,  'f'  };

    private static void data_dump(String hint, byte[] data){
        String output="";

        for(byte d:data){
            output = String.format("%s 0x%02x",output, d);
        }
        Log.d(TAG, hint+output);
    }

    private static byte[] htonll(long timestamp){
        byte[] t=new byte[8];

        t[7] = (byte) (timestamp&0xff);
        t[6] = (byte) ((timestamp>>8)&0xff);
        t[5] = (byte) ((timestamp>>16)&0xff);
        t[4] = (byte) ((timestamp>>24)&0xff);
        return t;
    }

    public   static  byte[] getMD5String( byte [] bytes) {
        MessageDigest messagedigest = null;
        try {
            messagedigest = MessageDigest.getInstance("MD5" );
            messagedigest.update(bytes);
            byte[] result = messagedigest.digest();
            return  result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] AESEncrypt(byte[] key, byte[] data, byte[] iv) throws Exception {

        byte[] buffer=null;
        if( (data.length%16)!=0 ){
            buffer = new byte[((data.length/16)+1)*16];
        }else{
            buffer = new byte[data.length];
        }

        System.arraycopy(data,0, buffer,0,data.length);

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec,new IvParameterSpec(iv));

        byte[] up = cipher.update(data);
        byte[] result = cipher.doFinal();

        byte[] empty = new byte[up.length+result.length];
        System.arraycopy(up,0, empty,0,up.length);
        System.arraycopy(result,0, empty,up.length,result.length);
        return empty;
    }

    public static String getToken( long timestamp, String ipAddr){
        byte[] expiretime = htonll(timestamp);
        byte[] inputData = new byte[ipAddr.length()+expiretime.length];

        System.arraycopy(ipAddr.getBytes(), 0, inputData,0,ipAddr.length());
        System.arraycopy(expiretime,0,inputData,ipAddr.length(),expiretime.length);

        data_dump("Before MD5", inputData);

        byte[] ipMd5 = getMD5String(inputData);

        data_dump("After MD5",ipMd5);

        byte[] resultAes = new byte[0];
        try {
            resultAes = AESEncrypt("FEED5D47C860F422712AC902A89865DB".getBytes(), inputData, "B4D1514F22CF3C43".getBytes() );
        } catch (Exception e) {
            e.printStackTrace();
        }

        data_dump("after aes256", resultAes);

        byte[] Buffer = new byte[ipMd5.length+resultAes.length];
        System.arraycopy(ipMd5, 0, Buffer, 0, ipMd5.length);
        System.arraycopy(resultAes,0,Buffer,ipMd5.length, resultAes.length);

        String base64 = Base64.encodeToString(Buffer, Base64.DEFAULT);

        Log.d(TAG, "after base64="+base64);
        return base64;
    }
}