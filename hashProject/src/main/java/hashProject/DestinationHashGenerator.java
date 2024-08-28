package hashProject;

import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN_Number> <JSON_File_Path>");
            System.exit(1);
        }

        String prnNumber = args[0];
        String jsonFilePath = args[1];

        try {
            String jsonContent = new String(Files.readAllBytes(new File(jsonFilePath).toPath()));
            JSONObject jsonObject = new JSONObject(jsonContent);

            String destinationValue = findDestination(jsonObject);
            if (destinationValue == null) {
                System.out.println("Destination key not found in the JSON file.");
                System.exit(1);
            }

            String randomString = generateRandomString(8);
            String concatenatedString = prnNumber + destinationValue + randomString;
            String hashedValue = generateMD5Hash(concatenatedString);

            System.out.println(hashedValue + ";" + randomString);

        } catch (FileNotFoundException e) {
            System.out.println("JSON file not found: " + jsonFilePath);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String findDestination(Object json) {
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            if (jsonObject.has("destination")) {
                return jsonObject.getString("destination");
            }
            for (String key : jsonObject.keySet()) {
                String result = findDestination(jsonObject.get(key));
                if (result != null) {
                    return result;
                }
            }
        } else if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            for (int i = 0; i < jsonArray.length(); i++) {
                String result = findDestination(jsonArray.get(i));
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
