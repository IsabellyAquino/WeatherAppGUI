import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    public WeatherAppGui(){
        //configurando a nossa interface gráfica(GUI) e adicionando tulo
        super("Aplicativo de clima");

        //configurando gui para encerrar o processo do programa assim que ele for fechado
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //definindo o tamanho da gui
        setSize( 450,  650);

        //carrega nossa interface gráfica no centro da tela
        setLocationRelativeTo(null);

        // torna nosso gerenciador de layout nulo para posicionar manualmente nossos componentes dentro da interface gráfica
        setLayout(null);

        // evita qualquer redimensionamento da nossa interface gráfica
        setResizable(false);

        addGuiComponentes();

    }

    private void addGuiComponentes(){
        //campo de pesquisa
        JTextField searchTextFiel = new JTextField();

        searchTextFiel.setBounds(15,15,351,45);

        searchTextFiel.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextFiel);

        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);

        add(searchButton);
    }

    private ImageIcon loadImage(String resourcePath){
        try{
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Não foi possível encontrar o recurso.");
        return null;
    }
}

