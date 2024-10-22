#include <iostream>
#include <openssl/aes.h>
#include <openssl/sha.h>
#include <stdio.h>
#include <cstddef>
#include <cstring>
#pragma warning(disable : 4996)
using namespace std;



class AESCipher {
private:
	static const int aes_128=128;
	static const int aes_256=256;
	unsigned char message[100], encrypted_text[48], iv[16], decrypted_message[48];
	unsigned char key_128[16], key_256[32];
	int aes_alg,leng;
	AES_KEY key;
public:
	//Basic Constructor with 5 parameters: unsigned char msg for the byte array of the given message, int alg to select an algorithm, int len is strlen of the given message, and keys 128 and 256 are the keys for each algorithm.
	AESCipher(unsigned char msg[100],int alg, int len, unsigned char key128[16], unsigned char key256[32])
	{
		
		leng = len;
		cout << "\n............................................................" << endl << "Initial message as byte array in hexadecimal representation: \n\n";
		for (int i = 0; i < len; ++i) {
			this->message[i] = msg[i];
			printf("%02X ", message[i]);
		}
		cout << "\n............................................................\n";
		aes_alg = alg;
	
		if (this->aes_alg == aes_128)
		{
			for (int i = 0; i < 16; i++)
				key_128[i] = key128[i];
		}
		else
			if (this->aes_alg == aes_256)
			{
				for (int i = 0; i < 32; i++)
					key_256[i] = key256[i];
			}
		
	}
	//Method to encrypt the given text. The parameter given is the initial value, which is taken from main.
	void encrypt(unsigned char iv_o[32])
	{
		for (int i = 0; i < 32; i++)
			iv[i] = iv_o[i];
		
		if (this->aes_alg == aes_128)
		{
			AES_set_encrypt_key(key_128, (sizeof(key_128) * 8), &key);
		}
		else
			if (this->aes_alg == aes_256)
			{
				AES_set_encrypt_key(key_256, (sizeof(key_256) * 8), &key);
			}
		AES_cbc_encrypt(message,encrypted_text, sizeof(encrypted_text), &key, iv, AES_ENCRYPT);

		printf("\n\n\n..................................................\nEncrypted message: \n\n");
		for (unsigned int i = 0; i < sizeof(encrypted_text); i++)
			printf("%02X ", encrypted_text[i]);
		printf("\n..................................................\n\n\n\n");

		
		
	}
	//Method to decrypt the encrypted text. The parameter given has to be the same initial value for it to work.
	void decrypt(unsigned char iv_d[32])
	{
		for (int i = 0; i < 32; i++)
			iv[i] = iv_d[i];
		if (this->aes_alg == aes_128)
		{
			AES_set_decrypt_key(key_128, (sizeof(key_128) * 8), &key);
		}
		else
		{
			AES_set_decrypt_key(key_256, (sizeof(key_256) * 8), &key);
		}
		cout << endl << encrypted_text << endl;
		AES_cbc_encrypt(encrypted_text, decrypted_message, leng, &key, iv, AES_DECRYPT);
		

		printf("............................................................\nDecrypted message: \n\n");
		for (unsigned int i = 0; i < leng; i++)
			printf("%02X ", decrypted_message[i]);
		int flag = 1;
		for (int i = 0; i < leng; i++)
		{
			if (message[i] != decrypted_message[i])
			{
				flag = 0;
				i = leng;
			}
		}
		printf("\n............................................................\n");
		if (flag==1)
			printf("\nDecryption was successful!\n\n");
		else
			printf("\nSomething went wrong with the decryption...\n\n");
	}
	~AESCipher()
	{

	}
};



int main()
{
	unsigned char key_128[] = { 0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef,
								0x0a, 0x1b, 0x2c, 0x3d, 0x4e, 0x5f, 0x67, 0x89 };
	unsigned char key_256[] = { 0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef,
								0x0a, 0x1b, 0x2c, 0x3d, 0x4e, 0x5f, 0x67, 0x89,
								0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef,
								0x0a, 0x1b, 0x2c, 0x3d, 0x4e, 0x5f, 0x67, 0x89 };
	
	unsigned char iv[] = {		0x10, 0x30, 0x50, 0x70, 0x90, 0xb0, 0xd0, 0xf0,
								0x20, 0x40, 0x60, 0x80, 0xa0, 0xc0, 0xe0, 0x00 };
	unsigned char iv_d[] = {	0x10, 0x30, 0x50, 0x70, 0x90, 0xb0, 0xd0, 0xf0,
								0x20, 0x40, 0x60, 0x80, 0xa0, 0xc0, 0xe0, 0x00 };


	char message[100];
	int alg_nr,len;
	cout << "Type a message: ";
	cin.get(message, 100);
	len = strlen(message);
	cout << "Type an algorithm('128' or '256'): ";
	cin >> alg_nr;
	if (alg_nr != 128 && alg_nr != 256)
	{
		cout << endl << "This algorithm isn't recognised...";
	}
	else
	{
		unsigned char* messageByteArray = new unsigned char[strlen(message)];
		// Copy the characters from the char to the byte array
		memcpy(messageByteArray, message, strlen(message));
		AESCipher a(messageByteArray, alg_nr, len, key_128, key_256);
		a.encrypt(iv);
		a.decrypt(iv_d);
		cin.get();
	}
	return 1;
}

