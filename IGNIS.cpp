#pragma warning(disable:4996)
#include <iostream>
#include <openssl/aes.h>
#include <openssl/sha.h>
#include <fstream>
#include <string>
#include<io.h>
#include <fcntl.h> 
using namespace std;

//For this application, we have been given a file named "ignis" with the most used 1 million passwords in the world. The task is to find the correct one after it had been salted and encrypted.

bool invalidChar(char c)
{
    return !(c >= 0 && c < 128);
}
void stripUnicode(string& str)
{
    str.erase(remove_if(str.begin(), str.end(), invalidChar), str.end());
}



int main()
{
    
   //Obtaining the hashes for the aes key and password, and decrypting the password
    AES_KEY aes_key;
    unsigned char decryptedPass[32];
    FILE *key = fopen("aes.key", "r+");
    fseek(key, 0, SEEK_END);
    long int size = ftell(key);
    fclose(key);
    key = fopen("aes.key", "r+");
    unsigned char key_val[32];
    printf("Key's Hash: ");
    for (int i = 0; i < size; i++)
    {
        key_val[i] = fgetc(key);
        printf("%02X ", key_val[i]);
    }
    FILE* pass = fopen("pass.enc", "r+");
    fseek(pass, 0, SEEK_END);
    long int size2 = ftell(pass);
    fclose(pass);
    pass = fopen("pass.enc", "r+");
    unsigned char pass_val[32];
    //printf("\nHash-ul parolei: ");
    for (int i = 0; i < size2; i++)
    {
        pass_val[i] = fgetc(pass);
        //printf("%02X ", pass_val[i]);
    }
    AES_set_decrypt_key(key_val, (sizeof(key_val) * 8), &aes_key);

    for (int i = 0; i < sizeof(pass_val); i+=AES_BLOCK_SIZE)
        AES_decrypt(pass_val+i,(decryptedPass+i), &aes_key);

    printf("\nDecrypted Password's Hash: ");
    for (unsigned int i = 0; i < sizeof(key_val); i++)
        printf("%02X ", decryptedPass[i]);

    cout << endl;

    //Searching for the correct password by creating all the hashes with the added prefix "ismsap" and comparing them to the password's
    
    ifstream file1;
    file1.open("ignis3.txt");

    string line,word;
    string ism = "ismsap";
    unsigned char hash[32];
    int nr = 1;
    if (file1.is_open())
    {
        while (file1)
        {
            int flag = 1;
            if(nr!=511733 || nr!= 2731998 || nr!= 4904420)
            getline(file1,line);
            word = ism + line;
            //cout << word << endl;
            unsigned char* ucw = (unsigned char*)word.c_str();//unsigned char word
            SHA256(ucw, strlen((char*)ucw), hash);
            int i;
            for (i = 0; i < 32; i++) {
                //printf("%02X ", hash[i]);
                if (hash[i] != decryptedPass[i])
                    flag = 0;
            }
            if (flag == 1)
            {
                printf("Password is: ");
                cout << line;
                break;
            }
            else
            {
                //printf("Searching...\n");
                //system("CLS");
            }
            //cout << endl;
            i++;
        }
    }
    else
        cout << "The file did not open!";
        
    file1.close();
    fclose(key);
    fclose(pass);
}

