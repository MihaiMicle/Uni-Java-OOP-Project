package repository;

import domain.Client;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;

public class ClientsRepositoryXML extends FileRepository<Integer, Client> {

    public ClientsRepositoryXML(String filename) {
        super(filename);
    }

    @Override
    protected void readFromFile() {
        this.map = new HashMap<>();
        File file = new File(filename);
        if (!file.exists()) return;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("client");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    Integer id = Integer.parseInt(getTagValue("id", e));
                    String firstName = getTagValue("firstname", e);
                    String lastName = getTagValue("lastname", e);
                    String email = getTagValue("email", e);
                    String phone = getTagValue("phone", e);

                    Client client = new Client(id, firstName, lastName, email, phone);
                    this.map.put(id, client);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void writeToFile() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("clients");
            doc.appendChild(rootElement);

            for (Client client : this.map.values()) {
                Element clientElem = doc.createElement("client");
                rootElement.appendChild(clientElem);

                addTag(doc, clientElem, "id", String.valueOf(client.getID()));
                addTag(doc, clientElem, "firstname", client.getFirstname());
                addTag(doc, clientElem, "lastname", client.getLastname());
                addTag(doc, clientElem, "email", client.getEmail());
                addTag(doc, clientElem, "phone", client.getPhone());
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filename));

            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTagValue(String tag, Element element) {
        return element.getElementsByTagName(tag).item(0).getTextContent();
    }

    private void addTag(Document doc, Element parent, String tagName, String text) {
        Element elem = doc.createElement(tagName);
        elem.appendChild(doc.createTextNode(text));
        parent.appendChild(elem);
    }
}