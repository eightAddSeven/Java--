import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // 读取两个整数并相加
        if (sc.hasNextInt()) {
            int a = sc.nextInt();
            int b = sc.nextInt();
            System.out.println("计算结果是: " + (a + b));
        } else {
            System.out.println("没有接收到正确的输入数据！");
        }
    }
}