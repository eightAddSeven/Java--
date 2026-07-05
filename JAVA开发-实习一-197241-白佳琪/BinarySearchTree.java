/**
 * 二分搜索树实现
 * 要求：
 * 1. 不能使用模板（泛型）
 * 2. 使用Comparable实现通用数据存储
 * 3. 使用嵌套类优化
 * 4. 实现insert(), visit(), search()方法
 * 5. 处理异常
 * 6、这句话用来验证实习三的保存功能
 */
import java.lang.Comparable;

public class BinarySearchTree<T extends Comparable<T>> {
    // 定义节点类
    private class Node{
        T data;
        Node left;
        Node right;
        Node(T data){
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;
    private int size;

    //构造二叉搜索树
    public BinarySearchTree(){
        root=null;
        size=0;
    }

    public Node insertRec(Node node,T value){
        if(node==null){
            return new Node(value);
        }

        int cmp=value.compareTo(value);

        if(cmp<0)
        {
            node.left=insertRec(node.left,value);
        }
        else{
            node.right=insertRec(node.right,value);
        }

        return node;
    }

    //插入
    public void insert(T value){
        root =insertRec(root, value);
        size++;
    }

    //查找
    public boolean search(T value){
        return containsRec(root,value);
    }

    public boolean containsRec(Node node,T value){
        if(node==null){
            return false;
        }

        int cmp=value.compareTo(node.data);

        if(cmp<0)
        {
            return containsRec(node.left, value);
        }
        else if(cmp>0){
            return containsRec(node.right, value);
        }
        else{
            return true;
        }
    }

    public void visit(){
        visitRec(root);
    }

    //访问visit
    public void visitRec(Node node){
        if(node==null)return;

        visitRec(node.left);
        System.out.println(node.data);
        visitRec(node.right);
    }

    public static void main(String[] args)
    {
        BinarySearchTree<Integer> bst=new BinarySearchTree<>();
        bst.insert(5);
        bst.insert(3);
        bst.insert(7);
        bst.insert(2);
        bst.insert(4);
        bst.insert(6);
        bst.insert(8);
        System.out.println("中序遍历：");
        bst.visit();
        System.out.println("搜索7: " + bst.search(7)); // true
        System.out.println("搜索10: " + bst.search(10)); // false

    }
}