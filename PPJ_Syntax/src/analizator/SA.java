package analizator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import analizator.SA.SyntaxTree.Node;

public class SA {

    private List<String> tokens;
    private List<String[]> LRTable = new ArrayList<>();
    private List<String> syncTokens = new ArrayList<>();

    private SyntaxTree syntaxTree;

    private Stack<Node> nodes;
    private Stack<Integer> states;

    public SA(String[] args) {
        input(args);
    }

    private SyntaxTree parse() {
        nodes = new Stack<Node>();
        states = new Stack<Integer>();

        // pocetno stanje
        states.push(0);

        for (int i = 0; i < tokens.size() + 1; i++) {
            String token = "#";
            String tokenData = "";

            if (i < tokens.size()) {
                tokenData = tokens.get(i);
                token = tokenData.split(" ")[0];
            }

            int state = states.peek();

            String action = getAction(token, state);

            if (action.equals("Prihvati")) {
                // korijen sintaksnog stabla
                Node root = nodes.pop();

                syntaxTree = new SyntaxTree(root);
            } else if (action.startsWith("p")) {
                // pomakni

                // napravi novi cvor (list)
                Node tokenNode = new Node(tokenData, state);

                nodes.push(tokenNode);
                states.push(Integer.parseInt(action.substring(1)));
            } else if (action.startsWith("<")) {
                // reduciraj

                String[] splitAction = action.split("->", 2);

                String[] productionTokens = splitProduction(splitAction[1]);

                // novi unutarnji cvor (nezavrsni znak)

                Node productionNode = new Node(splitAction[0], state);
                List<Node> nodeList = new ArrayList<Node>();

                if (productionTokens.length == 1 && productionTokens[0].equals("$")) {
                    // u slucaju epsilon produkcije nista ne skidamo sa stoga
                    productionNode.addChild(new Node("$", state));
                } else {
                    for (int j = 0; j < productionTokens.length; j++) {
                        states.pop();
                        nodeList.add(0, nodes.pop()); // u obrnutom poretku
                    }
                }

                nodeList.forEach(node -> productionNode.addChild(node));

                // nadji akciju stavi u LR tablici
                int row = states.peek() + 1; // +1 jer je prvi red zaglavlje
                int col = LRColumn(splitAction[0]);

                String putAction = LRTable.get(row)[col];

                // pushni nezavrsni znak i stanje iz akcije sBroj
                nodes.push(productionNode);
                states.push(Integer.parseInt(putAction.substring(1)));

                // nismo obradili trenutni ulazni znak
                i--;

            } else {
                // odbaci, nadji sinkronizacijski znak

                // System.err.println("Error! " + tokenData);

                while (i < tokens.size()) {
                    String nextToken = tokens.get(i).split(" ", 2)[0].trim();
                    if (syncTokens.contains(nextToken)) {
                        // pop stack until you find defined action
                        // i++ in the next for iteration

                        findLastValidToken(states, nodes, nextToken);

                        i--;

                        break;
                    }
                    i++;
                }
            }
        }

        return syntaxTree;
    }

    /**
     * Searches through syntax to find last token for which valid action is
     * defined.
     * 
     * @param states
     *            state stack.
     * @param nodes
     *            node stack.
     */
    private void findLastValidToken(Stack<Integer> states, Stack<Node> nodes, String syncToken) {
        Node node = nodes.peek();
        int state = states.peek();

        while (!checkNode(node, state, syncToken)) {
            nodes.pop();
            states.pop();
            node = nodes.peek();
            state = states.peek();
        }
    }

    private boolean checkNode(Node node, int state, String syncToken) {
        String action = getAction(syncToken, state);

        return !action.equals("-");

    }

    private String getAction(String token, int state) {
        String[] LRRow = LRTable.get(state + 1);
        int LRCol = LRColumn(token);

        return LRRow[LRCol];
    }

    private int LRColumn(String token) {
        String[] characters = LRTable.get(0);

        for (int col = 0; col < characters.length; col++) {
            if (characters[col].equals(token.trim())) {
                return col;
            }
        }

        return -1;
    }

    private String[] splitProduction(String production) {
        return production.trim().split(" ");
    }

    private void input(String[] args) {

        // tokens
        try (BufferedReader scanner = new BufferedReader(
                new InputStreamReader(args.length > 0 ? new FileInputStream(args[0]) : System.in))) {

            tokens = scanner.lines().collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // LR(1) table

        try (BufferedReader reader = Files.newBufferedReader(Paths.get("sa.data"))) {
            reader.lines().forEachOrdered(line -> LRTable.add(line.split("\t")));

            int size = LRTable.size();

            for (String syncToken : LRTable.get(size - 1)) {
                syncTokens.add(syncToken);
            }

            LRTable.remove(size - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class SyntaxTree {
        private Node root;

        public SyntaxTree() {
        }

        public SyntaxTree(Node root) {
            this.root = root;
        }

        public void print() {
            root.print(0);
        }

        public static class Node {
            private String data;
            private int state;
            private List<Node> children = new ArrayList<Node>();

            public Node(String data, int state) {
                this.data = data.trim();
                this.state = state;
            }

            /**
             * Prints tree preorder.
             * 
             * @param level
             *            current level in the tree.
             */
            public void print(int level) {
                System.out.println(whitespace(level) + data);

                for (Node node : children) {
                    node.print(level + 1);
                }
            }

            public void addChild(Node child) {
                children.add(child);
            }

            private String whitespace(int length) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < length; i++) {
                    sb.append(' ');
                }

                return sb.toString();
            }
        }
    }

    public static void main(String[] args) {
        SA sa = new SA(args);

        SyntaxTree syntaxTree = sa.parse();

        syntaxTree.print();
    }

}
