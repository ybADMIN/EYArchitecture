package mvp.data.download.down.entity;

import java.io.File;

/**
 * Created by ericYang on 2017/6/15.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class ChuckRelevance {
    public File tempFile;
    public File saveFile;

    public ChuckRelevance( File saveFile,File tempFile) {
        this.tempFile = tempFile;
        this.saveFile = saveFile;
    }
    public ChuckRelevance(String saveFile,String tempFile ) {
        this.tempFile =new File(tempFile);
        this.saveFile = new File(saveFile);
    }
}
