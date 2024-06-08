import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Cadastro extends JFrame {
    private JPanel cadastro;
    private JTextField nomeUsuario;
    private JLabel labelNome;
    private JLabel menu;
    private JLabel labelEmail;
    private JTextField email;
    private JLabel labelCpf;
    private JTextField cpf;
    private JLabel labelSenha;
    private JPasswordField senha;
    private JButton botaoCadastro;
    private JToolBar toolbar;
    private JLabel logo;
    private JLabel titulo;

    public Cadastro() {
        setContentPane(cadastro);
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

        // Adiciona ação ao botão de cadastro
        botaoCadastro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nomeUsuarioText = getNomeUsuario();
                    String emailText = getEmail();
                    String cpfText = getCpf();
                    String senhaText = getSenha();

                    // Verifica se os campos não estão vazios
                    if (nomeUsuarioText.isEmpty() || emailText.isEmpty() || cpfText.isEmpty() || senhaText.isEmpty()) {
                        throw new Exception("Por favor, preencha todos os campos.");
                    }

                    // Valida o CPF
                    if (!validateCPF(cpfText)) {
                        throw new Exception("Por favor, insira um CPF válido.");
                    }

                    // Envia os dados para o banco de dados
                    sendDataToDatabase(nomeUsuarioText, emailText, cpfText, senhaText);

                    // Exibe mensagem de sucesso e redireciona para a tela de login
                    JOptionPane.showMessageDialog(Cadastro.this, "Cadastro realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    new Login(); // Abre uma nova janela de Login
                    dispose(); // Fecha a janela atual
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Cadastro.this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Método para enviar os dados para o banco de dados
    private void sendDataToDatabase(String nomeUsuario, String email, String cpf, String senha) throws Exception {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5434/mixMateus", "postgres", "admin");

            String query = "INSERT INTO usuarios (nome, email, cpf, senha) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nomeUsuario);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, cpf);
            preparedStatement.setString(4, senha);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception("Erro ao enviar os dados.");
        }
    }

    // Método para validar o CPF
    private boolean validateCPF(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");

        // Checks if the CPF has 11 digits
        if (cpf.length() != 11) {
            return false;
        }

        // Checks for CPFs with all digits the same
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Validates the CPF using the algorithm
        int[] numbers = new int[11];
        for (int i = 0; i < 11; i++) {
            numbers[i] = Character.getNumericValue(cpf.charAt(i));
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += numbers[i] * (10 - i);
        }

        int remainder = sum % 11;
        int expectedDigit1 = (remainder < 2) ? 0 : (11 - remainder);

        if (numbers[9] != expectedDigit1) {
            return false;
        }

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += numbers[i] * (11 - i);
        }

        remainder = sum % 11;
        int expectedDigit2 = (remainder < 2) ? 0 : (11 - remainder);

        return numbers[10] == expectedDigit2;
    }

    // Métodos getter e setter
    public String getNomeUsuario() {
        return nomeUsuario.getText();
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario.setText(nomeUsuario);
    }

    public String getEmail() {
        return email.getText();
    }

    public void setEmail(String email) {
        this.email.setText(email);
    }

    public String getCpf() {
        return cpf.getText();
    }

    public void setCpf(String cpf) {
        this.cpf.setText(cpf);
    }

    public String getSenha() {
        return new String(senha.getPassword());
    }

    public void setSenha(String senha) {
        this.senha.setText(senha);
    }

}
