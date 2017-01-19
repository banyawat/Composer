package cpe.com.composer.soundengine;

/**
 *  This class use to store information about track
 *  That import from database
 */
public class TrackCell {
    private int ID;
    private String title;
    private int channel;
    private int program;
    private String note;

    public TrackCell(int ID, String title, int channel, int program, String note){
        this.ID = ID;
        this.title = title;
        this.channel = channel;
        this.program = program;
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getProgram() {
        return program;
    }

    public void setProgram(int program) {
        this.program = program;
    }

}
