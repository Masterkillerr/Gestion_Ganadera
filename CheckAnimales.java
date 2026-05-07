import java.sql.*;

public class CheckAnimales {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:5432/postgres";
        String user = "postgres.rtkuurairzrhhegvpwob";
        String pass = "lolitopapul";
        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT column_name, is_nullable, data_type FROM information_schema.columns WHERE table_name = 'animales'");
            while (rs.next()) {
                System.out.println(rs.getString(1) + " | " + rs.getString(2) + " | " + rs.getString(3));
            }
        }
    }
}
