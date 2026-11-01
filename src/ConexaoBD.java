import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String URL = "jdbc:mysql://localhost:3306/petshop";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    public static Connection getConexao() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado. Adicione o MySQL Connector/J ao projeto.");
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
