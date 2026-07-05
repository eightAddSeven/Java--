import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Josephus {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("请输入人数");
        int n = sc.nextInt();   
        //每个玩家都有姓名和编号，编号从1到n
        //使用键值对存储玩家信息，键为编号，值为姓名
        Map<Integer, String> players = new HashMap<>();

        for(int i = 0; i < n; i++){
            System.out.println("请输入第" + (i + 1) + "个人的姓名");
            players.put(i + 1, sc.next());
        }

        System.out.println("请输入报数的数");
        int m = sc.nextInt();
        
        // 初始化数组
        int[] arr = new int[n];
        for(int i = 0; i < n; i++){
            arr[i] = i + 1;
        }
        
        int count = 0;      // 报数计数器
        int index = 0;      // 当前索引
        int remaining = n;  // 剩余人数 ✅ 新增
        
        System.out.println("出列的人是：");
        while(remaining > 1){  // ✅ 修改：当剩余人数>1时继续
            if(arr[index] != 0){
                count++;
                if(count == m){
                    System.out.print(arr[index] + " ");
                    //同时输出出列玩家的姓名
                    System.out.println("(姓名: " + players.get(arr[index]) + ")");
                    arr[index] = 0;
                    count = 0;
                    remaining--;  // ✅ 剩余人数减1
                }
            }
            index = (index + 1) % n;
        }
        
        // 找出最后剩下的人
        System.out.println();  // 换行
        for(int i = 0; i < n; i++){
            if(arr[i] != 0){
                System.out.println("最后剩下的人是: " + arr[i]);
                break;
            }
        }
        
        sc.close();
    }
}