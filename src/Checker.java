import java.util.List;

public interface Checker extends Runnable {
    List<DuplicateInfo> getResult();
}

