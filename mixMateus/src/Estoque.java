import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Estoque extends JFrame {
    private JPanel estoque;
    private JToolBar toolbar;
    private JLabel menu;
    private JLabel logo;
    private JScrollPane scrollPane;
    private JTable tabelaEstoque;
    private JButton cadastro;
    private JButton excluir;

    public Estoque() {
        setContentPane(estoque);
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

        // Adiciona ação para a opção de cadastro
        produtosItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aqui você pode adicionar a lógica para abrir uma nova janela de Cadastro
                new CadProdutos();
                dispose(); // Fecha a janela atual
            }
        });

        // Adiciona ação para a opção de produtos
        estoqueItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aqui você pode adicionar a lógica para abrir uma nova janela de Produtos
                new Estoque();
                dispose(); // Fecha a janela atual
            }
        });

        homeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aqui você pode adicionar a lógica para abrir uma nova janela de Home
                new Home();
                dispose(); // Fecha a janela atual
            }
        });

        // Adiciona ação para o botão cadastro
        cadastro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CadProdutos();
                dispose(); // Fecha a janela atual
            }
        });

        String[] colunas = {"ID", "Nome", "Preço", "Quantidade"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Apenas a primeira coluna (ID) não é editável
                return column != 0;
            }
        };
        tabelaEstoque = new JTable(model);
        scrollPane.setViewportView(tabelaEstoque);

        // Puxe os dados do banco de dados e adicione-os à tabela
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5434/mixMateus", "postgres", "admin");
            String query = "SELECT id, nome, preco, quantidade FROM produtos";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                double preco = resultSet.getDouble("preco");
                int quantidade = resultSet.getInt("quantidade");
                model.addRow(new Object[]{id, nome, preco, quantidade});
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao carregar os dados do banco de dados.");
        }

        // Adiciona um ouvinte de eventos para detectar as alterações na tabela
        tabelaEstoque.getModel().addTableModelListener(e -> {
            int linha = e.getFirstRow();
            int coluna = e.getColumn();
            DefaultTableModel modelTabela = (DefaultTableModel) tabelaEstoque.getModel();
            int id = (int) modelTabela.getValueAt(linha, 0);
            String nome = (String) modelTabela.getValueAt(linha, 1);
            double preco = 0;
            int quantidade = 0;

            try {
                preco = Double.parseDouble(modelTabela.getValueAt(linha, 2).toString());
                quantidade = Integer.parseInt(modelTabela.getValueAt(linha, 3).toString());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Por favor, insira um valor válido para o preço e a quantidade.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Atualiza o banco de dados com as novas informações
            atualizarBancoDeDados(id, nome, preco, quantidade);
        });

        // Adiciona um ouvinte de eventos para o botão "excluir"
        excluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verifica se alguma linha da tabela está selecionada
                int linhaSelecionada = tabelaEstoque.getSelectedRow();
                if (linhaSelecionada == -1) {
                    JOptionPane.showMessageDialog(null, "Por favor, selecione um produto para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Obtém o ID do produto selecionado
                int idProduto = (int) tabelaEstoque.getValueAt(linhaSelecionada, 0);

                // Pede confirmação ao usuário antes de excluir o produto
                int confirmacao = JOptionPane.showConfirmDialog(null, "Tem certeza de que deseja excluir este produto?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirmacao == JOptionPane.YES_OPTION) {
                    // Chama o método para excluir o produto do banco de dados
                    excluirProduto(idProduto);

                    // Remove a linha da tabela
                    DefaultTableModel model = (DefaultTableModel) tabelaEstoque.getModel();
                    model.removeRow(linhaSelecionada);

                    // Verifica se a tabela está vazia e adiciona uma linha vazia se necessário
                    if (model.getRowCount() == 0) {
                        model.addRow(new Object[]{null, null, null, null});
                    }
                }
            }
        });
    }

    // Método para atualizar o banco de dados com as novas informações
    private void atualizarBancoDeDados(int id, String nome, double preco, int quantidade) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5434/mixMateus", "postgres", "admin");
            String query = "UPDATE produtos SET nome=?, preco=?, quantidade=? WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nome);
            preparedStatement.setDouble(2, preco);
            preparedStatement.setInt(3, quantidade);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao atualizar o banco de dados.");
        }
    }

    // Método para excluir um produto do banco de dados
    private void excluirProduto(int idProduto) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5434/mixMateus", "postgres", "admin");
            String query = "DELETE FROM produtos WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idProduto);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao excluir o produto do banco de dados.");
        }
    }

    // Métodos getter e setter
    public String getNome() {
        int linhaSelecionada = tabelaEstoque.getSelectedRow();
        if (linhaSelecionada != -1) {
            return tabelaEstoque.getValueAt(linhaSelecionada, 1).toString();
        }
        return null;
    }

    public void setNome(String nome) {
        int linhaSelecionada = tabelaEstoque.getSelectedRow();
        if (linhaSelecionada != -1) {
            tabelaEstoque.setValueAt(nome, linhaSelecionada, 1);
        }
    }

    public String getPreco() {
        int linhaSelecionada = tabelaEstoque.getSelectedRow();
        if (linhaSelecionada != -1) {
            return tabelaEstoque.getValueAt(linhaSelecionada, 2).toString();
        }
        return null;
    }

    public void setPreco(String preco) {
        int linhaSelecionada = tabelaEstoque.getSelectedRow();
        if (linhaSelecionada != -1) {
            tabelaEstoque.setValueAt(preco, linhaSelecionada, 2);
        }
    }

    public String getQuantidade() {
        int linhaSelecionada = tabelaEstoque.getSelectedRow();
        if (linhaSelecionada != -1) {
            return tabelaEstoque.getValueAt(linhaSelecionada, 3).toString();
        }
        return null;
    }

    public void setQuantidade(String quantidade) {
        int linhaSelecionada = tabelaEstoque.getSelectedRow();
        if (linhaSelecionada != -1) {
            tabelaEstoque.setValueAt(quantidade, linhaSelecionada, 3);
        }
    }
}
