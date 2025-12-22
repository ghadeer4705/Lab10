package Frontend;

public class View {
    private Controllable controller;

    public View(Controllable controller) {
        this.controller = controller;
    }

    public Controllable getController() {
        return controller;
    }
}