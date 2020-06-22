
import java.io.*;
import java.sql.*;

/**
 * Created by hung1 on 6/21/2020.
 */
public class DBProcesser {
    private String datasetFolder;

    public DBProcesser() {
        this.datasetFolder = SV.DATASET_FOLDER;
    }

    public void process(){
        File folder = new File(this.datasetFolder);
        File[] listImage = folder.listFiles();

        ColorExtractor colorExtractor = new ColorExtractor();

        Connection con = DBConnector.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select max(idimage) from image");
            int imageID = 0;
            if(rs.next()) imageID = rs.getInt(1);
            else System.out.println("NULL");

            PreparedStatement ps = con.prepareStatement("insert into " + SV.IMAGE_TABLE_NAME + " values(?,?,?,0)");
            for(File image:listImage){
                String imageURL = image.getCanonicalPath();
                float[] imageFeature = colorExtractor.getFeature(imageURL);
                imageID++;

                byte[] byteFeature = floatToBytes(imageFeature);

                ByteArrayInputStream bin = new ByteArrayInputStream(byteFeature);

                ps.setInt(1, imageID);
                ps.setString(2, imageURL);
                ps.setBinaryStream(3, bin);
                int rc = ps.executeUpdate();
                //if(rc > 0) System.out.println("OK!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] floatToBytes(float res[]){
        int n = res.length;
        byte[] dst = new byte[n*4];
        for(int i = 0; i< n; i++){
            int tmp = Float.floatToIntBits(res[i]);
            dst[i*4] = (byte) (tmp);
            dst[i*4 + 1] = (byte) (tmp >> 8);
            dst[i*4 + 2] = (byte) (tmp >> 16);
            dst[i*4 + 3] = (byte) (tmp >> 24);
        }
        return dst;
    }

    public float[] byteToFloat(byte res[]){
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
