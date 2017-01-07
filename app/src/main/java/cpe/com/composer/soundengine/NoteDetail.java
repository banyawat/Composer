package cpe.com.composer.soundengine;

public class NoteDetail {
    private int channel;
    private int note;
    private int velocity;
    private int ppq;
    private int duration;

    public NoteDetail(int channel, int note, int velocity, int ppq, int duration){
        this.channel = channel;
        this.note = note;
        this.velocity = velocity;
        this.ppq = ppq;
        this.duration = duration;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getPpq() {
        return ppq;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setPpq(int ppq) {
        this.ppq = ppq;
    }
}
