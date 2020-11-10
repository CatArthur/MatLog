import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;

public class Main {
    public static void main(String[] args) {
        //Scanner sc=new Scanner(System.in);
        //String expression="A+B";
        String expression = "!!a";
        String expression2 = "P1’->!QQ->!R10&S|!T&U&V";
        System.out.println(calculation(expression));
        System.out.println(calculation(expression2));
    }

    private static String calculation(String expression) {
        expression = expression.replaceAll("->", "%") + " ";
        Pattern pt = Pattern.compile("!?[a-zA-Z][a-zA-z0-9’]*+[$[^>]]");
        Matcher mt = pt.matcher(expression);
        while (mt.find()) {
            expression = expression.substring(0, mt.start())
                    + "<" + expression.substring(mt.start(), mt.end() - 1) + ">"
                    + expression.substring(mt.end() - 1);
            mt = pt.matcher(expression);
        }
        expression = expression.replaceAll("[^a-zA-Z0-9’<>()!%&|]", "");
        return calc(expression);
    }

    private static String calc(String expression) {

        Pattern pt = Pattern.compile("\\(");
        Matcher mt = pt.matcher(expression);
        while (mt.find()) {
            char[] e = expression.toCharArray();
            int brackets = 0;
            int start = 0;
            int end = 0;
            for (int i = 0; i < e.length; i++) {
                if (e[i] == '(') {
                    brackets++;
                    if (brackets == 1)
                        start = i;
                }
                if (e[i] == ')') {
                    brackets--;
                    if (brackets == 0) {
                        end = i;
                        break;
                    }
                }
            }
            expression = expression.substring(0, start)
                    + "<" + calc(expression.substring(start + 1, end)) + ">"
                    + expression.substring(end + 1);
            mt = pt.matcher(expression);
        }
        while (expression.contains("!<")) {
            int start = expression.indexOf("!<");
            char[] e = expression.substring(start).toCharArray();
            int brackets = 1;
            int end = 0;
            for (int i = 2; i < e.length; i++) {
                if (e[i] == '<') {
                    brackets++;
                }
                if (e[i] == '>') {
                    brackets--;
                    if (brackets == 0) {
                        end = i;
                        break;
                    }
                }
            }
            expression = expression.substring(0, start)
                    + "<!." + expression.substring(start + 1, start + end + 1) + ">"
                    + expression.substring(start + end + 1);
            mt = pt.matcher(expression);
        }
        expression = operation(expression, '&', true);
        expression = operation(expression, '|', true);
        expression = operation(expression, '%', false);


        return replaceExtra(expression);
    }

    private static String replaceExtra(String expression) {
        Pattern pt = Pattern.compile("<[a-zA-Z][a-zA-z0-9’]*>");
        Matcher mt = pt.matcher(expression);
        while (mt.find()) {
            expression = expression.substring(0, mt.start())
                    + expression.substring(mt.start() + 1, mt.end() - 1)
                    + expression.substring(mt.end());
            mt = pt.matcher(expression);
        }
        expression = expression.replaceAll("<", "(");
        expression = expression.replaceAll(">", ")");
        expression = expression.replaceAll("\\.", "");
        expression = expression.replaceAll("%", "->");
        return expression;
    }

    private static String operation(String expression, char operator, boolean usualway) {
        while (expression.contains(">" + operator + "<")) {
            int middle = (usualway ? expression.indexOf(">" + operator + "<") : expression.lastIndexOf(">" + operator + "<"));
            char[] e = expression.substring(middle).toCharArray();
            int brackets = 1;
            int end = 0;
            for (int i = 3; i < e.length; i++) {
                if (e[i] == '<') {
                    brackets++;
                }
                if (e[i] == '>') {
                    brackets--;
                    if (brackets == 0) {
                        end = i;
                        break;
                    }
                }
            }
            e = expression.substring(0, middle).toCharArray();
            brackets = -1;
            int start = 0;
            for (int i = middle - 1; i > 0; i--) {
                if (e[i] == '<') {
                    brackets++;
                    if (brackets == 0) {
                        start = i;
                        break;
                    }
                }
                if (e[i] == '>') {
                    brackets--;
                }
            }
            String A = expression.substring(start, middle + 1);
            String B = expression.substring(middle + 2, middle + end + 1);
            expression = expression.substring(0, start)
                    + "<" + operator + "," + A + "," + B + ">"
                    + expression.substring(middle + end + 1);
        }
        return expression;
    }
}
//(->,P1’,(->,(!QQ),(|,(&,(!R10),S),(&,(&,(!T),U),V))))
//(->,P1’,(->,(!QQ),(|,(&,(!R10),S),(&,(&,(!T),U),V))))

//        Object[] strings= Arrays.stream(expression.split("[<>]")).filter(x->!x.equals("")).toArray();
//        for (int i = 0; i < strings.length; i++) {
//            System.out.print(strings[i]+"; ");
//        }
//        System.out.println();


        /*pt = Pattern.compile("(_?[a-zA-Z]+\\d*’?)|(\\[.+\\])\\|(_?[a-zA-Z]+\\d*’?)|(\\[.+\\])");
        mt = pt.matcher(expression);
        while (mt.find()){
            String expr=expression.substring(mt.start(),mt.end());
            Matcher m=Pattern.compile("(_?[a-zA-Z]+\\d*’?)|(\\[.+\\])\\|").matcher(expr);
            m.find();
            String A=expr.substring(0, m.end()-1);
            String B=expr.substring(m.end());
            expression = expression.substring(0,mt.start())
                    + "[ &, "+A + ", "+B +"]"
                    + expression.substring(mt.end());
            mt = pt.matcher(expression);
        }*/
//        pt = Pattern.compile("(_?[a-zA-Z]+\\d*’?)|(\\[.+\\])>(_?[a-zA-Z]+\\d*’?)|(\\[.+\\])");
//        mt = pt.matcher(expression);
//        while (mt.find()){
//            String expr=expression.substring(mt.start(),mt.end());
//            Matcher m=Pattern.compile("(_?[a-zA-Z]+\\d*’?)|(\\[.+\\])\\>").matcher(expr);
//            m.find();
//            String A=expr.substring(0, m.end()-1);
//            String B=expr.substring(m.end());
//            expression = expression.substring(0,mt.start())
//                    + "[ &, "+A + ", "+B +"]"
//                    + expression.substring(mt.end());
//            mt = pt.matcher(expression);
//        }
//        //sum and sub
//        pt = Pattern.compile("-?\\d+[+[-]]-?\\d+");
//        mt = pt.matcher(expression);
//        while (mt.find()){
//            expression = expression.substring(0, mt.start())
//                    + operation(expression.substring(mt.start(), mt.end()))
//                    + expression.substring(mt.end());
//            mt = pt.matcher(expression);
//        }




//        pt = Pattern.compile("![a-zA-Z]+\\d*’?");
//        mt = pt.matcher(expression);
//        while (mt.find()){
//            expression = expression.substring(0, mt.start())
//                    + "_"+expression.substring(mt.start()+1, mt.end())
//                    + expression.substring(mt.end());
//            mt = pt.matcher(expression);
//        }