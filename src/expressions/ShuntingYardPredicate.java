package expressions;

import java.util.LinkedList;
import java.util.Stack;

import interpreter.CompParser;

public class ShuntingYardPredicate {

    public static double calc(String expression) {
        if (!validations(expression))
            System.out.println("throw exception");

        LinkedList<String> str_queue = new LinkedList<>();
        Stack<String> str_stack = new Stack<>();
        int len = expression.length();

        String token = "";
        String tmp_str=null;
        for (int i = 0; i < len; i++) {
            if (expression.charAt(i) >= '0' && expression.charAt(i) <= '9') {
                token = expression.charAt(i) + "";
                while ((i + 1 < len && expression.charAt(i + 1) >= '0' && expression.charAt(i + 1) <= '9')
                        || (i + 1 < len && expression.charAt(i + 1) == '.'))
                    token = token + expression.charAt(++i);
            }
            else if((expression.charAt(i) >= '<' && expression.charAt(i) <= '>')||expression.charAt(i)=='!')
            {
                if(expression.charAt(i+1)=='=') {
                    token = expression.charAt(i) + "";
                    token = token + expression.charAt(++i);
                }
                else
                    token = expression.charAt(i) + "";
            }
            else if ((expression.charAt(i) >= 'A' && expression.charAt(i) <= 'Z')||(expression.charAt(i) >= 'a' && expression.charAt(i) <= 'z')) {
                token = expression.charAt(i) + "";
                while (i<expression.length()-1&&((expression.charAt(i+1) >= 'A' && expression.charAt(i+1) <= 'Z')||(expression.charAt(i+1) >= 'a' && expression.charAt(i+1) <= 'z')))
                    token = token + expression.charAt(++i);
                token= CompParser.symbols.get(token).getV()+"";
            } else
                token = expression.charAt(i) + "";


            switch (token) {

                case "+":
                case "-":
                    while (!str_stack.isEmpty() && !str_stack.peek().equals("("))
                        str_queue.addFirst(str_stack.pop());
                    str_stack.push(token);
                    break;
                case "||":
                case "&&":
                case "*":
                case "/":
                    while (!str_stack.isEmpty() && (str_stack.peek().equals("*") || str_stack.peek().equals("/")))
                        str_queue.addFirst(str_stack.pop());
                    str_stack.push(token);
                    break;
                case "<":
                case "<=":
                case ">":
                case ">=":
                case "!=":
                case "==":
                    tmp_str=token;
                    break;
                case "(":
                    str_stack.push(token);
                    break;

                case ")":
                    while (!str_stack.isEmpty() && !(str_stack.peek().equals("(")))
                        str_queue.addFirst(str_stack.pop());
                    str_stack.pop();
                    break;
                default: // Always a number
                    str_queue.addFirst(token);
                    break;
            }
        }
        while (!str_stack.isEmpty())
            str_queue.addFirst(str_stack.pop());
        str_queue.addFirst(tmp_str);
        Expression finalExpression = buildExpression(str_queue);
        double answer = finalExpression.calculate();
        return Double.parseDouble(String.format("%.3f", answer));
    }

    private static boolean validations(String expression) {
        return true; // TODO implement validations

    }

    private static Expression buildExpression(LinkedList<String> queue) {
        Expression returnedExpression = null;
        Expression right = null;
        Expression left = null;
        String currentExpression = queue.removeFirst();
        if (currentExpression.equals("+") || currentExpression.equals("-") || currentExpression.equals("*")
                || currentExpression.equals("/")|| currentExpression.equals("<")|| currentExpression.equals(">")
                || currentExpression.equals("<=")|| currentExpression.equals(">=")|| currentExpression.equals("==")|| currentExpression.equals("!=")) {
            right = buildExpression(queue);
            left = buildExpression(queue);
        }
        switch (currentExpression) {
            case "+":
                returnedExpression = new Plus(left, right);
                break;
            case "-":
                returnedExpression = new Minus(left, right);
                break;
            case "*":
                returnedExpression = new Mul(left, right);
                break;
            case "/":
                returnedExpression = new Div(left, right);
                break;
            case "<=":
            case ">":
            case ">=":
            case "==":
            case "!=":
            case "<":
                returnedExpression = new PredicateExp(left, right,currentExpression);
                break;
            default:
                returnedExpression = new Number(
                        Double.parseDouble(String.format("%.2f", Double.parseDouble(currentExpression))));
                break;
        }

        return returnedExpression;
    }

}
