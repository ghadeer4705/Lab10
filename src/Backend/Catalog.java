package Backend;

class Catalog
{
     private boolean current; // True if there is a game in progress, False otherwise.
   private boolean allModesExist; // True if there is at least one game available for each

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
