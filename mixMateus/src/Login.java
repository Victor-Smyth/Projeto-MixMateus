import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {
    private JPanel login;
    private JToolBar toolbar;
    private JLabel menu;
    private JLabel labelEmail;
    private JTextField email;
    private JLabel labelSenha;
    private JPasswordField senha;
    private JButton botaoLogin;
    private JLabel logo;
    private JLabel titulo;

    public JPanel getLogin() {
        return login;
    }

    public void setLogin(JPanel login) {
        this.login = login;
    }

    public JToolBar getToolbar() {
        return toolbar;
    }

    public void setToolbar(JToolBar toolbar) {
        this.toolbar = toolbar;
    }

    public JLabel getMenu() {
        return menu;
    }

    public void setMenu(JLabel menu) {
        this.menu = menu;
    }

    public JLabel getLabelEmail() {
        return labelEmail;
    }

    public void setLabelEmail(JLabel labelEmail) {
        this.labelEmail = labelEmail;
    }

    public JTextField getEmail() {
        return email;
    }

    public void setEmail(JTextField email) {
        this.email = email;
    }

    public JLabel getLabelSenha() {
        return labelSenha;
    }

    public void setLabelSenha(JLabel labelSenha) {
        this.labelSenha = labelSenha;
    }

    public JPasswordField getSenha() {
        return senha;
    }

    public void setSenha(JPasswordField senha) {
        this.senha = senha;
    }

    public JButton getBotaoLogin() {
        return botaoLogin;
    }

    public void setBotaoLogin(JButton botaoLogin) {
        this.botaoLogin = botaoLogin;
    }

    public Login() {
        setContentPane(login);
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Cria o menu pop-up
        JPopupMenu popupMenu = new JPopupMenu();

        // Cria as opções do menu
        JMenuItem cadastroItem = new JMenuItem("Cadastro");
        JMenuItem loginItem = new JMenuItem("Login");

        // Adiciona as opções ao menu
        popupMenu.add(cadastroItem);
        popupMenu.add(loginItem);

        // Adiciona um MouseListener à JLabel menu para mostrar o popupMenu quando clicado
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Verifica se o botão clicado é o botão esquerdo do mouse
                    popupMenu.show(menu, e.getX(), e.getY());
                }
            }
        });

        // Adiciona ação para a opção de cadastro
        cadastroItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Cadastro(); // Abre uma nova janela de Cadastro
                dispose(); // Fecha a janela atual
            }
        });

        // Adiciona ação para a opção de login
        loginItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login(); // Abre uma nova janela de Login
                dispose(); // Fecha a janela atual
            }
        });

        // Adiciona ação para o botão de login
        botaoLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String emailUsuario = email.getText();
                    String senhaUsuario = new String(senha.getPassword());

                    // Verifica se os campos não estão vazios
                    if (emailUsuario.isEmpty() || senhaUsuario.isEmpty()) {
                        throw new Exception("Por favor, preencha todos os campos.");
                    }

                    // Verifica o login
                    if (verificarLogin(emailUsuario, senhaUsuario)) {
                        // Se o login for válido, abra a próxima janela
                        new Home().setVisible(true);
                        dispose(); // Fecha a janela atual
                    } else {
                        // Caso contrário, exiba uma mensagem de erro
                        throw new Exception("Campos inválidos, tente novamente.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Login.this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Método para verificar o login
    private boolean verificarLogin(String email, String senha) {
        String url = "jdbc:postgresql://localhost:5432/mixMateus";
        String usuarioBD = "postgres";
        String senhaBD = "admin";

        try (Connection conn = DriverManager.getConnection(url, usuarioBD, senhaBD)) {
            String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, senha);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next(); // Se encontrar um resultado, o login é válido
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao conectar.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
