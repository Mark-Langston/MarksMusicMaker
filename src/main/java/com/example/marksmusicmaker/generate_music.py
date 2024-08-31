from music21 import stream, note, chord, midi, instrument, tempo, scale
import random

def generate_melody(scale, length_in_seconds, tempo_bpm):
    try:
        print("Generating melody...")
        melody = stream.Stream()
        melody.append(tempo.MetronomeMark(number=tempo_bpm))

        total_time = 0
        note_count = 0
        while total_time < length_in_seconds and note_count < 200:  # Limit to 200 notes
            pitch = random.choice(scale.pitches)
            n = note.Note(pitch)
            n.quarterLength = random.choice([0.5, 1.0, 1.5])
            n.volume.velocity = random.randint(60, 100)

            if total_time + n.quarterLength > length_in_seconds:
                n.quarterLength = length_in_seconds - total_time

            melody.append(n)
            total_time += n.quarterLength * 60 / tempo_bpm
            note_count += 1
            if note_count % 20 == 0:  # Log every 20 notes
                print(f"Added note {note_count}: {n}, Total time: {total_time:.2f}s")

        print(f"Melody generated successfully with {len(melody)} notes.")
        return melody
    except Exception as e:
        print(f"Error generating melody: {e}")

def generate_chord_progression(scale, length_in_seconds, tempo_bpm):
    try:
        print("Generating chord progression...")
        chords = stream.Stream()
        chords.append(tempo.MetronomeMark(number=tempo_bpm))

        chord_options = [
            [scale.pitches[i], scale.pitches[i + 2], scale.pitches[i + 4]]
            for i in range(len(scale.pitches) - 4)
        ]

        total_time = 0
        chord_count = 0
        while total_time < length_in_seconds and chord_count < 50:  # Limit to 50 chords
            chosen_pitches = random.choice(chord_options)
            new_chord = chord.Chord(chosen_pitches)
            new_chord.quarterLength = random.choice([1.0, 2.0])
            new_chord.volume.velocity = random.randint(50, 80)

            if total_time + new_chord.quarterLength > length_in_seconds:
                new_chord.quarterLength = length_in_seconds - total_time

            chords.append(new_chord)
            total_time += new_chord.quarterLength * 60 / tempo_bpm
            chord_count += 1
            if chord_count % 10 == 0:  # Log every 10 chords
                print(f"Added chord {chord_count}: {new_chord}, Total time: {total_time:.2f}s")

        print(f"Chord progression generated successfully with {len(chords)} chords.")
        return chords
    except Exception as e:
        print(f"Error generating chords: {e}")

def add_orchestral_instruments(song):
    try:
        print("Adding orchestral instruments...")
        instruments = [
            instrument.Violin(), instrument.Viola(), instrument.Violoncello(), instrument.Contrabass(),
            instrument.Flute(), instrument.Oboe(), instrument.Bassoon(),
            instrument.Horn(), instrument.Trumpet(), instrument.Trombone(),
            instrument.Harp(), instrument.Percussion()
        ]

        for inst in instruments[:4]:  # Limiting to 4 instruments for testing
            part = stream.Part()
            part.append(inst)
            song.insert(0, part)
            print(f"Added {inst.instrumentName}")

        print("Instruments added successfully.")
    except Exception as e:
        print(f"Error adding instruments: {e}")

def generate_1_minute_song(filename):
    try:
        print("Generating 1-minute song...")
        chosen_scale = scale.MajorScale('C')
        length_in_seconds = 60  # 1 minute length
        tempo_bpm = random.randint(110, 130)  # Random tempo for variety

        melody = generate_melody(chosen_scale, length_in_seconds, tempo_bpm)
        harmony = generate_chord_progression(chosen_scale, length_in_seconds, tempo_bpm)

        song = stream.Stream()
        add_orchestral_instruments(song)

        song.append(melody)
        song.append(harmony)

        # Save to MIDI file
        try:
            print("Saving to MIDI file...")
            mf = midi.translate.music21ObjectToMidiFile(song)
            mf.open(filename, 'wb')
            mf.write()
            mf.close()
            print(f"MIDI file saved as {filename}")
        except Exception as e:
            print(f"Error saving MIDI file: {e}")
    except Exception as e:
        print(f"Error generating song: {e}")

if __name__ == "__main__":
    import sys
    import os
    if len(sys.argv) != 2:
        print("Usage: python generate_music.py <output_filename>")
    else:
        output_path = os.path.join(os.path.dirname(__file__), sys.argv[1])
        generate_1_minute_song(output_path)
