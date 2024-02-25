import java.util.LinkedList
import java.util.Queue

/**
 * desc: TreeNodeTest
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/12/11 18:05
 */
// 定义树的节点类
class TreeNode<T>(val value: T) {
  val children: MutableList<TreeNode<T>> = mutableListOf()
}

// 定义树类
class Tree<T>(rootValue: T) {
  private val root = TreeNode(rootValue)

  // 添加子节点
  fun addChild(parent: TreeNode<T>, childValue: T) {
    val childNode = TreeNode(childValue)
    parent.children.add(childNode)
  }

  // 删除子节点
  fun removeChild(parent: TreeNode<T>, child: TreeNode<T>) {
    parent.children.remove(child)
  }

  // 查找节点
  fun findNode(value: T, node: TreeNode<T>? = root): TreeNode<T>? {
    if (node?.value == value) {
      return node
    }
    node?.children?.forEach { child ->
      val result = findNode(value, child)
      if (result != null) {
        return result
      }
    }
    return null
  }

  // 深度优先搜索遍历(DFS)
  fun depthFirstTraversal(node: TreeNode<T>? = root, visit: (T) -> Unit) {
    if (node != null) {
      visit(node.value)
      node.children.forEach { child ->
        depthFirstTraversal(child, visit)
      }
    }
  }

  // 广度优先搜索遍历(BFS)
  fun breadthFirstTraversal(visit: (T) -> Unit) {
    val queue: Queue<TreeNode<T>> = LinkedList()
    queue.add(root)

    while (queue.isNotEmpty()) {
      val node = queue.remove()
      visit(node.value)
      node.children.forEach {
        queue.add(it)
      }
    }
  }

}

fun main() {
  // 创建一个整型树
  val tree = Tree(1)

  // 添加子节点
  val rootNode = tree.findNode(1)
  if (rootNode != null) {
    tree.addChild(rootNode, 2)
    tree.addChild(rootNode, 3)
  }

  // 查找节点
  val node = tree.findNode(2)
  if (node != null) {
    println("找到节点: ${node.value}")
  }

  // 遍历整个树方式一
  tree.depthFirstTraversal { value ->
    println("遍历树结果1：$value")
  }

  // 遍历整个树方式二
  tree.breadthFirstTraversal { value ->
    println("遍历树结果2：$value")
  }
}
