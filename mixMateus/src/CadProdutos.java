import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CadProdutos extends JFrame {
    private JPanel cadProdutos;
    private JToolBar toolbar;
    private JLabel menu;
    private JLabel labelnome;
    private JTextField nomeProduto;
    private JLabel labelpreco;
    private JTextField preco;
    private JLabel labelquantidade;
    private JTextField quantidade;
    private JButton cadastrarProdutoButton;
    private JLabel logo;
    private JLabel titulo;

    public CadProdutos() {
        setContentPane(cadProdutos);
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Cria o menu pop-up
        JPopupMenu popupMenu = new JPopupMenu();

        // Cria as opções do menu
        JMenuItem homeItem = new JMenuItem("Home");
        JMenuItem estoqueItem = new JMenuItem("Estoque");
        JMenuItem produtosItem = new JMenuItem("Cadastrar produtos");

        // Adiciona as opções ao menu
        popupMenu.add(homeItem);
        popupMenu.add(estoqueItem);
        popupMenu.add(produtosItem);

        // Adiciona um MouseListener à JLabel menu para mostrar o popupMenu quando clicado
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Verifica se o botão clicado é o botão esquerdo do mouse
                    popupMenu.show(menu, e.getX(), e.getY());
                }
            }
        });

        // Adiciona ação para a opção de cadastro de produtos
        produtosItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aqui você pode adicionar a lógica para abrir uma nova janela de Cadastro de Produtos
                new CadProdutos();
                dispose(); // Fecha a janela atual
            }
        });

        // Adiciona ação para a opção de estoque
        estoqueItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aqui você pode adicionar a lógica para abrir uma nova janela de Estoque
                new Estoque();
                dispose(); // Fecha a janela atual
            }
        });

        // Adiciona ação para a opção de home
        homeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aqui você pode adicionar a lógica para abrir uma nova janela de Home
                new Home();
                dispose(); // Fecha a janela atual
            }
        });

        // Adiciona ação ao botão de cadastrar produto
        cadastrarProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nomeProdutoText = getNomeProduto();
                    double precoProduto = Double.parseDouble(getPreco());
                    int quantidadeProduto = Integer.parseInt(getQuantidade());

                    // Verifica se os campos não estão vazios
                    if (nomeProdutoText.isEmpty() || getPreco().isEmpty() || getQuantidade().isEmpty()) {
                        throw new Exception("Por favor, preencha todos os campos.");
                    }

                    // Envia os dados para o banco de dados
                    sendDataToDatabase(nomeProdutoText, precoProduto, quantidadeProduto);

                    // Exibe mensagem de sucesso e redireciona para a janela de Estoque
                    JOptionPane.showMessageDialog(CadProdutos.this, "Produto cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    new Estoque(); // Abre uma nova janela de Estoque
                    dispose(); // Fecha a janela atual
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CadProdutos.this, "Por favor, insira valores válidos para o preço e a quantidade.", "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CadProdutos.this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Método para enviar os dados para o banco de dados
    private void sendDataToDatabase(String nomeProduto, double preco, int quantidade) throws Exception {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5434/mixMateus", "postgres", "admin");

            String query = "INSERT INTO produtos (nome, preco, quantidade) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nomeProduto);
            preparedStatement.setDouble(2, preco);
            preparedStatement.setInt(3, quantidade);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception("Erro ao enviar os dados.");
        }
    }

    // Métodos getter e setter
    public String getNomeProduto() {
        return nomeProduto.getText();
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto.setText(nomeProduto);
    }

    public String getPreco() {
        return preco.getText();
    }

    public void setPreco(String preco) {
        this.preco.setText(preco);
    }

    public String getQuantidade() {
        return quantidade.getText();
    }

    public void setQuantidade(String quantidade) {
        this.quantidade.setText(quantidade);
    }
}
