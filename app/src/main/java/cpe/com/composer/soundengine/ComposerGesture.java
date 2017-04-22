package cpe.com.composer.soundengine;

public class ComposerGesture {
    private int id;
    private String title;
    private int bpm;
    private int flag;

    ComposerGesture(int id, String title, int bpm, int flag){
        this.id = id;
        this.title = title;
        this.bpm = bpm;
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

    int getBpm() {
        return bpm;
    }
}
