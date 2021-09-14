/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import java.util.Properties;

/**
 *
 * @author laender
 */
public class Constantes {

    public static final String BANCO = "gestor";
    public static final String H0ST = "localhost";
    public static final String PORTA = getPorta();
    public static final String USUARIO = "postgres";
    public static final String SENHA = "p1nc3l";
    public static final String DIR_DUMP = "C:\\Program Files\\PostgreSQL\\9.1\\bin\\pg_dump.exe";
    public static final String DIR_DUMP_LINUX = "\\\\\\usr\\bin\\pg_dump"; ///usr/bin/pg_dump --host localhost --port 5432 --username "postgres" --no-password  --format custom --blobs --verbose --file "/opt/backup_postgres/27_03_2019" "gestor"
    public static final String DIR_RESTORE = "C:\\Program Files\\PostgreSQL\\9.1\\bin\\restore.exe";
    public static final String DIR_BKP = "C:\\Users\\laend\\Google Drive\\backup_banco\\";
    
    public static final String CLASSIFICACAO_ABC_A = "A";
    public static final String CLASSIFICACAO_ABC_B = "B";
    public static final String CLASSIFICACAO_ABC_C = "C";

    // resolver o problema do postgres insalado no server com porta 5432 e o ambiente de desenvolvimento usa 5433... 
    private static String getPorta() {
       Properties properties = System.getProperties();
        String so = String.valueOf( System.getProperty("os.name") );
        if (so.equals("Linux")){
            return "5432";
        }
        if (so.contains("Windows")){
            return "5432";
        }
        return null;
    }
}
