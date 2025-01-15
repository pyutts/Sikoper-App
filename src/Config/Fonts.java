/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Config;

/**
 *
 * @author pyuts
 */

import javafx.scene.text.Font;
import java.io.InputStream;

public class Fonts {
    public static Font loadFont(String fontPath, double size) {
        Font font = null;

        try (InputStream fontStream = Fonts.class.getResourceAsStream(fontPath)) {
            if (fontStream == null) {
                System.err.println("Font tidak ditemukan di path: " + fontPath);
            } else {
                font = Font.loadFont(fontStream, size);
                System.out.println("Font berhasil dimuat: " + fontPath);
            }
        } catch (Exception e) {
            System.err.println("Error saat memuat font: " + e.getMessage());
        }
        
        return font;
    }
}
