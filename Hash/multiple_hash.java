import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class GherghisanRobert {
	public static void main(String[] args) throws NoSuchAlgorithmException {
		long tstart = System.currentTimeMillis();
		try {
		File ignis = new File("ignis-10M.txt");
		Scanner Scanner = new Scanner(ignis);
		String ismsap="ismsap";
		String pass="fb6c6ebe761e4e79f07c715b380826544117b7a68e66af639313401e56271028";
		MessageDigest md = MessageDigest.getInstance("MD5");
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		System.out.println("Searching...");
		int flag=0;
		
	      while (Scanner.hasNextLine())
	      {
	    	  
	        String weak_pass = Scanner.nextLine();
	        String original_weak_pass=weak_pass;
	        weak_pass=ismsap+weak_pass;
	        byte[] messageDigest = md.digest(weak_pass.getBytes());
            byte[] sha = sha256.digest(messageDigest);
            StringBuilder hexString = new StringBuilder(); 
            for (byte b : sha) { 
            	hexString.append(String.format("%02x", b));
            } 
            String S = hexString.toString();
            
            
            if (S.equals(pass))
            {
            	System.out.println("Success!");
            	System.out.println("Password after being hashed with MD5 then SHA256: "+ (S));
            	System.out.println("Password: "+(original_weak_pass));
            	flag=1;
            	break;
            }
	       
	      }
	      if(flag==0)
	      System.out.println("Password was not found...");
	      Scanner.close();
		}catch (FileNotFoundException e){
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		long tfinal = System.currentTimeMillis();
		System.out.println("Duration is : " + (tfinal-tstart));
		
	}
}
