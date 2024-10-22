#include <stdio.h>
#include <stdio.h>
#include <stdlib.h>
#include<math.h>

using namespace std;


int main()
{
	unsigned char buffer[100], buffer2[100], buffer3[100],buffer4[100];
	FILE* input;
	FILE* key;
	printf("The password is too short!");
	input = fopen("input.txt", "rb");
	key = fopen("key.txt", "rb");

	fread(buffer, sizeof(buffer), 1, input);
	fread(buffer2, sizeof(buffer2), 1, key);
	fseek(input, 0L, SEEK_END);
	fseek(key, 0L, SEEK_END);

	int passL, keyL, passAr1[100], passAr2[100], keyAr1[100], keyAr2[100],encAr[100],decAr[100];
	passL = ftell(input);
	keyL = ftell(key);
	if (passL < keyL)
	{
		printf("The password is too short!");
		return 1;
	}

	int passMat[100][8],keyMat[100][8],encMat[100][8],decMat[100][8];
	printf("Password:");
	for (int i = 0; i < passL; i++)
		printf("%c", buffer[i]);
	printf("\n");
	printf("Key:");
	for (int i = 0; i < keyL; i++)
		printf("%c", buffer2[i]);
	printf("\n");

	//Creating the binary matrix of the password and the key
	for (int i = 0; i < passL; i++)
	{
		
		passAr1[i] = (int)buffer[i];
		keyAr1[i] = (int)buffer2[i];
		passAr2[i] = (int)buffer[i];
		keyAr2[i] = (int)buffer2[i];
		for (int j = 7;j >= 0;j--)
		{
			passMat[i][j] = passAr2[i] % 2;
			passAr2[i] /= 2;
			keyMat[i][j] = keyAr2[i] % 2;
			keyAr2[i] /= 2 ;
		}
	}
	//XOR ENCRYPT
	int k = 0,p;
	for (int i = 0; i < passL; i++)
	{
		p = 0;
		encAr[i] = 0;
		for (int j = 7;j >= 0;j--)
		{
			encMat[i][j] = keyMat[k][j] ^ passMat[i][j];
			encAr[i] = encAr[i]+encMat[i][j] * pow(2, p);
			p++;
		}
		
		if (k == keyL-1)
			k = -1;
		k++;
		buffer3[i] = (char)encAr[i];
	}
	printf("Password after encryption:");
	for (int i = 0; i < passL; i++)
		printf("%c", buffer3[i]);
	printf("\n");
	//XOR DECRYPT
	k = 0;
	for (int i = 0; i < passL; i++)
	{
		p = 0;
		decAr[i] = 0;
		for (int j = 7;j >= 0;j--)
		{
			decMat[i][j] = keyMat[k][j] ^ encMat[i][j];
			decAr[i] = decAr[i] + decMat[i][j] * pow(2, p);
			p++;
		}

		if (k == keyL - 1)
			k = -1;
		k++;
		buffer4[i] = (char)decAr[i];
	}
	printf("Password after decryption:");
	for (int i = 0; i < passL; i++)
		printf("%c", buffer4[i]);
	printf("\n");
	//Verifying decryption
	int flag = 0;
	for (int i = 0; i < passL; i++)
	{
		if (buffer[i] != buffer4[i])
		{
			flag = 1;
			break;
		}
	}
	if (flag == 0)
	{
		printf("The decryption is correct.");
	}
	else
		printf("The decryption failed.");

	/*
	for (int i = 0; i < passL; i++)
	{
		for (int j = 0;j < 8;j++)
			printf("%d ", encMat[i][j]);
		printf("\n");
	}
	printf("\n");
	
	for (int i = 0; i < passL; i++)
	{
		printf("%c %d ", buffer[i],passAr1[i]);
		for (int j = 0;j < 8;j++)
			printf("%d ", passMat[i][j]);
		printf("\n");
	}
	printf("\n");
	for (int i = 0; i < keyL; i++)
	{
		printf("%c %d ", buffer2[i], keyAr1[i]);
		for (int j = 0;j < 8;j++)
			printf("%d ", keyMat[i][j]);
		printf("\n");
	}
	printf("\n");
	*/

	fclose(input);
	fclose(key);


	return 1;


}
