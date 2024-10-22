// App1.cpp : This file contains the 'main' function. Program execution begins and ends there.
//


//A small app that lets users create accounts and log in. The data is encrypted and stored in txt files.
//work in progress
#include <iostream>
#include<fstream>
#include<string>
#include<cstring>
#include <cstdlib>
#include <openssl/aes.h>
#include <openssl/sha.h>
#include <stdio.h>
#include <cstddef>
#include <cstring>
#include<array>
#include <iomanip>
#include <windows.h>

#pragma warning(disable : 4996)
using namespace std;

class User {
    string username; 
    char password[100];
    unsigned char encryptedPass[48];


public:
    

    User(string username, char password[100])
    {
        this->username=username;
        strcpy(this->password, password);
    }
    User()
    {
        this->username="No username";
        strcpy(this->password, "No password");
    }
    void set(string username, char password[100])
    {
        this->username=username;
        strcpy(this->password, password);
    }
    void setEncryptedPass(unsigned char enc[48])
    {
        for (int i = 0; i < sizeof(enc); i++)
        {
            this->encryptedPass[i] = enc[i];
        }

        
    }

    
    bool checkPassword()
    {
        const char* specialChars = "!@#$+-%=^&*(){}[];:|,<.>/'\"?|";
        const char* digits = "0123456789";
        const char* checker1 = strpbrk(password, specialChars);
        const char* checker2 = strpbrk(password, digits);
        if (strlen(password) < 8 or checker1 == nullptr or checker2 == nullptr)
        {
            return 0;
        }
        else
            return 1;
    }
    bool checkName()
    {
        fstream accountNames;
        accountNames.open("Usernames.txt", std::ios::in);
        string line;
        accountNames >> line;
        while (!accountNames.eof())
        {
            if(line==this->username)
            return 0;
            accountNames >> line;
        }
        return 1;
    }
    bool login()
    {
        
    }
    
    
};
class AESCipher {
public:
    AESCipher()
    {

    }
private:
    static const int aes_256 = 256;
    unsigned char message[100], encrypted_text[48], iv[16], decrypted_message[48];
    unsigned char key_256[32];
    int leng;
    AES_KEY key;
public:
    //Basic Constructor with 3 parameters: unsigned char msg for the byte array of the given message, int alg to select an algorithm, int len is strlen of the given message, and keys 128 and 256 are the keys for each algorithm.
    AESCipher(unsigned char msg[100], int len, unsigned char key256[32])
    {

        leng = len;
        for (int i = 0; i < len; ++i) {
            this->message[i] = msg[i];
        }
        for (int i = 0; i < 32; i++)
            key_256[i] = key256[i];
            

    }
    //Method to encrypt the given text. The parameter given is the initial value, which is taken from main.
    void encrypt(unsigned char iv_o[32],string username)
    {
        for (int i = 0; i < 32; i++)
            iv[i] = iv_o[i];

        AES_set_encrypt_key(key_256, (sizeof(key_256) * 8), &key);

        AES_cbc_encrypt(message, encrypted_text, sizeof(encrypted_text), &key, iv, AES_ENCRYPT);


        fstream accounts;
        fstream accountNames;
        accounts.open("userNameAndPass.txt", std::ios::app);
        accountNames.open("Usernames.txt", std::ios::app);
        if (accounts.is_open() && accountNames.is_open())
        {

            accounts << username << std::endl;
            accountNames << username << std::endl;
            for (unsigned int i = 0; i < sizeof(encrypted_text); i++)
                //accounts << std::setw(2) << std::setfill('0') << std::hex << static_cast<int>(encrypted_text[i]);
                accounts << encrypted_text[i];
            accounts << endl;
            std::cout << "User succesfully added!";
            accounts.close();
            accountNames.close();
        }
        else
            std::cout << "File not open!";

        /*
        printf("\n\n\n..................................................\nEncrypted message: \n\n");
        for (unsigned int i = 0; i < sizeof(encrypted_text); i++)
            printf("%02X ", encrypted_text[i]);
        printf("\n..................................................\n\n\n\n");
        */



    }
    //Method to decrypt the encrypted text. The parameter given has to be the same initial value for it to work.
    void decrypt(unsigned char iv_d[32])
    {
        for (int i = 0; i < 32; i++)
            iv[i] = iv_d[i];

        AES_set_decrypt_key(key_256, (sizeof(key_256) * 8), &key);
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
        if (flag == 1)
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
   
    int userInput=1;
    User user1;
    
    unsigned char key_256[] = { 0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef,
                            0x0a, 0x1b, 0x2c, 0x3d, 0x4e, 0x5f, 0x67, 0x89,
                            0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef,
                            0x0a, 0x1b, 0x2c, 0x3d, 0x4e, 0x5f, 0x67, 0x89 };

    unsigned char iv[] = { 0x10, 0x30, 0x50, 0x70, 0x90, 0xb0, 0xd0, 0xf0,
                                0x20, 0x40, 0x60, 0x80, 0xa0, 0xc0, 0xe0, 0x00 };
    unsigned char iv_d[] = { 0x10, 0x30, 0x50, 0x70, 0x90, 0xb0, 0xd0, 0xf0,
                                0x20, 0x40, 0x60, 0x80, 0xa0, 0xc0, 0xe0, 0x00 };
    
    

    while (userInput!=0)
    {
        
        std::cout << "\nWelcome, user!\nPress 1 to login\nPress 2 to register\nPress 0 to exit\n\n>";
        std::cin >> userInput;
        switch (userInput) {
            case 0:
                break;
            case 1:
            {
                system("cls");
                bool checkPassword=0;
                string userName;
                char userPassword[100];
                unsigned char encryptedPass[48];
                std::cout << "Enter your name: \n";
                std::cin >> userName;
                std::cout << "Enter your password: \n";
                std::cin.get();
                std::cin.get(userPassword,100);
                
                fstream accountNames;
                accountNames.open("userNameAndPass.txt", std::ios::in);
                string line;
                accountNames >> line;
                while (!accountNames.eof())
                {
                    
                    
                    if (line == userName)
                    {
                        accountNames >> line;
                        std::strcpy(reinterpret_cast<char*>(encryptedPass), line.c_str());
                        
                        AES_KEY key;
                        unsigned char encrypted_text[48], iv[16], decrypted_message[100];
                        
                        for (int i = 0; i < 32; i++)
                            iv[i] = iv_d[i];
                        
                        AES_set_decrypt_key(key_256, (sizeof(key_256) * 8), &key);
                        
                        AES_cbc_encrypt(encryptedPass, decrypted_message, strlen(userPassword), &key, iv, AES_DECRYPT);
                        char decrypt[100];
                        for (int i = 0; i < strlen(userPassword); i++)
                            decrypt[i] = decrypted_message[i];
                        decrypt[strlen(userPassword)] = '\0';
                        
                        if (strcmp(decrypt, userPassword) == 0)
                        {
                            system("cls");
                            std::cout << "Welcome, " << userName<<"!";
                            Sleep(3000);
                            userInput= 0;
                        }

                    }
                    else
                    {
                        accountNames >> line;
                    }
                    

                }
                if (userInput == 0)
                    break;
                else
                {
                    system("cls");
                    std::cout << "Username or password incorrect";
                    Sleep(3000);
                    break;
                }
             
            }
            case 2:
            {
                system("cls");
                string userName;
                char userPassword[100];
                std::cout << "Create a new account\n";
                std::cout << "Enter your name: \n";
                cin >> userName;
                std::cout << "Enter your password: \n";
                std::cin.get();
                std::cin.get(userPassword, 100);
                std::cin.get();
                user1.set(userName, userPassword);
                if (user1.checkPassword() == 0)
                {
                 //system("cls");
                    std::cout << "The password must be atleast 8 characters long, have a special character and have atleast one number";
                Sleep(5000);
                system("cls");
                break;
                }
                
                if (user1.checkName() == 0)
                {
                    //system("cls");
                    std::cout << "This username is already in use.";
                    Sleep(5000);
                    system("cls");
                    break;
                }
                
                unsigned char* messageByteArray = new unsigned char[strlen(userPassword)];
                // Copy the characters from the char to the byte array
                memcpy(messageByteArray, userPassword, strlen(userPassword));
                AESCipher encrypt(messageByteArray, strlen(userPassword), key_256);
                encrypt.encrypt(iv, userName); 
            }

            }
    }
    system("cls");
    std::cout << "                             WELCOME!\n\n\n\n\n";

}

