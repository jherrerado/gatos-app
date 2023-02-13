package com.jherrera.gatos_app;

import com.google.gson.Gson;
import com.squareup.okhttp.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class GatosService {
    public static void verGatos() throws IOException {
        // Traer los datos de la api (Esta informacion la ofrece postman)
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.thecatapi.com/v1/images/search").get().build();
        Response response = client.newCall(request).execute();
        String json = response.body().string();

        //Convertir el json a atributos de objeto
        json = json.substring(1, json.length());
        json = json.substring(0, json.length() -1);

        Gson gson = new Gson();
        Gatos gatos = gson.fromJson(json, Gatos.class);

        //Redimencinar imagenes
        Image image = null;
        try{
            URL url = new URL(gatos.getUrl());
            image = ImageIO.read(url);

            ImageIcon fondoGato = new ImageIcon(image);

            if (fondoGato.getIconWidth() > 800){
                Image fondo = fondoGato.getImage();
                Image modificada = fondo.getScaledInstance(800, 600, Image.SCALE_SMOOTH);
                fondoGato = new ImageIcon(modificada);
            }


            // menu
            String menu = "Opciones: \n"
                    + "1. Ver otro gato \n"
                    + "2. marcar el gato como favorito \n"
                    + "3. Volver al menu \n";
            String[] botones = {"1. Ver otro gato", "2. marcar el gato como favorito", "3. Volver al menu"};
            String id_gato = gatos.getUrl();
            String opcion = (String) JOptionPane.showInputDialog(null, menu, id_gato,
                    JOptionPane.INFORMATION_MESSAGE, fondoGato, botones, botones[0]);
            int seleccion = -1;
            for(int i = 0; i < botones.length; i++) {
                if (opcion.equals(botones[i])) {
                    seleccion = i;

                }
            }

            switch (seleccion){
                case 0:
                    verGatos();
                    break;
                case 1:
                    favoritoGato(gatos);
                    break;
                default:
                    break;
            }

        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static void favoritoGato(Gatos gato){
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\r\n    \"image_id\": \"" + gato.getId()+ "\"\r\n\r\n}");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites")
                    .method("POST", body)
                    .addHeader("Content-type", "application/json")
                    .addHeader("x-api-key", gato.getApikey())
                    .build();
            Response response = client.newCall(request).execute();

        } catch (IOException e){
            System.out.println(e);
        }
    }

    public static void verFavorito(String apiKey){
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites")
                    .addHeader("x-api-key", "live_1MHAsEwhFkKjet0ysWwR7ONkcy9Bs3LbHjoxa3hwJxjkwvjaPJqfTr0uXDbVvzW0")
                    .addHeader("Content-type", "Content-type")
                    .build();
            Response response = client.newCall(request).execute();

            String json = response.body().string();
            // Objeto gson
            Gson gson = new Gson();
            GatosFav[] gatosArray = gson.fromJson(json, GatosFav[].class);

            if(gatosArray.length > 0){
                int min = 1;
                int max = gatosArray.length;
                int aleatorio = (int) (Math.random() * ((max - min)) + 1) + min;
                int indice = aleatorio - 1;

                GatosFav gatosFav = gatosArray[indice];

                Image image = null;
                try{
                    URL url = new URL(gatosFav.image.getUrl());
                    image = ImageIO.read(url);

                    ImageIcon fondoGato = new ImageIcon(image);

                    if (fondoGato.getIconWidth() > 800){
                        Image fondo = fondoGato.getImage();
                        Image modificada = fondo.getScaledInstance(800, 600, Image.SCALE_SMOOTH);
                        fondoGato = new ImageIcon(modificada);
                    }


                    // menu
                    String menu = "Opciones: \n"
                            + "1. Ver otro gato \n"
                            + "2. Eliminar favorito \n"
                            + "3. Volver al menu \n";
                    String[] botones = {"1. Ver otro gato", "2. Eliminar favorito", "3. Volver al menu"};
                    String id_gato = gatosFav.getImage_id();
                    String opcion = (String) JOptionPane.showInputDialog(null, menu, id_gato,
                            JOptionPane.INFORMATION_MESSAGE, fondoGato, botones, botones[0]);
                    int seleccion = -1;
                    for(int i = 0; i < botones.length; i++) {
                        if (opcion.equals(botones[i])) {
                            seleccion = i;

                        }
                    }

                    switch (seleccion){
                        case 0:
                            verFavorito(apiKey);
                            break;
                        case 1:
                            borrarFavorito(gatosFav);
                            break;
                        default:
                            break;
                    }

                }catch (IOException e){
                    System.out.println(e);
                }
            }
        } catch (IOException e){
            System.out.println(e);
        }
    }

    public static void borrarFavorito(GatosFav gatosFav){
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites/" + gatosFav.getId())
                    .addHeader("x-api-key", gatosFav.getApikey())
                    .addHeader("Content-type", "Content-type")
                    .build();
            Response response = client.newCall(request).execute();
        }catch (IOException e){
            System.out.println(e);
        }
    }
}
