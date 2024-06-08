import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Home extends JFrame {
    private JPanel home;
    private JToolBar tollbar;
    private JLabel menu;
    private JToolBar toolbar;
    private JLabel logo;
    private JLabel titulo;

    public JPanel getHome() {
        return home;
    }

    public void setHome(JPanel home) {
        this.home = home;
    }

    public JToolBar getTollbar() {
        return tollbar;
    }

    public void setTollbar(JToolBar tollbar) {
        this.tollbar = tollbar;
    }

    public JLabel getMenu() {
        return menu;
    }

    public void setMenu(JLabel menu) {
        this.menu = menu;
    }

    public Home() {
        setContentPane(home);
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
    }
}
