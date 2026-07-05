import java.util.*;

//公共类 JosephusProblem：必须与文件名相同
public class JosephusProblem {
    
    // 定义节点类
    static class Node {
        int data;
        Node next;
        
        Node(int data) {
            this.data = data;
        }
    }
    
    /**
     * 使用循环链表解决约瑟夫问题
     * @param n 总人数
     * @param m 报数到m的人出列
     * @return 出列顺序
     */
    public static List<Integer> josephusWithLinkedList(int n, int m) {
        List<Integer> result = new ArrayList<>();
        if (n <= 0 || m <= 0) return result;
        
        // 创建循环链表
        Node head = new Node(1);
        Node prev = head;
        for (int i = 2; i <= n; i++) {
            Node node = new Node(i);
            prev.next = node;
            prev = node;
        }
        prev.next = head; // 形成循环
        
        // 开始游戏
        Node current = head;
        Node previous = prev;
        int count = 1;
        
        while (n > 0) {
            if (count == m) {
                // 移除当前节点
                result.add(current.data);
                previous.next = current.next;
                current = current.next;
                count = 1;
                n--;
            } else {
                previous = current;
                current = current.next;
                count++;
            }
        }
        
        return result;
    }
    
    //程序入口
    public static void main(String[] args) {
        int n = 7; // 7个人
        int m = 3; // 数到3的人出列
        
        List<Integer> order = josephusWithLinkedList(n, m);
        System.out.println("出列顺序：" + order);
        System.out.println("最后剩下的人：" + order.get(order.size() - 1));
    }
}