
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by hung1 on 6/21/2020.
 */
public class ImageSearcher {
    private static final int numImageSearch = SV.NUM_IMAGE_SEARCH;
    private static ArrayList<Image> imageList;

    public static int getFeaturesFromDB(){
        Connection con = DBConnector.getConnection();
        ImageSearcher.imageList = new ArrayList<Image>(6000);

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from " + SV.IMAGE_TABLE_NAME);
            while(rs.next()){
                InputStream in = rs.getBinaryStream(3);
                String imageUrl = rs.getString(2);
                int id = rs.getInt(1);
                byte[] byteFeature = new byte[4 * (SV.SUM_COLOR_BINS + 3)];
                in.read(byteFeature);
                float[] imageFeature = ImageSearcher.byteToFloat(byteFeature);
                ImageSearcher.imageList.add(new Image(id, imageUrl, imageFeature));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        } catch (IOException e) {
            e.printStackTrace();
            return 3;
        }
        return 1;
    }

    public static ArrayList<Image> retrieveImage(String url){
        ImageSearcher.getFeaturesFromDB();

        ColorExtractor colorExtractor = new ColorExtractor();
        float[] feature = colorExtractor.getFeature(url);

        ArrayList<Pair<Image, Float>> distanceList = new ArrayList<Pair<Image, Float>>(6000);
        for(Image image : ImageSearcher.imageList){
            float distance = ImageSearcher.calcDistance(feature, image.feature);
            Pair<Image, Float> tmp = new Pair<>(image, Float.valueOf(distance));
            distanceList.add(tmp);
        }

        distanceList.sort((o1, o2) -> {
            return (o1.getValue().compareTo(o2.getValue()));
        });

        ArrayList<Image> result = new ArrayList<>(SV.NUM_IMAGE_SEARCH);

        for(int i = 0; i< SV.NUM_IMAGE_SEARCH; i++){
            System.out.println(distanceList.get(i).getKey().url + " " + distanceList.get(i).getValue());
            result.add(distanceList.get(i).getKey());
        }
        return result;
    }

    public static float calcDistance(float[] feature1, float[] feature2){
        float distance = 0;
        for(int i = 0; i < feature1.length; i++) distance += Math.abs(feature1[i] - feature2[i]);
        return distance;
    }



    public static float[] byteToFloat(byte res[]){
        int n = res.length;
        float[] dst = new float[n/4];
        int v = 0;
        for(int i = 0; i< n/4; i++){
            int tmp = 0;

            v = 0x000000FF & res[i*4];
            tmp = tmp | v;

            v = 0x000000FF & res[i*4 + 1];
            tmp = tmp | (v<<8);

            v = 0x000000FF & res[i*4 + 2];
            tmp = tmp | (v<<16);

            v = 0x000000FF & res[i*4 + 3];
            tmp = tmp | (v<<24);

            dst[i] = Float.intBitsToFloat(tmp);
        }
        return dst;
    }
}
