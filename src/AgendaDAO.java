import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AgendaDAO {

    public static void salvar(Consulta consulta) throws SQLException {
        String sql = "INSERT INTO consultas (horario_index, especie, nome_animal, idade, nome_dono, "
                + "telefone_dono, caracteristica, sintomas) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            Animal paciente = consulta.getPaciente();
            stmt.setInt(1, consulta.getHorarioIndex());
            stmt.setString(2, paciente.getTipo());
            stmt.setString(3, paciente.getNome());
            stmt.setInt(4, paciente.getIdade());
            stmt.setString(5, paciente.getNomeDono());
            stmt.setString(6, paciente.getTelefoneDono());
            stmt.setString(7, paciente.getCaracteristica());
            stmt.setString(8, consulta.getSintomas());
            stmt.executeUpdate();
        }
    }

    public static Consulta[] buscarAgenda() throws SQLException {
        Consulta[] agenda = new Consulta[10];
        String sql = "SELECT * FROM consultas ORDER BY horario_index";

        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                int indice = resultado.getInt("horario_index");
                String especie = resultado.getString("especie");
                String nome = resultado.getString("nome_animal");
                int idade = resultado.getInt("idade");
                String nomeDono = resultado.getString("nome_dono");
                String telefone = resultado.getString("telefone_dono");
                String caracteristica = resultado.getString("caracteristica");
                String sintomas = resultado.getString("sintomas");

                if (indice < 0 || indice > 9 || especie == null) {
                    continue;
                }

                Animal paciente;
                if (especie.equalsIgnoreCase("Cachorro")) {
                    paciente = new Cachorro(nome, idade, nomeDono, telefone, caracteristica);
                } else if (especie.equalsIgnoreCase("Gato")) {
                    paciente = new Gato(nome, idade, nomeDono, telefone, caracteristica);
                } else {
                    continue;
                }

                Consulta consulta = new Consulta(paciente, indice, sintomas);
                if (consulta.dadosValidos()) {
                    agenda[indice] = consulta;
                }
            }
        }

        return agenda;
    }

    public static int atualizar(Consulta consulta) throws SQLException {
        String sql = "UPDATE consultas SET especie = ?, nome_animal = ?, idade = ?, nome_dono = ?, "
                + "telefone_dono = ?, caracteristica = ?, sintomas = ? WHERE horario_index = ?";

        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            Animal paciente = consulta.getPaciente();
            stmt.setString(1, paciente.getTipo());
            stmt.setString(2, paciente.getNome());
            stmt.setInt(3, paciente.getIdade());
            stmt.setString(4, paciente.getNomeDono());
            stmt.setString(5, paciente.getTelefoneDono());
            stmt.setString(6, paciente.getCaracteristica());
            stmt.setString(7, consulta.getSintomas());
            stmt.setInt(8, consulta.getHorarioIndex());
            return stmt.executeUpdate();
        }
    }

    public static int cancelar(int horarioIndex) throws SQLException {
        String sql = "DELETE FROM consultas WHERE horario_index = ?";

        try (Connection conexao = ConexaoBD.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, horarioIndex);
            return stmt.executeUpdate();
        }
    }
}
