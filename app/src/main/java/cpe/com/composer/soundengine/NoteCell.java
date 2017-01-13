package cpe.com.composer.soundengine;

public class NoteCell {
    private int channel;
    private int pitch;
    private int velocity;
    private int ppq;
    private int duration;

    public NoteCell(int channel, int pitch, int velocity, int ppq, int duration){
        this.channel = channel;
        this.pitch = pitch;
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

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
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
