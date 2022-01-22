# course_work_parallel_computing
Inverted index simple realization with concurrency
@author Kosenko Anton 
group IT-ZP01

VARIANT 5

Tasks:
1) Create programs: to build an inverted index and to use an inverted index;
2) download the input text data according to the option as follows:

Data stored in archive that could be explore by the next link:
https://drive.google.com/file/d/1O5Nf6Y28Oqu9_h2diRhL7aBmqZZFC9H6/view?usp=sharing

Structure of text data files for processing:
1.	aclImdb\test\neg – N=12500 files
2.	aclImdb\test\pos – N=12500 files
3.	aclImdb\train\neg – N=12500 files
4.	aclImdb\train\pos – N=12500 files
5.	aclImdb\train\unsup – N=50000 files

We need to select 250 files from 1-4 directories, and 1000 files from directory 5.
In each directory, the files are indexed in ascending order.
Initial index according to the variant: N / 50 * (5 - 1)
Final index according to the variant: N / 50 * 5
