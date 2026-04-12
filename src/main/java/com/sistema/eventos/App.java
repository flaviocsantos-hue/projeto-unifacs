package com.sistema.eventos;

import com.sistema.eventos.model.PerfilAcesso;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.service.*;
import com.sistema.eventos.view.LoginView;

import javax.swing.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });

    }
}
