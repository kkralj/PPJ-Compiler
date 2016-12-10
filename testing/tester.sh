#!/bin/bash

TEST_FOLDER_PATH=/home/u1/Desktop/adsa
CLASS_FOLDER_PATH=/home/u1/coding/gitProjects/PPJ/PPJ_Syntax/out/production/PPJ_Syntax
SOURCE_FOLDER_PATH=/home/u1/coding/gitProjects/PPJ/PPJ_Syntax
RESULT="";

for f in $(find $TEST_FOLDER_PATH -name '*.san'); do 
java -cp $CLASS_FOLDER_PATH GSA < $f > /dev/null;
java -cp $CLASS_FOLDER_PATH analizator.SA < ${f: :-4}".in" > $SOURCE_FOLDER_PATH/out.txt 2>&1;
RESULT=$(diff $SOURCE_FOLDER_PATH/out.txt ${f: :-4}".out" -q -b);
if [[ $RESULT = *[!\ ]* ]]; then
  echo "ERROR: "${f::-8}"  !!!!!!!!!!!!!!!!";
  #break;
else
  echo "PASSED:"${f::-8};
fi
done

