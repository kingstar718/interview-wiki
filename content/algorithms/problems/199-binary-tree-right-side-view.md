---
topics:
  - 二叉树
techniques:
  - BFS层序
---

# 199. 二叉树的右视图（Binary Tree Right Side View）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

从二叉树右侧看过去，返回从上到下能看到的所有节点值。

**示例**：
```
输入: [1,2,3,null,5,null,4]
输出: [1,3,4]
```

## 思路

**层序遍历取每层最后一个**：BFS 逐层遍历，每层记录最后一个节点值加入结果。

**DFS 版（先右后左）**：根 → 右 → 左的顺序 DFS，depth 首次到达时记录该节点——等价于"先走右边，每层第一个访问到的就是右视图看到的"。

## 代码

```java
// BFS 版 — 直观但 O(n) 空间
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    if (root == null) return res;
    Queue<TreeNode> q = new ArrayDeque<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int size = q.size();
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            if (i == size - 1) res.add(node.val);  // 每层最后一个
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
    }
    return res;
}
```

```java
// DFS 版 — O(height) 空间（优先推荐）
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    dfs(root, 0, res);
    return res;
}

private void dfs(TreeNode node, int depth, List<Integer> res) {
    if (node == null) return;
    if (depth == res.size()) res.add(node.val);  // 每层第一个访问到的节点
    dfs(node.right, depth + 1, res);   // 先右后左
    dfs(node.left, depth + 1, res);
}
```

## 层属性 vs 路径属性 + BFS 分层不变式（二叉树组 4/6）

```
用户两版演进全亲手写，双坑实录：
v1 择一入队贪心（右优先无右取左）——单条路径走天下，右路断头丢整棵子树
  （1/2(4,5),3：1→3 停，5 属于被弃的 2 的子树 → [1,3] 错，应 [1,3,5]）
  病根：把"每层最右"当"每层走最右孩子"——前者是【层属性】（要看整层才能判），
  后者是【路径属性】（单路径贪心）
  【自测全绿≠语义对】第 3 例：6 个哨兵全是"右路畅通/断头不深"形状，贪心恰好全过——
  哨兵要按【算法的死法】设计，不是按题面示例（骨架补救：加"右路断头"哨兵）
v2 右先入队（right 先 offer）——层内从右到左排队，i==size-1 收到最左（[1,2,5]）
v3 正解：左先右后入队 → 本层在队列从左到右 → 末位=最右；size 锚+双子全入+末位收集
BFS 分层不变式（用户问"怎么保证连续"）：
  下一层节点只会由"弹出本层节点"产生（offer 只在 poll 后）→ 本层入队时刻全早于下层任一节点
  → FIFO 下层永远排在本层之后 → 同层天然连续；归纳：每轮开始队列=下一层全部节点从左到右
  收集时刻判据：不依赖节点是谁的孩子（左孩子当本层最右的 case 自然通过）
```

## 复杂度

- **BFS**：时间 O(n)，空间 O(n)
- **DFS**：时间 O(n)，空间 O(height)

## 边界条件

- 空树：返回空列表
- 单节点：返回 [val]
- 单支树：整条链都是右视图（DFS 先走右 = 一直向下，直到右到底才走左）

## 变式

- **左视图**：BFS 取每层第一个，或 DFS 先左后右
- **俯视图**：按 x 坐标分桶，层序保证覆盖
- **[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md)**：输出整层而不是最后一个

## 易错点

- DFS 版 `depth == res.size()` 的时机：第一次到达新的一层时才会触发，因为有 right 优先，所以每层第一个访问到的就是右视图
- BFS 版 `i == size - 1` 判定每层最后一个元素——不要在 for 循环外判断 queue 是否为空来取最后一个
- 不要混淆层数 depth 和结果列表的 index：res.size() 表示已经记录到的层数

## 面试追问

- **DFS 版的思路来源？** 本质是让右子树优先遍历，每层"第一个"访问到的节点就是该层最右的——层序遍历的"每层最后一个"在 DFS 里等价于"右优先的首个"。能答出两种解法说明遍历方式理解透彻

## 关联题

- 同套路：[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md) —— 层序遍历的所有变式都是这个模板
- 进阶：[236. 二叉树的最近公共祖先](236-lowest-common-ancestor-of-a-binary-tree.md) —— 另一种"不需要处理整层"的递归解法
- 知识点：DFS 遍历顺序的灵活控制（先右后左）见[二叉树](../二叉树.md)

