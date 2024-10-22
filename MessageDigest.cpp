#include<openssl/sha.h>
#include<iostream>
#include<cstring>
#pragma warning(disable : 4996)

using namespace std;

class MessageDigest
{
private:
	static const int sha256 = 256;
	static const int sha512 = 512;
	char message[100];
	char *sha256_encrypt;
	char* sha512_encrypt;
	int digest_alg;
	string al;
public:
	//Basic Constructor with 2 parameters: char msg used for saving the given text and int algrtm to select which algorithm to use.
	MessageDigest(char msg[100], int algrtm)
	{
		this->digest_alg = algrtm;
		strcpy(message,msg);
	}
	
	//Method to encrypt the given message. It doesn't have any parameters because all the required information is given within the constructor.
	void encrypt()
	{
		if (this->digest_alg == sha256)
		{

			SHA256_CTX sha;
			SHA256_Init(&sha);
			SHA256_Update(&sha, this->message, strlen(message));
			unsigned char sha256_hash[SHA256_DIGEST_LENGTH];
			SHA256_Final(sha256_hash, &sha);

			// Converting to hexa
			cout << "Sha256: ";
			for (int i = 0; i < SHA256_DIGEST_LENGTH; ++i) {
				printf("%02X ", sha256_hash[i]);
			}
		}
		else
			if (this->digest_alg == sha512)
			{
				SHA512_CTX sha;
				SHA512_Init(&sha);
				SHA512_Update(&sha, this->message, strlen(message));
				unsigned char sha512_hash[SHA512_DIGEST_LENGTH];
				SHA512_Final(sha512_hash, &sha);

				cout << "Sha512: ";
				for (int i = 0; i < SHA512_DIGEST_LENGTH; ++i) {
					printf("%02X ", sha512_hash[i]);
				}
			}
			else
			{
				cout << "Hash algorithm isn't an option or doesn't exist...";
			}

	}

	
	~MessageDigest()
	{

	}

};

int main()
{

	char message[100];
	int alg_nr;

	cout << "Type a message: " << endl;
	cin.get(message,100);
	cout << "Type a SHA algorithm('256' or '512'): ";
	cin >> alg_nr;
	cout << endl;
	MessageDigest a(message, alg_nr);
	a.encrypt();
	return 0;

}

