package tutorial.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorial.espeak.Espeak;
import tutorial.espeak.Voice;

@Data
@Configuration
@ConfigurationProperties("espeak")
public class ESpeakConfig {

    private int pitch = 50;

    private int amplitude = 100;

    private int speed = 175;

    private int gap = 0;

    @Bean
    public Espeak eSpeak() {
        Voice voice = new Voice();
        voice.setPitch(getPitch());
        voice.setAmplitude(getAmplitude());
        voice.setSpeed(getSpeed());
        voice.setGap(getGap());
        Espeak espeak = new Espeak(voice);
        return espeak;
    }
}
