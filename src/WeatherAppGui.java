import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {

    private JSONObject weatherData;

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
        addGuiComponents();

    }

    private void addGuiComponents(){
        //campo de pesquisa
        JTextField searchTextFiel = new JTextField();

        //define a localização e o tamanho do nosso componente
        searchTextFiel.setBounds(15,15,351,45);

        // altera o estilo e tamanho da fonte
        searchTextFiel.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextFiel);

        //imagem do tempo
        JLabel weatherConditionImage = new  JLabel(loadImage("src/assets/icons8-cloud-100.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //texto de temperatura
        JLabel temperatureText = new JLabel(" °C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 28));

        //centraliza o texto
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // descrição da condição climática
        JLabel weatherConditionDesc = new JLabel("clima");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 24));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // imagem de umidade
        JLabel humidityImage =  new JLabel(loadImage("src/assets/icons8-humidity-64.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        // texo de umidade
        JLabel humidityText =  new JLabel("<html><b>Humidade<b> 100%</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 11));
        add(humidityText);

        // imagem de velocidade do vento
        JLabel windspeedImage =  new JLabel(loadImage("src/assets/icons8-windy-64.png"));
        windspeedImage.setBounds(220,500,74,66);
        add(windspeedImage);

        // texto de velocidade do vento
        JLabel windspeedText =  new JLabel("<html><b>Velocidade do vento<b> 15km/h</html>");
        windspeedText.setBounds(310,500,100,55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 11));
        add(windspeedText);

        //botão de pesquisa
        JButton searchButton = new JButton(loadImage("src/assets/icons8-search-38.png"));
        //muda o cursor para um cursor manual ao passar o mouse sobre este botão
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //obtém a localização pelo usuário
                String userInput = searchTextFiel.getText();

                // valida a entrada - remove os espaços em branco para garantir que o texto não esteja vazio
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;

                }

                // recupera dados meteorológicos
                weatherData = WeatherApp.getWeatherData(userInput);

                //atualiza a interface

                //atualiza a imagem do tempo
                String weatherCondition = (String)  weatherData.get("weather_condition");

                // dependendo da condição, atualizaremos a imagem meteorológica que corresponde à condição
                switch (weatherCondition){
                    case "Limpo":
                        weatherConditionImage.setIcon(loadImage("src/assets/icons8-sun-100.png"));
                        break;
                    case "Nublado":
                        weatherConditionImage.setIcon(loadImage("src/assets/icons8-cloudy-100.png"));
                        break;
                    case "Chuva":
                        weatherConditionImage.setIcon(loadImage("src/assets/icons8-rain-100.png"));
                        break;
                    case "Neve":
                        weatherConditionImage.setIcon(loadImage("src/assets/icons8-snow-storm-100.png"));
                        break;
                }

                //atualiza o texto da temperatura
                double temperature = (double)  weatherData.get("temperature");
                temperatureText.setText(temperature + " °C");

                // atualiza o texto da condição climática
                weatherConditionDesc.setText(weatherCondition);

                //atualiza o texto de umidade
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidade</b> " + humidity + "%</html>");

                //atualiza o texto de velocidade do vento
                double winspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Velocidade do vento</b> " + humidity + "km/h</html>");

            }
        });
        add(searchButton);
    }

    // usado para criar imagens em nossos componentes gui
    private ImageIcon loadImage(String resourcePath){
        try{
            // lê o arquivo de imagem do caminho fornecido
            BufferedImage image = ImageIO.read(new File(resourcePath));
            // retorna um ícone de imagem para que nosso componente possa renderizá-lo
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Não foi possível encontrar o recurso.");
        return null;
    }
}

