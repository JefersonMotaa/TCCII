package com.duo.superior.duo;


import java.io.*;

public class Memoria {

    public static void writeToFile(String data, String path, String filename) {

        path = path + '/' + filename;
        try {
            OutputStream out = new FileOutputStream(path);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFromFile(String path, String filename) {
        path = path + '/' + filename;
        String ret = "";

        try {
            InputStream inputStream = new ByteArrayInputStream(path.getBytes());

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static String lerAlarme(InputStream is) {
        String text;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);

            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String lerLinha(int num,InputStream is) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line = null;
        for(int n=0;n<=num;n++) {
            line = bf.readLine();

        }

        bf.close();
        return line;

    }

}