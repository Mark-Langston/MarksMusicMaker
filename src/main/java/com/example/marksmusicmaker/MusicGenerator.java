package com.example.marksmusicmaker;

import javax.sound.midi.*;
import javax.swing.*;
import java.io.File;
import java.util.Random;

public class MusicGenerator {
    private static final int MAX_LENGTH_TICKS = 1440; // 1 minute at 120 BPM (1440 ticks per minute)

    public void generate(String filename, JProgressBar progressBar) {
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            if (synthesizer.getDefaultSoundbank() != null) {
                synthesizer.loadAllInstruments(synthesizer.getDefaultSoundbank());
            }

            Sequence sequence = new Sequence(Sequence.PPQ, 4);

            // Create different tracks for melody, harmony, bass, and percussion
            Track melodyTrack = sequence.createTrack();
            Track harmonyTrack = sequence.createTrack();
            Track bassTrack = sequence.createTrack();
            Track percussionTrack = sequence.createTrack();

            Random random = new Random();

            // Add tempo meta message (120 BPM)
            MetaMessage tempoMessage = new MetaMessage();
            int tempo = 500000; // microseconds per quarter note (120 BPM)
            byte[] data = {(byte) (tempo >> 16), (byte) (tempo >> 8), (byte) tempo};
            tempoMessage.setMessage(0x51, data, data.length);
            melodyTrack.add(new MidiEvent(tempoMessage, 0));

            // Generate structured music
            int tick = 0;
            while (tick < MAX_LENGTH_TICKS) {
                // Melody
                tick = addStructuredNotes(melodyTrack, 0, getProgramForInstrument("Strings"), tick, random, new int[]{60, 62, 64, 65, 67, 69, 71, 72}, 80, 100);

                // Harmony
                tick = addStructuredNotes(harmonyTrack, 1, getProgramForInstrument("Brass"), tick, random, new int[]{48, 50, 52, 53, 55, 57, 59, 60}, 60, 80);

                // Bass
                tick = addStructuredNotes(bassTrack, 2, getProgramForInstrument("Bass"), tick, random, new int[]{36, 38, 40, 41, 43, 45, 47, 48}, 70, 90);

                // Percussion
                addPercussionNotes(percussionTrack, 9, tick, random);

                int progress = (int) ((tick / (float) MAX_LENGTH_TICKS) * 100);
                progressBar.setValue(progress); // Update the progress bar
            }

            // Adjust end notes to make the loop seamless
            ensureSeamlessLoop(melodyTrack);
            ensureSeamlessLoop(harmonyTrack);
            ensureSeamlessLoop(bassTrack);

            File midiFile = new File(filename.endsWith(".midi") ? filename : filename + ".midi");
            MidiSystem.write(sequence, 1, midiFile);

            progressBar.setValue(100); // Set progress bar to 100% on completion
            JOptionPane.showMessageDialog(null, "Music generated and saved as " + filename);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error generating music: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int addStructuredNotes(Track track, int channel, int program, int startTick, Random random, int[] scale, int minVelocity, int maxVelocity) throws InvalidMidiDataException {
        // Program change to set the instrument
        ShortMessage programChange = new ShortMessage();
        programChange.setMessage(ShortMessage.PROGRAM_CHANGE, channel, program, 0);
        track.add(new MidiEvent(programChange, startTick));

        // Define a rhythm pattern and add notes
        int[] rhythmPattern = {4, 4, 8, 2, 2, 4, 4}; // Simple rhythm pattern in ticks

        // Create structured melody by iterating over rhythm patterns
        for (int duration : rhythmPattern) {
            if (startTick >= MAX_LENGTH_TICKS) break; // Prevent exceeding 1 minute

            int note = scale[random.nextInt(scale.length)];
            int velocity = random.nextInt(maxVelocity - minVelocity) + minVelocity;

            track.add(createMidiEvent(ShortMessage.NOTE_ON, channel, note, velocity, startTick));
            track.add(createMidiEvent(ShortMessage.NOTE_OFF, channel, note, 0, startTick + duration));
            startTick += duration;
        }

        return startTick;
    }

    private void addPercussionNotes(Track track, int channel, int startTick, Random random) throws InvalidMidiDataException {
        // Add a basic percussion rhythm
        int[] percussionNotes = {35, 38, 42, 46}; // Bass drum, snare, closed hi-hat, open hi-hat
        int[] rhythmPattern = {4, 4, 8, 4, 4, 2, 2}; // Simple rhythm pattern for drums

        for (int duration : rhythmPattern) {
            if (startTick >= MAX_LENGTH_TICKS) break; // Prevent exceeding 1 minute

            int percussionNote = percussionNotes[random.nextInt(percussionNotes.length)];
            track.add(createMidiEvent(ShortMessage.NOTE_ON, channel, percussionNote, 100, startTick));
            track.add(createMidiEvent(ShortMessage.NOTE_OFF, channel, percussionNote, 0, startTick + duration));
            startTick += duration;
        }
    }

    private void ensureSeamlessLoop(Track track) throws InvalidMidiDataException {
        // Ensure the last notes are in harmony with the first notes for seamless looping
        if (track.size() > 1) {
            MidiEvent firstEvent = track.get(1); // Skips the tempo message
            MidiEvent lastEvent = track.get(track.size() - 1);

            // Adjust the last note off to align with the loop point
            ShortMessage lastNoteOff = (ShortMessage) lastEvent.getMessage();
            if (lastNoteOff.getCommand() == ShortMessage.NOTE_OFF) {
                track.add(new MidiEvent(lastNoteOff, MAX_LENGTH_TICKS));
            }
        }
    }

    private MidiEvent createMidiEvent(int command, int channel, int note, int velocity, int tick) throws InvalidMidiDataException {
        ShortMessage message = new ShortMessage();
        message.setMessage(command, channel, note, velocity);
        return new MidiEvent(message, tick);
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
