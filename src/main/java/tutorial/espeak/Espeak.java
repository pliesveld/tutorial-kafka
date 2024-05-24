package tutorial.espeak;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Reference: http://espeak.sourceforge.net/commands.html
 */
@Log4j2
public class Espeak {

    public static final String COMMAND_ESPEAK = "espeak";
    private Voice voice;

    public Espeak() {
        this(new Voice());
    }

    public Espeak(Voice voice) {
        this.voice = voice;
    }

    /**
     * Create a new espeak process
     *
     * @param text - the text to speak
     */
    public void speak(String text) {
        Thread child = execute(COMMAND_ESPEAK,
                "-v", buildVariant(voice),
                "-p", Integer.toString(voice.getPitch()),
                "-a", Integer.toString(voice.getAmplitude()),
                "-s", Integer.toString(voice.getSpeed()),
                "-g", Integer.toString(voice.getGap()),
                text);

        child.start();
        try {
            child.join(30000);
        } catch (InterruptedException e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    private String buildVariant(Voice voice) {
        StringBuilder builder = new StringBuilder();
        if (voice.getName() != null && !voice.getName().isEmpty()) {
            builder.append(voice.getName());
        }

        if (voice.getVariant() != null && !voice.getVariant().isEmpty()) {
            builder.append("+");
            builder.append(voice.getVariant());
        }

        return builder.toString();
    }

    private static Thread execute(final String ... command) {
        String threadName = "espeak";

        return new Thread(new Runnable() {
            public void run() {
                ProcessBuilder b = new ProcessBuilder(command);
                b.redirectErrorStream(true);
                try {
                    Process process = b.start();

                    readErrors(process);
                    process.waitFor();
                    process.destroy();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            private void readErrors(Process process) throws IOException {
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    System.err.println(line);
                }
            }
        }, threadName);
    }

}
