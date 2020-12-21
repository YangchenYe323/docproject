package yangchen.experiment.docproject.Component;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates the chinese text parsing library, which will be used
 * to parse policy text to a list of tokens for further analysis
 */
@Component
public class ParsingComponent {

    private String text;
    private List<String> tokens = null;

    public ParsingComponent(){
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    public List<String> getTokens(){
        if (tokens == null){
            //do the parse work
            tokens = new ArrayList<>();
            List<Word> words = WordSegmenter.seg(text);
            for(Word word: words){
                tokens.add(word.toString());
            }
        }

        return tokens;
    }

}
