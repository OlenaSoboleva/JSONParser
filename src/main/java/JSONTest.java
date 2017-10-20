import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JSONTest {
    public static void main(String[] args) {
        String path = "C:\\Temp\\files\\";
        List<String> files = getFilesInFolder(path);
        for (String file : files) {
            {
                String line, json = "";
                String result;

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    while ((line = br.readLine()) != null) {
                        if (isLogLine(line)) {
                            if (!json.isEmpty()) {
                                parseJson(json);
                                result = checkJason(json);
                                if ("" == result) {
                                } else {
                                    fileWriter(result);
                                }
                                json = "";

                            }
                        } else {
                            json += line;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isLogLine(String line) {
        return line.matches("^\\d{2}\\-\\w{3}\\-\\d{4}\\s\\d{2}:\\d{2}:\\d{2},.+$") || line.contentEquals("Request json:") || line.contentEquals("java.lang.NullPointerException") || line.matches("^\\s\\w.+$");


    }

    private static void parseJson(String json) throws Exception {
        if (!json.startsWith("{") && !json.endsWith("}")) json = "{" + json + "}";
//        ObjectMapper om = new ObjectMapper();
//        System.out.println(om.readValue(json, Object.class));
    }

    private static String checkJason(String json) throws IOException, JSONException {

        DocumentContext context = JsonPath.parse(json);
        List<String> eq = context.read("$..capture.urn");
        String result = "";
        if (eq.size() > 0) {
            String example = "urn:WR:" +
                    (((context.read("$..customer.identity.branchBudgetCentre")).toString() + ":" + (context.read("$..consultation.reference")).toString().split("/")[1] + ":" + (context.read("$..customer.identity.customerReferenceNumber")).toString()).replace("[", "").replace("]", "").replace("\"", ""));
            for (String item : eq) {
                if (example.compareTo(item) == 0) {
                    continue;
                } else {
                    result += example + " not matched  " + item + "\n";

                }
            }
            return result;
        }
        return result;
    }


    private static void fileWriter(String result) throws IOException {
        File file = new File("C:\\Temp\\files\\Errors.txt");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(file, true);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        PrintWriter pw = new PrintWriter(bw);
        pw.println(result);
        pw.close();
        System.out.println(result);
    }

    private static List<String> getFilesInFolder(String folderPath) {

        List<String> files = new ArrayList<>();
        if (folderPath != null) {
            File folder = new File(folderPath);
            if (folder.exists()) {
                File[] listOfFiles = folder.listFiles();
                if (listOfFiles != null) {
                    for (File file : listOfFiles) {
                        if (file.isFile()) {
                            files.add(file.getAbsolutePath());
                        }
                    }
                }
            }
        }
        return files;
    }
}

