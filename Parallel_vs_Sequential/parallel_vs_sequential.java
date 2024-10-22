import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class parallel {
	
	public static void sequential () throws FileNotFoundException, NoSuchAlgorithmException
	{
		long tstart = System.currentTimeMillis();
		File file=new File("1mil.txt");
		Scanner scanner = new Scanner(file);
		String prefix="parallel";
		String pass="00c3cc7c9d684decd98721deaa0729d73faa9d9b";
		String pass1="d537227e87426ca4dbd985add081fa90db980c59";
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		int flag=0,index=0;
		try {
			while(scanner.hasNextLine())
			{
				String current_line = scanner.nextLine();
		        String original_line=current_line;
		        current_line=prefix+current_line;
		        byte[] hash = sha1.digest(current_line.getBytes());
		        
	            StringBuilder hexString = new StringBuilder(); 
	            for (byte b : hash) { 
	            	hexString.append(String.format("%02x", b));
	            } 
	            String S = hexString.toString();
	            
	            byte[]B=sha1.digest(S.getBytes());
	            hexString = new StringBuilder(); 
	            for (byte b : B) { 
	            	hexString.append(String.format("%02x", b));
	            } 
	            //System.out.println(hexString.toString());
	            
	            index++;
	            if (hexString.toString().equals(pass))
	            {
	            	System.out.println("Success!");
	            	System.out.println("Password: "+(original_line));
	            	System.out.println("Password's Index:"+(index-1));
	            	flag=1;
	            	break;
	            }
	           
	  		      
	  		      
			}
			 if(flag==0)
	  		      System.out.println("Password was not found...");
			scanner.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long tfinal = System.currentTimeMillis();
		System.out.println("Sequential duration: " + (tfinal-tstart)+" milliseconds");
	}
	
	public static void parallel() throws FileNotFoundException, NoSuchAlgorithmException, InterruptedException {
        long time_start = System.currentTimeMillis();
        final AtomicBoolean flag = new AtomicBoolean(false);
        //int coreCount = Runtime.getRuntime().availableProcessors();
        int coreCount =5;
        String PREFIX="parallel";
		String pass="00c3cc7c9d684decd98721deaa0729d73faa9d9b";
        Thread[] threads = new Thread[coreCount];
        File file = new File("1mil.txt");
        long segm = 200000;
        int i;
        int threadCounter = 0;
        
        for (i = 0; i < coreCount; i++) {
        	final int currentThreadId = threadCounter++;
        	
            threads[i] = new Thread(() -> {
                try {
                    Scanner thread_scanner = new Scanner(file);
                    
                    long segm_start=currentThreadId*segm;
                    long segm_end=(currentThreadId+1)*segm;
                    //System.out.println("start: "+start+" ||| end: "+end+" ||| id: "+currentThreadId);
                    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                    
                    if(currentThreadId!=0)
                    {
                    	for (long j=0;j<segm_start;j++)
                    	{
                    		if(thread_scanner.hasNextLine())
                    		{
                    		String pass_current = thread_scanner.nextLine();
                    		}
                    	}
                    }
                    
                    while (thread_scanner.hasNextLine() && !flag.get() && segm_start<segm_end) {
                        String pass_current = thread_scanner.nextLine();
                        String pass_modified = PREFIX + pass_current;
                        byte[] first_sha1 = sha1.digest(pass_modified.getBytes());
                        String first_sha1_hex = bytesToHex(first_sha1);
                        byte[] second_sha1 = sha1.digest(first_sha1_hex.getBytes());
                        String second_sha1_hex = bytesToHex(second_sha1);
                        segm_start++;

                        
                        if (second_sha1_hex.equals(pass)) {
                            System.out.println("Success!");
                            System.out.println( "Password: " + pass_current);
                            System.out.println("Password's Index " + (segm_start-1));
                            flag.set(true);
                            thread_scanner.close();
                            return;
                        }
                    }
                    
                    thread_scanner.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
            threads[i].start();
            
        }
        

        for (Thread thread : threads) {
            thread.join();
        }

        if (!flag.get()) {
            System.out.println("Password not found...");
        }
        

        long time_end = System.currentTimeMillis();
        System.out.println("Parallel duration: " + (time_end - time_start) + " milliseconds");
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
    
	
	public static void main(String[] args) throws NoSuchAlgorithmException, FileNotFoundException, InterruptedException {
		System.out.println("===== Sequential =====");
			sequential();
			System.out.println("===== Parallel =====");
			parallel();
	}
}

