package com.trafico;

/**
 * Punto de entrada del programa.
 * Inicializa la simulación y lanza la interfaz JavaFX.
 */
// public class Main {
class Main extends Application{
    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 1200, 800);

            primaryStage.setTitle("Sistema de Tráfico Inteligente - Simulación de Ambulancia");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Iniciar controlador
            com.trafico.controller.MapaController controller =
                    new com.trafico.controller.MapaController(root);
            controller.inicializar();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    // TODO: Implementar el método main
    // TODO: Configurar la aplicación JavaFX
    // TODO: Cargar la escena principal
    // TODO: Mostrar la ventana de la simulación
}

