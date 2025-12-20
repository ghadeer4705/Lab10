package Backend;
//meno n3rf law fy game mawgoda wla la2 w law fy kol mode game mawgoda wla la2
public class Catalog
{
    private boolean current; // True if there is a game in progress, False otherwise.
   private boolean allModesExist; // True if there is at least one game available for each mode, False otherwise.

    public Catalog(){
        this.current = false;
        this.allModesExist = false;
    }
    public Catalog(boolean current,boolean allModesExist){
        this.current = current;
        this.allModesExist = allModesExist;
    }
    public boolean isCurrent() {
        return current;
    }
    public void setCurrent(boolean current) {
        this.current = current;
    }
    public boolean isAllModesExist() {
        return allModesExist;
  }
    public void setAllModesExist(boolean allModesExist) {
        this.allModesExist = allModesExist;
    }
}
