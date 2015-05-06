package ifs.docmaw;

public class OracleLikeJava {

   public static boolean match(String input, String regExp){
      String tempRegExp = regExp.replaceAll("\\%",".\\*");
      String finalRegExp = tempRegExp.replaceAll("_", ".");
      System.out.println(regExp + " -> "+ finalRegExp);
      return input.matches(finalRegExp);
   }

   /**
    * @param args
    */
   public static void main(String[] args) {
      // TODO Auto-generated method stub
      String a = "abcde001Za{z[][]";
      String b = "a%e_01%";
      System.out.println(match(a, b));
      System.out.println(a.substring(1, 2));
   }

}
