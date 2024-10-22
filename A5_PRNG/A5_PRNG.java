

import java.util.Scanner;

public class A5_PRNG {
	public static class LFSR {
		byte[] register = new byte[8];
		
		public void init(byte[] seed) {
			if(seed.length != 8) {
				throw new UnsupportedOperationException("Seed size is wrong");
			}
			else {
				for(int i = 0; i < 8; i++) {
					this.register[i] = seed[i];
				}
			}
	
		}
		
		private byte getBitAtIndex(int index) {
			if(index < 0 || index  > 63) {
				throw new UnsupportedOperationException("Wrong index");
			}
			
			int byteIndex = 7 - (index / 8);
			int bitIndex = index % 8;
			
			byte bitMask = (byte) (1 << bitIndex);
			
			return (byte) ((this.register[byteIndex] & bitMask) >> bitIndex);
		}
		

		private byte shiftWithInsertRegisterByte(byte input, int index) {
			byte registerByte = this.register[index];
			
			byte outBit = (byte) (registerByte & 1);
			registerByte = (byte) ((registerByte & 0xFF) >> 1);
			registerByte = (byte) (registerByte | (input << 7));
			
			this.register[index] = registerByte;
			
			return outBit;
		}
		
		private byte doStep() {
			byte xorResult = (byte) (getBitAtIndex(63) ^ getBitAtIndex(62) ^ getBitAtIndex(61)^getBitAtIndex(58));
			byte tempBit = shiftWithInsertRegisterByte(xorResult,0);
			
			xorResult = (byte) (getBitAtIndex(44) ^ getBitAtIndex(43));
			tempBit = shiftWithInsertRegisterByte(xorResult, 3);
			xorResult = (byte) (getBitAtIndex(22) ^ getBitAtIndex(21) ^ getBitAtIndex(20) ^ getBitAtIndex(7));
			tempBit = shiftWithInsertRegisterByte(xorResult, 5);
			byte resultBit = shiftWithInsertRegisterByte(tempBit, 7);
		
			return resultBit;
		}
		
		public byte getRandomByte() {
			byte result = 0;
			for(int i = 0; i < 8; i++) {
				result = (byte) (result << 1);
				byte randomBit = this.doStep();
				result = (byte) (result | randomBit);
			}
			return result;
		}
	}
	
	public static String getHexString(byte value) {
		StringBuilder result = new StringBuilder();
		result.append(String.format(" %02X", value));
		return result.toString();
	}
	
	public static void main(String[] args) {
		LFSR lfsr = new LFSR();
		byte[] seed = {01,01,01,01,01,01,01,01};
		
		lfsr.init(seed);
		
		Scanner scanner = new Scanner(System.in);
		
	        
	        System.out.print("0x");
		
		for(int i = 0 ; i <20; i++) {
			byte randomByte = lfsr.getRandomByte();
			System.out.print(" " + getHexString(randomByte));
	}
		System.out.println();
		System.out.print("Enter the number of pseudo-random bytes you wish to generate: ");
        int byteNumber = scanner.nextInt();
        
        System.out.print("0x");
		
		for(int i = 0 ; i < byteNumber; i++) {
			byte randomByte = lfsr.getRandomByte();
			System.out.print(" " + getHexString(randomByte));
	}
	}
}