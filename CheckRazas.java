import java.sql.*;

public class CheckRazas {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:5432/postgres";
        String user = "postgres.rtkuurairzrhhegvpwob";
        String pass = "lolitopapul";
        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM razas");
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.print(rsmd.getColumnName(i) + " | ");
            }
            System.out.println("\n-------------------------");
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + " | ");
                }
                System.out.println();
            }
        }
    }
}
