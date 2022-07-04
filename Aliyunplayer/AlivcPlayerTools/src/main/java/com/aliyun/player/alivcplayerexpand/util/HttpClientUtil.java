package com.aliyun.player.alivcplayerexpand.util;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class HttpClientUtil {
    private static final int CONNECTION_TIMEOUT = 10000;

    public static String doGet(String serverUrl) {
        if (serverUrl.startsWith("https://")) {
            return doHttpsGet(serverUrl);
        } else if (serverUrl.startsWith("http://")) {
            return doHttpGet(serverUrl);
        } else {
            return null;
        }
    }

    public static String doPost(String serverUrl, String data) {
        if (serverUrl.startsWith("https://")) {
            return doHttpsPost(serverUrl, data);
        } else if (serverUrl.startsWith("http://")) {
            return doHttpPost(serverUrl, data);
        } else {
            return null;
        }
    }


    private static String doHttpGet(String serverURL) {
        HttpURLConnection connection = null;
        InputStream in = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufr = null;
        try {
            URL url = new URL(serverURL);
            URLConnection urlConnection = url.openConnection();
            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(CONNECTION_TIMEOUT);
            connection.connect();


            StringBuilder response = null;

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = connection.getInputStream();
                //下面对获取到的输入流进行读取
                inputStreamReader = new InputStreamReader(in);
                bufr = new BufferedReader(inputStreamReader);
                response = new StringBuilder();
                String line = null;
                while ((line = bufr.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } else {
                in = connection.getErrorStream();
                //下面对获取到的输入流进行读取
                inputStreamReader = new InputStreamReader(in);
                bufr = new BufferedReader(inputStreamReader);
                response = new StringBuilder();
                String line = null;
                while ((line = bufr.readLine()) != null) {
                    response.append(line);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("StatusCode", responseCode);
                jsonObject.put("ResponseStr", response.toString());

                return jsonObject.toString();

            }
        } catch (Exception e) {
            Log.d("HttpClientUtil", e.getMessage());
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {

                }
            }

            if (bufr != null) {
                try {
                    bufr.close();
                } catch (IOException e) {

                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private static String doHttpsGet(String serverURL) {
        HttpsURLConnection connection = null;
        InputStream in = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufr = null;
        try {
            URL url = new URL(serverURL);
            URLConnection urlConnection = url.openConnection();
            if (!(urlConnection instanceof HttpsURLConnection)) {
                return null;
            }

            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(CONNECTION_TIMEOUT);
            connection.connect();


            StringBuilder response = null;

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                in = connection.getInputStream();
                //下面对获取到的输入流进行读取
                inputStreamReader = new InputStreamReader(in);
                bufr = new BufferedReader(inputStreamReader);
                response = new StringBuilder();
                String line = null;
                while ((line = bufr.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } else {
                in = connection.getErrorStream();
                //下面对获取到的输入流进行读取
                inputStreamReader = new InputStreamReader(in);
                bufr = new BufferedReader(inputStreamReader);
                response = new StringBuilder();
                String line = null;
                while ((line = bufr.readLine()) != null) {
                    response.append(line);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("StatusCode", responseCode);
                jsonObject.put("ResponseStr", response.toString());

                return jsonObject.toString();

            }
        } catch (Exception e) {
            Log.d("HttpClientUtil", e.getMessage());
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {

                }
            }

            if (bufr != null) {
                try {
                    bufr.close();
                } catch (IOException e) {

                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }


    private static String doHttpPost(String serverURL, String data) {
        HttpURLConnection connection = null;
        InputStream in = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufr = null;
        try {
            URL url = new URL(serverURL);
            URLConnection urlConnection = url.openConnection();
            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(CONNECTION_TIMEOUT);

            //设置输入流和输出流,都设置为true
            connection.setDoOutput(true);
            connection.setDoInput(true);

            //把提交的数据以输出流的形式提交到服务器
            OutputStream os = connection.getOutputStream();
            os.write(data.getBytes());

            connection.connect();


            StringBuilder response = null;

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                in = connection.getInputStream();
                //下面对获取到的输入流进行读取
                inputStreamReader = new InputStreamReader(in);
                bufr = new BufferedReader(inputStreamReader);
                response = new StringBuilder();
                String line = null;
                while ((line = bufr.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } else {
                in = connection.getErrorStream();
                //下面对获取到的输入流进行读取
                inputStreamReader = new InputStreamReader(in);
                bufr = new BufferedReader(inputStreamReader);
                response = new StringBuilder();
                String line = null;
                while ((line = bufr.readLine()) != null) {
                    response.append(line);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("StatusCode", responseCode);
                jsonObject.put("ResponseStr", response.toString());

                return jsonObject.toString();

            }
        } catch (Exception e) {
            Log.d("HttpClientUtil", e.getMessage());
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {

                }
            }

            if (bufr != null) {
                try {
                    bufr.close();
                } catch (IOException e) {

                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }


    private static String doHttpsPost(String serverURL, String data) {
        HttpsURLConnection connection = null;
        InputStream in = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufr = null;
        try {
            URL url = new URL(serverURL);
            URLConnection urlConnection = url.openConnection();
            if (!(urlConnection instanceof HttpsURLConnection)) {
                return null;
            }

            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(CONNECTION_TIMEOUT);

            //设置输入流和输出流,都设置为true
            connection.setDoOutput(true);
            connection.setDoInput(true);

            //把提交的数据以输出流的形式提交到服务器
            OutputStream os = connection.getOutputStream();
            os.write(data.getBytes());

            connection.connect();


            StringBuilder response = null;

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                in = connection.getInputStream();
                //下面对获取到的输入流进行读取
                inputStreamReader = new InputStreamReader(in);
                bufr = new BufferedReader(inputStreamReader);
                response = new StringBuilder();
                String line = null;
                while ((line = bufr.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } else {
                in = connection.getErrorStream();
                //下面对获取到的输入流进行读取
                inputStreamReader = new InputStreamReader(in);
                bufr = new BufferedReader(inputStreamReader);
                response = new StringBuilder();
                String line = null;
                while ((line = bufr.readLine()) != null) {
                    response.append(line);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("StatusCode", responseCode);
                jsonObject.put("ResponseStr", response.toString());

                return jsonObject.toString();

            }
        } catch (Exception e) {
            Log.d("HttpClientUtil", e.getMessage());
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {

                }
            }

            if (bufr != null) {
                try {
                    bufr.close();
                } catch (IOException e) {

                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

}
