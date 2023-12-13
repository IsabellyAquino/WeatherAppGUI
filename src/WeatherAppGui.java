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

        JLabel weatherConditionImage = new  JLabel(loadImage("src/assets/icons8-cloudy-64.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        JLabel humidityImage =  new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        JLabel humidityText =  new JLabel("<html><b>Humidade<b> 100%</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 10));
        add(humidityText);

        JLabel windspeedImage =  new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220,500,74,66);
        add(windspeedImage);

        JLabel windspeedText =  new JLabel("<html><b>Velocidade do vento<b> 15km/h</html>");
        windspeedText.setBounds(310,500,85,55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 10));
        add(windspeedText);

        //botão de pesquisa
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        //muda o cursor para um cursor manual ao passar o mouse sobre este botão
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String userInput = searchTextFiel.getText();


                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;

                }


                weatherData = WeatherApp.getWeatherData(userInput);




                String weatherCondition = (String)  weatherData.get("weather_condition");


                switch (weatherCondition){
                    case "Limpo":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Nublado":
                        weatherConditionImage.setIcon(loadImage("src/assets/icons8-cloudy-64.png"));
                        break;
                    case "Chuva":
                        weatherConditionImage.setIcon(loadImage("src/assets/icons8-cloud-lightning-64.png"));
                        break;
                    case "Neve":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }


                double temperature = (double)  weatherData.get("temperature");
                temperatureText.setText(temperature + " C");


                weatherConditionDesc.setText(weatherCondition);


                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidade</b> " + humidity + "%</html>");


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

