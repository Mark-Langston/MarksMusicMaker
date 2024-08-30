package com.example.marksmusicmaker;

import javax.sound.midi.*;
import javax.swing.*;
import java.io.File;
import java.util.Map;
import java.util.Random;

public class MusicGenerator {
    private static final int MAX_LENGTH_TICKS = 1440 * 3; // 3 minutes at 120 BPM (1440 ticks per minute)

    public void generate(String filename, Map<String, Boolean> selectedInstruments, JProgressBar progressBar) {
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            if (synthesizer.getDefaultSoundbank() != null) {
                synthesizer.loadAllInstruments(synthesizer.getDefaultSoundbank());
            }

            Sequence sequence = new Sequence(Sequence.PPQ, 4);
            Track track = sequence.createTrack();
            Random random = new Random();
            int tick = 0;
            int totalTicks = MAX_LENGTH_TICKS;

            while (tick < MAX_LENGTH_TICKS) {
                for (Map.Entry<String, Boolean> entry : selectedInstruments.entrySet()) {
                    if (entry.getValue()) {
                        tick = addRandomNotes(track, getChannelForInstrument(entry.getKey()), getProgramForInstrument(entry.getKey()), tick, random);
                    }
                }
                int progress = (int) ((tick / (float) totalTicks) * 100);
                progressBar.setValue(progress); // Update the progress bar
            }

            File midiFile = new File(filename.endsWith(".midi") ? filename : filename + ".midi");
            MidiSystem.write(sequence, 1, midiFile);

            progressBar.setValue(100); // Set progress bar to 100% on completion
            JOptionPane.showMessageDialog(null, "Music generated and saved as " + filename);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error generating music: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int addRandomNotes(Track track, int channel, int program, int startTick, Random random) throws InvalidMidiDataException {
        ShortMessage programChange = new ShortMessage();
        programChange.setMessage(ShortMessage.PROGRAM_CHANGE, channel, program, 0);
        track.add(new MidiEvent(programChange, startTick));

        int[] notes = {60, 62, 64, 65, 67, 69, 71, 72}; // C major scale
        int duration = random.nextInt(8) + 1; // Duration between 1 and 8 ticks

        for (int i = 0; i < 8; i++) {
            int note = notes[random.nextInt(notes.length)];
            int velocity = random.nextInt(40) + 80;
            track.add(createMidiEvent(ShortMessage.NOTE_ON, channel, note, velocity, startTick));
            track.add(createMidiEvent(ShortMessage.NOTE_OFF, channel, note, 0, startTick + duration));
            startTick += duration + random.nextInt(4);
        }

        return startTick;
    }

    private MidiEvent createMidiEvent(int command, int channel, int note, int velocity, int tick) throws InvalidMidiDataException {
        ShortMessage message = new ShortMessage();
        message.setMessage(command, channel, note, velocity);
        return new MidiEvent(message, tick);
    }

    private int getChannelForInstrument(String instrument) {
        // Map instrument names to MIDI channels
        switch (instrument) {
            case "Drums": return 9;
            default: return 0; // Default to channel 0 for most instruments
        }
    }

    private int getProgramForInstrument(String instrument) {
        // Map instrument names to MIDI program numbers (0-127)
        switch (instrument) {
            case "Piano": return 0;
            case "Electric Piano": return 4;
            case "Organ": return 16;
            case "Guitar": return 24;
            case "Bass": return 32;
            case "Strings": return 48;
            case "Ensemble": return 48;
            case "Brass": return 56;
            case "Reed": return 64;
            case "Pipe": return 72;
            case "Synth Lead": return 80;
            case "Synth Pad": return 88;
            case "Synth Effects": return 96;
            case "Ethnic": return 104;
            case "Percussive": return 112;
            case "Sound Effects": return 120;
            default: return 0; // Default to Acoustic Grand Piano
        }
    }
}