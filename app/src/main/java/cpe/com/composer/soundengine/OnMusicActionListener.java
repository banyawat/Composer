package cpe.com.composer.soundengine;

public interface OnMusicActionListener {
    void onTrackAdded(int id, String title, int program);
    void onTrackDeleted(int id);
    void onTrackReplaced(int removeId,int id, String title, int program);
}
