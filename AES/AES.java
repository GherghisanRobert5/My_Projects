import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Scanner;


public class GherghisanRobert {
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, CertificateException {
		
            // Create random 128 bit key 
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            //
            File response=new File("response.txt");
            Scanner Scanner = new Scanner(response);
            String originalText = Scanner.nextLine();
            
            //Encryption
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            System.out.println("Original Text: "+originalText);
            byte[]encryptedText=cipher.doFinal(originalText.getBytes());
            StringBuilder hexEncryptedText = new StringBuilder();
            for (byte b : encryptedText) {
            	hexEncryptedText.append(String.format("%02X ", b));
            }
            System.out.println("Encrypted Text: "+hexEncryptedText.toString());
            
            //Decryption
            Cipher decipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[]decryptedText=decipher.doFinal(encryptedText);
            String check_decryption=new String (decryptedText, StandardCharsets.UTF_8);
            System.out.println("Decrypted Text: "+check_decryption);
            
            //Write the encrypted response in file
            FileOutputStream fileOutputStream = new FileOutputStream("response.enc");
            fileOutputStream.write(encryptedText);
            
            //Show random key
            byte[] keyBytes = secretKey.getEncoded();
            StringBuilder AESKey = new StringBuilder();
            
            for (byte b : keyBytes) {
            	AESKey.append(String.format("%02X ", b));
            }
            System.out.println("Generated AES Key: " + AESKey);
            
            //Encrypting the AES key
            String certPath = "C:\\Users\\ser1\\eclipse-workspace\\GherghisanRobert_Assignment3\\SimplePGP_ISM.cer";
            FileInputStream FileInputStream = new FileInputStream(certPath);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(FileInputStream);
            FileInputStream.close();

            PublicKey RSAPublicKey = x509Certificate.getPublicKey();
            
            Cipher RSAcipher = Cipher.getInstance("RSA");
    		RSAcipher.init(Cipher.ENCRYPT_MODE, RSAPublicKey);
    		byte[]AES_EncryptedKey=RSAcipher.doFinal(keyBytes);
    		StringBuilder hex_AES_EncryptedKey = new StringBuilder();
            
            for (byte b : AES_EncryptedKey) {
            	hex_AES_EncryptedKey.append(String.format("%02X ", b));
            }
            System.out.println("AES Key after RSA encryption: " + hex_AES_EncryptedKey);
            FileOutputStream fileOutputStream1 = new FileOutputStream("aes_key.sec");
            fileOutputStream1.write(AES_EncryptedKey);  
            
	}
}
