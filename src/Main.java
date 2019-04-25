import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private BinarySearch binarySearch = new BinarySearch();
    private String[] field = new String[3];

    public static void main(String[] args) {
        Main m = new Main();
        m.addAll("shuffled_dict.txt");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(Strings.ConsolePointer);
            switch (scanner.next()) {
                case "add":
                    scanner.nextLine();
                    System.out.print(Strings.WordIndicator);
                    m.field[0] = scanner.nextLine();
                    System.out.print(Strings.ClassIndicator);
                    m.field[1] = scanner.nextLine();
                    System.out.print(Strings.DescIndicator);
                    m.field[2] = scanner.nextLine();
                    m.binarySearch.add(new Node(m.field[0], m.field[1], m.field[2]));
                    break;
                case "delete":
                    m.binarySearch.delete(scanner.next(), false);
                    break;
                case "find":
                    ArrayList<Node> tT = m.binarySearch.find(scanner.next());
                    if (tT.size() <= 0) System.out.println(Strings.NotFound);
                    else for (Node t : tT) System.out.println(t);
                    break;
                case "deleteall":
                    m.deleteAll(scanner.next());
                    break;
                case "size":
                    System.out.printf(Strings.ThereAre, m.binarySearch.size);
                    break;
                case "exit":
                    return;
                default:
                    System.out.println(Strings.UnknownCommand);
                    break;
            }
        }
    }


    private Node readNode(Scanner scanner) {
        String t = scanner.nextLine();
        field[0] = t.substring(0, t.indexOf('(') - 1);
        field[1] = t.substring(t.indexOf('(') + 1, t.lastIndexOf(')'));
        field[2] = t.substring(t.lastIndexOf(')') + 1);
        return new Node(field[0], field[1], field[2]);
    }

    private void addAll(String path) {
        Scanner fScanner;
        try {
            fScanner = new Scanner(new File(path));
            int count = 0;
            while (fScanner.hasNext()) {
                binarySearch.add(readNode(fScanner));
                count++;
            }
            System.out.println(count + Strings.AddAllComplete);
        } catch (FileNotFoundException e) {System.out.println(Strings.NotFound);}
    }

    private void deleteAll(String path) {
        Scanner fScanner;
        try {
            fScanner = new Scanner(new File(path));
            int count = 0;
            while (fScanner.hasNext()) {
                binarySearch.delete(fScanner.nextLine(), true);
                count++;
            }
            System.out.println(count + Strings.DelAllComplete);
        } catch (FileNotFoundException e) {System.out.println(Strings.NotFound);}
    }
}

class BinarySearch {
    int size = 0;
    private Node root = null;
    void add(Node adder) {
        Node t = root, preT = null;
        boolean flag = false;
        int diff;
        if (t == null) {
            root = adder;
            size++;
            return;
        }
        while (true) {
            if (t == null) {
                if (flag) preT.setLeft(adder);
                else preT.setRight(adder);
                size++;
                return;
            }
            diff = adder.word.compareToIgnoreCase(t.word);
            if (diff < 0) {
                preT = t;
                t = t.left;
                flag = true;
            } else {
                preT = t;
                t = t.right;
                flag = false;
            }

        }
    }

    ArrayList<Node> find(String keyword) {
        ArrayList<Node> ret = new ArrayList<>();
        Node t = root;
        int diff;
        while (true) {
            if (t == null) return ret;
            diff = keyword.compareToIgnoreCase(t.word);
            if (diff < 0) t = t.left;
            else if (diff > 0) t = t.right;
            else {
                ret.add(t);
                t = t.right;
            }
        }
    }

    void delete(String keyword, boolean mute) {
        ArrayList<Node> dels = find(keyword);
        boolean flag = false;
        if (dels.size() == 0) {
            if (!mute) System.out.println(Strings.NotFound);
            return;
        }
        while (dels.size() > 0) {
            Node del = dels.get(0);
            Node change = del.getMinRight();
            if (change == null) change = del.getMaxLeft();
            if (change == null) change = del;
            else flag = true;
            if (!del.equals(change)) del.replace(change);
            if (flag && change.right != null) {
                if (change.equals(change.parent.right)) {
                    change.parent.setRight(change.right);
                } else if (change.equals(change.parent.left)) {
                    change.parent.setLeft(change.right);
                }
            } else if (!flag && change.left != null) {
                if (change.equals(change.parent.right)) {
                    change.parent.setRight(change.left);
                } else if (change.equals(change.parent.left)) {
                    change.parent.setLeft(change.left);
                }
            } else {
                if (change.equals(change.parent.right)) {
                    change.parent.setRight(null);
                } else if (change.equals(change.parent.left)) {
                    change.parent.setLeft(null);
                }
            }
            dels = find(keyword);
        }
        if (!mute) System.out.println(Strings.DelAllComplete);
    }
}

class Node {
    String word;
    private String wordClass, desc;
    Node left, right, parent;

    void setLeft(Node target) {
        if (target != null) target.parent = this;
        left = target;
    }

    void setRight(Node target) {
        if (target != null) target.parent = this;
        right = target;
    }

    public Node(String word, String wordClass, String desc) {
        this.word = word;
        this.wordClass = wordClass;
        this.desc = desc;
    }

    @Override
    public String toString() {return word + Strings.Open + wordClass + Strings.Close + desc;}

    Node getMaxLeft() {return left != null ? left.getRightRecursive() : null;}

    Node getMinRight() {return right != null ? right.getLeftRecursive() : null;}

    private Node getLeftRecursive() {return left!=null?left.getLeftRecursive():this;}

    private Node getRightRecursive() { return right!=null?right.getRightRecursive():this;}

    void replace(Node target) {
        word = target.word;
        wordClass = target.wordClass;
        desc = target.desc;
    }
}

class Strings {
    static String NotFound = "Not found";
    static String Open = " (";
    static String Close = ") ";
    static String AddAllComplete = " word(s) successfully added.";
    static String DelAllComplete = " Successfully deleted.";
    static String UnknownCommand = " Unknown Command.";
    static String ConsolePointer = "$ ";
    static String WordIndicator = "word : ";
    static String ClassIndicator = "class : ";
    static String DescIndicator = "meaning : ";
    static String ThereAre = "There are %d word(s).\n";
}