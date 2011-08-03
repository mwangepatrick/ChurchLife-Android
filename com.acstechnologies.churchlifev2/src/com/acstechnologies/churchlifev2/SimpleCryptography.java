package com.acstechnologies.churchlifev2;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;

import android.util.Base64;

/**
 * Usage:
 * <pre>
 * String crypto = SimpleCrypto.encrypt(masterpassword, cleartext)
 * ...
 * String cleartext = SimpleCrypto.decrypt(masterpassword, crypto)
 * </pre>
 * @author ferenc.hechler
 * mas - modified to use Base64 encoding
 */
public class SimpleCryptography {
	
    	protected static final String UTF8 = "utf-8";
    		
        public static String encrypt(String seed, String cleartext) throws Exception {
                byte[] rawKey = getRawKey(seed.getBytes());
                byte[] result = encrypt(rawKey, cleartext.getBytes());
                //return toHex(result);							' Hex
                return toBase64(result);
        }
        
        public static String decrypt(String seed, String encrypted) throws Exception {
                byte[] rawKey = getRawKey(seed.getBytes());
                //byte[] enc = toHexByte(encrypted);				' Hex
                byte[] enc = toBase64Byte(encrypted);                               
                byte[] result = decrypt(rawKey, enc);
                
                return new String(result);
        }

        private static byte[] getRawKey(byte[] seed) throws Exception {
                KeyGenerator kgen = KeyGenerator.getInstance("AES");
                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                sr.setSeed(seed);
            kgen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();
            return raw;
        }

        
        private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(clear);
                return encrypted;
        }

        private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decrypted = cipher.doFinal(encrypted);
                return decrypted;
        }

        public static byte[] toBase64Byte(String base64String){
        	byte[] result = Base64.decode(base64String,  Base64.NO_WRAP);
        	return result;
        }
        
        public static String toBase64(byte[] buf) throws AppException{
        	try {
				return new String(Base64.encode(buf, Base64.NO_WRAP),UTF8);
			} catch (UnsupportedEncodingException e) {
				AppException exception = new AppException();

				ExceptionInfo info = exception.addInfo();
			    info.setCause(e);
			    info.setErrorId("toBase64");
			    info.setContextId("SimpleCryptography");

			    info.setErrorType(ExceptionInfo.TYPE_ERROR.UNEXPECTED);
			    info.setSeverity(ExceptionInfo.SEVERITY_ERROR.CRITICAL);

			    info.setErrorDescription("Error converting Base64 byte array to a string.");

			    throw exception;				    							
			}        	
        }
        
        
        //not currently used - hex
        public static String toHex(String txt) {
                return toHex(txt.getBytes());
        }
        //not currently used - hex
        public static String fromHex(String hex) {
                return new String(toHexByte(hex));
        }
        //not currently used - hex
        public static byte[] toHexByte(String hexString) {
                int len = hexString.length()/2;
                byte[] result = new byte[len];
                for (int i = 0; i < len; i++)
                        result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
                return result;
        }

        // not currently used - Hex
        public static String toHex(byte[] buf) {
                if (buf == null)
                        return "";
                StringBuffer result = new StringBuffer(2*buf.length);
                for (int i = 0; i < buf.length; i++) {
                        appendHex(result, buf[i]);
                }
                return result.toString();
        }
        private final static String HEX = "0123456789ABCDEF";
        private static void appendHex(StringBuffer sb, byte b) {
                sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
        }
                
}