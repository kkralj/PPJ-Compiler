import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SemantickiAnalizator {

    public static class SyntaxTree {
        private Node root;

        public SyntaxTree(Node root) {
            this.root = root;
        }
        
        public void print() {
            root.print(0);
        }
    }

    public static class Node {
        public String data;
        public Node parent = null;
        public List<Node> children = new ArrayList<Node>();

        public Node(String data, Node parent) {
            this.data = data;
            this.parent = parent;
        }

        public void addChild(Node node) {
            children.add(node);
        }

        public void print(int level) {
            System.out.println(whitespace(level) + data);

            for (Node node : children) {
                node.print(level + 1);
            }
        }

        private String whitespace(int length) {
            String res = "";
            for (int i = 0; i < length; i++) {
                res += " ";
            }
            return res;
        }
    }

    private static int countPrefixSpace(String s) {
        int res = 0;

        for (; res < s.length() && s.charAt(res) == ' '; res++)
            ;

        return res;
    }

    private static Node buildSyntaxTree(List<String> lines) {
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

        return cleanSyntaxTree(node);
    }

    private static Node cleanSyntaxTree(Node root) {
        root.data = root.data.trim();

        for (Node node : root.children)
            cleanSyntaxTree(node);

        return root;
    }

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader((args.length > 0 ? new FileInputStream(args[0]) : System.in)))) {
            Node root = buildSyntaxTree(br.lines().collect(Collectors.toList()));

            root.print(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
