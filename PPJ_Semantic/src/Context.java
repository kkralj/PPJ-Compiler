public class Context {

    private Context parent;
    private String functionName;

    public Context(Context parent) {
        this.parent = parent;
    }

    public Context getParent() {
        return parent;
    }

    public void setParent(Context parent) {
        this.parent = parent;
    }

    public boolean isDeclared(Node child) {
        return false;
    }
}
