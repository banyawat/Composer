package cpe.com.composer.soundengine;

public class ChordCell {
    private int id;
    private String title;
    private int key;
    private int minor=0;

    public ChordCell(int id, String title, int key, int minor){
        this.id = id;
        this.title = title;
        this.key = key;
        this.minor = minor;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }
}
