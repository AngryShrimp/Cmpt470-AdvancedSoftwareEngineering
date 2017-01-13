#include <stdio.h>

void exit(int);
    int A[10000]; 
        int B[10000];
int main()
{
FILE *f = fopen("input.txt", "r");
for (int i = 0; i <= 9999; i++)
{
if (i > 0 && i % 100 == 0)
fgetc(f);

A[i] = (fgetc(f) == '*');
}

fclose(f);

for (int i = 0; i < 100000; i++) {
	      if (i % 10 == 0)printf("Step %d\n", i);
                        for (int a = 0; a < 100; a++) {
                        for (int b = 0; b < 100; b++) {
				      int c = 0;
				      if (i % 2) {
//					    printf("#10\n");
					      if (a > 0)
					      {
					      if (b > 0)c += B[a * 100 + b - 101];
					      c +=  B[a * 100 + b - 100];
					      if (b < 99) c += B[a * 100 + b - 99];
					      }
					      if (b > 0) c +=  B[a * 100 + b - 1];
					      if (b < 99) c += B[a * 100 + b + 1];
					      if (a < 99)
					      {
					      if (b > 0) c +=  B[a * 100 + b + 99];
					      c += B[a * 100 + b + 100];
					      if (b < 99) c += B[a * 100 + b + 101];
					      }

					      A[a * 100 + b] = (c == 3) || ((B[a * 100 + b] == 1) && c >= 2 && c <= 3); 
					      }
				      else {
					      if (a > 0)
					      {
					      if (b > 0) c += A[a * 100 + b - 101];
					      c += A[a * 100 + b - 100];
					      if (b < 99) c += A[a * 100 + b - 99];
					      }
					      if (b > 0) c += A[a * 100 + b - 1];
					      if (b < 99) c += A[a * 100 + b + 1];
					      if (a < 99)
					      {
					      if (b > 0) c += A[a * 100 + b + 99];
					      c += A[a * 100 + b + 100];
					      if (b < 99) c += A[a * 100 + b + 101];
					      }
					      B[a * 100 + b] = (c == 3) || ((A[a * 100 + b] == 1) && c >= 2 && c <= 3); }}}}


f = fopen("output.txt", "w");  
for (int i = 0; i <= 9999; i++) 
{
if (i > 0 && i % 100 == 0) fputc('\n', f);
fputc(A[i] == 1 ? '*' : ' ', f);
}
fputc('\n', f);
fclose(f);
}
