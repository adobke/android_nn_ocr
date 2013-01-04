//Alistair Dobke and Mark Mann
//Parser for math OCR project
//http://cs.hmc.edu/~adobke/nn/

package com.example.math_ocr;

class Parser {
int idx = 0;

public Parser() {
	  // nothing to do currently...
}

private int getNextInt(char[] chars) {
 int sum;
 if (idx < chars.length)
   sum = chars[idx++] - 48;
 else
   return 0 ;
 while (idx < chars.length && Character.isDigit(chars[idx])) {
   sum *= 10;
   sum += chars[idx++] - 48;
 }
 return sum;
}

public int parse(String exp) {
 int sum = 0;
 idx = 0;
 char[] chars = exp.toCharArray();

 if ( !Character.isDigit(chars[0]) )
   return Integer.MAX_VALUE;

 sum = getNextInt(chars);

 while (idx < chars.length ) {
   switch (chars[idx++]) {
     case '+':
       sum += getNextInt(chars);
       break;
     case '-':
       sum -= getNextInt(chars);
       break;
   }
 }
 idx = 0;
 return sum;
}

//public static void main(String[] args) {
// System.out.println("hello");
// Parser p = new Parser();
// System.out.println("Should be 1: " + p.parse("1"));
// System.out.println("Should be 15: " + p.getNextInt("15".toCharArray()));
// System.out.println("Should be 15: " + p.parse("15"));
// System.out.println("Should be 15: " + p.parse("15+"));
// System.out.println("Should be 16: " + p.parse("15+1"));
// System.out.println("Should be 63: " + p.parse("42+21"));
// System.out.println("Should be 0: " + p.parse("42-42"));
// System.out.println("Should be -42: " + p.parse("100-142"));
// System.out.println("Should be 7: " + p.parse("1+2+3-4+5"));
//}

}

