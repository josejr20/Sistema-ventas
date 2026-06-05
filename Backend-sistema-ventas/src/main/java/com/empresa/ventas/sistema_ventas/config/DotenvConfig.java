package com.empresa.ventas.sistema_ventas.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void init() {
        String envPath = System.getProperty("user.dir") + "/.env";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(envPath));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String key = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();
                    if (System.getProperty(key) == null) {
                        System.setProperty(key, value);
                    }
                }
            }
            reader.close();
            System.out.println("✅ Archivo .env cargado correctamente desde: " + envPath);
        } catch (IOException e) {
            System.out.println("⚠️  No se encontró archivo .env en: " + envPath);
        }
    }
}
