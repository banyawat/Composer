package cpe.com.composer.soundengine;

public class ChordCell {
    private int id;
    private String title;
    private int key;

    public ChordCell(int id, String title, int key){
        this.id = id;
        this.title = title;
        this.key = key;
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
}
