public class ModeFactory {

    public static Validator getValidator(int mode) {
        switch (mode) {
            case 0:
                return new Mode0Validator();
            default:
                throw new IllegalArgumentException("Invalid mode");

        }
    }

}
