package cpe.com.composer.datamanager;

import com.leff.midi.event.ProgramChange;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cpe.com.composer.R;

public class ComposerParam {
    public static final int DEFAULT_PPQ = 120;
    public static final int DEFAULT_NOTEDUR = 120;
    public static final int DEFAULT_FINGER = 5;
    public static final int INPUT_NUM = 3;
    public static final String BUNDLE_KEY = "presetbundle";
    public static final String MENU_BUNDLE_KEY = "menukey";
    public static final int MAX_PANELSLOT = 4;
    public static final Map<Integer, Integer> INSTRUMENT_MAP;
    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(ProgramChange.MidiProgram.ELECTRIC_GUITAR_CLEAN.programNumber(), R.drawable.ic_electric_guitar);
        map.put(ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber(), R.drawable.ic_bass);
        map.put(ProgramChange.MidiProgram.ACOUSTIC_GRAND_PIANO.programNumber(), R.drawable.ic_grand_piano);
        map.put(-1, R.drawable.ic_drum);
        map.put(-2, R.drawable.ic_key);
        map.put(-3, R.drawable.ic_beat);
        INSTRUMENT_MAP = Collections.unmodifiableMap(map);
    }
}
