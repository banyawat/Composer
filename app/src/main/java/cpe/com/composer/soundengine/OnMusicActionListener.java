package cpe.com.composer.soundengine;

public interface OnMusicActionListener {
    void onTrackAdded(int id, String title, int program);
    void onTrackDeleted(int id);
}
