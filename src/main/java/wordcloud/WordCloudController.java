package wordcloud;
import java.io.*;
import java.awt.*;
import com.kennycason.kumo.*;
import com.kennycason.kumo.bg.Background;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.FontWeight;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.FontScalar;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.LogFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.wordstart.CenterWordStart;
import com.kennycason.kumo.wordstart.RandomWordStart;
import com.kennycason.kumo.wordstart.WordStartStrategy;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.ws.rs.core.*;

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
        final Dimension dimension = new Dimension(200, 200);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setBackground(new CircleBackground(200));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wordCloud.writeToStreamAsPNG(bos);
        return bos.toByteArray();
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

