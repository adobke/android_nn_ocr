#!/bin/sh
echo "input = [" > outputs.m;
cat outputinputs.txt >> outputs.m;
echo "];" >> outputs.m;
echo "answers=[ " >> outputs.m;
cat outputanswers.txt >> outputs.m;
echo "];" >> outputs.m
