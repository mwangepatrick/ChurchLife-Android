package com.acstech.churchlife;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

import android.util.Base64;

/**
 * Usage:
 * <pre>
 * String crypto = SimpleCrypto.encrypt(masterpassword, cleartext)
 * ...
 * String cleartext = SimpleCrypto.decrypt(masterpassword, crypto)
 * </pre>
 * @author ferenc.hechler
 * softwarearchitect - modified to use Base64 encoding
 */
public class SimpleCryptography {
	
    	protected static final String UTF8 = "utf-8";
    		
        public static String encrypt(String seed, String cleartext) throws AppException {
                byte[] rawKey = getRawKey(seed.getBytes());
                byte[] result = encrypt(rawKey, cleartext.getBytes());
                //return toHex(result);							' Hex
                return toBase64(result);
        }
        
        public static String decrypt(String seed, String encrypted) throws AppException {
                byte[] rawKey = getRawKey(seed.getBytes());
                //byte[] enc = toHexByte(encrypted);			' Hex
                byte[] enc = toBase64Byte(encrypted);                               
                byte[] result = decrypt(rawKey, enc);
                
                return new String(result);
        }

        private static byte[] getRawKey(byte[] seed) throws AppException {
        	
        	KeyGenerator kgen;
            SecureRandom sr;
            byte[] raw = null;
               
			try {
				kgen = KeyGenerator.getInstance("AES");
				sr = SecureRandom.getInstance("SHA1PRNG");
				sr.setSeed(seed);
				kgen.init(128, sr); // 192 and 256 bits may not be available
				SecretKey skey = kgen.generateKey();
				raw = skey.getEncoded();									
			} catch (NoSuchAlgorithmException e) {
				throw AppException.AppExceptionFactory(e,
						ExceptionInfo.TYPE.UNEXPECTED,
						ExceptionInfo.SEVERITY.CRITICAL, 
						"100", "SimpleCryptography.getRawKey",
						e.getMessage());		
			}    
            return raw;
        }

        
        private static byte[] encrypt(byte[] raw, byte[] clear) throws AppException {
        	byte[] encrypted = null;
        	try
        	{
        		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            	Cipher cipher = Cipher.getInstance("AES");
            	cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            	encrypted = cipher.doFinal(clear);            	
        	}
        	// Since we are doing the EXACT same thing each time...sink the Exception
        	//  (normally not recommended)
        	catch (Exception e) {
        		throw AppException.AppExceptionFactory(e,
						ExceptionInfo.TYPE.UNEXPECTED,
						ExceptionInfo.SEVERITY.MODERATE, 
						"100", "SimpleCryptography.encrypt",
						e.getMessage());
        	}      
        	
        	/*
        	 * this did not work catch (NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException e)
        	        	
        	catch (NoSuchAlgorithmException e) {
        		
        	}
            catch (NoSuchPaddingException e) {
            	
            }
        	catch (BadPaddingException e)  {
            	
            }
        	catch (InvalidKeyException e) {
        		
        	}
        	catch (IllegalBlockSizeException e) {
        		
        	}
        	*/
        	return encrypted;
        }

        private static byte[] decrypt(byte[] raw, byte[] encrypted) throws AppException {
        	byte[] decrypted = null;
        	try
        	{
        		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        		Cipher cipher = Cipher.getInstance("AES");
        		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        		decrypted = cipher.doFinal(encrypted);
        	}
        	// Since we are doing the EXACT same thing each time...sink the Exception
        	//  (normally not recommended)
        	catch (Exception e) {
        		throw AppException.AppExceptionFactory(e,
						ExceptionInfo.TYPE.UNEXPECTED,
						ExceptionInfo.SEVERITY.MODERATE, 
						"100", "SimpleCryptography.decrypt",
						e.getMessage());
        	} 
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
				throw AppException.AppExceptionFactory(e,
						ExceptionInfo.TYPE.UNEXPECTED,
						ExceptionInfo.SEVERITY.CRITICAL, 
						"100", "SimpleCryptography.toBase64",
						"Error converting Base64 byte array to a string.");												    						
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