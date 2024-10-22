import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.classfile.Signature;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

public class exam {
	public static String base64ToHex(String base64) {
        // Decode the Base64 string into a byte array
        byte[] bytes = Base64.getDecoder().decode(base64);
        
        // Convert each byte to its hexadecimal representation
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // Convert byte to hex and ensure it is two digits
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        
        return hexString.toString();
    }



	    // Method to compute the SHA-256 hash of a file
	    public static String computeSHA256(File file) throws IOException, NoSuchAlgorithmException {
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        FileInputStream fis = new FileInputStream(file);
	        byte[] byteArray = new byte[1024];
	        int bytesCount = 0;

	        while ((bytesCount = fis.read(byteArray)) != -1) {
	            digest.update(byteArray, 0, bytesCount);
	        }
	        fis.close();

	        byte[] bytes = digest.digest();
	        StringBuilder sb = new StringBuilder();
	        for (byte b : bytes) {
	            sb.append(String.format("%02x", b));
	        }
	        return sb.toString();
	    }

	    // Method to verify .user files against the given SHA-256 hash
	    public static void verifyUserFiles(String directoryPath, String expectedHash) {
	    	AtomicInteger index= new AtomicInteger(0);
	        try {
	            Files.walk(Paths.get(directoryPath))
	                .filter(Files::isRegularFile)
	                .filter(path -> path.toString().endsWith(".user"))
	                .forEach(path -> {
	                	int index1 = index.incrementAndGet();
	                	String filename= path.getFileName().toString(); 
	                    try {
	                        String fileHash = computeSHA256(path.toFile());
	                        if (fileHash.equalsIgnoreCase(expectedHash)) {
	                            System.out.println(index1+". File " + path + " matches the expected hash.");
	                            throw new MatchException("Found matching file: "+filename, null);
	                            
	                        } else {
	                            System.out.println(index1+". File " + path + " does not match the expected hash.");
	                        }
	                       
	                    } catch (IOException | NoSuchAlgorithmException e) {
	                        e.printStackTrace();
	                    }
	                   
	                });
	            
	            
	            
	        }
	        catch (MatchException e) {
	            System.out.println(e.getMessage());
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	       
	    }
	    public static void decrypt(String filename,String cipherFilename, String alg, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException
	    {
	    	File inputFile = new File(filename);
			if(!inputFile.exists()) {
				throw new UnsupportedOperationException("Missing file");
			}
			File cipherFile = new File(cipherFilename);
			if(!cipherFile.exists()) {
				cipherFile.createNewFile();
			}
			
			FileInputStream fis = new FileInputStream(inputFile);
			FileOutputStream fos = new FileOutputStream(cipherFile);
	    	//byte[] encryptedData = Files.readAllBytes(inputFile.toPath());
	    	Cipher cipher = Cipher.getInstance(alg + "/CBC/NoPadding");
	    	byte[] IV={ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	    	//sau byte[] IV = new byte [16];
	    	//IV[8]=(byte) 0xFF;
	    	SecretKeySpec key = new SecretKeySpec(password.getBytes(), alg);
			IvParameterSpec ivSpec = new IvParameterSpec(IV);
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
			
			byte[] buffer = new byte[cipher.getBlockSize()];
			int noBytes = 0;
			
			while(true) {
				noBytes = fis.read(buffer);
				if(noBytes == -1) {
					break;
				}
				byte[] cipherBlock = cipher.update(buffer, 0, noBytes);
				fos.write(cipherBlock);
			}
			byte[] lastBlock = cipher.doFinal();
			fos.write(lastBlock);
			
			fis.close();
			fos.close();
	    }
	    public static byte[] getPBKDF(String userPassword, String algorithm,String salt,int noIterations) throws NoSuchAlgorithmException, InvalidKeySpecException {
			
			SecretKeyFactory pbkdf = 
					SecretKeyFactory.getInstance(algorithm);
			PBEKeySpec pbkdfSpecifications = 
					new PBEKeySpec(
							userPassword.toCharArray(), 
							salt.getBytes(), 
							noIterations,160);
			SecretKey secretKey = pbkdf.generateSecret(pbkdfSpecifications);
			return secretKey.getEncoded();
			
		}
	    	public static String getHexString(byte[] value) {
	    		StringBuilder result = new StringBuilder();
	    		for(byte b : value) {
	    			result.append(String.format("%02X ", b));
	    		}
	    		return result.toString();
	    	}
	    

	    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidKeySpecException, SignatureException, CertificateException, UnrecoverableKeyException {
	    	
	    	String base64 = "GGltfOPsbry4JagJWTcE4ryViWfHrcegWAr2/osZbPA="; // Example Base64 string
	        String hex = base64ToHex(base64);
	        System.out.println("Hexadecimal representation: " + hex);
	        String directoryPath = "C:\\Users\\blued\\Downloads\\ISM_Exam_C_Jan26\\ISM_Exam_Java_Jan26\\users";
	        verifyUserFiles(directoryPath, hex);
	        decrypt("User225QSM.user","cipher.txt","AES","userfilepass@5@7");
	        File decryptFile = new File("cipher.txt");
	        BufferedReader br= new BufferedReader(new FileReader(decryptFile));
	        String st;
	        st = br.readLine();
	        System.out.println(st);
	        byte[] saltedHash = getPBKDF(st, "PBKDF2WithHmacSHA1", "ism2021", 150);
			System.out.println("Salted hash of the password: ");
			System.out.println(getHexString(saltedHash));
			String filePath = "C:\\Users\\blued\\eclipse-workspace\\SAP_pregatire\\bin.bin";
			 try (FileOutputStream fos = new FileOutputStream(filePath)) {
		            fos.write(saltedHash);
		            System.out.println("Data has been written to " + filePath);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			 byte[] checkHash=new byte [20];
			 try (FileInputStream fos = new FileInputStream(filePath)) {
		            fos.read(checkHash);
		            
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			 System.out.println(getHexString(checkHash));
	    
	    /*
	     * keytool.exe -genkey -keyalg RSA -alias ismkey1 -keypass passism1 -storepass passks -keystore ismkeystore.ks -dname "cn=ISM, ou=ISM, o=IT&C Security Master, c=RO"

		keypass: ismjavacert1

		keytool.exe -export -alias ismkey1 -file ISMCertificateX509.cer -keystore ismkeystore.ks -storepass passks
	     */
	    
	    try {
            // Load the keystore
            FileInputStream is = new FileInputStream("ismkeystore.ks");
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, "passks".toCharArray());

            // Get the private key
            Key key = keystore.getKey("ismkey1", "passks".toCharArray());
            if (key instanceof PrivateKey) {
                PrivateKey privateKey = (PrivateKey) key;

                // Load the binary file
                FileInputStream fis = new FileInputStream("bin.bin");
                byte[] fileBytes = fis.readAllBytes();
                fis.close();

                // Sign the binary file
                java.security.Signature signature = java.security.Signature.getInstance("SHA256WithRSA");
                signature.initSign(privateKey);
                signature.update(fileBytes);
                byte[] digitalSignature = signature.sign();

                // Save the signature to a file
                FileOutputStream fos = new FileOutputStream("signature.sig");
                fos.write(digitalSignature);
                fos.close();

                System.out.println("Binary file signed successfully.");
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | SignatureException| IOException e) {
	    e.printStackTrace();
        }
}
}


