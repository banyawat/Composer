package cpe.com.composer.soundengine;

import android.app.Activity;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

import cpe.com.composer.R;

public class SimplePatch {
    private static final String PATCH_TITLE = "MainPatch.pd";
    private PdUiDispatcher dispatcher;
    private Activity parentActivity;

    public SimplePatch(Activity parentActivity) throws IOException{
        final int sampleRate = AudioParameters.suggestSampleRate();
        final int nOut = Math.min(AudioParameters.suggestOutputChannels(), 2);
        this.parentActivity = parentActivity;
        PdAudio.initAudio(sampleRate, 0, nOut, 8, true);
        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);

        File dir = this.parentActivity.getFilesDir();
        IoUtils.extractZipResource(this.parentActivity.getResources().openRawResource(R.raw.main), dir, true);
        File pdPatch = new File(dir, PATCH_TITLE);
        PdBase.openPatch(pdPatch.getAbsolutePath());
    }

    public void startAudio(){
        PdAudio.startAudio(this.parentActivity);
    }

    public void stopAudio(){
        PdAudio.stopAudio();
    }

}
