import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.StemmerUtil;

import java.io.IOException;

/**
 * Created by Domi on 06.07.17.
 */
public class DoubleVowelFilter extends TokenFilter {
    private static final int N = 0;
    private static final int V = 1;
    private static final int U = 2;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public DoubleVowelFilter(TokenStream input) {
        super(input);
    }

    public boolean incrementToken() throws IOException {
        if(this.input.incrementToken()) {
            char[] buffer = this.termAtt.buffer();
            int length = this.termAtt.length();

            for(int i = 0; i < length; ++i) {
                char c = buffer[i];
                switch(c) {
                    case 'a':
                        if(i < length - 1) {
                            if (buffer[i + 1] == 'a') {
                                System.arraycopy(buffer, i + 1, buffer, i, length - i - 1);
                                --length;
                            }
                        }
                        break;
                    case 'e':
                        if(i < length - 1) {
                            if (buffer[i + 1] == 'e') {
                                System.arraycopy(buffer, i + 1, buffer, i, length - i - 1);
                                --length;
                            }
                        }break;
                    case 'i':
                        if(i < length - 1) {
                            if (buffer[i + 1] == 'i') {
                                System.arraycopy(buffer, i + 1, buffer, i, length - i - 1);
                                --length;
                            }
                        }
                        break;
                    case 'o':
                        if(i < length - 1) {
                            if (buffer[i + 1] == 'o') {
                                System.arraycopy(buffer, i + 1, buffer, i, length - i - 1);
                                --length;
                            }
                        }
                        break;
                    case 'u':
                        if(i < length - 1) {
                            if (buffer[i + 1] == 'u') {
                                System.arraycopy(buffer, i + 1, buffer, i, length - i - 1);
                                --length;
                            }
                        }
                        break;
                    default:
                }
            }
            this.termAtt.setLength(length);
            return true;
        } else {
            return false;
        }
    }
}