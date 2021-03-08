package test;

import model.CMessage;
import util.MessageUtil;

public class TestDriver {

    public static void main(String[] args) {
        String test1 = "!c :123213213213 hello!";
        test(test1);

        String test2 = "!c";
        test(test2);

        String test4 = "!c:";
        test(test4);

        String test3 = "!c: 123123 ";
        test(test3);

        String test5 = "!c :123123";
        test(test5);

        String test6 = "!c : 123123";
        test(test6);


    }

    private static void test(String message) {
        CMessage cMessage2 = new MessageUtil().parseMessage(message);
        System.out.println(cMessage2.toString());
    }
}
