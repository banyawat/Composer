package cpe.com.composer.soundengine;

public class ComposerGesture {
    private int id;
    private String title;
    private int detail;
    private int flag;

    public ComposerGesture(int id, String title, int detail, int flag){
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getFlag() {
        return flag;
    }

    public int getDetail() {
        return detail;
    }
}
