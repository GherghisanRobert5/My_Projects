#define _CRT_SECURE_NO_WARNINGS
#include <iostream>
#include <fstream>
#include <string>
#include "sha1-1.h"
#include <omp.h>
#include <cstdlib>

using namespace std;

void sequential()
{
    ifstream seq_file;
    seq_file.open("1mil.txt");
    string line, current_pass, pas = "00c3cc7c9d684decd98721deaa0729d73faa9d9b";
    string prefix = "parallel";
    double time_start, time_end;
    int index = 0;

    if (!seq_file.is_open())
    {
        printf("File not open!");
        return;
    }
    else
    {

        time_start = omp_get_wtime();
        while (seq_file)
        {

            int flag = 1;

            getline(seq_file, line);
            current_pass = prefix + line;
            current_pass = sha1(sha1(current_pass));

            if (current_pass.compare(pas) != 0)
                flag = 0;
            if (flag == 1)
            {
                printf("Password: ");
                cout << line;
                printf("\n Password's Index = %d", index);
                time_end = omp_get_wtime();
                break;
            }
            index++;
        }
    }
    printf("\nElapsed time = %g seconds\n", time_end - time_start);
    

    seq_file.close();
}

void parallel()
{
        string pass = "00c3cc7c9d684decd98721deaa0729d73faa9d9b";
        string prefix = "parallel";
        bool flag = false;
        double time_start, time_end;


        omp_set_num_threads(10);

        ifstream par_file("1mil.txt");
        if (!par_file.is_open()) {
            cout << "File not open!" << endl;
            return;
        }

        int NO_lines = 0;
        string current_line;
        while (getline(par_file, current_line)) {
            NO_lines++;
        }

        par_file.clear();
        par_file.seekg(0, ios::beg);

        int NO_pass_in_thread = NO_lines / 10;

        time_start = omp_get_wtime();

#pragma omp parallel shared(flag)
        {
            ifstream thread_file("1mil.txt");
            int thread_id,thread_start,thread_end;

            thread_id = omp_get_thread_num();
            thread_start = NO_pass_in_thread * thread_id;
            thread_end = thread_start + NO_pass_in_thread;
            string line;

            for (int i = 0; i < thread_start && getline(thread_file, line); i++);
                

            for (int i = thread_start; i < thread_end && getline(thread_file, line) && !flag; i++) {
                string word = prefix + line;
                string hash = sha1(sha1(word));

                if (hash == pass) {
#pragma omp critical
                    {
                        if (!flag) {
                            flag = true;
                            cout << "Password : " << line << endl<<"Password's Index: "<<i<<endl;
                        }
                    }
                }
            }
        }
        time_end = omp_get_wtime();
        par_file.close();
        printf("Elapsed time = %g seconds\n", time_end - time_start);
    
}


int main()
{
    cout << "==========Sequential==========" << endl<<endl;
    sequential();
    cout <<endl<< "==========Parallel==========" << endl << endl;
    parallel();
    return 0;
}
