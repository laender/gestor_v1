/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.util;

import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Laender
 */
public abstract class Utils {

    private static final String SDF = "dd/MM/yy hh:mm:ss";

    private static final Map<String, SimpleDateFormat> DATE_FORMAT = new LinkedHashMap<>();

    static Gson gson;

    public static Gson getInstanceGson() {
        return new Gson();
    }

    public static String getJson(Object obj) {
        gson = new Gson();
        return gson.toJson(obj);
    }

    public static <T> T getObject(String json, Class<T> clazz) {
        return getInstanceGson().fromJson(json, clazz);
    }

    public static Date toDate(String dateString, String format) {
        SimpleDateFormat sdf = getDateFormat(format);
        try {
            return sdf.parse(dateString);
        } catch (ParseException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static SimpleDateFormat getDateFormat(String format) {
        if (!DATE_FORMAT.containsKey(format)) {
            DATE_FORMAT.put(format, new SimpleDateFormat(format));
        }
        return DATE_FORMAT.get(format);
    }

    public static boolean empty(Number val) {
        return val == null;
    }

    public static boolean empty(Collection val) {
        return val == null || val.isEmpty();
    }

    public static boolean empty(String val) {
        return val == null || val.trim().isEmpty();
    }

    public static <T> T nvl(T valor, T padrao) {
        if (valor instanceof String && (valor + "").trim().isEmpty()) {
            valor = null;
        }
        if (valor == null) {
            return padrao;
        }
        return valor;
    }

    public static Integer nvl(Integer valor, Integer padrao) {
        if (valor == null) {
            return padrao;
        }
        return valor;
    }

    public static double zeroToOne(double valor) {
        if (valor == 0d) {
            return 1d;
        }
        return valor;
    }

    public static String dataToString(Date data) {
        if (data == null) {
            return "";
        }
        return dataToString(data, SDF);
    }

    public static String dataToString(Date data, String format) {
        SimpleDateFormat dateFormat = getDateFormat(format);
        return dateFormat.format(data);
    }

    public static int diffDate(Date c1, Date c2, int tipo) {
        long dif = 0;
        switch (tipo) {
            case Calendar.SECOND:
                dif = (c2.getTime() - c1.getTime()) / (1000);
                break;
            case Calendar.MINUTE:
                dif = (c2.getTime() - c1.getTime()) / (1000 * 60);
                break;
            case Calendar.HOUR:
                dif = (c2.getTime() - c1.getTime()) / (1000 * 60 * 60);
                break;
            case Calendar.DAY_OF_YEAR:
                dif = (c2.getTime() - c1.getTime()) / (1000 * 60 * 60 * 24);
                break;
            case Calendar.MONTH:
                dif = ((c2.getTime() - c1.getTime()) / (1000 * 60 * 60 * 24)) / 30;
                break;
            case Calendar.YEAR:
                dif = (((c2.getTime() - c1.getTime()) / (1000 * 60 * 60 * 24)) / 30) / 12;
                break;
            default:
                break;
        }

        return (int) dif;
    }

    public static double getDiferencaTempoHoras(Date dhInicio, Date dhFim) {
        Date date1 = dhInicio;
        Date date2 = dhFim != null ? dhFim : new Date();
        double differenceMilliSeconds = date2.getTime() - date1.getTime();
        double dif = (differenceMilliSeconds / 1000 / 60 / 60);
        return dif;
    }

    public static double getDiferencaTempoMinutos(Date dhInicio, Date dhFim) {
        Date date1 = dhInicio;
        Date date2 = dhFim != null ? dhFim : new Date();
        double differenceMilliSeconds = date2.getTime() - date1.getTime();
        return (differenceMilliSeconds / 1000 / 60);
    }

    public static Date getFirstHourDate(Date data) {
        Date dataI = null;
        if (data != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            dataI = cal.getTime();
            return dataI;
        }
        return null;
    }

    public static Date getLastHourDate(Date data) {
        if (data != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            cal.set(Calendar.HOUR, 23);
            cal.set(Calendar.MINUTE, 59);
            data = cal.getTime();
            return data;
        }
        return null;
    }

    public static Date getLastWeekend(Date dataInicial) {
        int dias = 7;
        Date data_comparar = dataInicial != null ? dataInicial : new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(data_comparar);
        gc.set(Calendar.DATE, gc.get(Calendar.DATE) - dias);
        data_comparar = gc.getTime();
        return data_comparar;
    }

    public static boolean emailValido(String email) {
        Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher m = p.matcher(email);
        return m.find();
    }
    
    public static byte[] encrypt(byte[] data){
     byte[] enc = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            enc[i] = (byte) ((i % 2 == 0) ? data[i] + 1 : data[i] - 1);
        }
        return enc;
    }
    
    public static byte[] decrypt(byte[] data){
     byte[] enc = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            enc[i] = (byte) ((i % 2 == 0) ? data[i] - 1 : data[i] + 1);
        }
        return enc;
    }

}
