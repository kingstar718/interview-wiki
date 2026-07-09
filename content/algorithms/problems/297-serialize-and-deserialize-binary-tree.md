---
topics:
  - 二叉树
---

# 297. 二叉树的序列化与反序列化（Serialize and Deserialize Binary Tree）

频次 ★★★★ · 难度 🔴 · 高频：字节/阿里

## 题目

设计算法将二叉树序列化为字符串，并能反序列化还原为原树。格式不限（本题用前序 + 空标记）。

**示例**：
```
序列化: [1,2,3,null,null,4,5] → "1,2,#,#,3,4,#,#,5,#,#"
反序列化 → 原树
```

## 思路

**前序遍历 + 空标记 `#`**：

- **序列化**：前序遍历，遇到 null 写 `#`，非 null 写节点值，用逗号分隔
- **反序列化**：将字符串按逗号分割成列表，用一个指针**全局消费**列表：读一个值，如果为 `#` 返回 null；否则创建节点，递归构造左子树、右子树

为什么用前序？因为**前序的顺序天然"先根后左右"**，反序列化时读完根就知道接下来该读左子树，且左子树读完一定紧接着读右子树——不需要额外状态。

## 代码

```java
// Encoder
public String serialize(TreeNode root) {
    StringBuilder sb = new StringBuilder();
    serialize(root, sb);
    return sb.toString();
}

private void serialize(TreeNode node, StringBuilder sb) {
    if (node == null) {
        sb.append("#,");
        return;
    }
    sb.append(node.val).append(",");
    serialize(node.left, sb);
    serialize(node.right, sb);
}

// Decoder
public TreeNode deserialize(String data) {
    Queue<String> nodes = new ArrayDeque<>(Arrays.asList(data.split(",")));
    return deserialize(nodes);
}

private TreeNode deserialize(Queue<String> nodes) {
    String val = nodes.poll();
    if ("#".equals(val)) return null;
    TreeNode node = new TreeNode(Integer.parseInt(val));
    node.left = deserialize(nodes);    // 消费完左子树的全部节点后
    node.right = deserialize(nodes);   // 自动接着消费右子树
    return node;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(n) —— 序列化字符串的长度

## 边界条件

- 空树：序列化 = `"#,"`；反序列化返回 null
- 单节点：`"1,#,#,"`
- 负数节点值：`Integer.parseInt` 天然支持

## 变式

- **层序序列化**：[102](102-binary-tree-level-order-traversal.md) 的层序遍历输出，反序列化时用队列逐层填子节点——更符合题目给的示例格式
- **后序/中序序列化**：后序可以但反序列化需要从尾部开始；中序单独不行（根位置不明）
- **判定两棵二叉树是否相同（100）**：序列化后字符串比较

## 易错点

- **分隔符不能忘**：序列化时每个值后加 `,`，包括 `#`。`split(",")` 遇到末尾空串会自动忽略但不影响（因为最后一个 `#` 后也有逗号）
- 反序列化用 `Queue` 来消费列表：保证"用完即弃"的全局指针效果——比用 `int[] index` 更优雅
- `Integer.parseInt` 直接解析节点值，隐含值在 int 范围内
- 没有 `#` 标记来示 null，前序序列化无法唯一还原

## 面试追问

- **为什么前序序列化不需要中序辅助？** 因为 `#` 标记了空指针的位置，使得树的**形状被完整记录**。而 105 题的重建没有空指针信息，所以需要两个序列互补——对比一下展现对二叉树遍历的深度理解
- **如果树很大，序列化字符串存储开销怎么办？** 存储上可以压缩：用二进制格式、哈夫曼编码节点值去重；或优化空标记（差分空标记）

## 关联题

- 同套路：[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md) —— 另一种序列化格式
- 进阶：[105. 从前序与中序遍历构造二叉树](105-construct-binary-tree-from-preorder-and-inorder-traversal.md) —— 无空标记时的重建
- 知识点：递归消费列表的"全局指针"模式见[二叉树](二叉树.md)

