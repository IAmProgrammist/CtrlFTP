package rchat.info.ctrlftp.core.reflections;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * A utility class which purpose is to load classes for services and dependencies
 */
public class ClassesLoader {
    public static void loadDependencies(Set<Class<? extends AbstractDependency>> dependencyClasses,
                                        NodeList nodes) throws ClassNotFoundException {
        for (int i = 0; i < nodes.getLength(); i++) {
            if (!nodes.item(i).getNodeName().equals("dependency"))
                continue;
            var loadedClass = Class.forName(nodes.item(i).getChildNodes().item(0).getNodeValue());
            if (!AbstractDependency.class.isAssignableFrom(loadedClass))
                throw new ClassNotFoundException("Class " + loadedClass + " should be inherited from AbstractDependency");

            dependencyClasses.add((Class<? extends AbstractDependency>) loadedClass);
        }
    }

    public static void loadServices(Set<Class<?>> serviceClasses,
                                        NodeList nodes) throws ClassNotFoundException {
        for (int i = 0; i < nodes.getLength(); i++) {
            if (!nodes.item(i).getNodeName().equals("service"))
                continue;
            var loadedClass = Class.forName(nodes.item(i).getChildNodes().item(0).getNodeValue());

            serviceClasses.add(loadedClass);
        }
    }

    /**
     * Utility class to load services and dependencies from config files
     * @param serviceClasses a link for a set of service classes
     * @param dependencyClasses a link for a set of dependency classes
     * @param configFiles array of config files
     */
    public static void load(Set<Class<?>> serviceClasses, Set<Class<? extends AbstractDependency>> dependencyClasses,
                            List<String> configFiles) {
        for (var configFilePath : configFiles) {
            try {
                InputStream in = ClassesLoader.class.getClassLoader().getResourceAsStream(configFilePath);
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(in);

                var root = doc.getDocumentElement();
                var nodes = root.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    if (nodes.item(i).getNodeName().equalsIgnoreCase("dependencies")) {
                        loadDependencies(dependencyClasses, nodes.item(i).getChildNodes());
                    } else if (nodes.item(i).getNodeName().equalsIgnoreCase("services")) {
                        loadServices(serviceClasses, nodes.item(i).getChildNodes());
                    }
                }
            } catch (ParserConfigurationException | IOException | SAXException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
