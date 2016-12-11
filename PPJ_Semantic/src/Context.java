public class Context {

    private Context parent;

    public Context(Context parent) {
        this.parent = parent;
    }

    public Context getParent() {
        return parent;
    }

    public void setParent(Context parent) {
        this.parent = parent;
    }
}
