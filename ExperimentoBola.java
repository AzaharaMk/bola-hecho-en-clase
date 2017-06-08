import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.animation.Animation;
import java.util.Random;
import javafx.scene.input.KeyCode;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import javafx.scene.shape.Shape;

public class ExperimentoBola extends Application
{
    private int velocidadEnX;
    private int velocidadEnY;
    private int velocidadPlataforma;
    private static int RADIO = 20;
    private int tiempoEnSegundos;
    //Creamos un arrayList para guardar los ladrillos
    private ArrayList <Rectangle> ladrillos;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage escenario)
    {
        Group contenedor = new Group();

        velocidadEnX = 1;
        velocidadEnY = 1;
        tiempoEnSegundos = 70;

        Circle circulo = new Circle();
        circulo.setFill(Color.RED);  
        circulo.setRadius(RADIO);

        Rectangle plataforma = new Rectangle();
        plataforma.setWidth(50);
        plataforma.setHeight(5);
        plataforma.setTranslateX(225);
        plataforma.setTranslateY(480);
        plataforma.setFill(Color.BLUE);
        contenedor.getChildren().add(plataforma);

        velocidadPlataforma = 1;
        
        //Creamos un objeto de tipo Random.
        Random aleatorio = new Random();
        
        //Inicializamos el arrayList
        ladrillos = new ArrayList();
        
        
        //Creamos un bucle que de tantas vueltas como ladrillos queremos añadir
        for(int i = 0; i < 20 ;i++)
        {
            /*En primer lugar la booleana será false porque aún no ha creado ningún ladrillo.
             * Nos servirá para parar el bucle en cuanto se encuentre un ladrillo válido
             */
            boolean ladrilloAnadido = false;
            
            //Mientras no añadas el ladrillo te lo crea hasta que encuentre un ladrillo válido
            while(!ladrilloAnadido)
            {
                Rectangle ladrillo = new Rectangle();
                ladrillo.setWidth(30);
                ladrillo.setHeight(10);
                ladrillo.setTranslateX(aleatorio.nextInt(455) + 15);
                ladrillo.setTranslateY(aleatorio.nextInt(25) + 5);
                ladrillo.setFill(Color.ORANGE);
                ladrillo.setStroke(Color.BLACK);

                //creamos una booleana para saber si se intersectan.Damos por hecho de que no se intersectan por eso la iniciamos en false.
                boolean ladrilloIntersectado = false;
                //Recorremos el arrayList
                for(int j = 0; j < ladrillos.size() && !ladrilloIntersectado; j++)
                {
                    //Elegimos un ladrillo aleatorio
                    Shape interseccion= Shape.intersect(ladrillo,ladrillos.get(j));
                    double ancho = interseccion.getBoundsInParent().getWidth();
                    
                    //Si hemos encontrado un ladrillo válido
                    if(ancho != -1)
                    {
                        //Cambiamos la variable en true en el momento de que se solapen.
                        ladrilloIntersectado = true;   
                    }       
                }

                //En caso de que el booleano sea false lo añadimos al arrayList y a la escena ya que no se solapa.
                if(!ladrilloIntersectado)
                {
                    ladrillos.add(ladrillo);
                    contenedor.getChildren().add(ladrillo);
                    ladrilloAnadido = true;
                }
            }
        }

        circulo.setCenterX(20 + aleatorio.nextInt(500 - 40));
        circulo.setCenterY(50);
        contenedor.getChildren().add(circulo);

        Label tiempoPasado = new Label("0");
        contenedor.getChildren().add(tiempoPasado);

        Scene escena = new Scene(contenedor, 500, 500);
        escenario.setScene(escena);
        escenario.show();

        Timeline timeline = new Timeline();
        KeyFrame keyframe = new KeyFrame(Duration.seconds(0.01), event -> {

                    // Controlamos si la bola rebota a ziquierda o derecha
                    if (circulo.getBoundsInParent().getMinX() <= 0 ||
                    circulo.getBoundsInParent().getMaxX() >= escena.getWidth()) {
                        velocidadEnX = -velocidadEnX;                              
                    }

                    // Conrolamos si la bola rebota arriba y abajo
                    if (circulo.getBoundsInParent().getMinY() <= 0) {
                        velocidadEnY = -velocidadEnY;
                    }

                    if (circulo.getBoundsInParent().getMaxY() == plataforma.getBoundsInParent().getMinY()) {
                        double centroEnXDeLaBola = circulo.getBoundsInParent().getMinX() + RADIO;
                        double minEnXDeLaPlataforma = plataforma.getBoundsInParent().getMinX();
                        double maxEnXDeLaPlataforma = plataforma.getBoundsInParent().getMaxX();
                        if ((centroEnXDeLaBola >= minEnXDeLaPlataforma) &&
                        (centroEnXDeLaBola <= maxEnXDeLaPlataforma)) {
                            //La bola esta sobre la plataforma
                            velocidadEnY = -velocidadEnY;
                        }
                    }

                    circulo.setTranslateX(circulo.getTranslateX() + velocidadEnX);
                    circulo.setTranslateY(circulo.getTranslateY() + velocidadEnY);

                    plataforma.setTranslateX(plataforma.getTranslateX() + velocidadPlataforma);
                    if (plataforma.getBoundsInParent().getMinX() == 0  || 
                    plataforma.getBoundsInParent().getMaxX() == escena.getWidth()) {
                        velocidadPlataforma = 0;
                    }
                    
                    //Recorremos los array para ver si e ladrillo choca con la bola y borrar este.
                    for(int i = 0; i < ladrillos.size();i++)
                    {
                        //Cogemos un ladrillo y miramos si intersecta con la bola
                        Rectangle ladrilloAComprobar = ladrillos.get(i);
                        Shape interseccion = Shape.intersect(circulo,ladrilloAComprobar);
                        if(interseccion.getBoundsInParent().getWidth() != -1)
                        {
                            contenedor.getChildren().remove(ladrilloAComprobar);
                            ladrillos.remove(ladrilloAComprobar);
                            i--;
                        }
                    }
                    
                    
                    // Actualizamos la etiqueta del tiempo
                    int minutos = tiempoEnSegundos / 60;
                    int segundos = tiempoEnSegundos % 60;
                    tiempoPasado.setText(minutos + ":" + segundos);                        

                    // Comrpobamos si el juego debe detenerse
                    if (circulo.getBoundsInParent().getMinY() > escena.getHeight()) {
                        Label mensajeGameOver = new Label("Game over");
                        mensajeGameOver.setTranslateX(escena.getWidth() / 2);
                        mensajeGameOver.setTranslateY(escena.getHeight() / 2);
                        contenedor.getChildren().add(mensajeGameOver);
                        timeline.stop();
                    }

                });  
        timeline.getKeyFrames().add(keyframe);

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();     

        escena.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.RIGHT && 
                plataforma.getBoundsInParent().getMaxX() != escena.getWidth()) {
                    velocidadPlataforma = 1;
                }
                else if (event.getCode() == KeyCode.LEFT && 
                plataforma.getBoundsInParent().getMinX() != 0) {
                    velocidadPlataforma = -1;
                }
            });

        TimerTask tarea = new TimerTask() {
                @Override
                public void run() {
                    tiempoEnSegundos++;
                }                        
            };
        Timer timer = new Timer();
        timer.schedule(tarea, 0, 1000);

    }

}

