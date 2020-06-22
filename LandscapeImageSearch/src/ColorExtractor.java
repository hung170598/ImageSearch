import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Created by hung1 on 6/19/2020.
 */
public class ColorExtractor {
    private float[] feature;
    private Mat image;

    public ColorExtractor() {
        feature = new float[SV.SUM_COLOR_BINS + 3];

        for(int i = 0; i < feature.length; i++) feature[i] = 0;

    }

    public ColorExtractor(String url) {
        feature = new float[SV.SUM_COLOR_BINS + 3];
        for(int i = 0; i < feature.length; i++) feature[i] = 0;

        image = loadImage(url);
        extract();
    }

    public float[] getFeature(String url) {
        image = loadImage(url);
        extract();
        return feature;
    }

    public Mat loadImage(String url) {
        Imgcodecs imageCodecs = new Imgcodecs();
        Mat matrix = imageCodecs.imread(url);
        return matrix;
    }

    public void extract(){
        int width = (int) image.size().width;
        int height = (int) image.size().height;

        for(int i = 0; i < feature.length; i++) feature[i] = 0;

        for(int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                double[] pixel = image.get(i,j);
                int bBins =(int) pixel[0]*SV.EACH_CHANEL_COLOR_BINS/SV.MAX_COLOR_VALUE;
                int gBins =(int) pixel[1]*SV.EACH_CHANEL_COLOR_BINS/SV.MAX_COLOR_VALUE;
                int rBins =(int) pixel[2]*SV.EACH_CHANEL_COLOR_BINS/SV.MAX_COLOR_VALUE;
                int bins = (bBins*SV.EACH_CHANEL_COLOR_BINS + gBins)*SV.EACH_CHANEL_COLOR_BINS + rBins;
                feature[bins] += 1.0/(width*height);
            }
        }
        //for(int i = 0; i < SV.SUM_COLOR_BINS; i++) System.out.print(feature[i] + " ");
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ColorExtractor extractor = new ColorExtractor("..\\dataset\\00000000.jpg");
    }


}