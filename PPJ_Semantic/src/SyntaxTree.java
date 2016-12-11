import java.util.List;

public class SyntaxTree {

    private Node root;

    public Node build(List<String> lines) {
        Node node = null;

        for (String line : lines) {
            int nodeDepth = node != null ? countPrefixSpace(node.data) : -1;
            int currentDepth = countPrefixSpace(line);

            if (node == null) {
                node = new Node(line, null);

            } else if (currentDepth > nodeDepth) {
                Node child = new Node(line, node);
                node.addChild(child);
                node = child;

            } else {
                for (int i = 0; i < nodeDepth - currentDepth; i++) {
                    node = node.parent;
                }

                Node child = new Node(line, node.parent);
                node.parent.addChild(child);
                node = child;
            }
        }

        while (node.parent != null) {
            node = node.parent;
        }

        return this.root = cleanSyntaxTree(node);
    }

    private int countPrefixSpace(String s) {
        int res = 0;

        while (res < s.length() && s.charAt(res) == ' ') {
            res++;
        }

        return res;
    }

    private Node cleanSyntaxTree(Node root) {
        root.data = root.data.trim();

        for (Node node : root.children) {
            cleanSyntaxTree(node);
        }

        return root;
    }

    public Node getRoot() {
        return root;
    }
}