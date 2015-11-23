package michii.de.scannapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import michii.de.scannapp._view.activities.MainActivity;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.conversion.ConcreteStrategy_Ocr_Text;
import michii.de.scannapp.model.business_logic.conversion.ConversionStrategy;
import michii.de.scannapp.model.business_logic.document.Attribute;
import michii.de.scannapp.model.business_logic.document.Conversion;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.data.file.FileUtil;
import michii.de.scannapp.rest.tests.TestUtils;
import michii.de.scannapp.rest.utils.StopWatch;


/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 05.07.2015
 */
@RunWith(AndroidJUnit4.class)
public class C103_PerformanceTest {


    private static final BigDecimal MILLION = new BigDecimal("1000000");
    private static final BigDecimal MILLIARDE = new BigDecimal("1000000000");
    private static final String TAG = "SPEED";
    private static final int PIC_QUALITY_LOW = 0;
    private static final int PIC_QUALITY_MEDIUM = 1;
    private static final int PIC_QUALITY_HIGH = 2;
    private static final int PIC_BOOK = 3;
    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private Context mContext;
    private DatamodelApi mDatamodelApi;

    @Before
    public void setup() {
        mContext = activityTestRule.getActivity();
        mDatamodelApi = new DatamodelApi(mContext);

        TestUtils.cleanDatabase(mContext);
    }

//    @Test
//    public void testLoadRows() throws Exception {
//        int[] times = {100, 1000, 10000};
//        for (int t : times) {
//            int repeat = 10;
//            long max_time = 0;
//            long min_time = 0;
//            long whole_time = 0;
//
//            for (int r = 0; r < repeat; r++) {
//                TestUtils.deleteDocs();
//                for (int j = 0; j < t; j++) {
//                    Document doc = mDatamodelApi.addDoc();
//                }
//
//                StopWatch watch = new StopWatch();
//                watch.start();
//                List<Document> docs = new RushSearch().find(Document.class);
//                watch.stop();
//                assertEquals("Should be " + t, t, docs.size());
//                max_time = getMax(max_time, watch.toValue());
//                min_time = getMin(min_time, watch.toValue());
//                whole_time += watch.toValue();
//            }
//            double avg_time = whole_time / repeat;
//            double avg_avg = avg_time / t;
//            print2("Load", t, min_time, max_time, avg_time, avg_avg);
//        }
//
//    }
//
//
//    @Test
//    public void testSaveRows() throws Exception {
//        int[] times = {100, 1000, 10000};
//
//        for (int t : times) {
//            int repeat = 10;
//            long max_time = 0;
//            long min_time = 0;
//            long whole_time = 0;
//
//            for (int r = 0; r < repeat; r++) {
//
//                TestUtils.deleteDocs();
//                StopWatch watch = new StopWatch();
//                watch.start();
//                for (int j = 0; j < t; j++) {
//                    Document doc = mDatamodelApi.addDoc();
//                }
//                watch.stop();
//                max_time = getMax(max_time, watch.toValue());
//                min_time = getMin(min_time, watch.toValue());
//                whole_time += watch.toValue();
//            }
//            double avg_time = whole_time / repeat;
//            double avg_avg = avg_time / t;
//            print2("Save", t, min_time, max_time, avg_time, avg_avg);
//        }
//
//
//    }

//    @Test
//    public void testSaveRowsInTransition() throws Exception {
//        int[] times = {10, 100, 1000, 10000};
//
//        for (int t : times) {
//            int repeat = 5;
//            long max_time = 0;
//            long min_time = 0;
//            long whole_time = 0;
//
//            for (int r = 0; r < repeat; r++) {
//                TestUtils.cleanDatabase(mContext);
//
//                StopWatch watch = new StopWatch();
//                watch.start();
//                List<Document> docs = new ArrayList<>();
//                for (int j = 0; j < t; j++) {
//                    docs.add(mDatamodelApi.addDoc());
//                }
//                RushCore.getInstance().save(docs);
//                watch.stop();
//
//                max_time = getMax(max_time, watch.toValue());
//                min_time = getMin(min_time, watch.toValue());
//                whole_time += watch.toValue();
//            }
//            double avg_time = whole_time / repeat;
//            double avg_avg = avg_time / t;
//            print2("SaveTransition", t, min_time, max_time, avg_time, avg_avg);
//        }
//    }

//    @Test
//    public void testSaveWithMultiplePics() throws Exception {
//        int[] times = {1, 5, 10, 25};
//        for (int t : times) {
//            int repeat = 5;
//            long max_time = 0;
//            long min_time = 0;
//            long whole_time = 0;
//
//            for (int r = 0; r < repeat; r++) {
//                TestUtils.cleanDatabase(mContext);
//
//                StopWatch watch = new StopWatch();
//                watch.start();
//                Document doc = mDatamodelApi.addDoc();
//                for (int j = 0; j < t; j++) {
////                    mDatamodelApi.addPic(doc, getUriString(PIC_QUALITY_LOW), false);
//                }
////                mDatamodelApi.saveWithPictures(doc);
//                watch.stop();
//                TestUtils.deletePictures(mContext, doc);
//
//                max_time = getMax(max_time, watch.toValue());
//                min_time = getMin(min_time, watch.toValue());
//                whole_time += watch.toValue();
//            }
//            long avg_time = whole_time / repeat;
//            long avg_avg = avg_time / t;
//            print2("SavePics", t, min_time, max_time, avg_time, avg_avg);
//        }
//    }
//
    @Test
    public void testConvertOcr() throws Exception {
        int[] times = {1, 5, 10, 25};
        for (int t : times) {
            int repeat = 5;
            long max_time = 0;
            long min_time = 0;
            long whole_time = 0;

            for (int r = 0; r < repeat; r++) {
                TestUtils.deleteDocs();
                Document doc = getDocumentDummy(t, PIC_QUALITY_MEDIUM);

                StopWatch watch = new StopWatch();
                watch.start();
                ConversionStrategy strategy = new ConcreteStrategy_Ocr_Text(mContext);
                mDatamodelApi.doConvert(doc, strategy);
                watch.stop();

                max_time = getMax(max_time, watch.toValue());
                min_time = getMin(min_time, watch.toValue());
                whole_time += watch.toValue();
            }
            double avg_time = whole_time / repeat;
            double avg_avg = avg_time / t;
            print2("Ocr", t, min_time, max_time, avg_time, avg_avg);
        }
    }

//    @Test
//    public void testConvertPdf() throws Exception {
//        int[] times = {1, 5, 10, 25};
//        for (int t : times) {
//            int repeat = 5;
//            long max_time = 0;
//            long min_time = 0;
//            long whole_time = 0;
//
//            for (int r = 0; r < repeat; r++) {
//                TestUtils.cleanDatabase(mContext);
//
//                Document doc = getDocumentDummy(t, PIC_QUALITY_LOW);
//
//                StopWatch watch = new StopWatch();
//                watch.start();
//                ConversionStrategy strategy = new ConcreteStrategy_Pdf(mContext);
//                Uri uri = mDatamodelApi.doConvert(doc, strategy);
//                watch.stop();
//
//                max_time = getMax(max_time, watch.toValue());
//                min_time = getMin(min_time, watch.toValue());
//                whole_time += watch.toValue();
//            }
//            double avg_time = whole_time / repeat;
//            double avg_avg = avg_time / t;
//            print2("Pdf", t, min_time, max_time, avg_time, avg_avg);
//        }
//    }


    private Document getDocumentDummy() {
        Document document = new Document("Name", 0);
        document.addAttribute(new Attribute("note", "this is some note"));
        document.addConversion(new Conversion());
        return document;
    }

    private Document getDocumentDummy(int times, int pic_quality) throws IOException {
        Document doc = mDatamodelApi.addDoc();
        Uri uri =  getUri(pic_quality);
        for (int i = 0; i < times; i++) {
            mDatamodelApi.addPic(doc, uri, false);
        }
        doc.save();
        return doc;
    }

    private Uri getUri(int quality) throws IOException {
        int drawableId;
        switch (quality) {
            case PIC_QUALITY_LOW:
                drawableId = R.drawable.ic_launcher;
                break;
            case PIC_QUALITY_MEDIUM:
                drawableId = R.drawable.bon_ocr;
                break;
            case PIC_QUALITY_HIGH:
                drawableId = R.drawable.ic_launcher;
                break;
            case PIC_BOOK:
                drawableId = R.drawable.test_doc2_img;
                break;
            default:
                drawableId = R.drawable.ic_launcher;
                break;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId);
//        File file = File.createTempFile("popel.jpg", null, folder.getRoot());
//        File file = File.createTempFile("junit", null, folder.getRoot());
        File file = mDatamodelApi.createTempImageFile("pic");
//        File file = folder.newFile();
        file = FileUtil.createImageFile(file, bitmap, 90);
//        File file = FileUtil.createTempImageFile(folder.getRoot(), "test");
//        file = FileUtil.createImageFile(file, bitmap, 90);

        //TODO
        return Uri.fromFile(file);

    }

    private void print(String name, int times, long totalTime, double avgTime) {
        String totalTimeString = toString(totalTime);
        String avgTimeString = toString((long) avgTime);
        String s = name + " (";
        s += "times=" + times + ",";
        s += "totalTime= " + totalTimeString + ", ";
        s += "avgTime= " + avgTimeString + ")";
        Log.d(TAG, s);
    }

    private String trenn = ", ";
    private void print2(String name, int times, long minTime, long maxTime, double avgTime, double avgAvgTime) {
        String s = name + "(";
        s += "times=" + times + trenn;
        s += "min=" + toString(minTime) + trenn;
        s += "max=" + toString(maxTime) + trenn;
        s += "avg=" + toString((long) avgTime) + trenn;
        s += "avgAvg=" + toString((long) avgAvgTime) + ")";
        Log.d(TAG, s);

    }

    public String toString(long val) {
        StringBuilder result = new StringBuilder();
        BigDecimal value = new BigDecimal(val);//scale is zero
        //millis, with 3 decimals:
        value = value.divide(MILLION, 3, BigDecimal.ROUND_HALF_EVEN);
        result.append(value);
//        result.append(" ms");
        return result.toString();
    }

    public long getMax(long max_value, long value) {
        if (max_value == 0 || max_value < value) {
            return value;
        }
        return max_value;
    }

    public long getMin(long min_value, long value) {
        if (min_value == 0 || min_value > value) {
            return value;
        }
        return min_value;
    }

}