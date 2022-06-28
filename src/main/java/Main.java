import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.*;
import org.w3c.dom.ls.LSOutput;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }


    public static void writeString(String filename, String content) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(content);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<Employee> parseXML(String filename) throws ParserConfigurationException, IOException, SAXException {
        long idParse = 0;
        String firstNameParse = null;
        String lastNameParse = null;
        String countryParse = null;
        int ageParse = 0;
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filename));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node1 = nodeList.item(i);
            if (Node.ELEMENT_NODE == node1.getNodeType()) {
                NodeList nodeListInner = node1.getChildNodes();
                for (int j = 0; j < nodeListInner.getLength(); j++) {
                    Node node2 = nodeListInner.item(j);
                    if (Node.ELEMENT_NODE == node2.getNodeType()) {
                        Element element = (Element) node2;
                        switch (element.getNodeName()) {
                            case "id":
                                idParse = Long.parseLong(element.getTextContent());
                                break;
                            case "firstName":
                                firstNameParse = element.getTextContent();
                                break;
                            case "lastName":
                                lastNameParse = element.getTextContent();
                                break;
                            case "country":
                                countryParse = element.getTextContent();
                                break;
                            case "age":
                                ageParse = Integer.parseInt(element.getTextContent());
                                break;
                        }
                    }
                }
                list.add(new Employee(idParse, firstNameParse, lastNameParse, countryParse, ageParse));
            }
        }
        return list;
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileNameJSON = "data.json";
        //CSV - JSON парсер
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(fileNameJSON, json);

        String fileName1 = "data.xml";
        String fileNameJSON1 = "data1.json";
        //XML - JSON парсер
        List<Employee> list1 = parseXML(fileName1);
        String json1 = listToJson(list1);
        writeString(fileNameJSON1, json1);
    }
}

