package wordcloud;
import java.awt.Color;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Random;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WordCloudController {
    @GetMapping("/ping")
    public String ping() throws java.net.UnknownHostException {
        String s = String.format("LocalHost: %s %s, Remote Address: %s %s\n" , 
            InetAddress.getLocalHost().getHostAddress(),
            InetAddress.getLocalHost().getHostName(),
            InetAddress.getLoopbackAddress().getHostAddress(),
            InetAddress.getLoopbackAddress().getHostName()
        );  
        return "Hello World! From " + s;
    }

    // curl -d '{"text":"some where where some there oh yes no yes"}' -H "Content-Type: application/json" -X POST http://localhost:8082/wordcloud > bod.png
    @PostMapping("/wordcloud")
    public byte[] makeWordCloud(@RequestBody TextForWordCloud textForWordCloud)  throws IOException {    
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();

        InputStream is =  new ByteArrayInputStream(textForWordCloud.getText().getBytes());
        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load( is );
      
        final Dimension dimension = new Dimension(300, 300);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setBackgroundColor(new Color(0xFFFFFF));
        wordCloud.setPadding(0);
        wordCloud.setColorPalette(buildRandomColorPalette(20));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wordCloud.writeToStreamAsPNG(bos);
        return bos.toByteArray();
    }
    private static final Random RANDOM = new Random();

    private static ColorPalette buildRandomColorPalette(final int n) {
        final Color[] colors = new Color[n];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new Color(RANDOM.nextInt(230) + 25, RANDOM.nextInt(230) + 25, RANDOM.nextInt(230) + 25);
        }
        return new ColorPalette(colors);
    }


    static class TextForWordCloud {
        private String text;
        TextForWordCloud() {}
            TextForWordCloud(String text) {
                this.text = text;
        }
    
        public String getText(){
            return text;
        }
    }

}

